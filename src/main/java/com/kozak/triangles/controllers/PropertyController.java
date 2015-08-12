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
import com.kozak.triangles.utils.DateUtils;
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
     * переход на страницу рынка недвижимости
     * 
     * @param request
     *            для получения параметров редиректа (для отображ. поздравления о покупке)
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
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

        int currUserId = user.getId();
        String userBalance = trRep.getUserBalance(currUserId);
        model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, currUserId));
        model.addAttribute("reps", reps);
        model.addAttribute("types", SearchCollections.getCommBuildTypes());
        model.addAttribute("areas", SearchCollections.getCityAreas());
        model.addAttribute("proposals", proposals);
        model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, page));
        model.addAttribute("marketEmpty", rePrRep.allPrCount(false) == 0);

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
            long userSolvency = Util.getSolvency(trRep, prRep, user.getId()); // состоятельность пользователя
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
                            + "Ваш максимум = " + Util.moneyFormat(Util.getSolvency(trRep, prRep, userId)) + "&tridot;");
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

        // Long propCount = Long.valueOf(dbResult.get(0).toString());
        // int lastPageNumber = (int) (propCount / Consts.ROWS_ON_PAGE) + ((propCount % Consts.ROWS_ON_PAGE != 0) ? 1 :
        // 0);

        cps.setPrice(rangeValues.get(0), rangeValues.get(1)); // установка мин и макс цены продажи
        cps.setDepreciation(rangeValues.get(2), rangeValues.get(3)); // установка мин и макс износа

        String userBalance = trRep.getUserBalance(userId);
        model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId));
        model.addAttribute("cps", cps);
        model.addAttribute("comProps", dbResult.get(1));
        // model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, page));
        model.addAttribute("types", SearchCollections.getCommBuildTypes());
        model.addAttribute("userHaveProps", prRep.allPrCount(userId, false, false) > 0);

        // если собирали наличку с кассы - для информационного popup окна
        String cash = (String) model.asMap().getOrDefault("changeBal", "");
        if (!cash.isEmpty()) {
            model.addAttribute("changeBal", cash);
        }

        return "b_commerc_pr";
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

        String userBalance = trRep.getUserBalance(userId);
        model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId));
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
     * обработчик запросов по ремонту имущества
     * 
     * @param type
     *            тип запроса (это пока только info или уже repair)
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/repair", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> repairJqueryRequest(@RequestParam("type") String type,
            @RequestParam("propId") Integer propId, User user) {
        JSONObject resultJson = new JSONObject();

        int userId = user.getId();

        Property prop = prRep.getSpecificProperty(userId, propId);

        if (prop == null) {
            putErrorMsg(resultJson, "Произошла ошибка (код: 1)!");
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
                    putErrorMsg(resultJson, "Ваша состоятельность не позволяет вам ремонтировать имущество!");
                } else {
                    repair(resultJson, prop, newDeprPerc, newSellingPrice, userMoney, repairSum, userId);
                }
            } else if (type.equals("info")) {
                if (userSolvency <= 0) {
                    resultJson.put("zeroSolvency", true);
                    putErrorMsg(resultJson, "Ваша состоятельность не позволяет вам ремонтировать имущество!");
                } else {
                    putErrorMsg(resultJson, String.format(
                            "Сумма ремонта: %s <br/> Процент износа после ремонта: %.2f%%", repairSum, newDeprPerc));
                }
            }
        }

        System.err.println(resultJson.toJSONString());
        String json = resultJson.toJSONString();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
    }

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
            prop.setNextProfit(DateUtils.getPlusDay(new Date(), 1));
            prop.setNextDepreciation(DateUtils.getPlusDay(new Date(), 7));
        }
        prRep.updateProperty(prop);

        Transaction t = new Transaction("Ремонт имущества: " + prop.getName(), new Date(), repairSum, TransferT.SPEND,
                prop.getUserId(), userMoney - repairSum, ArticleCashFlowT.PROPERTY_REPAIR);
        trRep.addTransaction(t);

        resultJson.put("error", false);
        resultJson.put("percAfterRepair", deprPercent);
        addBalanceData(resultJson, repairSum, userMoney, userId);
    }

    @SuppressWarnings("unchecked")
    private void addBalanceData(JSONObject resultJson, long sum, long userMoney, int userId) {
        resultJson.put("changeBal", "-" + sum);
        resultJson.put("newBalance", userMoney - sum);
        resultJson.put("newSolvency", Util.getSolvency(String.valueOf(userMoney - sum), prRep, userId));
    }

    /**
     * добавление сообщения об ошибке в JSON
     */
    @SuppressWarnings("unchecked")
    private void putErrorMsg(JSONObject resultJson, String msg) {
        resultJson.put("error", true);
        resultJson.put("message", msg);
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
            putErrorMsg(resultJson, "Произошла ошибка (код: 1 - нет такого имущества)!");
        } else {
            long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя

            if (obj.equals("cash")) { // если это повышение для кассы
                int nCashLevel = prop.getCashLevel() + 1; // уровень, к которому будем повышать

                if (nCashLevel > Consts.MAX_CASH_LEVEL) { // отправить сообщение, что достигли последнего уровня
                    putErrorMsg(resultJson, "Достигнут последний уровень.");
                } else { // повысить уровень или просто получить сумму повышения
                    doActionsForCashLevelUp(resultJson, prop, nCashLevel, action, userSolvency, userId, obj);
                }
            } else if (obj.equals("prop")) {
                int nPropLevel = prop.getLevel() + 1; // уровень, к которому будем повышать

                if (nPropLevel > Consts.MAX_PROP_LEVEL) { // отправить сообщение, что достигли последнего уровня
                    putErrorMsg(resultJson, "Достигнут последний уровень.");
                } else { // повысить уровень или просто получить сумму повышения
                    doActionsForPropLevelUp(resultJson, prop, nPropLevel, action, userSolvency, userId, obj);
                }
            }
        }
        System.err.println(resultJson.toJSONString());
        String json = resultJson.toJSONString();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
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
            putErrorMsg(resultJson, "Не хватает денег. Нужно: " + sum);
        } else {
            resultJson.put("nextSum", sum);
        }
    }

    private void upLevel(JSONObject resultJson, Property prop, long userSolvency, long sum, int nLevel, int userId,
            String obj) {
        if (userSolvency >= sum) {
            if (obj.equals("cash")) {
                upCashLevel(resultJson, prop, nLevel, userId, sum); // повысить уровень кассы
            } else if (obj.equals("prop")) {
                upPropLevel(resultJson, prop, nLevel, userId, sum); // повысить уровень имущества
            }
        } else { // состоятельность < суммы улучшения
            putErrorMsg(resultJson, "Не хватает денег. Нужно: " + sum);
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
     */
    @SuppressWarnings("unchecked")
    private void upCashLevel(JSONObject resultJson, Property prop, int nCashLevel, int userId, long sum) {
        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

        // новая вместимость кассы
        long nCashCapacity = mapData.get(prop.getCommBuildingType().name()).getCashCapacity().get(nCashLevel);

        prop.setCashLevel(nCashLevel);
        prop.setCashCapacity(nCashCapacity);
        prRep.updateProperty(prop);

        // снять деньги
        String descr = String.format("Улучшение кассы до уровня: %s. Касса им-ва: %s", nCashLevel, prop.getName());
        long currBal = Long.parseLong(trRep.getUserBalance(userId));
        Transaction tr = new Transaction(descr, new Date(), sum, TransferT.SPEND, userId, currBal - sum,
                ArticleCashFlowT.UP_CASH_LEVEL);
        trRep.addTransaction(tr);
        // //

        // получить сумму улучшения до след. уровня + 1
        long nextSum = Math.round(prop.getInitialCost() * Consts.UNIVERS_K[nCashLevel + 1] / Consts.K_DECREASE_CASH_L);
        long userSolvency = Util.getSolvency(trRep, prRep, userId); // получить состоятельность после снятия денег

        resultJson.put("upped", true); // уровень был поднят
        resultJson.put("cashCap", nCashCapacity); // new cash capacity для отображения

        if (nCashLevel == Consts.MAX_CASH_LEVEL) {
            putErrorMsg(resultJson, "Достигнут последний уровень.");
        } else if (userSolvency < nextSum) {
            putErrorMsg(resultJson, "Не хватает денег. Нужно: " + nextSum);
        }

        addBalanceData(resultJson, sum, currBal, userId);
        resultJson.put("currLevel", nCashLevel);
        resultJson.put("nextSum", nextSum); // сумма след. повышения
    }

    @SuppressWarnings("unchecked")
    private void upPropLevel(JSONObject resultJson, Property prop, int nPropLevel, int userId, long sum) {
        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

        prop.setLevel(nPropLevel);
        prRep.updateProperty(prop);

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
            putErrorMsg(resultJson, "Достигнут последний уровень.");
        } else if (userSolvency < nextSum) {
            putErrorMsg(resultJson, "Не хватает денег. Нужно: " + nextSum);
        }

        addBalanceData(resultJson, sum, currBal, userId);
        resultJson.put("currLevel", nPropLevel);
        resultJson.put("nextSum", nextSum); // сумма след. повышения
    }
}
