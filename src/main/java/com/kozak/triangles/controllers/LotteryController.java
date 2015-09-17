package com.kozak.triangles.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.NoResultException;

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
import com.kozak.triangles.entities.LotteryInfo;
import com.kozak.triangles.entities.Predictions;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.entities.WinningsData;
import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.LotteryArticles;
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.search.LotterySearch;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.utils.Consts;
import com.kozak.triangles.utils.Random;
import com.kozak.triangles.utils.ResponseUtil;
import com.kozak.triangles.utils.SingletonData;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@Controller
@RequestMapping(value = "/lottery")
public class LotteryController extends BaseController {
    private static TreeMap<Integer, WinningsData> winningsData = null;

    /**
     * Класс для группировки по статье затрат информации о выигрыше в лотерею
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

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String buildingPage(Model model, User user, LotterySearch ls) {
        if (ls.isNeedClear())
            ls.clear();
        model.addAttribute("ls", ls);

        int userId = user.getId();
        String userBalance = trRep.getUserBalance(userId);
        int userDomi = userRep.getUserDomi(userId);
        long upPropCount = lotteryRep.getPljushkiCountByArticle(userId, LotteryArticles.PROPERTY_UP);
        long upCashCount = lotteryRep.getPljushkiCountByArticle(userId, LotteryArticles.CASH_UP);
        long lic2Count = lotteryRep.getPljushkiCountByArticle(userId, LotteryArticles.LICENSE_2);
        long lic3Count = lotteryRep.getPljushkiCountByArticle(userId, LotteryArticles.PROPERTY_UP);
        long lic4Count = lotteryRep.getPljushkiCountByArticle(userId, LotteryArticles.PROPERTY_UP);

        model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId), userDomi);
        model.addAttribute("articles", SearchCollections.getLotteryArticles()); // статьи выигрыша
        model.addAttribute("ticketsCount", user.getLotteryTickets()); // количество лотерейных билетов
        model.addAttribute("lotteryStory", lotteryRep.getLotteryStory(userId)); // информация о розыгрышах
        model.addAttribute("upPropCount", upPropCount); // количество доступных повышений имуществ
        model.addAttribute("upCashCount", upCashCount); // количество доступных повышений кассы
        model.addAttribute("lic2Count", lic2Count); // количество лицензий 2 ур
        model.addAttribute("lic3Count", lic3Count); // количество лицензий 3 ур
        model.addAttribute("lic4Count", lic4Count);// количество лицензий 4 ур
        model.addAttribute("isPredictionAvailable", lotteryRep.isUserHasPrediction(userId)); // есть ли предсказание

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
        int userId = user.getId();

        long userMoney = Long.parseLong(trRep.getUserBalance(userId));
        long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя

        // проверки на правильность количества покупаемых билетов
        if (count != 1 && count != 10 && count != 50) {
            ResponseUtil.putErrorMsg(resultJson, "Такое количество недоступно для покупок.");
        } else {
            // цена за один
            int priceToOneTicket = 0;
            if (count == 1) {
                priceToOneTicket = Consts.LOTTERY_TICKETS_PRICE[0];
            } else if (count == 10) {
                priceToOneTicket = Consts.LOTTERY_TICKETS_PRICE[1];
            } else if (count == 50) {
                priceToOneTicket = Consts.LOTTERY_TICKETS_PRICE[2];
            }

            // сумма покупки
            long purchaseSum = priceToOneTicket * count;

            // не хватает денег
            if (userSolvency < purchaseSum) {
                ResponseUtil.putErrorMsg(resultJson, "Сумма покупки <b>(" + purchaseSum
                        + "&tridot;)</b> слишком большая. Вам не хватает денег.");
            } else {
                // установка пользователю нового количества лотерейных билетов
                user.setLotteryTickets(user.getLotteryTickets() + count);
                userRep.updateUser(user);

                // снятие денег со счета
                String descr = String.format("Покупка лотерейных билетов, %s шт.", count);
                long newBalance = userMoney - purchaseSum;

                Transaction tr = new Transaction(descr, new Date(), purchaseSum, TransferT.SPEND, userId, newBalance,
                        ArticleCashFlowT.LOTTERY_TICKETS_BUY);
                trRep.addTransaction(tr);

                // добавить информацию о новом значении баланса, состоятельности, количества билетов
                ResponseUtil.addBalanceData(resultJson, purchaseSum, userMoney, userId, prRep);
                resultJson.put("ticketsValue", user.getLotteryTickets());
            }
        }
        return ResponseUtil.getResponseEntity(resultJson);
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
        int userId = user.getId();
        int userTickets = user.getLotteryTickets();

        // проверки на правильность количества покупаемых билетов
        if (count != 1 && count != 5 && count != 10 && count != 0) {
            ResponseUtil.putErrorMsg(resultJson, "Игра в лото на такое количество билетов недоступна.");
        } else if (userTickets == 0) {
            ResponseUtil.putErrorMsg(resultJson,
                    "У вас нет билетов. Купите или ждите зачисления за очки доминантности.");
        } else {
            int gamesCount = computeGamesCount(userTickets, count); // вычислить количество игр
            Date date = new Date(); // дата игры

            // сгруппированный результат игры на все билеты
            HashMap<LotteryArticles, WinGroup> lotoResult = playLotoAndGetResult(gamesCount, userId);

            // взять сгруппированный результат и добавить выигранное пользователю
            // (сформировать транзакции, добавить имущество, предсказания и пр.)
            for (Map.Entry<LotteryArticles, WinGroup> lotoRes : lotoResult.entrySet()) {
                LotteryArticles article = lotoRes.getKey();
                WinGroup groupedRes = lotoRes.getValue();

                if (article.equals(LotteryArticles.TRIANGLES)) {
                    giveMoneyToUser(groupedRes, userId, date); // начислить пользователю деньги, что он выиграл
                } else if (article.equals(LotteryArticles.PROPERTY_UP) || article.equals(LotteryArticles.CASH_UP)
                        || article.equals(LotteryArticles.LICENSE_2) || article.equals(LotteryArticles.LICENSE_3)
                        || article.equals(LotteryArticles.LICENSE_4)) {

                    handleCommonArticle(groupedRes, userId, article, date);
                } else if (article.equals(LotteryArticles.STALL) || article.equals(LotteryArticles.VILLAGE_SHOP)
                        || article.equals(LotteryArticles.STATIONER_SHOP)) {

                    handlePropertyArticle(groupedRes, userId, article, date);
                } else if (article.equals(LotteryArticles.PREDICTION)) {
                    givePredictionToUser(userId, date);
                } else {
                    ResponseUtil.putErrorMsg(resultJson,
                            "Возникла ошибка! Одна из статьей выигрыша не была обработана!");
                }
            }
            // снять билеты пользователя
            user.setLotteryTickets(userTickets - gamesCount);
            userRep.updateUser(user);
        }
        return ResponseUtil.getResponseEntity(resultJson);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/get-predict", method = RequestMethod.GET, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> getPredict(User user) {

        JSONObject resultJson = new JSONObject();
        int userId = user.getId();
        try {
            LotteryInfo userPrediction = lotteryRep.getUserPrediction(userId);
            resultJson.put("predictId", userPrediction.getCount()); // ID предсказания

            Predictions predict = lotteryRep.getPredictionById(userPrediction.getCount());
            resultJson.put("predictText", predict.getPrediction()); // текст предсказания

            userPrediction.setRemainingAmount(0);
            lotteryRep.updateLotoInfo(userPrediction);
        } catch (NoResultException e) {
            ResponseUtil.putErrorMsg(resultJson, "Работа всезнающего - мыслить, ваша работа - играть в лотерею.");
        }
        return ResponseUtil.getResponseEntity(resultJson);
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
        int userId = user.getId();

        LotteryArticles levelArticle = LotteryArticles.valueOf("LICENSE_" + licLevel);
        if (levelArticle != null) {
            LotteryInfo licenses = lotteryRep.getUserLicenses(userId, levelArticle);
            if (licenses == null) {
                ResponseUtil.putErrorMsg(resultJson,
                        String.format("У вас нет лицензий уровня %s. Нужно больше играть в лотерею.", licLevel));
            } else {
                // назначить лицензию пользователю
                Date licExpire = BuildingController.setNewLicenseToUser(userRep, userId, licLevel);
                resultJson.put("licExpire", licExpire);

                // изменить количество оставшихся лицензий
                licenses.setRemainingAmount(licenses.getRemainingAmount() - 1);
                lotteryRep.updateLotoInfo(licenses);
            }
        }
        return ResponseUtil.getResponseEntity(resultJson);
    }

    /**
     * Вводит в эксплуатацию один из видов коммерческого имущества (в зависимости от стастьи выигрыша в лотерею). Все
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
    private void handlePropertyArticle(WinGroup groupedRes, int userId, LotteryArticles article, Date date) {

        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

        // ввести в эксплуатацию все выигранное имущество
        int countOfProperties = groupedRes.entitiesCount;
        // данные конкретного имущества
        CommBuildData buildData = mapData.get(article.name());
        for (int i = 0; i < countOfProperties; i++) {
            String name = "property-" + new Random().getHash(5); // имя нового имущества
            long price = buildData.getPurchasePriceMin(); // цена нового имущества (всегда минимальная)

            Property prop = new Property(buildData, userId, CityAreasT.GHETTO, new Date(), price, name);
            prRep.addProperty(prop);
        }

        // внести информацию о выигрыше
        String mapDataStr = groupedRes.countMap.entrySet().toString().replace("=", "×");
        String description = "Имущество [Кол-во имущества × Кол-во билетов]. " + mapDataStr;
        LotteryInfo lInfo = new LotteryInfo(userId, description, article, groupedRes.entitiesCount,
                groupedRes.ticketsCount, 0, date);
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
    private void handleCommonArticle(WinGroup groupedRes, int userId, LotteryArticles article, Date date) {
        // внести информацию о выигрыше
        String descrPref = "";
        if (article.equals(LotteryArticles.PROPERTY_UP)) {
            descrPref = "Повышение уровня имущества [Кол-во повышений ×";
        } else if (article.equals(LotteryArticles.CASH_UP)) {
            descrPref = "Повышение уровня кассы [Кол-во повышений ×";
        } else if (article.equals(LotteryArticles.LICENSE_2) || article.equals(LotteryArticles.LICENSE_3)
                || article.equals(LotteryArticles.LICENSE_4)) {

            String artcNm = article.name();// название статьи выигрыша
            int artcNmL = artcNm.length(); // длина названия статьи выигрыша
            descrPref = String.format("Лицензии на строительство, ур.%s [Кол-во лицензий ×",
                    artcNm.substring(artcNmL - 1));
        }
        String mapDataStr = groupedRes.countMap.entrySet().toString().replace("=", "×");
        String description = descrPref + " Кол-во билетов]. " + mapDataStr;
        LotteryInfo lInfo = new LotteryInfo(userId, description, article, groupedRes.entitiesCount,
                groupedRes.ticketsCount, groupedRes.entitiesCount, date);
        lotteryRep.addLotoInfo(lInfo);
    }

    /**
     * Открыть пользователю мудрость или дать предсказание.
     * 
     * @param date
     */
    private void givePredictionToUser(int userId, Date date) {
        List<Integer> allPredIDs = lotteryRep.getAllPredictionIDs(); // все ID предсказаний
        int lastPredId = allPredIDs.get(allPredIDs.size() - 1); // последний ID из предсказаний

        // cгенерить ID предсказания (мудрости)
        Random r = new Random();
        int predictionId = (int) r.generateRandNum(1, lastPredId); // сгенерить ID
        while (!allPredIDs.contains(predictionId)) {
            predictionId = (int) r.generateRandNum(1, lastPredId); // получить ID
        }

        // добавить предсказание
        LotteryInfo lInfo = new LotteryInfo(userId, "Ты получил мудрость от всезнающего. Вникай.",
                LotteryArticles.PREDICTION, predictionId, 1, 1, date);
        lotteryRep.addLotoInfo(lInfo);
    }

