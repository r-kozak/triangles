package com.kozak.triangles.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.LotteryInfo;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.entities.WinningsData;
import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.LotteryArticles;
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.search.LotterySearch;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.utils.Consts;
import com.kozak.triangles.utils.Random;
import com.kozak.triangles.utils.ResponseUtil;
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
         * Карта <Возможное количество что можно выиграть, Количество билетов потраченное на это количество>
         * например, статья затрат TRIANGLES
         */
        HashMap<Integer, Integer> countMap = new HashMap<Integer, Integer>();

        public void addToGroup(int entCount) {
            ticketsCount++; // общее колво билетов
            entitiesCount += entCount; // общее колво сущностей по данной статье

            // количество в разрезе возможного количества выигрышей по статье (можно выиграть 2 киоска, а можно 3 шт.)
            Integer c = countMap.get(entCount);
            if (c == null) {
                countMap.put(entitiesCount, 1);
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
        model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId), userDomi);
        model.addAttribute("articles", SearchCollections.getLotteryArticles()); // статьи выигрыша
        model.addAttribute("ticketsCount", user.getLotteryTickets()); // количество лотерейных билетов

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

                Transaction tr = new Transaction(descr, new Date(), purchaseSum, TransferT.SPEND, userId,
                        newBalance, ArticleCashFlowT.LOTTERY_TICKETS_BUY);
                trRep.addTransaction(tr);

                // добавить информацию о новом значении баланса, состоятельности, количества билетов
                ResponseUtil.addBalanceData(resultJson, purchaseSum, userMoney, userId, prRep);
                resultJson.put("ticketsValue", user.getLotteryTickets());
            }
        }
        return ResponseUtil.getResponseEntity(resultJson);
    }

    /**
     * Метод игры в лото. Позволяет сыграть в лотерею на выбранное пользователем количество билетов.
     * Если пользователь выбирает игру на 5 билетов, а у него лишь 3, то будет игра на 3 билета, ошибки при этом не
     * будет. Если он выберет игру на 10, а у него 9, то игра будет на 9.
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

            // сгруппированный результат игры на все билеты
            HashMap<LotteryArticles, WinGroup> lotoResult = playLotoAndGetResult(gamesCount, userId);

            // TODO взять сгруппированный результат и добавить выигранное пользователю
            // (сформировать транзакции, добавить имущество, предсказания и пр.)
            for (Map.Entry<LotteryArticles, WinGroup> lotoRes : lotoResult.entrySet()) {
                LotteryArticles article = lotoRes.getKey();
                WinGroup groupedRes = lotoRes.getValue();

                if (article.equals(LotteryArticles.TRIANGLES)) {
                    giveMoneyToUser(groupedRes, userId); // начислить пользователю деньги, что он выиграл
                } else if (article.equals(LotteryArticles.PROPERTY_UP) ||
                        article.equals(LotteryArticles.CASH_UP) ||
                        article.equals(LotteryArticles.LICENSE_2) ||
                        article.equals(LotteryArticles.LICENSE_3) ||
                        article.equals(LotteryArticles.LICENSE_4)) {

                    handleCommonArticle(groupedRes, userId, article);
                } else if (article.equals(LotteryArticles.STALL) ||
                        article.equals(LotteryArticles.VILLAGE_SHOP) ||
                        article.equals(LotteryArticles.STATIONER_SHOP)) {

                    handlePropertyArticle(groupedRes, userId, article);
                } else if (article.equals(LotteryArticles.PREDICTION)) {
                    givePredictionToUser(userId);
                } else {
                    ResponseUtil.putErrorMsg(resultJson,
                            "Возникла ошибка! Одна из статьей выигрыша не была обработана!");
                }
            }
        }
        return ResponseUtil.getResponseEntity(resultJson);
    }

    private void handlePropertyArticle(WinGroup groupedRes, int userId, LotteryArticles article) {
        // TODO Auto-generated method stub

    }

    private void handleCommonArticle(WinGroup groupedRes, int userId, LotteryArticles article) {
        // TODO Auto-generated method stub

    }

    /**
     * Открыть пользователю мудрость или дать предсказание.
     */
    private void givePredictionToUser(int userId) {
        // TODO Auto-generated method stub
    }

    /**
     * Начислить пользователю деньги на счет, а также добавить информацию о выигрыше в таблицу с информацией о лотерее.
     * 
     * @param groupedRes
     *            - сгруппированный результат по выигрышу
     */
    private void giveMoneyToUser(WinGroup groupedRes, int userId) {
        // добавить транзакцию
        long userMoney = Long.parseLong(trRep.getUserBalance(userId));

        String descr = String.format("Выигрыш в лотерею за %s билетов", groupedRes.ticketsCount);
        int sum = groupedRes.entitiesCount;
        Transaction tr = new Transaction(descr, new Date(), sum, TransferT.PROFIT, userId, userMoney + sum,
                ArticleCashFlowT.LOTTERY_WINNINGS);
        trRep.addTransaction(tr);

        // добавить информацию в таблицу с лотерейными выигрышами
        String description = "Деньги [&tridot; × Кол-во билетов]. ["
                + groupedRes.countMap.entrySet().toString().replace("=", "×") + "]";
        LotteryInfo lInfo = new LotteryInfo(userId, description, LotteryArticles.TRIANGLES, sum,
                groupedRes.ticketsCount, 0);
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
