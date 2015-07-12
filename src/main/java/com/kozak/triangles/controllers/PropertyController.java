package com.kozak.triangles.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
     */
    @RequestMapping(value = "/r-e-market", method = RequestMethod.GET)
    String realEstMarket(@ModelAttribute("user") User user, Model model, HttpServletRequest request) {
	String contextPath = request.getContextPath();
	int page = Util.getPageNumber(request);

	// //
	Long propCount = rePrRep.allPrCount(false);
	int lastPageNumber = (int) (propCount / Consts.ROWS_ON_PAGE) + ((propCount % Consts.ROWS_ON_PAGE != 0) ? 1 : 0);
	List<RealEstateProposal> proposals = rePrRep.getREProposalsList(page);
	// //

	model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
	model.addAttribute("proposals", proposals);
	model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, contextPath + "/property/r-e-market?", page));

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
    String buyProperty(@PathVariable int prId, User user, Model model) {
	RealEstateProposal prop = rePrRep.getREProposalById(prId);

	if (prop == null || !prop.isValid()) { // уже кто-то купил
	    model.addAttribute("errorMsg",
		    "Вы не успели. Имущество уже было куплено кем-то. Попробуйте купить что-нибудь другое.");
	    return "error";
	} else {
	    Long userMoney = Long.parseLong(trRep.getUserBalance(user.getId()));
	    long sellSum = prRep.getSellingSumAllPropByUser(user.getId()) / 2;// (ост. стоим. всего имущества юзера / 2)
	    long bap = userMoney - prop.getPurchasePrice(); // balance after purchase

	    model.addAttribute("percent", Util.getAreaPercent(prop.getCityArea()));
	    model.addAttribute("prop", prop); // само предложение
	    model.addAttribute("data", buiDataRep.getCommBuildDataByType(prop.getCommBuildingType()));
	    model.addAttribute("bap", bap); // balance after purchase

	    if (userMoney >= prop.getPurchasePrice()) { // хватает денег
		model.addAttribute("title", "Покупка за наличные");
		return "apply_buy";
	    } else if (userMoney + sellSum >= prop.getPurchasePrice()) { // покупка в кредит
		model.addAttribute("title", "Покупка в кредит");
		return "apply_buy";
	    } else if (userMoney <= 0) { // баланс < 0
		model.addAttribute("errorMsg",
			"Ваш баланс равен или меньше нуля. Покупка невозможна. Продайте что-нибудь или заработайте денег.");
	    } else if (userMoney + sellSum < prop.getPurchasePrice()) { // низкая состоятельность
		model.addAttribute("errorMsg", "Ваша состоятельность не позволяет вам купить это имущество. "
			+ "Ваш максимум = " + Util.moneyFormat(userMoney + sellSum) + "&tridot;");
	    }
	    return "error";
	}
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
		return "error";
	    } else {
		int userId = user.getId();
		// баланс юзера и
		// (ост. стоим. всего имущества / 2) для расчета состоятельности (для кредита)
		Long userMoney = Long.parseLong(trRep.getUserBalance(userId));
		long sellSum = prRep.getSellingSumAllPropByUser(userId) / 2;

		boolean moneyEnough = userMoney >= prop.getPurchasePrice(); // хватает денег
		boolean inCredit = userMoney + sellSum >= prop.getPurchasePrice(); // берем в кредит

		if (moneyEnough || inCredit) {
		    // снять деньги
		    Date purchDate = new Date();
		    long price = prop.getPurchasePrice();

		    Transaction t = new Transaction("Покупка имущества", purchDate, price, TransferT.SPEND, userId,
			    userMoney - prop.getPurchasePrice(), ArticleCashFlowT.BUY_PROPERTY);

		    trRep.addTransaction(t);

		    // экзэмпляр данных про тот тип, который мы покупаем
		    CommBuildData data = buiDataRep.getCommBuildDataByType(prop.getCommBuildingType());
		    // добавить новое имущество пользователю
		    Property newProp = new Property(data, userId, prop.getCityArea(), purchDate, price);
		    prRep.addProperty(newProp);

		    // предложение на рынке теперь не валидное
		    prop.setValid(false);
		    rePrRep.updateREproposal(prop);

		    ra.addFlashAttribute("popup", true); // будем отображать поздравление о покупке
		} else if (userMoney <= 0) {
		    model.addAttribute("errorMsg",
			    "Ваш баланс равен или меньше нуля. Покупка невозможна. Продайте что-нибудь или заработайте денег.");
		    return "error";
		} else if (userMoney + sellSum < prop.getPurchasePrice()) {
		    model.addAttribute("errorMsg", "Ваша состоятельность не позволяет вам купить это имущество. "
			    + "Ваш максимум = " + Util.moneyFormat(userMoney + sellSum) + "&tridot;");
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
    String userProperty(@ModelAttribute("user") User user, Model model, HttpServletRequest request) {
	String contextPath = request.getContextPath();
	int page = Util.getPageNumber(request);
	int userId = user.getId();

	Util.profitCalculation(userId, buiDataRep, prRep); // начисление прибыли по имуществу пользователя

	Long propCount = prRep.allPrCount(userId, false, false);
	int lastPageNumber = (int) (propCount / Consts.ROWS_ON_PAGE) + ((propCount % Consts.ROWS_ON_PAGE != 0) ? 1 : 0);
	List<Property> comProps = prRep.getPropertyList(page, userId);

	model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
	model.addAttribute("comProps", comProps);
	model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, contextPath + "/property/commerc-pr?", page));

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
	model.addAttribute("type", buiDataRep.getCommBuildDataByType(prop.getCommBuildingType()).getBuildType());

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
	    if (newName.length() > 0) {
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
     * делает изымание денег с кассы имущества.
     * записывает транзакцию, обновляет имущество.
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
}
