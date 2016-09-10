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
import com.kozak.triangles.enums.CityAreas;
import com.kozak.triangles.enums.LotteryArticles;
import com.kozak.triangles.enums.TradeBuildingsTypes;
import com.kozak.triangles.enums.TransferTypes;
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
import com.kozak.triangles.utils.ResponseUtil;

@Service
public class LicenseMarketServiceImpl implements LicenseMarketService {

	// надбавка к требованию доминантности за каждый уровень магазина
	private static final int DOMI_PREMIUM_FOR_LEVEL = 250;

	private static final int LEVEL_OF_STATIONER_SHOP = 10;
	private static final int DOMI_COUNT_TO_BUILD = 1500;
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
	public boolean isMarketBuilt(int userId) {
		// если магазин есть в базе данных (!=null) - значит он построен
		return getLicenseMarket(userId, false) != null;
	}


	@Override
	public LicenseMarket getLicenseMarket(Integer userId, boolean isLoadConsignments) {
		LicenseMarket licenseMarket = licenseMarketRepository.getLicenseMarketByUserId(userId, isLoadConsignments);
		return licenseMarket;
	}

	@Override
	public List<Requirement> computeBuildRequirements(int userId) {
		List<Requirement> result = new ArrayList<>();

		// требование, что у пользователя достаточное количество доминантности для постройки
		Requirement domiRequirement = createDomiRequirement(userId, DOMI_COUNT_TO_BUILD);
		result.add(domiRequirement);

		// требование, что у пользователя есть достаточное количество Магазинов канцтоваров для постройки
		Requirement stationerShopRequirement = createStationerShopsRequirement(userId, LicenseMarket.START_LEVEL);
		result.add(stationerShopRequirement);

		// требование, что состоятельность пользователя позволяет построить магазин
		long requiredSum = getPriceOfBuild();
		long userSolvency = CommonUtil.getSolvency(transactionRep, propertyRep, userId); // состоятельность пользователя
		String moneyDescription = String.format("Цена постройки <b>%d &tridot;</b>. Необходимо иметь на счету.", requiredSum);
		Requirement moneyRequirement = new Requirement(userSolvency >= requiredSum, moneyDescription);
		result.add(moneyRequirement);

		return result;
	}

	@Override
	public List<Requirement> computeFunctionRequirements(int userId) {
		List<Requirement> result = new ArrayList<>();
		LicenseMarket licenseMarket = getLicenseMarket(userId, false);

		// требование, что у пользователя достаточное количество доминантности для функционирования
		// расчитывается, как [базовая ставка доминантности] + (([тек.уровень магазина] - [уровень магазина сразу, после
		// постройки]) * [надбавка к требованию доминантности за каждый уровень магазина])
		int domiCount = DOMI_COUNT_TO_BUILD + ((licenseMarket.getLevel() - LicenseMarket.START_LEVEL) * DOMI_PREMIUM_FOR_LEVEL);
		Requirement domiRequirement = createDomiRequirement(userId, domiCount);
		result.add(domiRequirement);

		// требование, что у пользователя достаточно Магазинов канцтоваров для функционирования
		// Для функционирования нужно иметь [уровень магазина лицензий] Магазинов канцтоваров, 10-го уровня, в центре
		Requirement stationerShopRequirement = createStationerShopsRequirement(userId, licenseMarket.getLevel());
		result.add(stationerShopRequirement);

		return result;
	}

	@Override
	public boolean isMarkerCanBeBuilt(int userId) {
		return isAllRequirementsCarriedOut(computeBuildRequirements(userId));
	}

	@Override
	public boolean isMarketCanFunction(int userId) {
		return isAllRequirementsCarriedOut(computeFunctionRequirements(userId));
	}

