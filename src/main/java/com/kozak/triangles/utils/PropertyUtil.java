package com.kozak.triangles.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.kozak.triangles.data.CityAreasTableData;
import com.kozak.triangles.data.TradeBuildingsTableData;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.entities.TradeBuilding;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TradeBuildingType;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.TransactionRep;

public class PropertyUtil {

    /**
     * начисление прибыли по каждому имуществу пользователя
     * 
     * @param userId
     */
    public static void profitCalculation(long userId, PropertyRep prRep) {
        // получить данные всех торговых строений
        Map<Integer, TradeBuilding> tradeBuildingsData = TradeBuildingsTableData.getTradeBuildingsDataMap();

        // получить всё валидное имущество, nextProfit которого < тек. даты
        List<Property> properties = prRep.getPropertyListForProfit(userId, true);

        // для каждого имущества
        for (Property p : properties) {
            TradeBuilding data = tradeBuildingsData.get(p.getTradeBuildingType().ordinal());

            long cashCap = p.getCashCapacity(); // получить вместимость кассы
            long cash = p.getCash(); // получить тек. значение кассы
            int pLevel = p.getLevel();

            // получить минимальную и максимальную прибыль
            int pMin = data.getProfitMin();
            int pMax = data.getProfitMax();

            // расчитать, за сколько дней нужно насчитать прибыль
            Date d1 = p.getNextProfit();
            Date d2 = new Date();
            int calcC = DateUtils.daysBetween(d1, d2) + 1; // количество начислений

            for (int i = 0; i < calcC; i++) {
                // згенерить и приплюсовать к кассе значение прибыли !!! учитывая район и уровень имущества!!!
                long gen = Math.round(Random.generateRandNum(pMin, pMax) * Ksyusha.computeCoef(pLevel)); // коэф. уровня
                gen += Math.round(gen * CityAreasTableData.getCityAreaPercent(p.getCityArea()) / 100); // процент района
                cash += gen;
            }

            Date dateFrom = d1;// дата, которая станет след.датой прибыли
            int countOfDay = calcC; // сколько дней прибавлять
            // если в кассе больше, чем вместимость - сделать, как вместимость
            if (cash > cashCap) {
                cash = cashCap;
                // касса заполнена. след. прибыль = к сейчас добавить один день
                dateFrom = new Date();
                countOfDay = 1;
            }

            // установить значение кассы и дату nextProfit
            p.setCash(cash);

            p.setNextProfit(DateUtils.addDays(dateFrom, countOfDay));

            prRep.updateProperty(p);// обновить имущество
        }
    }

    /**
     * Совершает оформление покупки б/у имущества
     * 
     * @return имя старого имущества
     */
    public static String buyUsedProperty(RealEstateProposal reProp, Date purchDate, long newOwnerId, PropertyRep prRep,
            TransactionRep trRep) {

        // получить имущество и данные бывшего владельца
        Property p = prRep.getPropertyById(reProp.getUsedId());
        if (p != null) {

            long oldOwnerId = p.getUserId();

            // предотвращение бага, где предыдущий юзер был 0, но предложение на рынке было все равно валидным
            if (oldOwnerId != 0) {
                long oldOwnerBal = Long.parseLong(trRep.getUserBalance(oldOwnerId));
                long price = p.getSellingPrice();
                // сформировать транзакцию продавца на получение денег
                Transaction t = new Transaction("Продажа имущества: " + p.getName(), purchDate, price, TransferType.PROFIT,
                        oldOwnerId, oldOwnerBal + price, ArticleCashFlow.SELL_PROPERTY);
                trRep.addTransaction(t);
            }

            if (newOwnerId == 0) {
                // если новый пользователь никто - удалить имущество
                prRep.removeProperty(p);
            } else {
                // изменить у имущества владельца
                p.setUserId(newOwnerId);
                p.setOnSale(false);
                prRep.updateProperty(p);
            }
            return p.getName();
        }
        return "";
    }

    /**
     * Генерирует новое имя для имущества на основании его типа и района размещения
     * 
     * @param tradeBuildingType
     *            - тип имущества
     * @param cityArea
     *            - район
     * @return строку с именем
     */
    public static String generatePropertyName(TradeBuildingType tradeBuildingType, CityArea cityArea) {
        StringBuffer sb = new StringBuffer();
        sb.append(TradeBuildingsTableData.getShortTradeBuildingName(tradeBuildingType));
        sb.append("_");
        sb.append(CityAreasTableData.getShortCityAreaName(cityArea));
        sb.append("-");
        sb.append(Random.getHash(5));

        return sb.toString();
    }
}
