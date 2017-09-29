package com.kozak.triangles.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.data.PredictionsTableData;
import com.kozak.triangles.entity.Property;
import com.kozak.triangles.entity.Transaction;
import com.kozak.triangles.entity.User;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.exceptions.LotteryPlayException;
import com.kozak.triangles.exceptions.NoPredictionException;
import com.kozak.triangles.exceptions.NoSuchLicenseLevelException;
import com.kozak.triangles.exceptions.WinHandlingException;
import com.kozak.triangles.search.LotterySearch;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.util.CommonUtil;
import com.kozak.triangles.util.Constants;
import com.kozak.triangles.util.DateUtils;
import com.kozak.triangles.util.ResponseUtil;
import com.kozak.triangles.util.TagCreator;

@SessionAttributes("user")
@Controller
@RequestMapping(value = "/lottery")
public class LotteryController extends BaseController {

    @Autowired
    private PropertyController propertyController;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String lotteryPage(Model model, User user, LotterySearch ls, HttpServletRequest req) throws ParseException {
        if (ls.isNeedClear())
            ls.clear();
        model.addAttribute("ls", ls);

        long userId = user.getId();
        user = userRep.find(userId);

        List<Object> fromDB = lotteryRep.getLotteryStory(userId, ls);
        long itemsCount = (long) fromDB.get(0);
        int totalPages = (int) (itemsCount / Constants.ROWS_ON_PAGE) + ((itemsCount % Constants.ROWS_ON_PAGE != 0) ? 1 : 0);

        if (totalPages > 1) {
            int currPage = Integer.parseInt(ls.getPage());
            String paginationTag = TagCreator.paginationTag(totalPages, currPage, req);
            model.addAttribute("paginationTag", paginationTag);
        }

        long upPropCount = winService.getRemainingAmount(userId, WinArticle.PROPERTY_UP);
        long upCashCount = winService.getRemainingAmount(userId, WinArticle.CASH_UP);

        model = addMoneyInfoToModel(model, user);
        model = addLicenseCountInfoToModel(model, userId);
        model.addAttribute("articles", SearchCollections.getWinArticles()); // статьи выигрыша
        model.addAttribute("ticketsCount", user.getLotteryTickets()); // количество лотерейных билетов
        model.addAttribute("lotteryStory", fromDB.get(1)); // информация о розыгрышах
        model.addAttribute("upPropCount", upPropCount); // количество доступных повышений имуществ
        model.addAttribute("upCashCount", upCashCount); // количество доступных повышений кассы
        model.addAttribute("isPredictionAvailable", winService.isUserHasPrediction(userId)); // есть ли предсказание
        model.addAttribute("ticketsPrice", Constants.LOTTERY_TICKETS_PRICE); // цены на билеты
        model.addAttribute("lotteryGamesLimit", Constants.LOTTERY_GAMES_LIMIT_PER_DAY); // лимит на количество розыграшей в день
        model.addAttribute("playsCountToday", lotteryRep.countOfPlaysToday(userId)); // количество розыграшей сегодня
        model.addAttribute("user", user);

        return "lottery";
    }

