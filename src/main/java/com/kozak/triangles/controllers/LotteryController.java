package com.kozak.triangles.controllers;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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
import com.kozak.triangles.data.TradeBuildingsTableData;
import com.kozak.triangles.entities.LotteryInfo;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.exceptions.NoPredictionException;
import com.kozak.triangles.exceptions.NoSuchLicenseLevelException;
import com.kozak.triangles.models.TradeBuilding;
import com.kozak.triangles.models.WinDataModel;
import com.kozak.triangles.repositories.LotteryRep;
import com.kozak.triangles.search.LotterySearch;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.services.WinService;
import com.kozak.triangles.utils.CommonUtil;
import com.kozak.triangles.utils.Constants;
import com.kozak.triangles.utils.DateUtils;
import com.kozak.triangles.utils.PropertyUtil;
import com.kozak.triangles.utils.Random;
import com.kozak.triangles.utils.ResponseUtil;
import com.kozak.triangles.utils.TagCreator;

@SessionAttributes("user")
@Controller
@RequestMapping(value = "/lottery")
public class LotteryController extends BaseController {

    @Autowired
    private PropertyController propertyController;

    /**
     * Класс для группировки по статье выигрыша информации о выигрыше в лотерею
     * 
     * @author Roman: 14 вер. 2015 21:49:39
     */
    private static class WinGroup {
        // общее количество билетов - например, 8 билетов.
        private int ticketsCount;
        /*
         * общее количество выигранных сущностей по конкретной статье затрат. Например, 10 киосков (получено за 8
         * билетов).
         */
        private int entitiesCount;
        /*
         * Карта <Возможное количество что можно выиграть, Количество билетов потраченное на это количество> например,
         * статья затрат TRIANGLES
         */
        HashMap<Integer, Integer> countMap = new HashMap<Integer, Integer>();

        public void addToGroup(int entCount) {
            ticketsCount++; // общее колво билетов
            entitiesCount += entCount; // общее колво сущностей по данной статье

            // количество в разрезе возможного количества выигрышей по статье (можно выиграть 2 киоска, а можно 3 шт.)
            Integer c = countMap.get(entCount);
            if (c == null) {
                countMap.put(entCount, 1);
            } else {
                countMap.put(entCount, ++c);
            }
        }
    }

    /**
     * Всезнающий, живущий в недрах программного кода, который может открыть пользователю свою мудрость
     * 
     * @author Roman: 20 вер. 2015 22:56:49
     */
    private static class Omniscient {
        /**
         * Открыть пользователю мудрость или дать предсказание.
         */
        private static void givePredictionToUser(long userId, Date date, LotteryRep lotteryRep, WinService winService) {
            TreeSet<Integer> allPredIDs = PredictionsTableData.getPredictionsIds(); // все ID предсказаний
            Integer lastPredId = allPredIDs.last(); // последний ID из предсказаний

            // cгенерить ID предсказания (мудрости), которое точно есть в базе
            Integer predictionId = null;
            do {
                predictionId = (int) Random.generateRandNum(1, lastPredId);
            } while (!allPredIDs.contains(predictionId));

            // добавить предсказание
            LotteryInfo lInfo = new LotteryInfo(userId, "Ты получил мудрость от всезнающего. Вникай.", WinArticle.PREDICTION, 1,
                    1, date);
            lotteryRep.addLotoInfo(lInfo);
            // вместо remainingAmount устанавливается ID предсказания, чтобы таким образом его хранить
            winService.addUserPrediction(userId, predictionId);
        }
    }

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
        model.addAttribute("articles", SearchCollections.getLotteryArticles()); // статьи выигрыша
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
                String descr = String.format("Покупка лотерейных билетов, %s шт.", count);
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
        user = userRep.find(userId);
        int userTickets = user.getLotteryTickets();

