package com.kozak.triangles.win_service.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kozak.triangles.data.PredictionsTableData;
import com.kozak.triangles.data.TradeBuildingsTableData;
import com.kozak.triangles.entity.LotteryInfo;
import com.kozak.triangles.entity.Property;
import com.kozak.triangles.entity.Transaction;
import com.kozak.triangles.entity.User;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.exceptions.WinHandlingException;
import com.kozak.triangles.model.TradeBuilding;
import com.kozak.triangles.model.WinDataModel;
import com.kozak.triangles.model.WinGroupModel;
import com.kozak.triangles.repository.LotteryRep;
import com.kozak.triangles.repository.PropertyRep;
import com.kozak.triangles.repository.TransactionRep;
import com.kozak.triangles.repository.UserRep;
import com.kozak.triangles.service.LandLotService;
import com.kozak.triangles.util.PropertyUtil;
import com.kozak.triangles.util.Random;
import com.kozak.triangles.win_service.WinService;

@Service("LotteryWinHandler")
public class LotteryWinHandler implements WinHandler {

    @Autowired
    private PropertyRep prRep;
    @Autowired
    private WinService winService;
    @Autowired
    private LotteryRep lotteryRep;
    @Autowired
    private LandLotService landLotService;
    @Autowired
    private TransactionRep trRep;
    @Autowired
    private UserRep userRep;

    private Date date; // дата игры

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

    @Override
    public void handle(List<WinDataModel> winModels, long userId) throws WinHandlingException {
        date = new Date();

        Map<WinArticle, WinGroupModel> groupedByArticle = groupWinModelsByArticle(winModels);

        // взять сгруппированный результат и добавить выигранное пользователю
        // (сформировать транзакции, добавить имущество, предсказания и пр.)
        for (Entry<WinArticle, WinGroupModel> lotoRes : groupedByArticle.entrySet()) {
            WinArticle article = lotoRes.getKey();
            WinGroupModel groupModel = lotoRes.getValue();

            if (article.equals(WinArticle.TRIANGLES)) {
                handleMoneyArticle(groupModel, userId); // начислить пользователю деньги, что он выиграл
            } else if (article.equals(WinArticle.PROPERTY_UP) || article.equals(WinArticle.CASH_UP)
                    || article.equals(WinArticle.LICENSE_2) || article.equals(WinArticle.LICENSE_3)
                    || article.equals(WinArticle.LICENSE_4)) {

                handleCommonArticle(groupModel, userId, article);
            } else if (article.equals(WinArticle.STALL) || article.equals(WinArticle.VILLAGE_SHOP)
                    || article.equals(WinArticle.STATIONER_SHOP)) {

                handlePropertyArticle(groupModel, userId, article);
            } else if (article.equals(WinArticle.PREDICTION)) {
                Omniscient.givePredictionToUser(userId, date, lotteryRep, winService);
            } else if (article.equals(WinArticle.LAND_LOT_GHETTO) || article.equals(WinArticle.LAND_LOT_OUTSKIRTS)
                    || article.equals(WinArticle.LAND_LOT_CHINATOWN) || article.equals(WinArticle.LAND_LOT_CENTER)) {

                handleLandLotArticle(groupModel, userId, article);
            } else if (article.equals(WinArticle.LOTTERY_TICKET)) {
                handleTicketsArticle(groupModel, userId);
            } else {
                throw new WinHandlingException(String.format("Возникла ошибка! Статья выигрыша %s не была обработана!", article));
            }
        }
    }

    /**
     * Добавляет выигранные билеты пользователю
     * 
     * @param groupModel
     * @param userId
     */
    private void handleTicketsArticle(WinGroupModel groupModel, long userId) {
        int wonCount = groupModel.getEntitiesCount();
        int ticketsCount = groupModel.getTicketsCount();

        User user = userRep.find(userId);
        user.setLotteryTickets(user.getLotteryTickets() + wonCount);
        userRep.updateUser(user);

        // внести информацию о выигрыше
        String description = "[Выиграно билетов × Кол-во билетов]. " + groupModel.toString();
        LotteryInfo lInfo = new LotteryInfo(userId, description, WinArticle.LOTTERY_TICKET, wonCount, ticketsCount, date);
        lotteryRep.addLotoInfo(lInfo);
    }

    /**
     * Группирует модели выигрыша по статье выигрыша
     * 
     * @param winModels
     * @return сгруппированный результат
     */
    private Map<WinArticle, WinGroupModel> groupWinModelsByArticle(List<WinDataModel> winModels) {
        Map<WinArticle, WinGroupModel> result = new HashMap<>();

        for (WinDataModel winDataModel : winModels) {
            WinArticle article = winDataModel.getArticle();
            WinGroupModel groupModel = result.get(article); // модель, сгруппированная по статье
            if (groupModel == null) {
                groupModel = new WinGroupModel();
            }
            // добавление количества выигранных сущностей (напр. 2 Киоска) к группе
            groupModel.setTicketsCount(groupModel.getTicketsCount() + 1); // увеличиваем на один билет
            groupModel.setEntitiesCount(groupModel.getEntitiesCount() + winDataModel.getCount());

            result.put(article, groupModel);
        }
        return result;
    }