    /**
     * Начислить пользователю деньги на счет, а также добавить информацию о выигрыше в таблицу с информацией о лотерее.
     * 
     * @param groupedRes
     *            - сгруппированный результат по выигрышу
     * @param date
     */
    private void giveMoneyToUser(WinGroup groupedRes, int userId, Date date) {
        // добавить транзакцию
        long userMoney = Long.parseLong(trRep.getUserBalance(userId));

        String descr = String.format("Выигрыш в лотерею за %s бил.", groupedRes.ticketsCount);
        int sum = groupedRes.entitiesCount;
        Transaction tr = new Transaction(descr, date, sum, TransferT.PROFIT, userId, userMoney + sum,
                ArticleCashFlowT.LOTTERY_WINNINGS);
        trRep.addTransaction(tr);

        // добавить информацию в таблицу с лотерейными выигрышами
        String mapDataStr = groupedRes.countMap.entrySet().toString().replace("=", "×");
        String description = "Деньги [&tridot; × Кол-во билетов]. " + mapDataStr;
        LotteryInfo lInfo = new LotteryInfo(userId, description, LotteryArticles.TRIANGLES, sum,
                groupedRes.ticketsCount, 0, date);
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
    private int computeGamesCount(int userTickets, int countUserWant) {
        int gamesCount = countUserWant; // количество игр в лото
        // если билетов меньше, чем пользователь хочет потратить на игру ИЛИ он хочет играть на все
        if (userTickets < countUserWant || countUserWant == 0) {
            gamesCount = userTickets; // количество игр будет ограниченно билетами
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
    private HashMap<LotteryArticles, WinGroup> playLotoAndGetResult(int ticketsCount, int userId) {
        // сгруппированные по статье результаты игры в лото
        HashMap<LotteryArticles, WinGroup> groupResult = new HashMap<LotteryArticles, LotteryController.WinGroup>();

        // получить данные со всеми возможными вариантами выигрышей
        TreeMap<Integer, WinningsData> winningsData = fillWinningsData();

        Random r = new Random(); // Рандом для генерации
        boolean userHasPrediction = lotteryRep.isUserHasPrediction(userId); // есть непросмотренные предсказания

        WinningsData tempWinData = null; // временные данные результата одного розыгрыша
        // цикл - это розыгрыш одного билета
        for (int i = 0; i < ticketsCount; i++) {
            // результат розыгрыша одного билета
            int ticRes = (int) r.generateRandNum(Consts.LOWER_LOTTERY_BOUND, Consts.UPPER_LOTTERY_BOUND);

            int flKey = winningsData.floorKey(ticRes); // ближайший нижный ключ в дереве
            tempWinData = winningsData.get(flKey); // данные о выигрыше

            // если выиграл предсказание
            if (tempWinData.getArticle().equals(LotteryArticles.PREDICTION)) {
                // если есть непросмотренные предсказания - не считать этот розыгрыш
                if (userHasPrediction) {
                    i--;
                    continue;
                } else {
                    // иначе - посчитать этот розыгрыш, но теперь у юзера уже есть непросмотренные предсказания
                    userHasPrediction = true;
                }
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
    private void groupTicketResult(WinningsData ticketResultData, HashMap<LotteryArticles, WinGroup> groupResult) {
        // группировка данных и добавление в сгруппированный результат
        LotteryArticles article = ticketResultData.getArticle(); // статья по текущему розыгрышу
        WinGroup wg = groupResult.get(article); // сгруппированные результаты по статье
        if (wg == null) {
            wg = new WinGroup();
        }
        // добавление количества выигранных сущностей (напр. 2 Киоска) к группе
        wg.addToGroup(ticketResultData.getCount());
        groupResult.put(article, wg); // вставка в группировочную Карту
    }

    /**
     * получает данные с базы данных информацию о всех вариантах выигрыша и заполняет ними дерево, которое потом может
     * использоваться для получение данных о выигрыше.
     * 
     * @return - дерево с данными.
     */
    private TreeMap<Integer, WinningsData> fillWinningsData() {
        if (winningsData == null) {
            winningsData = new TreeMap<Integer, WinningsData>();

            List<WinningsData> dbData = lotteryRep.getWinnignsData(); // получение данных с БД
            // заполнение дерево данными из базы
            for (WinningsData winData : dbData) {
                winningsData.put(winData.getRandomNumFrom(), winData);
            }
        }
        return winningsData;
    }
}
