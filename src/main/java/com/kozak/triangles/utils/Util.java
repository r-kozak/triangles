package com.kozak.triangles.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.springframework.ui.Model;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.interfaces.Consts;
import com.kozak.triangles.repositories.BuildingDataRep;
import com.kozak.triangles.repositories.PropertyRep;

public class Util {
    public static Model addBalanceToModel(Model model, String userBalance) {
        String balance = moneyFormat(Long.valueOf(userBalance));
        return model.addAttribute("balance", balance);
    }

    public static String moneyFormat(Number sum) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("ru"));
        String result = formatter.format(sum);
        return result;
    }

    public static Double numberRound(double numToRound, int precision) {
        double result = new BigDecimal(numToRound).setScale(precision, RoundingMode.HALF_UP).doubleValue();
        return result;
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
     * 
     * @param i
     *            - day number
     * @return sum of day bonus by day number
     */
    public static int getBonusSum(int i) {
        switch (i) {
        case 1:
            return Consts.FIRST_DAY;
        case 2:
            return Consts.SECOND_DAY;
        case 3:
            return Consts.THIRD_DAY;
        case 4:
            return Consts.FOURTH_DAY;
        case 5:
            return Consts.FIFTH_DAY;
        default:
            return 0;
        }
    }

    /**
     * 
     * @param level
     *            след. уровень имущества, кассы и т.д.
     * @return универсальный коэфициент
     */
    public static double getUniversalK(int level) {
        switch (level) {
        case 1:
            return Consts.UNIVERS_K_L1;
        case 2:
            return Consts.UNIVERS_K_L2;
        case 3:
            return Consts.UNIVERS_K_L3;
        case 4:
            return Consts.UNIVERS_K_L4;
        case 5:
            return Consts.UNIVERS_K_L5;
        default:
            return 1;
        }
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
        ProposalGenerator pg = new ProposalGenerator();

        // для каждого имущества
        for (Property p : properties) {
            CommBuildData data = mapData.get(p.getCommBuildingType().name());

            long cashCap = p.getCashCapacity(); // получить вместимость кассы
            long cash = p.getCash(); // получить тек. значение кассы

            // получить минимальную и максимальную прибыль
            int pMin = data.getProfitMin();
            int pMax = data.getProfitMax();

            // расчитать, за сколько дней нужно насчитать прибыль
            Date d1 = p.getNextProfit();
            Date d2 = new Date();
            int calcC = DateUtils.daysBetween(d1, d2) + 1; // количество начислений

            for (int i = 0; i < calcC; i++) {
                // згенерить и приплюсовать к кассе значение прибыли !!! учитывая район !!!
                long gen = pg.generateRandNum(pMin, pMax);
                gen += gen * Util.getAreaPercent(p.getCityArea()) / 100;
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

            p.setNextProfit(DateUtils.getPlusDay(dateFrom, countOfDay));

            prRep.updateProperty(p);// обновить имущество
        }
    }

    public static String getHash(int length) {
        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }
}
