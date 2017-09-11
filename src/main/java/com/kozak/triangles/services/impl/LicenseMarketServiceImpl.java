package com.kozak.triangles.services.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.data.LicensesTableData;
import com.kozak.triangles.entities.LicenseMarket;
import com.kozak.triangles.entities.LicensesConsignment;
import com.kozak.triangles.entities.LotteryInfo;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.LotteryArticle;
import com.kozak.triangles.enums.TradeBuildingType;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.exceptions.NoSuchLicenseLevelException;
import com.kozak.triangles.models.Requirement;
import com.kozak.triangles.repositories.LicenseMarketRepository;
import com.kozak.triangles.repositories.LicensesConsignmentRepository;
import com.kozak.triangles.repositories.LotteryRep;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.services.LicenseMarketService;
import com.kozak.triangles.utils.CommonUtil;
import com.kozak.triangles.utils.Constants;
import com.kozak.triangles.utils.DateUtils;
import com.kozak.triangles.utils.Ksyusha;
import com.kozak.triangles.utils.ResponseUtil;

@Service
public class LicenseMarketServiceImpl implements LicenseMarketService {

    private static final String COUNT_MUST_BE_MORE_THAN_0 = "Количество лицензий для продажи должно быть больше 0.";

    // надбавка к требованию доминантности за каждый уровень магазина
    private static final int DOMI_PREMIUM_FOR_LEVEL = 500;

    private static final int LEVEL_OF_STATIONER_SHOP = 10;
    private static final int BASE_DOMI_COUNT = 1000;
    private static final String DOMI_REQUIREMENT_DESCRIPTION = " доминантности.";
    private static final String LICENSES_NOT_ENOUGH = "Лицензий для продажи недостаточно!";
    private static final String MARKET_CANNOT_FUNCTION = "Магазин лицензий не может функционировать!";
    private static final int PREMIUM_PERCENT_FOR_ONE_LEVEL = 10;

    @Autowired
    private LicenseMarketRepository licenseMarketRepository;
    @Autowired
    private LicensesConsignmentRepository licensesConsignmentRepository;
    @Autowired
    private PropertyRep propertyRep;
    @Autowired
    private TransactionRep transactionRep;
    @Autowired
    private UserRep userRep;
    @Autowired
    private LotteryRep lotteryRep;

    @Override
    public boolean isMarketBuilt(long userId) {
        // если магазин есть в базе данных (!=null) - значит он построен
        return getLicenseMarket(userId, false) != null;
    }

    @Override
    public LicenseMarket getLicenseMarket(long userId, boolean isLoadConsignments) {
        LicenseMarket licenseMarket = licenseMarketRepository.getLicenseMarketByUserId(userId, isLoadConsignments);
        return licenseMarket;
    }

    @Override
    public List<Requirement> computeBuildRequirements(long userId) {
        List<Requirement> result = new ArrayList<>();

        // требование, что у пользователя достаточное количество доминантности для постройки
        int domiCount = BASE_DOMI_COUNT + (LicenseMarket.START_LEVEL * DOMI_PREMIUM_FOR_LEVEL);
        Requirement domiRequirement = createDomiRequirement(userId, domiCount);
        result.add(domiRequirement);

        // требование, что у пользователя есть достаточное количество Магазинов канцтоваров для постройки
        Requirement stationerShopRequirement = createStationerShopsRequirement(userId, LicenseMarket.START_LEVEL);
        result.add(stationerShopRequirement);

        // требование, что состоятельность пользователя позволяет построить магазин
        long requiredSum = getPriceOfBuild();
        Requirement moneyRequirement = createMoneyRequirement(userId, requiredSum);
        result.add(moneyRequirement);

        return result;
    }

    @Override
    public List<Requirement> computeFunctionRequirements(long userId) {
        List<Requirement> result = new ArrayList<>();
        LicenseMarket licenseMarket = getLicenseMarket(userId, false);

        // требование, что у пользователя достаточное количество доминантности для функционирования
        // расчитывается, как [базовая ставка доминантности] + (([тек.уровень магазина] - [уровень магазина сразу, после
        // постройки]) * [надбавка к требованию доминантности за каждый уровень магазина])
        int domiCount = BASE_DOMI_COUNT + ((licenseMarket.getLevel() - LicenseMarket.START_LEVEL) * DOMI_PREMIUM_FOR_LEVEL);
        Requirement domiRequirement = createDomiRequirement(userId, domiCount);
        result.add(domiRequirement);

        // требование, что у пользователя достаточно Магазинов канцтоваров для функционирования
        // Для функционирования нужно иметь [уровень магазина лицензий] АКТИВНЫХ Магазинов канцтоваров, 10-го уровня, в центре
        Requirement stationerShopRequirement = createStationerShopsRequirement(userId, licenseMarket.getLevel());
        result.add(stationerShopRequirement);

        return result;
    }

