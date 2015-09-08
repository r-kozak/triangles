package com.kozak.triangles.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.ConstructionProject;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.entities.UserLicense;
import com.kozak.triangles.entities.Vmap;
import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.enums.buildings.BuildingsT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;
import com.kozak.triangles.utils.Consts;
import com.kozak.triangles.utils.DateUtils;
import com.kozak.triangles.utils.ProposalGenerator;
import com.kozak.triangles.utils.Random;
import com.kozak.triangles.utils.SingletonData;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@Controller
public class HomeController extends BaseController {

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    String homeGET(User user, Model model) throws InterruptedException {
        if (user == null) {
            return "redirect:/";
        }
        User currUser = userRep.getCurrentUserByLogin(user.getLogin().toLowerCase());
        model.addAttribute("user", currUser); // чтобы в след. действиях был уже юзер со всеми полями

        // set currUser lastEnter
        currUser.setLastEnter(new Date());
        userRep.updateUser(currUser);

        int userId = currUser.getId();

        buildDataInit(); // инициализируем данные по строениям для каждого типа
        checkFirstTime(currUser); // проверка, первый ли вход в игру (вообще)
        giveDailyBonus(currUser); // начисление ежедневного бонуса
        giveCreditDeposit(userId); // начисление кредита/депозита
        manageREMarketProposals(userId); // очистить-добавить предложения на глобальный рынок недвижимости
        Util.profitCalculation(userId, buiDataRep, prRep); // начисление прибыли по имуществу пользователя
        propertyDepreciation(userId); // начисление амортизации
        levyOnProperty(userId); // сбор средств с имущества, где есть кассир
        salaryPayment(userId); // выдача зп работникам

        // начислить проценты завершенности для всех объектов строительства
        List<ConstructionProject> constrProjects = consProjectRep.getUserConstructProjects(userId);
        BuildingController.computeAndSetCompletePercent(constrProjects, consProjectRep);

        // проверить окончилась ли лицензия
        User userWithLicense = userRep.getUserWithLicense(userId); // пользователь с лицензиями
        UserLicense userLicense = userWithLicense.getUserLicense();
        Date licenseExpireDate = userLicense.getLossDate(); // дата окончания лицензии
        BuildingController.checkLicenseExpire(licenseExpireDate, userRep, userId); // если кончилась - назначить новую

        // статистика
        String userBalance = trRep.getUserBalance(userId);
        int userDomi = userRep.getUserDomi(userId);
        model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId), userDomi);
        model.addAttribute("rePrCo", rePrRep.allPrCount(false, userId)); // колво предложений на рынке имущества
        model.addAttribute("newRePrCo", rePrRep.allPrCount(true, userId)); // новых предложений на рын.имущ.
        model.addAttribute("ready", prRep.allPrCount(userId, true, false)); // колво готовых к сбору дохода
        model.addAttribute("comPrCount", prRep.allPrCount(userId, false, false)); // всего имущества
        model.addAttribute("nextProfit", prRep.getMinNextProfit(userId)); // дата следующей прибыли
        model.addAttribute("needRepair", prRep.allPrCount(userId, false, true)); // скольким имуществам нужен ремонт
        model.addAttribute("licenseLevel", userLicense.getLicenseLevel()); // уровень лицензии
        model.addAttribute("licenseExpire", licenseExpireDate); // окончание лицензии

        model.addAttribute("profitSum", trRep.getSumByTransfType(userId, TransferT.PROFIT)); // прибыль всего
        model.addAttribute("profitFromProp", trRep.getSumByAcf(userId, ArticleCashFlowT.LEVY_ON_PROPERTY));
        model.addAttribute("profitFromPropSell", trRep.getSumByAcf(userId, ArticleCashFlowT.SELL_PROPERTY));
        model.addAttribute("profitDB", trRep.getSumByAcf(userId, ArticleCashFlowT.DAILY_BONUS));
        model.addAttribute("profitDep", trRep.getSumByAcf(userId, ArticleCashFlowT.DEPOSIT));
        model.addAttribute("profitDomi", trRep.getSumByAcf(userId, ArticleCashFlowT.DOMINANT_TO_TRIAN));

        model.addAttribute("spendSum", trRep.getSumByTransfType(userId, TransferT.SPEND)); // расход всего
        model.addAttribute("spendCr", trRep.getSumByAcf(userId, ArticleCashFlowT.CREDIT));
        model.addAttribute("spendBuyPr", trRep.getSumByAcf(userId, ArticleCashFlowT.BUY_PROPERTY));
        model.addAttribute("spendRepair", trRep.getSumByAcf(userId, ArticleCashFlowT.PROPERTY_REPAIR));
        model.addAttribute("spendUpCash", trRep.getSumByAcf(userId, ArticleCashFlowT.UP_CASH_LEVEL));
        model.addAttribute("spendUpLevel", trRep.getSumByAcf(userId, ArticleCashFlowT.UP_PROP_LEVEL));

        return "index/home";
    }

    /**
     * wiki
     */
    @RequestMapping(value = "/wiki", method = RequestMethod.GET)
    String wiki(User user, Model model) {
        int userId = user.getId();
        String userBalance = trRep.getUserBalance(userId);
        int userDomi = userRep.getUserDomi(userId);
        model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId), userDomi);

        // данные имущества
        model.addAttribute("commBuData", buiDataRep.getCommBuildDataList());
        // коэфициенты вместимости кассы
        model.addAttribute("univCoef", Consts.UNIVERS_K);
        // мин и макс частота генерации предложений на рынке
        model.addAttribute("frp_min", Consts.FREQ_RE_PROP_MIN);
        model.addAttribute("frp_max", Consts.FREQ_RE_PROP_MAX);
        // районы города
        model.addAttribute("gp", Consts.GHETTO_P);
        model.addAttribute("op", Consts.OUTSKIRTS_P);
        model.addAttribute("chp", Consts.CHINA_P);
        model.addAttribute("cep", Consts.CENTER_P);
        // проценты типов строителей
        model.addAttribute("builders", Consts.BUILDERS_COEF);
        // цены и сроки лицензий на строительство
        model.addAttribute("licPrice", Consts.LICENSE_PRICE);
        model.addAttribute("licTerm", Consts.LICENSE_TERM);
        // ставки кредита и депозита
        model.addAttribute("cr_rate", Consts.CREDIT_RATE);
        model.addAttribute("dep_rate", Consts.DEPOSIT_RATE);
        // ежедневный бонус
        model.addAttribute("dailyBonus", Consts.DAILY_BONUS_SUM);
        // коэф-ты уменьшения сумм
        model.addAttribute("kdr", Consts.K_DECREASE_REPAIR);
        model.addAttribute("kdp", Consts.K_DECREASE_PROP_L);
        model.addAttribute("kdc", Consts.K_DECREASE_CASH_L);
        // максимальные уровни имущества и кассы
        model.addAttribute("max_prop_lev", Consts.MAX_PROP_LEVEL);
        model.addAttribute("max_cash_lev", Consts.MAX_CASH_LEVEL);
        return "wiki";
    }

    /**
     * еженедельное начисление амортизации. Есть основной % износа и доп. % износа. Доп. плюсуется или минусуется от
     * основного.
     * 
     * Основной = 100 / срок полезного использования имущества. Доп = Основной * РАНД(1, 30) / 100;
     * 
     * @param currUserId
     */
    private void propertyDepreciation(int userId) {
        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);
        // получить валидное имущество пользователя, у которого nD <= тек.дата
        ArrayList<Property> properties = (ArrayList<Property>) prRep.getPropertyListForProfit(userId, false);
        // генератор
        Random rand = new Random();

        // для каждого имущества
        for (Property p : properties) {
            CommBuildData data = mapData.get(p.getCommBuildingType().name());

            int deprSum = 0;
            double deprPerc = 0;

            // расчитать, за сколько недель нужно насчитать прибыль
            Date d1 = p.getNextDepreciation();
            Date d2 = new Date();
            int dayBetw = DateUtils.daysBetween(d1, d2) + 1;
            int calcC = (dayBetw / 7) + 1; // количество начислений

            for (int i = 0; i < calcC; i++) {
                // будем (+ || -) доп. процент износа
                boolean plus = (rand.generateRandNum(0, 1) == 0) ? true : false;

                // ОСНОВНОЙ % за неделю
                double mainPerc = 100 / data.getUsefulLife();

                // ДОП % (расч. от основного)
                double additPerc = mainPerc * (int) rand.generateRandNum(1, 30) / 100;

                // приплюсовать или отминусовать ПР от ПИ
                if (plus)
                    mainPerc += additPerc;
                else
                    mainPerc -= additPerc;

                // вычислить недельную сумму износа
                deprSum += p.getInitialCost() * mainPerc / 100;
                deprPerc += mainPerc;
            }

            // установить новые значения
            p.setNextDepreciation(DateUtils.addDays(new Date(), 7 - (dayBetw % 7)));
            p.setSellingPrice(p.getSellingPrice() - deprSum);
            p.setDepreciationPercent(Util.numberRound(p.getDepreciationPercent() + deprPerc, 2));
            prRep.updateProperty(p);

            // если имущество на продаже - изменить цену в предложении
            if (p.isOnSale()) {
                // получить предложение с этим имуществом на рынке и установить новую цену
                RealEstateProposal rePr = rePrRep.getProposalByUsedId(p.getId());
                rePr.setPurchasePrice(p.getSellingPrice());
                rePrRep.updateREproposal(rePr);
            }
        }
    }

    /**
     * Первоначальное добавление данных о каждом типе имущества в БД. Нужно для дальнейшего получения данных и генерации
     * предложений на рынке
     * 
     * После добавления нового типа (CommBuildingsT) необходимо его добавить и здесь
     */
    private void buildDataInit() {
        CommBuildData data = null;
        BuildingsT superTYPE = BuildingsT.TRADING;

        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

        // init STALL
        CommBuildingsT TYPE = CommBuildingsT.STALL;
        if (mapData.get(TYPE.name()) == null) {
            data = new CommBuildData(3, 6, 4500, 5500, TYPE, superTYPE, 4, 1, 2);
            buiDataRep.addCommBuildingData(data);
        }

        // init VILLAGE_SHOP
        TYPE = CommBuildingsT.VILLAGE_SHOP;
        if (mapData.get(TYPE.name()) == null) {
            data = new CommBuildData(2, 10, 10000, 15000, TYPE, superTYPE, 6, 2, 3);
            buiDataRep.addCommBuildingData(data);
        }

        // init STATIONER_SHOP
        TYPE = CommBuildingsT.STATIONER_SHOP;
        if (mapData.get(TYPE.name()) == null) {
            data = new CommBuildData(5, 12, 17000, 30000, TYPE, superTYPE, 7, 1, 4);
            buiDataRep.addCommBuildingData(data);
        }
    }

    /**
     * Управляет предложениями на рынке недвижимости
     * 
     * очищает рынок от устаревших предложений, а затем генерирует и добавляет новые предложения на рынок недвижимости,
     * генерирует след. дату генерации предложений
     */
    private void manageREMarketProposals(int userId) {
        clearREMarket(); // очищает рынок от устаревших предложений

        int activeUsers = userRep.countActiveUsers();
        boolean marketEmpty = rePrRep.allPrCount(false, userId) == 0;

        Vmap vm = vmRep.getNextProposeGen(); // получаем экземпляр константы (след. даты генерации предложений)
        Date nrep = DateUtils.stringToDate(vm.getValue()); // берем из нее значение даты
        boolean dateCome = (new Date().after(nrep)); // пришла след. дата генерации предложений

        if (marketEmpty || dateCome) {
            // получаем данные всех коммерческих строений
            HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);
            // генерируем предложения
            ProposalGenerator pg = new ProposalGenerator();
            ArrayList<RealEstateProposal> result = pg.generateProposalsREMarket(activeUsers, mapData);
            while (result.isEmpty()) {
                result = pg.generateProposalsREMarket(activeUsers, mapData);
            }
            for (RealEstateProposal prop : result) {
                rePrRep.addREproposal(prop);
            }
        }
        // если пришла дата след. генерации, значит нужно генерить новую
        if (dateCome) {
            generateNewNextDate(vm); // генерация новой даты NextREproposal (константа с Vmap)
        }
    }

    /**
     * генерирует новый экземпляр даты следующей генерации предложений
     * 
     * @param oldValue
     *            старый экземпляр константы для обновления
     */
    private void generateNewNextDate(Vmap oldValue) {
        ProposalGenerator pg = new ProposalGenerator();
        String newDate = pg.generateNEXT_RE_PROPOSE();

        oldValue.setValue(newDate);
        vmRep.updateVmapRow(oldValue);
    }

    /**
     * очищает рынок недвижимости от не актуальных предложений (lossDate которых меньше текущей)
     */
    private void clearREMarket() {
        List<RealEstateProposal> outdated = rePrRep.getOutdatedProposals();
        for (RealEstateProposal rep : outdated) {
            // если это б/у имущество - начислить деньги продавцу
            if (rep.getUsedId() != 0) {
                Util.buyUsedProperty(rep, new Date(), 0, "sold", prRep, trRep);
            }

            // rePrRep.removeReProposalByUsedId(rep.getId());
            rep.setValid(false);
            rePrRep.updateREproposal(rep);
        }
    }

    /**
     * проверка, первый ли раз вошли в игру. если первый - добавляем начальные транзакции пользователю
     * 
     * @throws InterruptedException
     * 
     */
    private void checkFirstTime(User user) throws InterruptedException {
        Date lastBonus = user.getLastBonus();
        if (lastBonus == null) {
            Date yest = DateUtils.getYesterday();

            // upd user
            user.setLastBonus(yest);
            user.setDayNumber(0);
            userRep.updateUser(user);

            // transaction for DAILY_BONUS
            Transaction firstT = new Transaction("Начальный капитал", yest, 17000, TransferT.PROFIT, user.getId(),
                    17000, ArticleCashFlowT.DAILY_BONUS);
            trRep.addTransaction(firstT);

            // transaction for DEPOSIT
            firstT = new Transaction("Начальный депозит", yest, 0, TransferT.PROFIT, user.getId(), 17000,
                    ArticleCashFlowT.DEPOSIT);
            trRep.addTransaction(firstT);

            // transaction for CREDIT
            firstT = new Transaction("Начальный кредит", yest, 0, TransferT.SPEND, user.getId(), 17000,
                    ArticleCashFlowT.CREDIT);
            trRep.addTransaction(firstT);

            // transaction for LEVY_ON_PROPERTY
            firstT = new Transaction("Начальный сбор с имущества", yest, 0, TransferT.PROFIT, user.getId(), 17000,
                    ArticleCashFlowT.LEVY_ON_PROPERTY);
            trRep.addTransaction(firstT);
        }
    }

    /**
     * начисление дневного бонуса.
     * 
     * @param currUserId
     */
    private void giveDailyBonus(User user) {
        // максимальный номер дня при начислении бонуса
        final int MAX_DAY_NUMBER = Consts.DAILY_BONUS_SUM.length - 1;

        Date lastBonus = user.getLastBonus();
        int dayNumber = user.getDayNumber();

        // пользователь сегодня уже получил бонус?
        boolean gotBonusToday = DateUtils.isSameDay(lastBonus, new Date());

        if (!gotBonusToday) {
            int daysBetw = DateUtils.daysBetween(DateUtils.getStart(lastBonus), DateUtils.getStart(new Date()));

            if (dayNumber == MAX_DAY_NUMBER || daysBetw > 1) {
                dayNumber = 1;
            } else {
                dayNumber++;
            }

            // добавляем транзакцию
            int bonusSum = Consts.DAILY_BONUS_SUM[dayNumber];
            String description = "Ежедневный бонус (день " + dayNumber + "-й)";
            long oldBalance = Long.parseLong(trRep.getUserBalance(user.getId()));
            Transaction t = new Transaction(description, new Date(), bonusSum, TransferT.PROFIT, user.getId(),
                    oldBalance + bonusSum, ArticleCashFlowT.DAILY_BONUS);
            trRep.addTransaction(t);

            // обновляем данные юзера
            user.setLastBonus(new Date());
            user.setDayNumber(dayNumber);
            userRep.updateUser(user);
        }
    }

    /**
     * начисление кредита/депозита начисляется каждый месяц ставки описаны в интерфейсе Consts
     * 
     * @param currUserId
     */
    private void giveCreditDeposit(int currUserId) {
        // получение транзакций пользователя для получения последней даты начисления
        List<Transaction> userTransactionsCr = trRep.getUserTransactionsByType(currUserId, ArticleCashFlowT.CREDIT);
        List<Transaction> userTransactionsDep = trRep.getUserTransactionsByType(currUserId, ArticleCashFlowT.DEPOSIT);

        // получение дат кредита и депозита, после чего взятие последней
        Date lastTransactionDateCr = userTransactionsCr.get(userTransactionsCr.size() - 1).getTransactDate();
        Date lastTransactionDateDep = userTransactionsDep.get(userTransactionsDep.size() - 1).getTransactDate();
        Date lastTransactionDate = (lastTransactionDateCr.after(lastTransactionDateDep)) ? lastTransactionDateCr
                : lastTransactionDateDep;

        int daysBetween = DateUtils.daysBetween(lastTransactionDate, new Date());
        if (daysBetween > 0) {
            int countI = daysBetween / 30;
            for (int i = 0; i < countI; i++) {
                long userBalance = Long.parseLong(trRep.getUserBalance(currUserId));
                double rate = (userBalance > 0 ? Consts.DEPOSIT_RATE : Consts.CREDIT_RATE);
                TransferT transferType = (userBalance > 0 ? TransferT.PROFIT : TransferT.SPEND);
                ArticleCashFlowT acf = (userBalance > 0 ? ArticleCashFlowT.DEPOSIT : ArticleCashFlowT.CREDIT);
                long sum = Math.round(userBalance * rate);
                long newBalance = userBalance + sum;

                // date for description
                Calendar c1 = Calendar.getInstance();
                // отнимаем необходимое количество дней, например с последнего начисления прошло 64 дня
                // начисляем за 2 месяца
                // итерация 1: тек дата - ((2 - 0) * 30) + 4 = 64. дата с 13.04.15 по 12.05.15
                // итерация 2: тек дата - ((2 - 1) * 30) + 4 = 34. дата с 12.05.15 по 11.06.15
                c1.add(Calendar.DATE, -((countI - i) * 30 + daysBetween % 30));
                Date dateFrom = c1.getTime();

                Calendar c2 = Calendar.getInstance();
                c2 = c1;
                c2.add(Calendar.DATE, 30);
                Date dateTo = c2.getTime();
                // //

                String description = (userBalance > 0 ? "Начислено депозит за: %td.%tm.%ty - %td.%tm.%ty"
                        : "Начислено кредит за: %td.%tm.%ty - %td.%tm.%ty");
                description = String.format(description, dateFrom, dateFrom, dateFrom, dateTo, dateTo, dateTo);

                Transaction cdTr = new Transaction(description, new Date(), Math.abs(sum), transferType, currUserId,
                        newBalance, acf);
                trRep.addTransaction(cdTr);
            }
        }
    }

    private void salaryPayment(int currUserId) {
        // TODO Auto-generated method stub

        // выдача зарплаты кассирам, продавцам
    }

    private void levyOnProperty(int currUserId) {
        // TODO Auto-generated method stub

        // для каждого коммерческого имущества юзера
        // если у имущества есть кассир - тогда собираем прибыль
    }

}