    /**
     * Метод покупки лотерейных билетов. Устанавливает новое значение лотерейных билетов, снимает деньги со счета за
     * покупку.
     * 
     * @param count
     *            - количество для покупки (1, 10, 50)
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/buy-tickets", method = RequestMethod.GET, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> buyTickets(@RequestParam("count") int count, User user) {

        JSONObject resultJson = new JSONObject();
        long userId = user.getId();

        long userMoney = Long.parseLong(trRep.getUserBalance(userId));
        long userSolvency = CommonUtil.getSolvency(trRep, prRep, userId); // состоятельность пользователя

        // проверки на правильность количества покупаемых билетов
        if (count != 1 && count != 10 && count != 50) {
            ResponseUtil.putErrorMsg(resultJson, "Такое количество недоступно для покупок.");
        } else {
            // цена за один
            int priceToOneTicket = 0;
            if (count == 1) {
                priceToOneTicket = Constants.LOTTERY_TICKETS_PRICE[0];
            } else if (count == 10) {
                priceToOneTicket = Constants.LOTTERY_TICKETS_PRICE[1];
            } else if (count == 50) {
                priceToOneTicket = Constants.LOTTERY_TICKETS_PRICE[2];
            }

            // сумма покупки
            long purchaseSum = priceToOneTicket * count;

            // не хватает денег
            if (userSolvency < purchaseSum) {
                ResponseUtil.putErrorMsg(resultJson,
                        "Сумма покупки <b>(" + purchaseSum + "&tridot;)</b> слишком большая. Вам не хватает денег.");
            } else {
                // установка пользователю нового количества лотерейных билетов
                user.setLotteryTickets(user.getLotteryTickets() + count);
                userRep.updateUser(user);

                // снятие денег со счета
                String descr = String.format("%s шт.", count);
                long newBalance = userMoney - purchaseSum;

                Transaction tr = new Transaction(descr, new Date(), purchaseSum, TransferType.SPEND, userId, newBalance,
                        ArticleCashFlow.LOTTERY_TICKETS_BUY);
                trRep.addTransaction(tr);

                // добавить информацию о новом значении баланса, состоятельности, количества билетов
                ResponseUtil.addBalanceData(resultJson, purchaseSum, userMoney, userId, prRep);
                resultJson.put("ticketsValue", user.getLotteryTickets());
            }
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * Метод игры в лото. Позволяет сыграть в лотерею на выбранное пользователем количество билетов. Если пользователь
     * выбирает игру на 5 билетов, а у него лишь 3, то будет игра на 3 билета, ошибки при этом не будет. Если он выберет
     * игру на 10, а у него 9, то игра будет на 9.
     * 
     * 
     * @param count
     *            - количество для игры (1, 5 - ≤5, 10 - ≤10, 0 - на все)
     */
    @RequestMapping(value = "/play-loto", method = RequestMethod.GET, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> playLoto(@RequestParam("count") int count, User user) {
        JSONObject resultJson = new JSONObject();
        long userId = user.getId();

        try {
            lotteryService.playLoto(count, userId);
        } catch (LotteryPlayException | WinHandlingException e) {
            ResponseUtil.putErrorMsg(resultJson, e.getMessage());
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * Отдает предсказания пользователя, если оно у него есть.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/get-predict", method = RequestMethod.GET, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> getPrediction(User user) {

        JSONObject resultJson = new JSONObject();
        long userId = user.getId();
        try {
            Integer predictId = winService.takeUserPredictionId(userId);
            resultJson.put("predictId", predictId); // ID предсказания
            resultJson.put("predictText", PredictionsTableData.getPrediction(predictId)); // текст предсказания
        } catch (NoPredictionException e) {
            ResponseUtil.putErrorMsg(resultJson, "Работа всезнающего - мыслить, ваша работа - играть в лотерею.");
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * Берет выигранные лицензии и устанавливает пользователю, если он забирает выигрыш.
     * 
     * @param licLevel
     *            - уровень, который выбрал user
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/use-license", method = RequestMethod.GET, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> useLicense(@RequestParam("licLevel") byte licLevel, User user) {

        JSONObject resultJson = new JSONObject();
        try {
            long userId = user.getId();

            WinArticle licenseArticle = WinArticle.getLicenseArticleByLevel(licLevel);
            long remainingLicenses = winService.getRemainingAmount(userId, licenseArticle);
            if (remainingLicenses == 0) {
                ResponseUtil.putErrorMsg(resultJson,
                        String.format("У вас нет лицензий уровня %s. Нужно больше играть в лотерею.", licLevel));
            } else {
                // назначить лицензию пользователю
                Date licExpire = BuildingController.setNewLicenseToUser(userRep, userId, licLevel);
                resultJson.put("licExpire", DateUtils.dateToString(licExpire));

                // списать с остатков 1 лицензию
                winService.takeAmount(userId, licenseArticle, 1);
            }
        } catch (NoSuchLicenseLevelException e) {
            ResponseUtil.putErrorMsg(resultJson, e.getMessage());
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * Метод получения списка имущества, уровень которого можно повысить или уровень кассы которого можно повысить.
     * 
     * @param obj
     *            - объект для повышения уровня [prop, cash]
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/level-up", method = RequestMethod.GET, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> levelUp(@RequestParam("reqObj") String obj, User user) {

        JSONObject resultJson = new JSONObject();
        long userId = user.getId();

        if (obj.equals("prop") || obj.equals("cash")) {
            Object[] countAndName = getPljushkiCountAndNameForLevelUp(obj, userId);
            long pljushkiCount = (long) countAndName[0];
            String objName = (String) countAndName[1];

            if (pljushkiCount <= 0) {
                String msg = String.format("У вас нет бесплатных плюшек для повышения уровня %s.", objName);
                ResponseUtil.putErrorMsg(resultJson, msg);
            } else {
                /*
                 * получить список имущества, уровень которого нужно повысить. Или уровень кассы которого нужно повысить
                 * obj - [prop || cash] (имущество или касса)
                 */
                List<Property> properties = prRep.getToLevelUpForPljushki(obj, userId); // список имущества
                resultJson.put("properties", properties);
            }
        } else {
            ResponseUtil.putErrorMsg(resultJson, "Нет таких объектов.");
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * Подтверждает повышение уровня имущества
     * 
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/confirm-level-up", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> confirmLevelUp(@RequestParam("obj") String obj,
            @RequestParam("propId") long propId, User user) {

        JSONObject resultJson = new JSONObject();
        long userId = user.getId();

        if (obj.equals("prop") || obj.equals("cash")) {
            Object[] countAndName = getPljushkiCountAndNameForLevelUp(obj, userId);
            long pljushkiCount = (long) countAndName[0];
            String objName = (String) countAndName[1];

            if (pljushkiCount <= 0) {
                String msg = String.format("У вас нет бесплатных плюшек для повышения уровня %s.", objName);
                ResponseUtil.putErrorMsg(resultJson, msg);
            } else {
                Property prop = prRep.getSpecificProperty(userId, propId);
                if (prop == null) {
                    ResponseUtil.putErrorMsg(resultJson, "У вас нет такого имущества!");
                } else {
                    if (obj.equals("cash")) { // если это повышение для кассы
                        int nCashLevel = prop.getCashLevel() + 1; // уровень, к которому будем повышать

                        if (nCashLevel > Constants.MAX_CASH_LEVEL) { // отправить сообщение, что достигли последнего уровня
                            ResponseUtil.putErrorMsg(resultJson, "Достигнут последний уровень.");
                        } else { // повысить уровень или просто получить сумму повышения
                            propertyController.upCashLevel(resultJson, prop, nCashLevel, userId, 0, false);
                            resultJson.put("maxLevel", Constants.MAX_CASH_LEVEL);

                            winService.takeAmount(userId, WinArticle.CASH_UP, 1); // списать с остатков повышение уровня
                        }
                    } else if (obj.equals("prop")) {
                        int nPropLevel = prop.getLevel() + 1; // уровень, к которому будем повышать

                        if (nPropLevel > Constants.MAX_PROP_LEVEL) { // отправить сообщение, что достигли последнего уровня
                            ResponseUtil.putErrorMsg(resultJson, "Достигнут последний уровень.");
                        } else { // повысить уровень или просто получить сумму повышения
                            propertyController.upPropLevel(resultJson, prop, nPropLevel, userId, 0, false);
                            resultJson.put("maxLevel", Constants.MAX_PROP_LEVEL);

                            winService.takeAmount(userId, WinArticle.PROPERTY_UP, 1); // списать с остатков повышение уровня
                        }
                    }
                }
            }
        } else {
            ResponseUtil.putErrorMsg(resultJson, "Нет таких объектов.");
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    private Object[] getPljushkiCountAndNameForLevelUp(String obj, long userId) {
        Object[] result = new Object[2];

        // проверить остаток бесплатных плюшек повышения уровня
        if (obj.equals("prop")) {
            result[0] = winService.getRemainingAmount(userId, WinArticle.PROPERTY_UP);
            result[1] = "имущества";
        } else {
            result[0] = winService.getRemainingAmount(userId, WinArticle.CASH_UP);
            result[1] = "кассы";
        }
        return result;
    }

}
