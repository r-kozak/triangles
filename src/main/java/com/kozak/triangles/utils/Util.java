package com.kozak.triangles.utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.springframework.ui.Model;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.repositories.BuildingDataRep;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.TransactionRep;

public class Util {
    public static Model addMoneyInfoToModel(Model model, String userBalance, Long userSolvency, int userDomi) {
        String balance = moneyFormat(Long.valueOf(userBalance));
        String solvency = moneyFormat(userSolvency);
        String domi = moneyFormat(userDomi);
        model.addAttribute("solvency", solvency);
        model.addAttribute("balance", balance);
        model.addAttribute("domi", domi);
        return model;
    }

    public static String moneyFormat(Number sum) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("ru"));
        String result = formatter.format(sum);
        return result;
    }

    public static double numberRound(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static int getAreaPercent(CityAreasT cityArea) {
        switch (cityArea.ordinal()) {
        case 0:
            return Consts.GHETTO_P;
        case 1:
            return Consts.OUTSKIRTS_P;
        case 2:
            return Consts.CHINA_P;
        case 3:
            return Consts.CENTER_P;
        }
        return 0;
    }

    /**
     * начисление прибыли по каждому имуществу пользователя
     * 
     * @param userId
     */
    public static void profitCalculation(int userId, BuildingDataRep bdRep, PropertyRep prRep) {
        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(bdRep);

        // получить всё валидное имущество, nextProfit которого < тек. даты
        ArrayList<Property> properties = (ArrayList<Property>) prRep.getPropertyListForProfit(userId, true);
        // генератор
        Random rand = new Random();

        // для каждого имущества
        for (Property p : properties) {
            CommBuildData data = mapData.get(p.getCommBuildingType().name());

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
                long gen = Math.round(rand.generateRandNum(pMin, pMax) * Consts.UNIVERS_K[pLevel]); // коэф. уровня
                gen += Math.round(gen * Util.getAreaPercent(p.getCityArea()) / 100); // процент района
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

    public static long getSolvency(TransactionRep trRep, PropertyRep prRep, int userId) {
        long userMoney = Long.parseLong(trRep.getUserBalance(userId));
        long sellSum = prRep.getSellingSumAllPropByUser(userId) / 2;// (ост. стоим. всего имущества юзера / 2)
        return userMoney + sellSum;
    }

    public static Long getSolvency(String userMoney, PropertyRep prRep, int userId) {
        long sellSum = prRep.getSellingSumAllPropByUser(userId) / 2;// (ост. стоим. всего имущества юзера / 2)
        return Long.valueOf(userMoney) + sellSum;
    }

    public static void buyUsedProperty(RealEstateProposal reProp, Date purchDate, int newOwnerId, String newPropName,
            PropertyRep prRep, TransactionRep trRep) {

        // получить имущество и данные бывшего владельца
        Property p = prRep.getPropertyById(reProp.getUsedId());
        int oldOwnerId = p.getUserId();
        long oldOwnerBal = Long.parseLong(trRep.getUserBalance(oldOwnerId));
        long price = p.getSellingPrice();

        // сформировать транзакцию продавца на получение денег
        Transaction t = new Transaction("Продажа имущества: " + p.getName(), purchDate, price, TransferT.PROFIT,
                oldOwnerId, oldOwnerBal + price, ArticleCashFlowT.SELL_PROPERTY);
        trRep.addTransaction(t);

        // изменить у имущества имя и владельца
        p.setUserId(newOwnerId);
        p.setName(newPropName);
        p.setOnSale(false);
        prRep.updateProperty(p);
    }
}
