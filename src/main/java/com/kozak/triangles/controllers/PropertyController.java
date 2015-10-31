package com.kozak.triangles.controllers;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.search.CommPropSearch;
import com.kozak.triangles.search.RealEstateProposalsSearch;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.utils.Consts;
import com.kozak.triangles.utils.DateUtils;
import com.kozak.triangles.utils.Random;
import com.kozak.triangles.utils.ResponseUtil;
import com.kozak.triangles.utils.SingletonData;
import com.kozak.triangles.utils.TagCreator;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@RequestMapping(value = "/property")
@Controller
public class PropertyController extends BaseController {

    private int domiAmount;

    /**
     * переход на страницу рынка недвижимости
     * 
     * @param request
     *            для получения параметров редиректа (для отображ. поздравления о покупке)
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/r-e-market", method = RequestMethod.GET)
    public String realEstMarket(@ModelAttribute("user") User user, Model model, HttpServletRequest request,
            RealEstateProposalsSearch reps) throws ParseException {

        int userId = user.getId();

        if (reps.isNeedClear())
            reps.clear();

        int page = Integer.parseInt(reps.getPage());

        List<Object> rangeValues = rePrRep.getRangeValues(userId);
        List<Object> dbResult = rePrRep.getREProposalsList(page, reps, userId);
        List<RealEstateProposal> proposals = (List<RealEstateProposal>) dbResult.get(1);

        // пагинация не нужна
        // Long propCount = Long.valueOf(dbResult.get(0).toString());
        // int lastPageNumber = (int) (propCount / Consts.ROWS_ON_PAGE) + ((propCount % Consts.ROWS_ON_PAGE != 0) ? 1 :
        // 0);

        reps.setPrice(rangeValues.get(0), rangeValues.get(1)); // установка мин и макс цены продажи

        String userBalance = trRep.getUserBalance(userId);
        int userDomi = userRep.getUserDomi(userId);
        model = ResponseUtil.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId),
                userDomi);
        model.addAttribute("reps", reps);
        model.addAttribute("types", SearchCollections.getCommBuildTypes());
        model.addAttribute("areas", SearchCollections.getCityAreas());
        model.addAttribute("proposals", proposals);
        // model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, page));
        model.addAttribute("marketEmpty", rePrRep.allPrCount(false, userId) == 0);

        Map<String, Object> map = (Map<String, Object>) RequestContextUtils.getInputFlashMap(request);
        if (map != null) {
            // this is redirect
            model.addAttribute("popup", map.getOrDefault("popup", false));
        }
        return "remarket";
    }

    /**
     * Операции с покупкой недвижимости (получить информацию о покупке, подтвердить покупку)
     * 
     * @param propId
     *            - id имущества
     * @param action
     *            - действие ("info" - получить информацию, "confirm" - подтвердить покупку)
     * @return JSON модель с информацией о покупке или с результатом покупки
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/buy", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> jqueryBuyProperty(@RequestParam("propId") Integer propId,
            @RequestParam("action") String action, User user) {

        JSONObject resultJson = new JSONObject();
        int userId = user.getId();
        RealEstateProposal prop = rePrRep.getREProposalById(propId);

        long userMoney = Long.parseLong(trRep.getUserBalance(userId));
        long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя

        if (prop == null) {
            ResponseUtil.putErrorMsg(resultJson, "Произошла ошибка (код: 1 - нет такого имущества)!");
        } else if (!prop.isValid()) {
            ResponseUtil.putErrorMsg(resultJson,
                    "Вы не успели. Имущество уже было куплено кем-то. Попробуйте купить что-нибудь другое.");
        } else if (userSolvency < prop.getPurchasePrice()) {
            ResponseUtil.putErrorMsg(resultJson, "Ваша состоятельность не позволяет вам купить это имущество. "
                    + "Ваш максимум = <b>" + Util.moneyFormat(userSolvency) + "&tridot;</b>");
        } else {
            long newBalance = userMoney - prop.getPurchasePrice(); // balance after purchase

            // получить данные всех коммерческих строений
            HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);
            // данные конкретного имущества
            CommBuildData buildData = mapData.get(prop.getCommBuildingType().name());

            if (action.equals("info")) { // получение информации
                String usedText = (prop.getUsedId() != 0) ? "<b>(Б/У)</b>" : "<b>(НОВОЕ)</b>";
                if (userMoney >= prop.getPurchasePrice()) { // хватает денег
                    resultJson.put("title", "Обычная покупка " + usedText);
                } else if (userSolvency >= prop.getPurchasePrice()) { // покупка в кредит
                    resultJson.put("title", "Покупка в кредит " + usedText);
                }
                resultJson.put("buildType", buildData.getCommBuildType().toString()); // тип недвиги
                resultJson.put("newBalance", newBalance); // balance after purchase
                putMainInfoAboutPurchase(resultJson, prop); // вставка основной информации о покупке
            } else if (action.equals("confirm")) { // подтверждение покупки
                if (userMoney >= prop.getPurchasePrice() || userSolvency >= prop.getPurchasePrice()) {
                    // данные операции
                    Date purchDate = new Date();
                    long price = prop.getPurchasePrice();

                    CityAreasT cityArea = prop.getCityArea();
                    // новое имя имущества
                    String propName = new Random().generatePropertyName(buildData.getCommBuildType(), cityArea);
                    // если имущ. новое - добавить новое имущество пользователю, иначе - изменить владельца у б/у
                    if (prop.getUsedId() == 0) {
                        Property newProp = new Property(buildData, userId, cityArea, purchDate, price,
                                propName);
                        prRep.addProperty(newProp);

                        MoneyController.upUserDomi(Consts.K_DOMI_BUY_PROP, userId, userRep); // повысить
                                                                                             // доминантность
                    } else {
                        // покупка б/у имущества
                        propName = Util.buyUsedProperty(prop, purchDate, userId, prRep, trRep);
                        prop.setUsedId(0);
                    }

                    // предложение на рынке теперь не валидное
                    prop.setValid(false);
                    rePrRep.updateREproposal(prop);

                    // снять деньги
                    Transaction t = new Transaction("Покупка имущества: " + propName, purchDate, price,
                            TransferT.SPEND, userId, userMoney - price, ArticleCashFlowT.BUY_PROPERTY);
                    trRep.addTransaction(t);

                    ResponseUtil.addBalanceData(resultJson, prop.getPurchasePrice(), userMoney, userId, prRep);
                    resultJson.put("newDomi", userRep.getUserDomi(userId)); // информация для обновления значения домин.
                }
            }
        }
        return ResponseUtil.getResponseEntity(resultJson);
    }

    /**
     * переход на страницу коммерческой недвижимости пользователя
     * 
     */
    @RequestMapping(value = "/commerc-pr", method = RequestMethod.GET)
    public String userProperty(@ModelAttribute("user") User user, Model model, CommPropSearch cps,
            HttpServletRequest req) {

        if (cps.isNeedClear())
            cps.clear();

        int userId = user.getId();
        Util.profitCalculation(userId, buiDataRep, prRep); // начисление прибыли по имуществу пользователя

        // результат с БД: количество всего; имущество с учетом пагинации;
        List<Object> dbResult = prRep.getPropertyList(userId, cps);

        long itemsCount = (long) dbResult.get(0);
        int totalPages = (int) (itemsCount / Consts.ROWS_ON_PAGE) + ((itemsCount % Consts.ROWS_ON_PAGE != 0) ? 1 : 0);

        if (totalPages > 1) {
            int currPage = Integer.parseInt(cps.getPage());
            String paginationTag = TagCreator.paginationTag(totalPages, currPage, req);
            model.addAttribute("paginationTag", paginationTag);
        }

        // результат с БД:
        // [
        // // MIN прод. цена имущества; MAX прод. цена имущества
        // // MIN процент износа; MAX процент износа
        // ]
        List<Object> rangeValues = prRep.getRangeValues(userId);

        cps.setPrice(rangeValues.get(0), rangeValues.get(1)); // установка мин и макс цены продажи
        cps.setDepreciation(rangeValues.get(2), rangeValues.get(3)); // установка мин и макс износа

        user = userRep.getCurrentUserByLogin(user.getLogin());

        String userBalance = trRep.getUserBalance(userId);
        model = ResponseUtil.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId),
                user.getDomi());
        model.addAttribute("cps", cps);
        model.addAttribute("comProps", dbResult.get(1)); // все коммерческое имущество юзера
        model.addAttribute("types", SearchCollections.getCommBuildTypes());
        model.addAttribute("userHaveProps", prRep.allPrCount(userId, false, false) > 0);
        model.addAttribute("ready", prRep.allPrCount(userId, true, false)); // колво готовых к сбору дохода

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
    public String specificPropertyPage(@ModelAttribute("prId") int prId, User user, Model model) {
        int userId = user.getId();
        Util.profitCalculation(userId, buiDataRep, prRep); // начисление прибыли по имуществу пользователя

        // получить конкретное имущество текущего пользоватетя
        Property prop = prRep.getSpecificProperty(userId, prId);
        // если получили null - значит это не имущество пользователя
        if (prop == null) {
            return "redirect:/property/commerc-pr";
        }

        String userBalance = trRep.getUserBalance(userId);
        int userDomi = userRep.getUserDomi(userId);
        model = ResponseUtil.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId),
                userDomi);
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
    public String propertyOperations(@ModelAttribute("prId") int prId, @ModelAttribute("action") String action,
            @ModelAttribute("newName") String newName, User user, Model model, RedirectAttributes ra) {

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
    public String getCash(@ModelAttribute("prId") int prId, @ModelAttribute("action") String action,
            @ModelAttribute("newName") String newName, User user, Model model, HttpServletRequest request,
            RedirectAttributes ra) {

        int userId = user.getId();
        long cash = 0;

        if (prId == 0) { // это сбор со всего имущества
            // получить список всего имущества, где в кассе > 0
            List<Property> allPrWithCash = prRep.getPropertyWithNotEmptyCash(userId);
            for (Property p : allPrWithCash) {
                cash += getCashFromProperty(p); // собрать доход с кассы
            }
        } else { // сбор с конкретного имущества

            // получить конкретное имущество текущего пользоватетя
            Property prop = prRep.getSpecificProperty(userId, prId);
            // если получили null - значит это не имущество пользователя
            if (prop == null) {
                return "redirect:/property/commerc-pr";
            }

            // изымаем деньги
            cash = getCashFromProperty(prop); // сколько налички собрали
        }
        if (cash > 0) {
            ra.addFlashAttribute("changeBal", "+" + cash); // передаем параметр
        }

        return "redirect:/property/commerc-pr";
    }

    /**
     * обработчик запросов по ремонту имущества
     * 
     * @param type
     *            тип запроса (это пока только info или уже repair)
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/repair", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> jqueryRepairRequest(@RequestParam("type") String type,
            @RequestParam("propId") Integer propId, User user) {
        JSONObject resultJson = new JSONObject();

        int userId = user.getId();

        Property prop = prRep.getSpecificProperty(userId, propId);

        if (prop == null) {
            ResponseUtil.putErrorMsg(resultJson, "Произошла ошибка (код: 1)!");
        } else {
            double pdp = prop.getDepreciationPercent(); // текущий процент износа

            long fullRepairSum = (long) (prop.getInitialCost() * pdp / 100); // сумма износа
            fullRepairSum /= Consts.K_DECREASE_REPAIR; // сумма ремонта (сумма износа / K)

            long userMoney = Long.parseLong(trRep.getUserBalance(userId)); // баланс
            long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя

            double newDeprPerc = Util.numberRound(pdp - (userSolvency * pdp) / fullRepairSum, 2); // проц после ремонта

            long repairSum = userSolvency; // сумма ремонта
            long newSellingPrice = prop.getSellingPrice() + userSolvency;
            if (userSolvency >= fullRepairSum) { // это полный ремонт
                newDeprPerc = 0.00;
                repairSum = fullRepairSum;
                newSellingPrice = prop.getInitialCost();
            }

            if (type.equals("repair")) {
                if (userSolvency <= 0) {
                    ResponseUtil.putErrorMsg(resultJson,
                            "Ваша состоятельность не позволяет вам ремонтировать имущество!");
                } else {
                    if (repairSum > 0) {
                        repair(resultJson, prop, newDeprPerc, newSellingPrice, userMoney, repairSum, userId);
                    }
                }
            } else if (type.equals("info")) {
                if (userSolvency <= 0) {
                    resultJson.put("zeroSolvency", true);
                    ResponseUtil.putErrorMsg(resultJson,
                            "Ваша состоятельность не позволяет вам ремонтировать имущество!");
                } else {
                    ResponseUtil.putErrorMsg(resultJson, String.format(
                            "Сумма ремонта:<b> %s&tridot;</b> <br/> Процент износа после ремонта: <b> %.2f%% </b>",
                            repairSum, newDeprPerc));
                }
            }
        }
        return ResponseUtil.getResponseEntity(resultJson);
    }

    /**
     * функция поднятия уровня имущества
     * 
     * @param action
     *            - действие [getSum; up] (получить сумму; поднять уровень)
     * @param obj
     *            - объект [prop; cash] (само имущество или его касса)
     */
    @RequestMapping(value = "/level-up", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> levelUp(@RequestParam("propId") Integer propId,
            @RequestParam("action") String action, @RequestParam("obj") String obj, User user) {

        JSONObject resultJson = new JSONObject();
        int userId = user.getId();
        Property prop = prRep.getSpecificProperty(userId, propId);

        if (prop == null) {
            ResponseUtil.putErrorMsg(resultJson, "Произошла ошибка (код: 1 - нет такого имущества)!");
        } else {
            long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя

            if (obj.equals("cash")) { // если это повышение для кассы
                int nCashLevel = prop.getCashLevel() + 1; // уровень, к которому будем повышать

                if (nCashLevel > Consts.MAX_CASH_LEVEL) { // отправить сообщение, что достигли последнего уровня
                    ResponseUtil.putErrorMsg(resultJson, "Достигнут последний уровень.");
                } else { // повысить уровень или просто получить сумму повышения
                    doActionsForCashLevelUp(resultJson, prop, nCashLevel, action, userSolvency, userId, obj);
                }
            } else if (obj.equals("prop")) {
                int nPropLevel = prop.getLevel() + 1; // уровень, к которому будем повышать

                if (nPropLevel > Consts.MAX_PROP_LEVEL) { // отправить сообщение, что достигли последнего уровня
                    ResponseUtil.putErrorMsg(resultJson, "Достигнут последний уровень.");
                } else { // повысить уровень или просто получить сумму повышения
                    doActionsForPropLevelUp(resultJson, prop, nPropLevel, action, userSolvency, userId, obj);
                }
            }
        }
        return ResponseUtil.getResponseEntity(resultJson);
    }

    /**
     * функция продажи имущества возвращает информацию об имуществе или же продает его
     * 
     * @param propId
     *            - id имущества, которое нужно продать
     * @param action
     *            - [info] - для получения информации; [sell] - для продажи
     * @param user
     *            - текущий пользователь
     * @return - информацию о продаже
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/sell", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> jquerySellProperty(@RequestParam("propIds") String[] propIds,
            @RequestParam("action") String action, User user) {

        JSONObject resultJson = new JSONObject();
        int userId = user.getId();

        for (String propId : propIds) {
            propId = propId.replace("[", "").replace("]", "").replace("\"", "");
            Property prop = prRep.getSpecificProperty(userId, Integer.parseInt(propId));

            if (prop == null) {
                ResponseUtil.putErrorMsg(resultJson, "Произошла ошибка (код: 1 - нет такого имущества)!");
            } else {
                if (action.equals("info")) {
                    resultJson.put("onSale", prop.isOnSale());
                    if (prop.isOnSale()) {
                        String dateVal = DateUtils
                                .dateToString(rePrRep.getProposalByUsedId(prop.getId()).getLossDate());
                        resultJson.put("endSaleDate", dateVal);
                    }
                } else if (action.equals("sell")) {
                    // установить признак, что имущество на продаже или не на продаже
                    prop.setOnSale(!prop.isOnSale());
                    prRep.updateProperty(prop);

                    // сформировать новое предложение о покупке или удалить
                    if (prop.isOnSale()) {
                        RealEstateProposal reProp = new RealEstateProposal(prop, buiDataRep);
                        rePrRep.addREproposal(reProp);

                        String dateVal = DateUtils.dateToString(reProp.getLossDate());
                        resultJson.put("endSaleDate", dateVal);
                    } else {
                        rePrRep.removeReProposalByUsedId(prop.getId());
                    }
                    resultJson.put("onSale", prop.isOnSale());
                }
            }
        }
        return ResponseUtil.getResponseEntity(resultJson);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////// PRIVATE
    /**
     * функция ремонта недвижимости
     * 
     * @param resultJson
     * @param prop
     *            имущество
     * @param deprPercent
     *            процент износа
     * @param sellPrice
     *            сумма продажи после ремонта
     * @param userMoney
     *            баланс
     * @param repairSum
     *            сумма ремонта
     */
    @SuppressWarnings("unchecked")
    private void repair(JSONObject resultJson, Property prop, Double deprPercent, long sellPrice, long userMoney,
            long repairSum, int userId) {
        prop.setDepreciationPercent(deprPercent);
        prop.setSellingPrice(sellPrice);
        // если данное имущество НЕ valid на момент ремонта - установить
        // nextProfit = tomorrow
        // nextDepreciation = next week
        if (!prop.isValid()) {
            prop.setNextProfit(DateUtils.addDays(new Date(), 1));
            prop.setNextDepreciation(DateUtils.addDays(new Date(), 7));
        }
        prRep.updateProperty(prop);

        Transaction t = new Transaction("Ремонт имущества: " + prop.getName(), new Date(), repairSum, TransferT.SPEND,
                prop.getUserId(), userMoney - repairSum, ArticleCashFlowT.PROPERTY_REPAIR);
        trRep.addTransaction(t);

        // изменить значения цены продажи и процента износа у предложения на рынке, если имущество на продаже
        changeSellingPriceAndDeprecPercent(prop);

        resultJson.put("error", false);
        resultJson.put("percAfterRepair", deprPercent);
        resultJson.put("propSellingPrice", Util.moneyFormat(sellPrice));
        ResponseUtil.addBalanceData(resultJson, repairSum, userMoney, userId, prRep);
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

    /**
     * делает действия для повышения уровня кассы
     * 
     * @param prop
     *            имущество
     * @param nCashLevel
     *            уровень, к которому будем повышать
     * @param action
     *            действие (получить сумму или повысить уровень)
     * @param userSolvency
     *            состоятельность
     * @param obj
     *            объект - "cash"
     */
    private void doActionsForCashLevelUp(JSONObject resultJson, Property prop, int nCashLevel, String action,
            long userSolvency, int userId, String obj) {

        // получить сумму повышения уровня
        // начальная стоимость имущества * коэф. уровня к которому повышаем / коэф. снижения суммы
        long sum = Math.round(prop.getInitialCost() * Consts.UNIVERS_K[nCashLevel] / Consts.K_DECREASE_CASH_L);

        // если запросу нужно вернуть сумму улучшения
        if (action.equals("getSum")) {
            levelUpGetSumAction(resultJson, userSolvency, sum); // получение суммы для информации
        } else if (action.equals("up")) { // если же действие запроса - повысить уровень
            upLevel(resultJson, prop, userSolvency, sum, nCashLevel, userId, obj);
        }
    }

    /**
     * делает действия для повышения уровня имущества
     * 
     * @param action
     *            действие - получить сумму или повысить уровень
     * @param userSolvency
     *            состоятельность
     * @param obj
     *            объект - "prop"
     */
    private void doActionsForPropLevelUp(JSONObject resultJson, Property prop, int nPropLevel, String action,
            long userSolvency, int userId, String obj) {

        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

        // получить сумму повышения уровня
        // максимальная стоимость имущества * коэф. уровня к которому повышаем / коэф. снижения суммы
        long maxPrice = mapData.get(prop.getCommBuildingType().name()).getPurchasePriceMax();
        long sum = Math.round(maxPrice * Consts.UNIVERS_K[nPropLevel] / Consts.K_DECREASE_PROP_L);

        // если запросу нужно вернуть сумму улучшения
        if (action.equals("getSum")) {
            levelUpGetSumAction(resultJson, userSolvency, sum); // получение суммы для информации
        } else if (action.equals("up")) { // если же действие запроса - повысить уровень
            upLevel(resultJson, prop, userSolvency, sum, nPropLevel, userId, obj);
        }
    }

    /**
     * функция добавления суммы повышения уровня для отображения на странице
     */
    @SuppressWarnings("unchecked")
    private void levelUpGetSumAction(JSONObject resultJson, long userSolvency, long sum) {
        if (userSolvency < sum) {
            ResponseUtil.putErrorMsg(resultJson, "Не хватает денег. Нужно: " + sum);
        } else {
            resultJson.put("nextSum", sum);
        }
    }

    /**
     * повышает уровень кассы или имущества
     */
    @SuppressWarnings("unchecked")
    private void upLevel(JSONObject resultJson, Property prop, long userSolvency, long sum, int nLevel, int userId,
            String obj) {
        if (userSolvency >= sum) {
            domiAmount = 0;
            if (obj.equals("cash")) {
                upCashLevel(resultJson, prop, nLevel, userId, sum, true); // повысить уровень кассы
                domiAmount = nLevel;
            } else if (obj.equals("prop")) {
                upPropLevel(resultJson, prop, nLevel, userId, sum, true); // повысить уровень имущества
                domiAmount = (int) Math.round(nLevel * Consts.K_PROP_LEVEL_DOMI);
            }
            MoneyController.upUserDomi(domiAmount, userId, userRep); // повышение доминантности
            resultJson.put("newDomi", userRep.find(userId).getDomi()); // новая доминантность
        } else { // состоятельность < суммы улучшения
            ResponseUtil.putErrorMsg(resultJson, "Не хватает денег. Нужно: " + sum);
        }
    }

    /**
     * метод повышения уровня кассы
     * 
     * @param prop
     *            имущество
     * @param nCashLevel
     *            уровень, к которому будем повышать
     * @param sum
     *            сумма повышения уровня
     * @param isPaidUp
     *            это платное повышение или нет (бесплатное, если за лотерейные плюшки)
     */
    @SuppressWarnings("unchecked")
    void upCashLevel(JSONObject resultJson, Property prop, int nCashLevel, int userId, long sum,
            boolean isPaidUp) {

        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

        // новая вместимость кассы
        long nCashCapacity = mapData.get(prop.getCommBuildingType().name()).getCashCapacity().get(nCashLevel);

        prop.setCashLevel(nCashLevel);
        prop.setCashCapacity(nCashCapacity);
        prRep.updateProperty(prop);

        // если имущество на продаже - изменить уровень кассы у предложения
        changeReProposalLevels("cash", prop, nCashLevel);

        resultJson.put("currLevel", nCashLevel);

        // если это платное повышение, а не за лотерейные плюшки
        if (isPaidUp) {
            // снять деньги
            String descr = String.format("Улучшение кассы до уровня: %s. Касса им-ва: %s", nCashLevel, prop.getName());
            long currBal = Long.parseLong(trRep.getUserBalance(userId));
            Transaction tr = new Transaction(descr, new Date(), sum, TransferT.SPEND, userId, currBal - sum,
                    ArticleCashFlowT.UP_CASH_LEVEL);
            trRep.addTransaction(tr);
            // //

            // получить сумму улучшения до след. уровня + 1
            long nextSum = Math.round(prop.getInitialCost() * Consts.UNIVERS_K[nCashLevel + 1]
                    / Consts.K_DECREASE_CASH_L);
            long userSolvency = Util.getSolvency(trRep, prRep, userId); // получить состоятельность после снятия денег

            if (nCashLevel == Consts.MAX_CASH_LEVEL) {
                ResponseUtil.putErrorMsg(resultJson, "Достигнут последний уровень.");
            } else if (userSolvency < nextSum) {
                ResponseUtil.putErrorMsg(resultJson, "Не хватает денег. Нужно: " + nextSum);
            }

            ResponseUtil.addBalanceData(resultJson, sum, currBal, userId, prRep);
            resultJson.put("nextSum", nextSum); // сумма след. повышения
            resultJson.put("cashCap", nCashCapacity); // new cash capacity для отображения
            resultJson.put("upped", true); // уровень был поднят
        }
    }

    /**
     * Если имущество продается, то повышении уровня имущества или уровня его кассы метод измененяет значения уровня
     * имущества или кассы имущества у соответствующего предложения на рынке коммерческого имущества
     * 
     * @param obj
     *            - признак того, уровень чего нужно изменить (prop, cash)
     * @param prop
     *            - имущество, у которого повысился уровень кассы или его уровень
     * @param levelVal
     *            - новое значение уровня
     */
    private void changeReProposalLevels(String obj, Property prop, int levelVal) {
        if (prop.isOnSale()) {
            RealEstateProposal rePr = rePrRep.getProposalByUsedId(prop.getId());
            if (obj.equals("cash")) {
                rePr.setCashLevel(levelVal);
            } else if (obj.equals("prop")) {
                rePr.setPropLevel(levelVal);
            }
            rePrRep.updateREproposal(rePr);
        }
    }

    /**
     * Если имущество на продаже, тогда при его ремонте, у соответствующего предложения на рынке коммерческого имущества
     * меняются значения цены продажи и процента износа
     * 
     * @param prop
     *            - имущество, у которого производился ремонт
     */
    private void changeSellingPriceAndDeprecPercent(Property prop) {
        if (prop.isOnSale()) {
            RealEstateProposal rePr = rePrRep.getProposalByUsedId(prop.getId());
            rePr.setPurchasePrice(prop.getSellingPrice());
            rePr.setDepreciation(prop.getDepreciationPercent());
            rePrRep.updateREproposal(rePr);
        }
    }

    @SuppressWarnings("unchecked")
    void upPropLevel(JSONObject resultJson, Property prop, int nPropLevel, int userId, long sum,
            boolean isPaidUp) {
        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

        prop.setLevel(nPropLevel);
        prRep.updateProperty(prop);

        // если имущество на продаже - изменить уровень кассы у предложения
        changeReProposalLevels("prop", prop, nPropLevel);

        resultJson.put("currLevel", nPropLevel);

        if (isPaidUp) {
            // снять деньги
            String descr = String.format("Улучшение им-ва: %s до уровня: %s", prop.getName(), nPropLevel);
            long currBal = Long.parseLong(trRep.getUserBalance(userId));
            Transaction tr = new Transaction(descr, new Date(), sum, TransferT.SPEND, userId, currBal - sum,
                    ArticleCashFlowT.UP_PROP_LEVEL);
            trRep.addTransaction(tr);
            // //

            // получить сумму улучшения до след. уровня + 1
            long maxPrice = mapData.get(prop.getCommBuildingType().name()).getPurchasePriceMax();
            long nextSum = Math.round(maxPrice * Consts.UNIVERS_K[nPropLevel + 1] / Consts.K_DECREASE_PROP_L);
            long userSolvency = Util.getSolvency(trRep, prRep, userId); // получить состоятельность после снятия денег

            resultJson.put("upped", true); // уровень был поднят

            if (nPropLevel == Consts.MAX_CASH_LEVEL) {
                ResponseUtil.putErrorMsg(resultJson, "Достигнут последний уровень.");
            } else if (userSolvency < nextSum) {
                ResponseUtil.putErrorMsg(resultJson, "Не хватает денег. Нужно: " + nextSum);
            }

            ResponseUtil.addBalanceData(resultJson, sum, currBal, userId, prRep);
            resultJson.put("nextSum", nextSum); // сумма след. повышения
        }
    }

    /**
     * вставляет основные данные о покупке в JSON объект
     * 
     * @param resultJson
     * @param prop
     *            - объект предложения на рынке из которого берутся все данные
     */
    @SuppressWarnings("unchecked")
    private void putMainInfoAboutPurchase(JSONObject resultJson, RealEstateProposal prop) {
        resultJson.put("propId", prop.getId()); // номер заказа
        resultJson.put("price", prop.getPurchasePrice()); // цена покупки
        resultJson.put("cityArea", prop.getCityArea().toString()); // район недвижимости
        resultJson.put("condition", (prop.getUsedId() == 0) ? "Новое" : "б/у"); // состояние
        resultJson.put("propLevel", prop.getPropLevel()); // уровень имущества
        resultJson.put("cashLevel", prop.getCashLevel()); // уровень кассы имущества
        resultJson.put("depreciation", prop.getDepreciation()); // износ имущества
    }
}