        // проверки на правильность количества покупаемых билетов
        if (userTickets == 0) {
            ResponseUtil.putErrorMsg(resultJson, "У вас нет билетов. Купите или ждите зачисления за очки доминантности.");
        } else if (lotteryRep.countOfPlaysToday(userId) >= Constants.LOTTERY_GAMES_LIMIT_PER_DAY) {
            ResponseUtil.putErrorMsg(resultJson, "Вы исчерпали суточный лимит розыгрышей лотереи. Приходите завтра.");
        } else if (count != 1 && count != 5 && count != 10 && count != 0) {
            ResponseUtil.putErrorMsg(resultJson, "Игра в лото на такое количество билетов недоступна.");
        } else {
            int gamesCount = computeGamesCount(userTickets, count, userId); // вычислить количество игр
            Date date = new Date(); // дата игры

            // сыграть в игру на все билеты и сгруппировать результат по лотерейной статье (LotteryArticle)
            HashMap<WinArticle, WinGroup> lotoResult = playLotoAndGetResult(gamesCount, userId);

            // взять сгруппированный результат и добавить выигранное пользователю
            // (сформировать транзакции, добавить имущество, предсказания и пр.)
            for (Map.Entry<WinArticle, WinGroup> lotoRes : lotoResult.entrySet()) {
                WinArticle article = lotoRes.getKey();
                WinGroup groupedRes = lotoRes.getValue();

                if (article.equals(WinArticle.TRIANGLES)) {
                    giveMoneyToUser(groupedRes, userId, date); // начислить пользователю деньги, что он выиграл
                } else if (article.equals(WinArticle.PROPERTY_UP) || article.equals(WinArticle.CASH_UP)
                        || article.equals(WinArticle.LICENSE_2) || article.equals(WinArticle.LICENSE_3)
                        || article.equals(WinArticle.LICENSE_4)) {

                    handleCommonArticle(groupedRes, userId, article, date);
                } else if (article.equals(WinArticle.STALL) || article.equals(WinArticle.VILLAGE_SHOP)
                        || article.equals(WinArticle.STATIONER_SHOP)) {

                    handlePropertyArticle(groupedRes, userId, article, date);
                } else if (article.equals(WinArticle.PREDICTION)) {
                    Omniscient.givePredictionToUser(userId, date, lotteryRep, winService);
                } else if (article.equals(WinArticle.LAND_LOT_GHETTO) || article.equals(WinArticle.LAND_LOT_OUTSKIRTS)
                        || article.equals(WinArticle.LAND_LOT_CHINATOWN) || article.equals(WinArticle.LAND_LOT_CENTER)) {

                    handleLandLotArticle(groupedRes, userId, article, date);
                } else if (article.equals(WinArticle.LOTTERY_TICKET)) {
                    userTickets += groupedRes.entitiesCount;
                } else {
                    ResponseUtil.putErrorMsg(resultJson,
                            String.format("Возникла ошибка! Статья выигрыша %s не была обработана!", article));
                    break;
                }
            }
            // снять билеты пользователя
            user.setLotteryTickets(userTickets - gamesCount);
            userRep.updateUser(user);
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * Отдает предсказания пользователя, если оно у него есть.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/get-predict", method = RequestMethod.GET, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> getPredict(User user) {

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

    /**
     * Вводит в эксплуатацию один из видов торгового имущества (в зависимости от стастьи выигрыша в лотерею). Все
     * имущество, которое выигрывается в лотерею, находится в районе Гетто, имеет минимальную цену для данного вида
     * имущества.
     * 
     * @param groupedRes
     *            - сгруппированный результат по статье
     * @param userId
     * @param article
     *            - статья выигрыша (STALL, VILLAGE_SHOP, ...)
     * @param date
     */
    private void handlePropertyArticle(WinGroup groupedRes, long userId, WinArticle article, Date date) {

        // ввести в эксплуатацию все выигранное имущество
        int countOfProperties = groupedRes.entitiesCount;
        // данные конкретного имущества
        TradeBuilding buildingData = TradeBuildingsTableData.getTradeBuildingDataByName(article.name());

        for (int i = 0; i < countOfProperties; i++) {
            // генерация имя для нового имущества
            String name = PropertyUtil.generatePropertyName(buildingData.getTradeBuildingType(), CityArea.GHETTO);
            long price = buildingData.getPurchasePriceMin(); // цена нового имущества (всегда минимальная)

            Property prop = new Property(buildingData, userId, CityArea.GHETTO, new Date(), price, name);
            prRep.addProperty(prop);
        }

        // внести информацию о выигрыше
        String mapDataStr = groupedRes.countMap.entrySet().toString().replace("=", "×");
        String description = "Имущество [Кол-во имущества × Кол-во билетов]. " + mapDataStr;
        LotteryInfo lInfo = new LotteryInfo(userId, description, article, groupedRes.entitiesCount, groupedRes.ticketsCount,
                date);
        lotteryRep.addLotoInfo(lInfo);
    }

    /**
     * Обработка общих статтей выигрыша, таких как повышение уровня имущества или выигрыш лицензий на строительство. Это
     * те статьи, выигрыш которых зачисляется не сразу, а накапливается и пользователь может забрать выигрыш позже.
     * 
     * @param groupedRes
     *            - сгруппированный результат по статье
     * @param userId
     * @param article
     *            - сама статья выигрыша
     * @param date
     */
    private void handleCommonArticle(WinGroup groupedRes, long userId, WinArticle article, Date date) {
        // внести информацию о выигрыше
        String descrPref = "";
        if (article.equals(WinArticle.PROPERTY_UP)) {
            descrPref = "Повышение уровня имущества [Кол-во повышений ×";
        } else if (article.equals(WinArticle.CASH_UP)) {
            descrPref = "Повышение уровня кассы [Кол-во повышений ×";
        } else if (article.equals(WinArticle.LICENSE_2) || article.equals(WinArticle.LICENSE_3)
                || article.equals(WinArticle.LICENSE_4)) {

            String artcNm = article.name();// название статьи выигрыша
            int artcNmL = artcNm.length(); // длина названия статьи выигрыша
            descrPref = String.format("Лицензии на строительство, ур.%s [Кол-во лицензий ×", artcNm.substring(artcNmL - 1));
        }
        String mapDataStr = groupedRes.countMap.entrySet().toString().replace("=", "×");
        String description = descrPref + " Кол-во билетов]. " + mapDataStr;
        int winAmount = groupedRes.entitiesCount;
        LotteryInfo lInfo = new LotteryInfo(userId, description, article, winAmount, groupedRes.ticketsCount, date);
        lotteryRep.addLotoInfo(lInfo);
        winService.addAmountOfWin(userId, article, winAmount); // добавить на остатки
    }

    /**
     * Обрабатывает статью выигрыша земельного участка. Добавляет конкретному пользователю выигранное количество участков в
     * конкретном районе, взависимости от лотерейной статьи.
     * 
     * @param groupedRes
     *            - сгруппированный результат по статье
     * @param userId
     * @param article
     *            - сама статья выигрыша
     * @param date
     */
    private void handleLandLotArticle(WinGroup groupedRes, long userId, WinArticle article, Date date) {
        // получить количество выигранных участков и начислить их пользователю
        int landLotsCount = groupedRes.entitiesCount;
        landLotService.addLandLots(userId, extractCityAreaFromArticle(article), landLotsCount);

        // создать информацию об игре в лото
        String mapDataStr = groupedRes.countMap.entrySet().toString().replace("=", "×");
        String description = "Земельные участки, [Кол-во участков × Кол-во билетов]. " + mapDataStr;
        LotteryInfo lInfo = new LotteryInfo(userId, description, article, groupedRes.entitiesCount, groupedRes.ticketsCount,
                date);
        lotteryRep.addLotoInfo(lInfo);
    }

    /**
     * Начислить пользователю деньги на счет, а также добавить информацию о выигрыше в таблицу с информацией о лотерее.
     * 
     * @param groupedRes
     *            - сгруппированный результат по выигрышу
     * @param date
     */
    private void giveMoneyToUser(WinGroup groupedRes, long userId, Date date) {
        // добавить транзакцию
        long userMoney = Long.parseLong(trRep.getUserBalance(userId));

        String descr = String.format("Выигрыш в лотерею за %s бил.", groupedRes.ticketsCount);
        int sum = groupedRes.entitiesCount;
        Transaction tr = new Transaction(descr, date, sum, TransferType.PROFIT, userId, userMoney + sum,
                ArticleCashFlow.LOTTERY_WINNINGS);
        trRep.addTransaction(tr);

        // добавить информацию в таблицу с лотерейными выигрышами
        String mapDataStr = groupedRes.countMap.entrySet().toString().replace("=", "×");
        String description = "Деньги [&tridot; × Кол-во билетов]. " + mapDataStr;
        LotteryInfo lInfo = new LotteryInfo(userId, description, WinArticle.TRIANGLES, sum, groupedRes.ticketsCount, date);
        lotteryRep.addLotoInfo(lInfo);
    }

    /**
     * Вычисляет, на сколько билетов нужно сыграть в лото.
     * 
     * @param userTickets
     *            - колво билетов пользователя
     * @param countUserWant
     *            - количество, на которое он хочет играть
     */
    private int computeGamesCount(int userTickets, int countUserWant, long userId) {
        int gamesCount = countUserWant; // количество игр в лото

        // если билетов меньше, чем пользователь хочет потратить на игру ИЛИ он хочет играть на все
        if (userTickets < countUserWant || countUserWant == 0) {
            gamesCount = userTickets; // количество игр будет ограниченно билетами
        }

        // вычислить, сколько осталось розыгрышей на сегодня, учитывая лимит на количество игр в день
        int remainToPlayToday = Constants.LOTTERY_GAMES_LIMIT_PER_DAY - lotteryRep.countOfPlaysToday(userId);
        if (gamesCount > remainToPlayToday) {
            // если количество игр больше, чем осталось сыграть сегодня, то ограничим остаточным количеством
            gamesCount = remainToPlayToday;
        }

        return gamesCount;
    }

    /**
     * разыгрывает лотерейные билеты
     * 
     * @param ticketsCount
     *            - количество билетов к розыгрышу
     * @param userId
     * @return сгруппированный результат розыгрышей
     */
    private HashMap<WinArticle, WinGroup> playLotoAndGetResult(int ticketsCount, long userId) {
        // сгруппированные по статье результаты игры в лото
        HashMap<WinArticle, WinGroup> groupResult = new HashMap<WinArticle, LotteryController.WinGroup>();

        // получить данные со всеми возможными вариантами выигрышей
        TreeMap<Integer, WinDataModel> winningsData = winService.getWinningsData();

        boolean userHasPrediction = winService.isUserHasPrediction(userId); // есть непросмотренные предсказания
        // имущество можно выиграть только в районе Гетто. Взять количество доступных участков
        long availableLandLotsCount = landLotService.getAvailableLandLotsCount(userId, CityArea.GHETTO);

        WinDataModel tempWinData = null; // временные данные результата одного розыгрыша
        // цикл - это розыгрыш одного билета
        for (int i = 0; i < ticketsCount; i++) {
            // результат розыгрыша одного билета
            int ticRes = (int) Random.generateRandNum(Constants.LOWER_LOTTERY_BOUND, Constants.UPPER_LOTTERY_BOUND);

            int flKey = winningsData.floorKey(ticRes); // ближайший нижный ключ в дереве
            tempWinData = winningsData.get(flKey); // данные о выигрыше

            WinArticle article = tempWinData.getArticle();
            
            // если выиграл предсказание
            if (article.equals(WinArticle.PREDICTION)) {
                // если есть непросмотренные предсказания - не считать этот розыгрыш
                if (userHasPrediction) {
                    i--;
                    continue;
                } else {
                    // иначе - посчитать этот розыгрыш, но теперь у юзера уже есть непросмотренные предсказания
                    userHasPrediction = true;
                }
            }
            // если выиграл имущество - проверить, есть ли для него участок
            if (article.equals(WinArticle.STALL) || article.equals(WinArticle.VILLAGE_SHOP)
                    || article.equals(WinArticle.STATIONER_SHOP)) {
                
                if (availableLandLotsCount <= 0) {
                    // участков не осталось совсем
                    i--;
                    continue;
                } else {
                    int wonPropertyCount = tempWinData.getCount(); // кол-во выигранного имущества
                    if (availableLandLotsCount < wonPropertyCount) {
                        // участков меньше, чем выиграно имущества
                        wonPropertyCount = (int) availableLandLotsCount;
                        tempWinData.setCount((int) availableLandLotsCount);
                    }
                    availableLandLotsCount -= wonPropertyCount; // уменьшить кол-во доступных участков
                }
            }
            // увеличить кол-во доступных участков, если выиграл участок
            // имущество можно выигрывать только в районе Гетто, значит брать во внимание только такие участки
            if (article.equals(WinArticle.LAND_LOT_GHETTO)) {
                availableLandLotsCount += tempWinData.getCount();
            }
            
            // сгруппировать данный результат розыгрыша
            groupTicketResult(tempWinData, groupResult);
        }
        return groupResult;
    }

    /**
     * группирует результат розыгрыша одного билета
     * 
     * @param ticketResultData
     *            - результат розыгрыша одного лотерейного билета
     * @param groupResult
     *            - сгруппированный результат, к которому будет добавлен текущий
     */
    private void groupTicketResult(WinDataModel ticketResultData, HashMap<WinArticle, WinGroup> groupResult) {
        // группировка данных и добавление в сгруппированный результат
        WinArticle article = ticketResultData.getArticle(); // статья по текущему розыгрышу
        WinGroup wg = groupResult.get(article); // сгруппированные результаты по статье
        if (wg == null) {
            wg = new WinGroup();
        }
        // добавление количества выигранных сущностей (напр. 2 Киоска) к группе
        wg.addToGroup(ticketResultData.getCount());
        groupResult.put(article, wg); // вставка в группировочную Карту
    }

    /**
     * @param article
     *            - статья, которая заканчивается на "_CITY_AREA_NAME", например: "LAND_LOT_CENTER"
     * @return район города
     */
    private CityArea extractCityAreaFromArticle(WinArticle article) {
        String articleName = article.name(); // название статьи выигрыша
        String cityAreaName = articleName.substring(articleName.lastIndexOf("_") + 1);
        return CityArea.valueOf(cityAreaName);
    }
}
