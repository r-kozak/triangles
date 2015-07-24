package com.kozak.triangles.controllers;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.interfaces.Consts;
import com.kozak.triangles.repositories.BuildingDataRep;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.ReProposalRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.search.CommPropSearch;
import com.kozak.triangles.search.RealEstateProposalsSearch;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.utils.SingletonData;
import com.kozak.triangles.utils.TagCreator;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@RequestMapping(value = "/property")
@Controller
public class PropertyController {
    @Autowired
    private TransactionRep trRep;
    @Autowired
    private BuildingDataRep buiDataRep;
    @Autowired
    private PropertyRep prRep;
    @Autowired
    private ReProposalRep rePrRep;

    /**
     * меню по недвижимости
     */
    @RequestMapping(method = RequestMethod.GET)
    String propertyGET(@ModelAttribute("user") User user, Model model) {
	model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
	return "property";
    }

    /**
     * переход на страницу рынка недвижимости
     * 
     * @param request
     *            для получения параметров редиректа (для отображ. поздравления о покупке)
     * @throws ParseException
     */
    @RequestMapping(value = "/r-e-market", method = RequestMethod.GET)
    String realEstMarket(@ModelAttribute("user") User user, Model model, HttpServletRequest request,
	    RealEstateProposalsSearch reps) throws ParseException {

	if (reps.isNeedClear())
	    reps.clear();

	int page = Integer.parseInt(reps.getPage());

	List<Object> rangeValues = rePrRep.getRangeValues();
	List<Object> dbResult = rePrRep.getREProposalsList(page, reps);
	List<RealEstateProposal> proposals = (List<RealEstateProposal>) dbResult.get(1);

	Long propCount = Long.valueOf(dbResult.get(0).toString());
	int lastPageNumber = (int) (propCount / Consts.ROWS_ON_PAGE) + ((propCount % Consts.ROWS_ON_PAGE != 0) ? 1 : 0);

	reps.setPrice(rangeValues.get(0), rangeValues.get(1)); // установка мин и макс цены продажи

	model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
	model.addAttribute("reps", reps);
	model.addAttribute("types", SearchCollections.getCommBuildTypes());
	model.addAttribute("areas", SearchCollections.getCityAreas());
	model.addAttribute("proposals", proposals);
	model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, page));
	model.addAttribute("marketEmpty", rePrRep.allPrCount(false) == 0);

	@SuppressWarnings("unchecked")
	Map<String, Object> map = (Map<String, Object>) RequestContextUtils.getInputFlashMap(request);
	if (map != null) {
	    // this is redirect
	    model.addAttribute("popup", map.getOrDefault("popup", false));
	}
	return "remarket";
    }

    /**
     * запрос на покупку имущества
     * 
     * @param prId
     *            id предложения с рынка
     */
    @RequestMapping(value = "/buy/{prId}", method = RequestMethod.GET)
    String buyProperty(@PathVariable("prId") int prId, User user, Model model) {
	RealEstateProposal prop = rePrRep.getREProposalById(prId);

	if (prop == null || !prop.isValid()) { // уже кто-то купил
	    model.addAttribute("errorMsg",
		    "Вы не успели. Имущество уже было куплено кем-то. Попробуйте купить что-нибудь другое.");
	    model.addAttribute("backLink", "property/r-e-market");
	    return "error";
	} else {
	    long userMoney = Long.parseLong(trRep.getUserBalance(user.getId()));
	    long sellSum = prRep.getSellingSumAllPropByUser(user.getId()) / 2;// (ост. стоим. всего имущества юзера / 2)
	    long userSolvency = getSolvency(user.getId()); // состоятельность пользователя
	    long bap = userMoney - prop.getPurchasePrice(); // balance after purchase

	    // получить данные всех коммерческих строений
	    HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

	    model.addAttribute("percent", Util.getAreaPercent(prop.getCityArea()));
	    model.addAttribute("prop", prop); // само предложение
	    model.addAttribute("data", mapData.get(prop.getCommBuildingType().name()));
	    model.addAttribute("bap", bap); // balance after purchase

	    if (userMoney >= prop.getPurchasePrice()) { // хватает денег
		model.addAttribute("title", "Обычная покупка");
		return "apply_buy";
	    } else if (userSolvency >= prop.getPurchasePrice()) { // покупка в кредит
		model.addAttribute("title", "Покупка в кредит");
		return "apply_buy";
	    } else if (userSolvency < prop.getPurchasePrice()) { // низкая состоятельность
		model.addAttribute("errorMsg", "Ваша состоятельность не позволяет вам купить это имущество. "
			+ "Ваш максимум = " + Util.moneyFormat(userSolvency) + "&tridot;");
		model.addAttribute("backLink", "property/r-e-market");
	    }
	    return "error";
	}
    }

    private long getSolvency(int userId) {
	long userMoney = Long.parseLong(trRep.getUserBalance(userId));
	long sellSum = prRep.getSellingSumAllPropByUser(userId) / 2;// (ост. стоим. всего имущества юзера / 2)
	return userMoney + sellSum;
    }

    /**
     * ответ о покупке имущества от пользователя
     * 
     * @param prId
     *            id предложения с рынка
     * @param action
     *            действие, которое выбрал пользователь (cancel, confirm)
     * @param ra
     *            RedirectAttributes - добавляется признак отображения инфо окна, ловится при редиректе на "remarket"
     */
    @RequestMapping(value = "/buy/{prId}", method = RequestMethod.POST)
    String confirmBuy(@ModelAttribute("prId") int prId, @ModelAttribute("action") String action, User user,
	    Model model, RedirectAttributes ra) {

	// пользователь подтвердил покупку
	if (action.equals("confirm")) {
	    RealEstateProposal prop = rePrRep.getREProposalById(prId);

	    if (prop == null || !prop.isValid()) {
		model.addAttribute("errorMsg",
			"Вы не успели. Имущество уже было куплено кем-то. Попробуйте купить что-нибудь другое.");
		model.addAttribute("backLink", "property/r-e-market");
		return "error";
	    } else {
		int userId = user.getId();
		// баланс юзера и
		// (ост. стоим. всего имущества / 2) для расчета состоятельности (для кредита)
		long userMoney = Long.parseLong(trRep.getUserBalance(userId));
		long sellSum = prRep.getSellingSumAllPropByUser(userId) / 2;

		boolean moneyEnough = userMoney >= prop.getPurchasePrice(); // хватает денег
		boolean inCredit = userMoney + sellSum >= prop.getPurchasePrice(); // берем в кредит

		if (moneyEnough || inCredit) {
		    // данные операции
		    Date purchDate = new Date();
		    long price = prop.getPurchasePrice();

		    // получить данные всех коммерческих строений
		    HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);
		    CommBuildData data = mapData.get(prop.getCommBuildingType().name());
		    // добавить новое имущество пользователю
		    String nameHash = Util.getHash(5);
		    Property newProp = new Property(data, userId, prop.getCityArea(), purchDate, price, nameHash);
		    prRep.addProperty(newProp);

		    // предложение на рынке теперь не валидное
		    prop.setValid(false);
		    rePrRep.updateREproposal(prop);

		    // снять деньги
		    Transaction t = new Transaction("Покупка имущества: property-" + nameHash, purchDate, price,
			    TransferT.SPEND, userId, userMoney - prop.getPurchasePrice(), ArticleCashFlowT.BUY_PROPERTY);
		    trRep.addTransaction(t);

		    ra.addFlashAttribute("popup", true); // будем отображать поздравление о покупке
		} else if (userMoney + sellSum < prop.getPurchasePrice()) {
		    model.addAttribute("errorMsg", "Ваша состоятельность не позволяет вам купить это имущество. "
			    + "Ваш максимум = " + Util.moneyFormat(getSolvency(userId)) + "&tridot;");
		    model.addAttribute("backLink", "property/r-e-market");
		    return "error";
		}
	    }
	}
	return "redirect:/property/r-e-market";
    }

    /**
     * переход на страницу коммерческой недвижимости пользователя
     * 
     */
    @RequestMapping(value = "/commerc-pr", method = RequestMethod.GET)
    String userProperty(@ModelAttribute("user") User user, Model model, CommPropSearch cps) {

	if (cps.isNeedClear())
	    cps.clear();

	int page = Integer.parseInt(cps.getPage());

	int userId = user.getId();
	Util.profitCalculation(userId, buiDataRep, prRep); // начисление прибыли по имуществу пользователя

	// результат с БД: количество всего; имущество с учетом пагинации;
	List<Object> dbResult = prRep.getPropertyList(page, userId, cps);

	// результат с БД:
	// [
	// // MIN прод. цена имущества; MAX прод. цена имущества
	// // MIN процент износа; MAX процент износа
	// ]
	List<Object> rangeValues = prRep.getRangeValues(userId);

	Long propCount = Long.valueOf(dbResult.get(0).toString());
	int lastPageNumber = (int) (propCount / Consts.ROWS_ON_PAGE) + ((propCount % Consts.ROWS_ON_PAGE != 0) ? 1 : 0);

	cps.setPrice(rangeValues.get(0), rangeValues.get(1)); // установка мин и макс цены продажи
	cps.setDepreciation(rangeValues.get(2), rangeValues.get(3)); // установка мин и макс износа

	model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
	model.addAttribute("cps", cps);
	model.addAttribute("comProps", dbResult.get(1));
	model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, page));
	model.addAttribute("types", SearchCollections.getCommBuildTypes());
	model.addAttribute("userHaveProps", prRep.allPrCount(userId, false, false) > 0);

	// если собирали наличку с кассы - для информационного popup окна
	String cash = (String) model.asMap().getOrDefault("changeBal", "");
	if (!cash.isEmpty()) {
	    model.addAttribute("changeBal", cash);
	}

	return "commerc_pr";
    }

    /**
     * получение страницы с конкретным экземпляром имущества
     */
    @RequestMapping(value = "/{prId}", method = RequestMethod.GET)
    String specificPropertyPage(@ModelAttribute("prId") int prId, User user, Model model) {
	int userId = user.getId();
	Util.profitCalculation(userId, buiDataRep, prRep); // начисление прибыли по имуществу пользователя

	// получить конкретное имущество текущего пользоватетя
	Property prop = prRep.getSpecificProperty(userId, prId);
	// если получили null - значит это не имущество пользователя
	if (prop == null) {
	    return "redirect:/commerc-pr";
	}

	model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
	model.addAttribute("prop", prop);
	// добавим вид деятельности
	// получить данные всех коммерческих строений
	HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);
	model.addAttribute("type", mapData.get(prop.getCommBuildingType().name()).getBuildType());

	// если собирали наличку с кассы - для информационного popup окна
	String cash = (String) model.asMap().getOrDefault("changeBal", "");
	if (!cash.isEmpty()) {
	    model.addAttribute("changeBal", cash);
	}

	return "specific_pr";
    }

    /**
     * операции с имуществом
     */
    @RequestMapping(value = "operations/{prId}", method = RequestMethod.POST)
    String propertyOperations(@ModelAttribute("prId") int prId, @ModelAttribute("action") String action,
	    @ModelAttribute("newName") String newName, User user, Model model, HttpServletRequest request,
	    RedirectAttributes ra) {

	int userId = user.getId();
	// получить конкретное имущество текущего пользоватетя
	Property prop = prRep.getSpecificProperty(userId, prId);
	// если получили null - значит это не имущество пользователя
	if (prop == null) {
	    return "redirect:/property/commerc-pr";
	}

	if (action.equals("change_name")) {
	    if (newName.length() > 0 && newName.length() <= 25) {
		prop.setName(newName);
		prRep.updateProperty(prop);
	    }
	} else if (action.equals("repair")) {
	    // TODO отремонтировать, снять деньги; если данное имущество НЕ valid на момент ремонта - установить
	    // nextProfit = tomorrow
	} else if (action.equals("get_cash")) {
	    long cash = getCashFromProperty(prop); // сколько налички собрали
	    if (cash > 0) {
		ra.addFlashAttribute("changeBal", "+" + cash); // передаем параметр
	    }
	}
	return "redirect:/property/" + prId;
    }

    /**
     * запрос на изьятие денег с кассы имущества
     */
    @RequestMapping(value = "get-cash/{prId}", method = RequestMethod.GET)
    String getCash(@ModelAttribute("prId") int prId, @ModelAttribute("action") String action,
	    @ModelAttribute("newName") String newName, User user, Model model, HttpServletRequest request,
	    RedirectAttributes ra) {

	int userId = user.getId();
	// получить конкретное имущество текущего пользоватетя
	Property prop = prRep.getSpecificProperty(userId, prId);
	// если получили null - значит это не имущество пользователя
	if (prop == null) {
	    return "redirect:/property/commerc-pr";
	}

	// изымаем деньги
	long cash = getCashFromProperty(prop); // сколько налички собрали
	if (cash > 0) {
	    ra.addFlashAttribute("changeBal", "+" + cash); // передаем параметр
	}

	return "redirect:/property/commerc-pr";
    }

    /**
     * делает изымание денег с кассы имущества. записывает транзакцию, обновляет имущество.
     * 
     * @param prop
     *            экземпляр имущество
     */
    private long getCashFromProperty(Property prop) {
	if (prop.getCash() > 0) {
	    int uId = prop.getUserId();

	    String desc = "Сбор с имущества: " + prop.getName();
	    Long cash = prop.getCash();
	    Long oldBalance = Long.parseLong(trRep.getUserBalance(uId));

	    Transaction t = new Transaction(desc, new Date(), cash, TransferT.PROFIT, uId, oldBalance + cash,
		    ArticleCashFlowT.LEVY_ON_PROPERTY);

	    trRep.addTransaction(t);

	    prop.setCash(0);
	    prRep.updateProperty(prop);

	    return cash;
	}
	return 0;
    }

    @RequestMapping(value = "/repair", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> repairJqueryRequest(@RequestParam("type") String type,
	    @RequestParam("propId") Integer propId,
	    User user) {
	JSONObject resultJson = new JSONObject();

	int userId = user.getId();

	Property prop = prRep.getSpecificProperty(userId, propId);
	long fullRepairSum = (long) (prop.getInitialCost() * prop.getDepreciationPercent() / 100);
	long userMoney = Long.parseLong(trRep.getUserBalance(userId)); // баланс
	long userSolvency = getSolvency(userId); // состоятельность

	if (fullRepairSum <= 0) {
	    putErrorMsg(resultJson, "Имущество не требует ремонта!");
	} else if (userSolvency <= 0) {
	    putErrorMsg(resultJson, "Ваша состоятельность = 0. Ремонт запрещен!");
	} else {
	    if (type.equals("full")) {
		if (userSolvency >= fullRepairSum) {
		    repair(resultJson, prop, 0.0, prop.getInitialCost(), userMoney, fullRepairSum);
		} else {
		    double newDeprPerc = (userSolvency * prop.getDepreciationPercent()) / fullRepairSum;
		    putErrorMsg(resultJson,
			    "Ваша состоятельность не позволяет сделать полный ремонт. <br/> Доступен ремонт на сумму: "
				    + userSolvency + "<br/> Процент износа после ремонта: "
				    + Util.numberRound(newDeprPerc, 2)
				    + "%");
		}
	    } else if (type.equals("allowed")) {
		double newDeprPerc = (userSolvency * prop.getDepreciationPercent()) / fullRepairSum;
		long newSellingPrice = prop.getSellingPrice() + userSolvency;
		repair(resultJson, prop, Util.numberRound(newDeprPerc, 2), newSellingPrice, userMoney, userSolvency);
	    }
	}

	System.err.println(resultJson.toJSONString());
	String json = resultJson.toJSONString();

	HttpHeaders responseHeaders = new HttpHeaders();
	responseHeaders.add("Content-Type", "application/json; charset=utf-8");
	return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
    }

    @SuppressWarnings("unchecked")
    private void repair(JSONObject resultJson, Property prop, Double deprPercent, long sellPrice, long userMoney,
	    long repairSum) {
	prop.setDepreciationPercent(deprPercent);
	prop.setSellingPrice(sellPrice);
	prRep.updateProperty(prop);

	Transaction t = new Transaction("Ремонт имущества: " + prop.getName(), new Date(), repairSum,
		TransferT.SPEND, prop.getUserId(), userMoney - repairSum, ArticleCashFlowT.PROPERTY_REPAIR);
	trRep.addTransaction(t);

	resultJson.put("error", false);
	resultJson.put("percAfterRepair", deprPercent);
	resultJson.put("changeBal", "-" + repairSum);
	resultJson.put("newBalance", userMoney - repairSum);
    }

    @SuppressWarnings("unchecked")
    private void putErrorMsg(JSONObject resultJson, String msg) {
	resultJson.put("error", true);
	resultJson.put("message", msg);
    }
}