    @Override
    public List<Requirement> computeRequirementsForLevelUp(long userId) {
        LicenseMarket market = getLicenseMarket(userId, false);
        int targetLevel = market.getLevel() + 1;

        List<Requirement> resultRequirements = new ArrayList<>();

        // требование, что у пользователя достаточное количество доминантности для повышения уровня
        // расчитывается, как [базовая ставка доминантности] + ([целевой уровень] * [надбавка к требованию доминантности за каждый
        // уровень магазина])
        int domiCount = BASE_DOMI_COUNT + (targetLevel * DOMI_PREMIUM_FOR_LEVEL);
        Requirement domiRequirement = createDomiRequirement(userId, domiCount);
        resultRequirements.add(domiRequirement);

        // требование, что у пользователя достаточно Магазинов канцтоваров для повышения уровня
        // Для функционирования нужно иметь [целевой уровень] АКТИВНЫХ Магазинов канцтоваров, 10-го уровня, в центре
        Requirement stationerShopRequirement = createStationerShopsRequirement(userId, targetLevel);
        resultRequirements.add(stationerShopRequirement);

        // требование наличия денежных средств для повышения уровня
        long requiredSum = getPriceOfLevelUp(targetLevel);
        Requirement moneyRequirement = createMoneyRequirement(userId, requiredSum);
        resultRequirements.add(moneyRequirement);

        return resultRequirements;
    }

    @Override
    public boolean isMarkerCanBeBuilt(long userId) {
        return isAllRequirementsCarriedOut(computeBuildRequirements(userId));
    }

    @Override
    public boolean isMarketCanFunction(long userId) {
        return isAllRequirementsCarriedOut(computeFunctionRequirements(userId));
    }

    @Override
    public void buildNewLicenseMarket(long userId) {
        boolean marketAlreayBuilt = isMarketBuilt(userId); // построен ли магазин
        boolean markerCanBeBuilt = isMarkerCanBeBuilt(userId); // можно ли его построить

        if (!marketAlreayBuilt && markerCanBeBuilt) {
            // если магазин НЕ построен и его можно построить
            // снять деньги
            String descr = "Постройка Магазина лицензий";
            long userMoney = Long.parseLong(transactionRep.getUserBalance(userId));
            long price = getPriceOfBuild();
            Transaction tr = new Transaction(descr, new Date(), price, TransferType.SPEND, userId, userMoney - price,
                    ArticleCashFlow.CONSTRUCTION_PROPERTY);
            transactionRep.addTransaction(tr);

            // создать новый магазин, пренадлежащий пользователю
            licenseMarketRepository.addLicenseMarket(new LicenseMarket(userId));
        }
    }

    @Override
    @Transactional
    public void processSoldLicenses(long userId) {
        List<LicensesConsignment> soldConsignments = licensesConsignmentRepository.getSoldConsignments(userId);
        for (LicensesConsignment consignment : soldConsignments) {
            // начислить деньги за продажу партии
            String descr = String.format("Продажа лицензий уровня %d, %d шт.", consignment.getLicenseLevel(),
                    consignment.getCountOnSell());
            long userMoney = Long.parseLong(transactionRep.getUserBalance(userId));
            Transaction tr = new Transaction(descr, new Date(), consignment.getProfit(), TransferType.PROFIT, userId,
                    userMoney + consignment.getProfit(), ArticleCashFlow.SELL_LICENSE);
            transactionRep.addTransaction(tr);

            // удалить партию лицензий
            licensesConsignmentRepository.removeConsignment(consignment);
        }
    }

