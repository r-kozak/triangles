package com.kozak.triangles.win_service.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kozak.triangles.entity.LotteryInfo;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.exceptions.WinHandlingException;
import com.kozak.triangles.model.LotteryWinGroupModel;
import com.kozak.triangles.model.WinDataModel;
import com.kozak.triangles.repository.LotteryRep;
import com.kozak.triangles.win_service.WinService;

@Service("LotteryWinHandler")
public class LotteryWinHandler extends AbstractWinHandler implements WinHandler {

    @Autowired
    private LotteryRep lotteryRep;
    @Autowired
    private WinService winService;
    @Autowired
    private Omniscient omniscient;

    private Date date; // дата игры

    @Override
    public void handle(List<WinDataModel> winModels, long userId) throws WinHandlingException {
        date = new Date();

        Map<WinArticle, LotteryWinGroupModel> groupedByArticle = groupWinModelsByArticle(winModels);

        // взять сгруппированный результат и добавить выигранное пользователю
        // (сформировать транзакции, добавить имущество, предсказания и пр.)
        for (Entry<WinArticle, LotteryWinGroupModel> lotoRes : groupedByArticle.entrySet()) {
            WinArticle article = lotoRes.getKey();
            LotteryWinGroupModel groupModel = lotoRes.getValue();

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
                handlePredictionArticle(userId);
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
     * Группирует модели выигрыша по статье выигрыша
     * 
     * @param winModels
     * @return сгруппированный результат
     */
    private Map<WinArticle, LotteryWinGroupModel> groupWinModelsByArticle(List<WinDataModel> winModels) {
        Map<WinArticle, LotteryWinGroupModel> result = new HashMap<>();

        for (WinDataModel winDataModel : winModels) {
            WinArticle article = winDataModel.getArticle();
            LotteryWinGroupModel groupModel = result.get(article); // модель, сгруппированная по статье
            if (groupModel == null) {
                groupModel = new LotteryWinGroupModel();
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
     * @throws WinHandlingException
     *             если участков для выигранного имущества оказалось недостаточно
     */
    private void handlePropertyArticle(LotteryWinGroupModel groupModel, long userId, WinArticle article) throws WinHandlingException {
        // Ввести в эксплуатацию все выигранное имущество.
        int propertiesCount = groupModel.getEntitiesCount();
        createWonProperties(article, propertiesCount, userId);

        // внести информацию о выигрыше
        String description = "[Имущество × Билеты] - " + groupModel.toString();
        int ticketsCount = groupModel.getTicketsCount();
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
    private void handleCommonArticle(LotteryWinGroupModel groupModel, long userId, WinArticle article) {
        // внести информацию о выигрыше
        String description = "";
        if (article.equals(WinArticle.PROPERTY_UP) || article.equals(WinArticle.CASH_UP)) {
            description = "[Повышения × Билеты] - " + groupModel.toString();
        } else if (article.equals(WinArticle.LICENSE_2) || article.equals(WinArticle.LICENSE_3)
                || article.equals(WinArticle.LICENSE_4)) {
            description = "[Лицензии × Билеты] - " + groupModel.toString();
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
    private void handleLandLotArticle(LotteryWinGroupModel groupModel, long userId, WinArticle article) {
        // начислить пользователю участков
        int landLotsCount = groupModel.getEntitiesCount();
        giveUserLandLots(userId, article, landLotsCount);

        // создать информацию об игре в лото
        String description = "[Участки × Билеты] - " + groupModel.toString();
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
    private void handleMoneyArticle(LotteryWinGroupModel groupModel, long userId) {
        // начислить деньги на баланс
        int ticketsCount = groupModel.getTicketsCount();
        int sum = groupModel.getEntitiesCount();
        String descr = String.format("За %s бил.", ticketsCount);
        createTransaction(userId, descr, date, sum, ArticleCashFlow.LOTTERY_WINNINGS);

        // добавить информацию в таблицу с лотерейными выигрышами
        String description = "[&tridot; × Билеты] - " + groupModel.toString();
        LotteryInfo lInfo = new LotteryInfo(userId, description, WinArticle.TRIANGLES, sum, ticketsCount, date);
        lotteryRep.addLotoInfo(lInfo);
    }

    /**
     * Обрабатывает статью Предсказание
     * 
     * @param userId
     */
    private void handlePredictionArticle(long userId) {
        omniscient.generatePredictionToUser(userId); // начислить предсказание пользователю
        // добавить предсказание
        String descr = "Ты получил мудрость от всезнающего. Вникай.";
        LotteryInfo lInfo = new LotteryInfo(userId, descr, WinArticle.PREDICTION, 1, 1, date);
        lotteryRep.addLotoInfo(lInfo);
    }

    /**
     * Добавляет выигранные билеты пользователю
     * 
     * @param groupModel
     * @param userId
     */
    private void handleTicketsArticle(LotteryWinGroupModel groupModel, long userId) {
        int wonCount = groupModel.getEntitiesCount();
        giveUserTickets(userId, wonCount); // начислить пользователю билеты

        // внести информацию о выигрыше
        String description = "[Выиграно бил. × Потрачено бил.] - " + groupModel.toString();
        int ticketsCount = groupModel.getTicketsCount();
        LotteryInfo lInfo = new LotteryInfo(userId, description, WinArticle.LOTTERY_TICKET, wonCount, ticketsCount, date);
        lotteryRep.addLotoInfo(lInfo);
    }

}
