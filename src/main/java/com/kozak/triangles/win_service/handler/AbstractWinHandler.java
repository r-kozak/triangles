package com.kozak.triangles.win_service.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kozak.triangles.data.TradeBuildingsTableData;
import com.kozak.triangles.entity.Property;
import com.kozak.triangles.entity.Transaction;
import com.kozak.triangles.entity.User;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.exceptions.WinHandlingException;
import com.kozak.triangles.model.TradeBuilding;
import com.kozak.triangles.repository.PropertyRep;
import com.kozak.triangles.repository.TransactionRep;
import com.kozak.triangles.repository.UserRep;
import com.kozak.triangles.service.LandLotService;
import com.kozak.triangles.util.PropertyUtil;

@Component
public abstract class AbstractWinHandler {

    @Autowired
    protected TransactionRep trRep;
    @Autowired
    protected PropertyRep prRep;
    @Autowired
    protected LandLotService landLotService;
    @Autowired
    private UserRep userRep;

    protected void createTransaction(long userId, String description, Date date, long sum, ArticleCashFlow article) {
        // добавить транзакцию
        long userMoney = Long.parseLong(trRep.getUserBalance(userId));
        Transaction tr = new Transaction(description, date, sum, TransferType.PROFIT, userId, userMoney + sum, article);
        trRep.addTransaction(tr);
    }

    /**
     * Создает новое имущество в ГЕТТО (только там можно выиграть имущество), генерирует для него имя и цену, вводит его в
     * эксплуатацию.
     * При этом, проверяет количество участков в ГЕТТО. Вводит в эксплуатацию столько имущества, сколько есть участков.
     * 
     * @param article
     *            статья, указывающая тип выигранного имущества
     * @param propertiesCount
     *            количество имущества, которое нужно создать и ввести в эксплуатацию
     * @param userId
     * @throws WinHandlingException
     *             если участков для выигранного имущества оказалось недостаточно
     */
    protected void createWonProperties(WinArticle article, int propertiesCount, long userId) throws WinHandlingException {
        // проверить, есть ли участки для введения имущества в эксплуатацию
        long landLotsCount = landLotService.getAvailableLandLotsCount(userId, CityArea.GHETTO);

        WinHandlingException exception = null;
        if (landLotsCount < propertiesCount) {
            exception = new WinHandlingException(String.format(
                    "Не хватило участков, чтобы ввести имущество в эксплуатацию. Введенно в эксплуатацию: %s/%s шт.",
                    landLotsCount, propertiesCount));

            propertiesCount = (int) landLotsCount;
        }

        // Данные конкретного имущества
        TradeBuilding buildingData = TradeBuildingsTableData.getTradeBuildingDataByName(article.name());

        // Ввести в эксплуатацию столько, сколько доступно участков.
        for (int i = 0; i < propertiesCount; i++) {
            // генерация имя для нового имущества
            String name = PropertyUtil.generatePropertyName(buildingData.getTradeBuildingType(), CityArea.GHETTO);
            long price = buildingData.getPurchasePriceMin(); // цена нового имущества (всегда минимальная)

            Property prop = new Property(buildingData, userId, CityArea.GHETTO, new Date(), price, name);
            prRep.addProperty(prop);
        }

        // если участков было недостаточно, то бросить исключение с информацией, сколько ожидалось и сколько было введено в
        // эксплуатацию
        if (exception != null) {
            throw exception;
        }
    }

    /**
     * Начисляет пользователю земельные участки в разных районах города
     * 
     * @param userId
     * @param article
     * @param landLotsCount
     */
    protected void giveUserLandLots(long userId, WinArticle article, int landLotsCount) {
        landLotService.addLandLots(userId, extractCityAreaFromArticle(article), landLotsCount);
    }

    /**
     * Начисляет пользователю лотерейные билеты
     * 
     * @param userId
     * @param ticketsToAdd
     */
    protected void giveUserTickets(long userId, int ticketsToAdd) {
        User user = userRep.find(userId);
        user.setLotteryTickets(user.getLotteryTickets() + ticketsToAdd);
        userRep.updateUser(user);
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
