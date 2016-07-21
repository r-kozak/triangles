package com.kozak.triangles.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.ConstructionProject;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.entities.UserLicense;
import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.enums.buildings.BuildersT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;
import com.kozak.triangles.repositories.ConstructionProjectRep;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.utils.Consts;
import com.kozak.triangles.utils.DateUtils;
import com.kozak.triangles.utils.Random;
import com.kozak.triangles.utils.ResponseUtil;
import com.kozak.triangles.utils.SingletonData;
import com.kozak.triangles.utils.TagCreator;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@Controller
@RequestMapping(value = "/building")
public class BuildingController extends BaseController {
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String buildingPage(Model model, User user) {

		int userId = user.getId();
		String userBalance = trRep.getUserBalance(userId);
		int userDomi = userRep.getUserDomi(userId);

		// начислить проценты завершенности для всех объектов строительства
		List<ConstructionProject> constrProjects = consProjectRep.getUserConstructProjects(userId);
		BuildingController.computeAndSetCompletePercent(constrProjects, consProjectRep);

		// проверить окончилась ли лицензия
		User userWithLicense = userRep.getUserWithLicense(userId); // пользователь с лицензиями
		UserLicense userLicense = userWithLicense.getUserLicense();
		Date licenseExpireDate = userLicense.getLossDate(); // дата окончания лицензии
		BuildingController.checkLicenseExpire(licenseExpireDate, userRep, userId); // если кончилась - назначить новую

		model = ResponseUtil.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId), userDomi);
		model.addAttribute("commBuData", SingletonData.getCommBuildDataArray(buiDataRep)); // данные всех имуществ
		model.addAttribute("constrProjects", consProjectRep.getUserConstructProjects(userId));
		model.addAttribute("licenseLevel", userLicense.getLicenseLevel()); // уровень лицензии
		model.addAttribute("licenseExpire", licenseExpireDate); // окончание лицензии
		// колво завершенных проектов
		long countCompletedProj = consProjectRep.getCountOfUserCompletedConstrProject(userId);
		model.addAttribute("countCompletedProj", countCompletedProj);
		// колво доступных для постройки сегодня
		long availableForBuild = Consts.CONSTRUCTION_LIMIT_PER_DAY - consProjectRep.getCountOfStartedProjectsToday(userId);
		model.addAttribute("availableForBuild", availableForBuild);

		return "building";
	}

	/**
	 * Вызывается перед началом стройки, чтобы пользователь мог выбрать район, посмотреть цену, дату приема в эксплуатацию, баланс
	 * после утверждения проекта и т.д.
	 * 
	 * @param buiType
	 *            - тип строения, который пользователь выбрал для строительства
	 * @param user
	 *            - пользователь
	 * @return - информация о строительном проекте
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pre-build", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
	public @ResponseBody ResponseEntity<String> preBuildProperty(@RequestParam("buiType") String buiType, User user) {

		// получить данные всех коммерческих строений
		HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

		JSONObject resultJson = new JSONObject();
		int userId = user.getId();

		long userMoney = Long.parseLong(trRep.getUserBalance(userId));
		long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя
		User userWithLicense = userRep.getUserWithLicense(userId); // пользователь с лицензиями
		byte userLicenseLevel = userWithLicense.getUserLicense().getLicenseLevel();

		CommBuildData dataOfBuilding = mapData.get(buiType); // данные конкретного типа имущества (здания)
		if (dataOfBuilding == null) {
			ResponseUtil.putErrorMsg(resultJson, "Нет такого типа имущества.");
		} else {
			long priceOfBuilt = dataOfBuilding.getPurchasePriceMin(); // цена постройки = минимальная цена покупки
			if (userSolvency < priceOfBuilt) { // не хватает денег
				ResponseUtil.putErrorMsg(resultJson,
						"Не хватает денег на постройку. Ваш максимум: <b>" + Util.moneyFormat(userSolvency) + "&tridot;</b>");
			} else {
				Date exploitation = DateUtils.addDays(new Date(), dataOfBuilding.getBuildTime());

				// тег с районом города для выбора пользователем
				resultJson.put("buiType", buiType);
				resultJson.put("cityAreaTag", TagCreator.cityAreaTag(userLicenseLevel));
				resultJson.put("price", "Цена постройки: <b>" + priceOfBuilt + "&tridot;</b>"); // цена постройки
				resultJson.put("balanceAfter", "Баланс после постройки: <b>" + (userMoney - priceOfBuilt) + "&tridot;</b>"); // баланс
																																// после
																																// постройки

				resultJson.put("solvencyAfter", "Состоятельность после постройки: <b>"
						+ Util.getSolvency(String.valueOf(userMoney - priceOfBuilt), prRep, userId) + "&tridot;</b>"); // состоятельность
																														// после
																														// постройки
				resultJson.put("exploitation",
						"Дата приема в эксплуатацию (при скорости 1.0): <b>" + DateUtils.dateToString(exploitation) + "</b>");
			}
		}
		return ResponseUtil.getResponseEntity(resultJson);
	}

	@RequestMapping(value = "/confirm-build", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
	public @ResponseBody ResponseEntity<String> confirmBuildProperty(@RequestParam("buiType") String buiType,
			@RequestParam("cityArea") String cityArea, @RequestParam("count") int count, User user) {

		// получить данные всех коммерческих строений
		HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

		JSONObject resultJson = new JSONObject();
		int userId = user.getId();

		User userWithLicense = userRep.getUserWithLicense(userId); // пользователь с лицензиями
		byte userLicenseLevel = userWithLicense.getUserLicense().getLicenseLevel();

		CommBuildData dataOfBuilding = mapData.get(buiType); // данные конкретного типа имущества (здания)

		if (dataOfBuilding == null) { // проверка на тип имущества
			ResponseUtil.putErrorMsg(resultJson, "Нет такого типа имущества.");
		} else {
			long priceOfBuilt = dataOfBuilding.getPurchasePriceMin(); // цена постройки = минимальная цена покупки

			for (int i = 0; i < count; i++) {
				long userMoney = Long.parseLong(trRep.getUserBalance(userId));
				long userSolvency = Util.getSolvency(String.valueOf(userMoney), prRep, userId); // состоятельность

				if (userSolvency < priceOfBuilt * (count - i)) { // не хватает денег
					ResponseUtil.putErrorMsg(resultJson,
							"Не хватает денег на постройку. Ваш максимум: <b>" + Util.moneyFormat(userSolvency) + "&tridot;</b>");
					break;
				} else {
					// проверка можно ли строить в районе, что выбрал пользователь
					boolean cityAreaError = userLicenseLevel - 1 < CityAreasT.valueOf(cityArea).ordinal();
					if (cityAreaError) {
						ResponseUtil.putErrorMsg(resultJson,
								"Ваша лицензия не позволяет строить в выбранном районе. Уровень лицензии: " + userLicenseLevel);
					} else {

						// создать модель имущества в процессе стройки
						CommBuildingsT type = dataOfBuilding.getCommBuildType(); // тип постройки
						CityAreasT cityAreaType = CityAreasT.valueOf(cityArea); // район города
						byte indexBuildersType = generateIndexOfBuilders(); // тип строителей числовой
						BuildersT buildersType = BuildersT.values()[indexBuildersType];// тип строителей

						// часов на постройку = дней на постройку * 24 часа * коеф. типа строителей
						int hoursToConstruct = Math
								.round((dataOfBuilding.getBuildTime() * 24) / Consts.BUILDERS_COEF[indexBuildersType]);

						Date finishDate = DateUtils.addHours(hoursToConstruct); // дата окончания стройки

						ConstructionProject constructionProject = new ConstructionProject(type, finishDate, cityAreaType,
								buildersType, userId);
						consProjectRep.addConstructionProject(constructionProject);

						// снять деньги у юзера
						String descr = String.format("Постройка имущества %s", constructionProject.getName());
						long newBalance = userMoney - priceOfBuilt;

						Transaction tr = new Transaction(descr, new Date(), priceOfBuilt, TransferT.SPEND, userId, newBalance,
								ArticleCashFlowT.CONSTRUCTION_PROPERTY);
						trRep.addTransaction(tr);

						int domiCount = type.ordinal();
						MoneyController.upUserDomi(domiCount, userId, userRep); // повысить доминантность
					}
				}
			}
		}
		return ResponseUtil.getResponseEntity(resultJson);
	}

	/**
	 * Принятие имущества в эксплуатацию
	 * 
	 * @param constrId
	 *            - ID проекта
	 * @param user
	 */
	@RequestMapping(value = "/from-construct", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
	public @ResponseBody ResponseEntity<String> propertyFromConstruct(@RequestParam("constrId") int constrId, User user) {

		JSONObject resultJson = new JSONObject();
		int userId = user.getId();

		if (constrId == 0) {
			allProjectsFromConstruct(resultJson, userId); // принять все проекты
		} else {
			singleProjectFromConstruct(resultJson, constrId, userId, null); // принять конкретный проект
		}
		return ResponseUtil.getResponseEntity(resultJson);
	}

	private void singleProjectFromConstruct(JSONObject resultJson, int constrId, int userId, ConstructionProject constrProj) {

		// если принимаем одно имущество, тогда проекта у нас нет, мы должны его получить
		// иначе проект у нас уже есть, т.к. мы его передали сюда из метода принятия всего имущества
		if (constrProj == null) {
			constrProj = consProjectRep.getUserConstrProjectById(constrId, userId);
		}

		if (constrProj == null) {
			ResponseUtil.putErrorMsg(resultJson, "У вас нет такого имущества. Перезагрузите страницу!");
		} else {
			if (constrProj.getCompletePerc() < 100) {
				ResponseUtil.putErrorMsg(resultJson, "Имущество еще не готово к эксплуатации.");
			} else {
				// получить данные всех коммерческих строений
				HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);
				// данные конкретного типа имущества (здания)
				CommBuildData dataOfBuilding = mapData.get(constrProj.getBuildingType().name());

				// добавить имущество (принять в эксплуатацию)
				Property property = new Property(dataOfBuilding, userId, constrProj.getCityArea(), new Date(),
						dataOfBuilding.getPurchasePriceMax(), constrProj.getName());
				prRep.addProperty(property);

				// удалить строительный проект
				consProjectRep.removeConstrProject(constrProj);
			}
		}
	}

	private void allProjectsFromConstruct(JSONObject resultJson, int userId) {
		List<ConstructionProject> allCompleted = consProjectRep.getCompletedUserConstructProjects(userId);

		for (ConstructionProject proj : allCompleted) {
			singleProjectFromConstruct(resultJson, 0, userId, proj);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/license-buy-info", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
	public @ResponseBody ResponseEntity<String> getLicenseBuyInfo(@RequestParam("level") byte level, User user) {

		JSONObject resultJson = new JSONObject();
		int userId = user.getId();
		long userMoney = Long.parseLong(trRep.getUserBalance(userId));
		long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя

		if (level < 1 || level > 4) {
			ResponseUtil.putErrorMsg(resultJson, "Нет такого уровня лицензии.");
		} else {
			int licensePrice = Consts.LICENSE_PRICE[level];
			if (userSolvency < licensePrice) { // не хватает денег Util.moneyFormat(userSolvency) + "&tridot;</b>");
				ResponseUtil.putErrorMsg(resultJson, "Не хватает денег на покупку. Ваш максимум: <b>"
						+ Util.moneyFormat(userSolvency) + "&tridot;</b> <br/> " + "Цена покупки: <b>" + licensePrice + "</b>");
			} else {
				resultJson.put("licenseLevel", "Вы покупаете лицензию уровня: <b>" + level + ".</b>");
				resultJson.put("licensePrice", "Стоимость покупки лицензии: <b>" + licensePrice + "&tridot;</b>");
				resultJson.put("balAfter", "Баланс после покупки: <b>" + (userMoney - licensePrice) + "&tridot;</b>");
				resultJson.put("licenseTerm", "Срок действия лицензии, дней: <b>" + Consts.LICENSE_TERM[level] + ".</b>");
			}
		}
		return ResponseUtil.getResponseEntity(resultJson);
	}

	@RequestMapping(value = "/license-buy", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
	public @ResponseBody ResponseEntity<String> buyLicense(@RequestParam("level") byte level, User user) {

		JSONObject resultJson = new JSONObject();
		int userId = user.getId();
		long userMoney = Long.parseLong(trRep.getUserBalance(userId));
		long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя

		if (level < 1 || level > 4) {
			ResponseUtil.putErrorMsg(resultJson, "Нет такого уровня лицензии.");
		} else {
			int licensePrice = Consts.LICENSE_PRICE[level];
			if (userSolvency < licensePrice) { // не хватает денег
				ResponseUtil.putErrorMsg(resultJson,
						"Не хватает денег на покупку. Ваш максимум: <b>" + Util.moneyFormat(userSolvency) + "&tridot;</b>");
			} else {
				// установить новую лицензию пользователю
				BuildingController.setNewLicenseToUser(userRep, userId, level);

				// снять деньги у пользователя
				String descr = String.format("Покупка лицензии на строительство. Уровень: %s", level);
				int price = Consts.LICENSE_PRICE[level];
				Transaction tr = new Transaction(descr, new Date(), price, TransferT.SPEND, userId, userMoney - price,
						ArticleCashFlowT.BUY_LICENSE);
				trRep.addTransaction(tr);
			}
		}
		return ResponseUtil.getResponseEntity(resultJson);
	}

	/**
	 * вычисляет и устанавливает процент завершения для каждого строительного проекта из списка
	 */
	public static void computeAndSetCompletePercent(List<ConstructionProject> constrProjects,
			ConstructionProjectRep constrPrRep) {

		for (ConstructionProject constProject : constrProjects) {
			Long startMs = constProject.getStartDate().getTime(); // миллисекунды даты начала стройки

			Long countStartToNowMs = new Date().getTime() - startMs;// мс с начала стройки до сейчас
			Long countStartToFinishMs = constProject.getFinishDate().getTime() - startMs;// кол-во мс с начала до конца
																							// стройки

			float completePercent = 0;
			if (countStartToNowMs >= countStartToFinishMs) {
				completePercent = 100;
			} else {
				completePercent = (float) countStartToNowMs * 100 / countStartToFinishMs;
				completePercent = (float) Util.numberRound(completePercent, 2); // округление
			}
			constProject.setCompletePerc(completePercent);
			constrPrRep.updateConstructionProject(constProject);
		}
	}

	/**
	 * Проверить, закончилась ли у пользователя лицензия на строительство и если закончилась - установить новую.
	 * 
	 * @param licenseExpireDate
	 *            - дата окончания лицензии
	 * @param userRep
	 * @param userId
	 * @return
	 */
	public static Date checkLicenseExpire(Date licenseExpireDate, UserRep userRep, int userId) {
		// если лицензия закончилась
		if (new Date().after(licenseExpireDate)) {
			byte newLevel = 1;
			// установить новую лицензию пользователю
			return BuildingController.setNewLicenseToUser(userRep, userId, newLevel);
		}
		return licenseExpireDate;
	}

	/**
	 * Установить пользователю новую лицензию на строительство.
	 * 
	 * @param userRep
	 * @param userId
	 * @param level
	 */
	static Date setNewLicenseToUser(UserRep userRep, int userId, byte level) {
		Date nextExpireDate = DateUtils.addDays(new Date(), Consts.LICENSE_TERM[level]); // сейчас + Х дней

		User user = userRep.getUserWithLicense(userId);
		UserLicense license = user.getUserLicense();
		license.setLicenseLevel(level);
		license.setLossDate(nextExpireDate);

		user.setUserLicense(license);
		userRep.updateUser(user);

		return nextExpireDate;
	}

	/**
	 * Генерирует рандомный индекс строителей для получения их коефициента и вычисления скорости постройки
	 */
	private byte generateIndexOfBuilders() {
		Random rand = new Random();
		byte randNum = (byte) rand.generateRandNum(0, 100);

		if (randNum > 90) {
			return 2; // украинцы
		} else if (randNum > 60) {
			return 1; // немцы
		} else {
			return 0; // гастарбайтеры
		}
	}
}