    @Override
    public Requirement isLicensesCanBeSold(int licensesLevel, long userId) {
        LicenseMarket licenseMarket = getLicenseMarket(userId, false);

        try {
            LotteryArticle licenseLevelArticle = LotteryArticle.getLicenseArticleByLevel(licensesLevel);
            long countOfLicenses = lotteryRep.getPljushkiCountByArticle(userId, licenseLevelArticle);
            if (countOfLicenses <= 0) {
                // Требование №1 - это то, что лицензий должно быть больше 0
                String description1 = MessageFormat.format("У вас нет лицензий уровня <b>{0}</b>.", licensesLevel);
                return new Requirement(countOfLicenses <= 0, description1);
            } else {
                // Требование №2 заключается в том, что минимальный уровень магазина должен равняться уровню лицензий для продажи
                // Уровень лицензий | Минимальный уровень магазина
                // -------------------------------------------------
                // 2 | 2
                // 3 | 3
                // 4 | 4
                String description2 = MessageFormat.format(
                        "Для продажи лицензий уровня <b>{0}</b> необходим Магазин лицензий уровня " + "<b>{0}</b>.",
                        licensesLevel);
                return new Requirement(licenseMarket.getLevel() >= licensesLevel, description2);
            }
        } catch (NoSuchLicenseLevelException e) {
            return new Requirement(false, e.getMessage());
        }
    }

    @Override
    public JSONObject confirmLicenseSelling(int licensesCount, byte licensesLevel, long userId) {
        JSONObject resultJson = new JSONObject();

        if (licensesCount <= 0) {
            // количество лицензий к продаже должно быть > 0
            ResponseUtil.putErrorMsg(resultJson, COUNT_MUST_BE_MORE_THAN_0);
        }

        if (isMarketCanFunction(userId)) {
            try {
                // получить количество лицензий определенного уровня на остатке
                LotteryArticle licenseLevelArticle = LotteryArticle.getLicenseArticleByLevel(licensesLevel);
                long countOfLicenses = lotteryRep.getPljushkiCountByArticle(userId, licenseLevelArticle);

                if (countOfLicenses >= licensesCount) {
                    // если лицензий определенного уровня достаточно на остатках, что были выиграны в лото
                    // создать партию лицензий на продаже
                    createLicensesConsignment(userId, licensesCount, licensesLevel);

                    // списать с остатков выиграных лицензий в лото
                    withdrawFromLotoInfo(userId, licenseLevelArticle, licensesCount);
                } else {
                    // иначе вернуть ошибку, что лицензий НЕ достаточно
                    ResponseUtil.putErrorMsg(resultJson, LICENSES_NOT_ENOUGH);
                }
            } catch (NoSuchLicenseLevelException e) {
                ResponseUtil.putErrorMsg(resultJson, e.getMessage());
            }
        } else {
            ResponseUtil.putErrorMsg(resultJson, MARKET_CANNOT_FUNCTION);
        }
        return resultJson;
    }

    @Override
    public boolean isMaxLevelAchieved(long userId) {
        LicenseMarket market = getLicenseMarket(userId, false);
        return market.getLevel() >= LicenseMarket.MAX_LEVEL;
    }

    @Override
    public boolean isPossibleToUpMarketLevel(long userId) {
        // все требования соблюдены и максимальный уровень магазина не достигнут
        return isAllRequirementsCarriedOut(computeRequirementsForLevelUp(userId)) && !isMaxLevelAchieved(userId);
    }

    @Override
    @Transactional
    public boolean upLicenseMarketLevel(long userId) {
        if (isPossibleToUpMarketLevel(userId)) {
            // повысить уровень магазина
            LicenseMarket market = getLicenseMarket(userId, false);
            int targetLevel = market.getLevel() + 1;
            market.setLevel((byte) targetLevel);
            licenseMarketRepository.updateMarket(market);

            // снять деньги
            long upSum = getPriceOfLevelUp(targetLevel);
            String descr = "Повышение уровня Магазина лицензий до " + targetLevel;
            long userMoney = Long.parseLong(transactionRep.getUserBalance(userId));
            Transaction tr = new Transaction(descr, new Date(), upSum, TransferType.SPEND, userId, userMoney - upSum,
                    ArticleCashFlow.UP_PROP_LEVEL);
            transactionRep.addTransaction(tr);

            return true; // операция выполнена успешно
        }
        return false; // невозможно повысить уровень
    }

    /**
     * Создает партию лицензий на продаже
     */
    private void createLicensesConsignment(long userId, int licensesCount, byte licensesLevel) {
        LicenseMarket market = getLicenseMarket(userId, true);

        LicensesConsignment consignment = new LicensesConsignment();
        consignment.setCountOnSell(licensesCount);
        consignment.setLicenseMarket(market);
        consignment.setLicenseLevel(licensesLevel);
        consignment.setSellDate(DateUtils.addHours(LicensesTableData.getHoursToSellLicenses(licensesLevel)));

        // вычислить прибыль
        // базовая прибыль
        long baseProfit = LicensesTableData.getLicensePrice(licensesLevel) * licensesCount;

        // проценты надбавки за уровень магазина, вычисляется, как:
        // = [Уровень магазина] - [Количество первых уровней, для которых нет надбавки к стоимости лицензий при их продаже] *
        // * [Процент за один уровень]
        int percentForLevel = (market.getLevel() - LicenseMarket.LEVELS_COUNT_WITHOUT_PREMIUM) * PREMIUM_PERCENT_FOR_ONE_LEVEL;

        long totalProfit = baseProfit;
        if (percentForLevel > 0) {
            totalProfit = baseProfit + ((baseProfit * percentForLevel) / 100);
        }
        consignment.setProfit(totalProfit);

        consignment = licensesConsignmentRepository.addLicenseConsignment(consignment); // сохранить новую партию
    }

