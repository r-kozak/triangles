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

	private static final int LEVEL_OF_STATIONER_SHOP = 10;
	private static final int DOMI_COUNT_TO_BUILD = 1500;
	private static final String DOMI_REQUIREMENT_DESCRIPTION = DOMI_COUNT_TO_BUILD + " доминантности.";
	private static final String LICENSES_NOT_ENOUGH = "Лицензий для продажи недостаточно!";
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

		// требование, что у пользователя достаточное доминантности для постройки
		int userDomi = userRep.getUserDomi(userId);
		Requirement domiRequirement = new Requirement(userDomi >= DOMI_COUNT_TO_BUILD, DOMI_REQUIREMENT_DESCRIPTION);
		result.add(domiRequirement);

		// требование, что у пользователя достаточно Магазинов канцтоваров для постройки
		// Для постройки нужно иметь 1 магазин канцтоваров, 10-го уровня, в центре
		List<Property> stationerShops = propertyRep.getPropertyListWithParams(userId, TradeBuildingsTypes.STATIONER_SHOP,
				CityAreas.CENTER, LEVEL_OF_STATIONER_SHOP, null);
		String shopsDescription = String.format("Необходимо %d магазинов канцтоваров (%d-го уровня, в центре)",
				LicenseMarket.START_LEVEL, LEVEL_OF_STATIONER_SHOP);
		Requirement stationerShopRequirement = new Requirement(stationerShops.size() >= LicenseMarket.START_LEVEL,
				shopsDescription);
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
	public boolean isMarkerCanBeBuilt(int userId) {
		for (Requirement requirement : computeBuildRequirements(userId)) {
			// если хотя бы одно из требований не выполнено, то магазин НЕ может быть построен
			if (!requirement.isCarriedOut()) {
				return false;
			}
		}
		return true;
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

	private long getPriceOfBuild() {
		return (long) (LicenseMarket.BASE_PRICE * Constants.UNIVERS_K[LicenseMarket.START_LEVEL]);
	}

	@Override
	public JSONObject confirmLicenseSelling(int licensesCount, byte licensesLevel, Integer userId, JSONObject resultJson) {
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
}