	@Override
	public void buildNewLicenseMarket(Integer userId) {
		boolean marketAlreayBuilt = isMarketBuilt(userId); // построен ли магазин
		boolean markerCanBeBuilt = isMarkerCanBeBuilt(userId); // можно ли его построить

		if (!marketAlreayBuilt && markerCanBeBuilt) {
			// если магазин НЕ построен и его можно построить
			// снять деньги
			String descr = "Постройка Магазина лицензий";
			long userMoney = Long.parseLong(transactionRep.getUserBalance(userId));
			long price = getPriceOfBuild();
			Transaction tr = new Transaction(descr, new Date(), price, TransferTypes.SPEND, userId, userMoney - price,
					ArticleCashFlow.CONSTRUCTION_PROPERTY);
			transactionRep.addTransaction(tr);

			// создать новый магазин, пренадлежащий пользователю
			licenseMarketRepository.addLicenseMarket(new LicenseMarket(userId));
		}
	}

	@Override
	@Transactional
	public void processSoldLicenses(Integer userId) {
		List<LicensesConsignment> soldConsignments = licensesConsignmentRepository.getSoldConsignments(userId);
		for (LicensesConsignment consignment : soldConsignments) {
			// начислить деньги за продажу партии
			String descr = String.format("Продажа лицензий уровня %d, %d шт.", consignment.getLicenseLevel(),
					consignment.getCountOnSell());
			long userMoney = Long.parseLong(transactionRep.getUserBalance(userId));
			Transaction tr = new Transaction(descr, new Date(), consignment.getProfit(), TransferTypes.PROFIT, userId,
					userMoney + consignment.getProfit(), ArticleCashFlow.SELL_LICENSE);
			transactionRep.addTransaction(tr);

			// удалить партию лицензий
			licensesConsignmentRepository.removeConsignment(consignment);
		}
	}

	@Override
	public Requirement isLicensesCanBeSold(int licensesLevel, int userId) {
		LicenseMarket licenseMarket = getLicenseMarket(userId, false);

		try {
			LotteryArticles licenseLevelArticle = LotteryArticles.getLicenseArticleByLevel(licensesLevel);
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
	public JSONObject confirmLicenseSelling(int licensesCount, byte licensesLevel, Integer userId, JSONObject resultJson) {
		if (isMarketCanFunction(userId)) {
			try {
				// получить количество лицензий определенного уровня на остатке
				LotteryArticles licenseLevelArticle = LotteryArticles.getLicenseArticleByLevel(licensesLevel);
				long countOfLicenses = lotteryRep.getPljushkiCountByArticle(userId, licenseLevelArticle);

				if (countOfLicenses >= 0) {
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

	/**
	 * Создает партию лицензий на продаже
	 */
	private void createLicensesConsignment(int userId, int licensesCount, byte licensesLevel) {
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
	private void withdrawFromLotoInfo(int userId, LotteryArticles licenseLevelArticle, int requiredCount) {
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
				break;
			} else {
				// в сущности на остатке < чем нужно
				lotteryInfo.setRemainingAmount(0); // забрать из остатков все
				withdrawals += countOfRemains;
			}
			lotteryRep.updateLotoInfo(lotteryInfo);
			leftToWithDraw = requiredCount - withdrawals;
			i++;
		}
	}

	/**
	 * @return требование количества доминантности у пользователя
	 */
	private Requirement createDomiRequirement(int userId, int domiCount) {
		int userDomi = userRep.getUserDomi(userId);
		return new Requirement(userDomi >= domiCount, domiCount + DOMI_REQUIREMENT_DESCRIPTION);
	}

	/**
	 * @return требование количества Магазинов канцтоваров у пользователя
	 */
	private Requirement createStationerShopsRequirement(int userId, int countOfShops) {
		List<Property> stationerShops = propertyRep.getPropertyListWithParams(userId, TradeBuildingsTypes.STATIONER_SHOP,
				CityAreas.CENTER, LEVEL_OF_STATIONER_SHOP, null);
		String shopsDescription = String.format("Необходимо %d шт. Магазин канцтоваров (%d-го уровня, в центре)", countOfShops,
				LEVEL_OF_STATIONER_SHOP);
		Requirement stationerShopRequirement = new Requirement(stationerShops.size() >= countOfShops, shopsDescription);
		return stationerShopRequirement;
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
		return (long) (LicenseMarket.BASE_PRICE * Constants.UNIVERS_K[LicenseMarket.START_LEVEL]);
	}
}