    /**
     * Вводит в эксплуатацию один из видов торгового имущества (в зависимости от стастьи выигрыша в лотерею). Все
     * имущество, которое выигрывается в лотерею, находится в районе Гетто, имеет минимальную цену для данного вида
     * имущества.
     * 
     * @param groupModel
     *            - сгруппированный результат по статье
     * @param userId
     * @param article
     *            - статья выигрыша (STALL, VILLAGE_SHOP, ...)
     * @param date
     */
    private void handlePropertyArticle(WinGroupModel groupModel, long userId, WinArticle article) {
        // ввести в эксплуатацию все выигранное имущество
        int propertiesCount = groupModel.getEntitiesCount();
        int ticketsCount = groupModel.getTicketsCount();
        // данные конкретного имущества
        TradeBuilding buildingData = TradeBuildingsTableData.getTradeBuildingDataByName(article.name());

        for (int i = 0; i < propertiesCount; i++) {
            // генерация имя для нового имущества
            String name = PropertyUtil.generatePropertyName(buildingData.getTradeBuildingType(), CityArea.GHETTO);
            long price = buildingData.getPurchasePriceMin(); // цена нового имущества (всегда минимальная)

            Property prop = new Property(buildingData, userId, CityArea.GHETTO, new Date(), price, name);
            prRep.addProperty(prop);
        }
        // внести информацию о выигрыше
        String description = "Имущество [Кол-во имущества × Кол-во билетов]. " + groupModel.toString();
        LotteryInfo lInfo = new LotteryInfo(userId, description, article, propertiesCount, ticketsCount, date);
        lotteryRep.addLotoInfo(lInfo);
    }

    /**
     * Обработка общих статтей выигрыша, таких как повышение уровня имущества или выигрыш лицензий на строительство. Это
     * те статьи, выигрыш которых зачисляется не сразу, а накапливается и пользователь может забрать выигрыш позже.
     * 
     * @param groupModel
     *            - сгруппированный результат по статье
     * @param userId
     * @param article
     *            - сама статья выигрыша
     * @param date
     */
    private void handleCommonArticle(WinGroupModel groupModel, long userId, WinArticle article) {
        // внести информацию о выигрыше
        String description = "";
        if (article.equals(WinArticle.PROPERTY_UP) || article.equals(WinArticle.CASH_UP)) {
            description = "[Кол-во повышений × Кол-во билетов]. " + groupModel.toString();
        } else if (article.equals(WinArticle.LICENSE_2) || article.equals(WinArticle.LICENSE_3)
                || article.equals(WinArticle.LICENSE_4)) {
            description = "[Кол-во лицензий × Кол-во билетов]. " + groupModel.toString();
        }
        int winAmount = groupModel.getEntitiesCount();
        LotteryInfo lInfo = new LotteryInfo(userId, description, article, winAmount, groupModel.getTicketsCount(), date);
        lotteryRep.addLotoInfo(lInfo);
        winService.addAmountOfWin(userId, article, winAmount); // добавить на остатки
    }

    /**
     * Обрабатывает статью выигрыша земельного участка. Добавляет конкретному пользователю выигранное количество участков в
     * конкретном районе, взависимости от лотерейной статьи.
     * 
     * @param groupModel
     *            - сгруппированный результат по статье
     * @param userId
     * @param article
     *            - сама статья выигрыша
     * @param date
     */
    private void handleLandLotArticle(WinGroupModel groupModel, long userId, WinArticle article) {
        // получить количество выигранных участков и начислить их пользователю
        int landLotsCount = groupModel.getEntitiesCount();
        landLotService.addLandLots(userId, extractCityAreaFromArticle(article), landLotsCount);

        // создать информацию об игре в лото
        String description = "[Кол-во участков × Кол-во билетов]. " + groupModel.toString();
        LotteryInfo lInfo = new LotteryInfo(userId, description, article, landLotsCount, groupModel.getTicketsCount(), date);
        lotteryRep.addLotoInfo(lInfo);
    }

    /**
     * Начислить пользователю деньги на счет, а также добавить информацию о выигрыше в таблицу с информацией о лотерее.
     * 
     * @param groupModel
     *            - сгруппированный результат по выигрышу
     * @param date
     */
    private void handleMoneyArticle(WinGroupModel groupModel, long userId) {
        // добавить транзакцию
        long userMoney = Long.parseLong(trRep.getUserBalance(userId));

        int ticketsCount = groupModel.getTicketsCount();
        int sum = groupModel.getEntitiesCount();
        String descr = String.format("За %s бил.", ticketsCount);
        Transaction tr = new Transaction(descr, date, sum, TransferType.PROFIT, userId, userMoney + sum,
                ArticleCashFlow.LOTTERY_WINNINGS);
        trRep.addTransaction(tr);

        // добавить информацию в таблицу с лотерейными выигрышами
        String description = "[&tridot; × Кол-во билетов]. " + groupModel.toString();
        LotteryInfo lInfo = new LotteryInfo(userId, description, WinArticle.TRIANGLES, sum, ticketsCount, date);
        lotteryRep.addLotoInfo(lInfo);
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
