package com.kozak.triangles.win_service.handler;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.exception.WinHandlingException;
import com.kozak.triangles.model.WinDataModel;
import com.kozak.triangles.win_service.WinService;

@Service("BonusWinHandler")
public class BonusWinHandler extends AbstractWinHandler implements WinHandler {

    @Autowired
    private WinService winService;
    @Autowired
    private Omniscient omniscient;

    private Date date; // дата бонуса

    @Override
    public void handle(List<WinDataModel> winModels, long userId) throws WinHandlingException {
        date = new Date();

        for (WinDataModel winDataModel : winModels) {
            WinArticle article = winDataModel.getArticle();

            if (article.equals(WinArticle.TRIANGLES)) {
                // начислить пользователю деньги
                createTransaction(userId, "-", date, winDataModel.getCount(), ArticleCashFlow.BONUS);
            } else if (article.equals(WinArticle.PROPERTY_UP) || article.equals(WinArticle.CASH_UP)
                    || article.equals(WinArticle.LICENSE_2) || article.equals(WinArticle.LICENSE_3)
                    || article.equals(WinArticle.LICENSE_4)) {

                // добавить на остатки
                winService.addAmountOfWin(userId, article, winDataModel.getCount());
            } else if (article.equals(WinArticle.STALL) || article.equals(WinArticle.VILLAGE_SHOP)
                    || article.equals(WinArticle.STATIONER_SHOP)) {

                // ввести бонусное имущество в эксплуатацию. Количество введенного ограничивается количеством участков
                createWonProperties(winDataModel.getArticle(), winDataModel.getCount(), userId);
            } else if (article.equals(WinArticle.PREDICTION)) {

                // сгенерировать предсказание пользователю
                omniscient.generatePredictionToUser(userId);
            } else if (article.equals(WinArticle.LAND_LOT_GHETTO) || article.equals(WinArticle.LAND_LOT_OUTSKIRTS)
                    || article.equals(WinArticle.LAND_LOT_CHINATOWN) || article.equals(WinArticle.LAND_LOT_CENTER)) {

                // начислить пользователю участков
                giveUserLandLots(userId, article, winDataModel.getCount());
            } else if (article.equals(WinArticle.LOTTERY_TICKET)) {

                // начислить пользователю билеты
                giveUserTickets(userId, winDataModel.getCount());
            } else {
                throw new WinHandlingException(String.format("Возникла ошибка! Статья бонуса %s не была обработана!", article));
            }
        }
    }


}