    /**
     * Списывает с остатков нужное количество лицензий определенного уровня
     * 
     * @param licensesLevel
     *            уровень лицензий
     * @param requiredCount
     *            количество лицензий, которое нужно списать
     */
    private void withdrawFromLotoInfo(long userId, LotteryArticle licenseLevelArticle, int requiredCount) {
        List<LotteryInfo> lotteryInfos = lotteryRep.getLotteryInfoListByArticle(userId, licenseLevelArticle); // остатки

        int withdrawals = 0; // снято всего
        int leftToWithDraw = requiredCount; // осталось снять
        int i = 0;
        while (withdrawals < requiredCount) {
            LotteryInfo lotteryInfo = lotteryInfos.get(i);
            int countOfRemains = lotteryInfo.getRemainingAmount(); // количество в сущности LotteryInfo
            if (countOfRemains >= leftToWithDraw) {
                // если в сущности на остатке >= чем нужно снять, то снять только нужное количество
                lotteryInfo.setRemainingAmount(countOfRemains - leftToWithDraw); // установить новое значение
                withdrawals += leftToWithDraw; // потребности в количестве удовлетворены
                lotteryRep.updateLotoInfo(lotteryInfo);
                break;
            } else {
                // в сущности на остатке < чем нужно
                lotteryInfo.setRemainingAmount(0); // забрать из остатков все
                withdrawals += countOfRemains;
                lotteryRep.updateLotoInfo(lotteryInfo);
            }
            leftToWithDraw = requiredCount - withdrawals;
            i++;
        }
    }

    /**
     * @return требование количества доминантности у пользователя
     */
    private Requirement createDomiRequirement(long userId, int domiCount) {
        int userDomi = userRep.getUserDomi(userId);
        return new Requirement(userDomi >= domiCount, domiCount + DOMI_REQUIREMENT_DESCRIPTION);
    }

    /**
     * @return требование количества Магазинов канцтоваров у пользователя
     */
    private Requirement createStationerShopsRequirement(long userId, int countOfShops) {
        List<Property> stationerShops = propertyRep.getPropertyListWithParams(userId, TradeBuildingType.STATIONER_SHOP,
                CityArea.CENTER, LEVEL_OF_STATIONER_SHOP, null, true);
        String shopsDescription = String.format("Необходимо %d шт. АКТИВНЫХ Магазинов канцтоваров (%d-го уровня, в центре)",
                countOfShops, LEVEL_OF_STATIONER_SHOP);
        Requirement stationerShopRequirement = new Requirement(stationerShops.size() >= countOfShops, shopsDescription);
        return stationerShopRequirement;
    }

    /**
     * @return требование наличия денег у пользователя (на балансе или состоятельность)
     */
    private Requirement createMoneyRequirement(long userId, long requiredSum) {
        long userSolvency = CommonUtil.getSolvency(transactionRep, propertyRep, userId); // состоятельность пользователя
        String moneyDescription = String.format("Цена операции <b>%d &tridot;</b>. Необходимо иметь на счету.", requiredSum);
        Requirement moneyRequirement = new Requirement(userSolvency >= requiredSum, moneyDescription);
        return moneyRequirement;
    }

    /**
     * @return все ли требования удовлетворены
     */
    private boolean isAllRequirementsCarriedOut(List<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            if (!requirement.isCarriedOut()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return стоимость постройки магазина лицензий
     */
    private long getPriceOfBuild() {
        return getPriceOfLevelUp(LicenseMarket.START_LEVEL);
    }

    /**
     * @param targetLevel
     *            целевой уровень, к которому идет повышение
     * @return цену повышения уровня Магазина лицензий
     */
    private long getPriceOfLevelUp(int targetLevel) {
        return (long) (LicenseMarket.BASE_PRICE * Ksyusha.computeCoef(targetLevel));
    }

}
