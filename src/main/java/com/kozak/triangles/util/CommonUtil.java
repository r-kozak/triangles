package com.kozak.triangles.util;

import java.text.NumberFormat;
import java.util.Locale;

import com.kozak.triangles.repository.PropertyRep;
import com.kozak.triangles.repository.TransactionRep;

public class CommonUtil {

    public static String moneyFormat(Number sum) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("ru"));
        String result = formatter.format(sum);
        return result;
    }

    public static double numberRound(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static Long getSolvency(TransactionRep trRep, PropertyRep prRep, long userId) {
        long userMoney = Long.parseLong(trRep.getUserBalance(userId));
        long sellSum = prRep.getSellingSumAllPropByUser(userId) / 2;// (ост. стоим. всего имущества юзера / 2)
        return userMoney + sellSum;
    }

    public static Long getSolvency(String userMoney, PropertyRep prRep, long userId) {
        long sellSum = prRep.getSellingSumAllPropByUser(userId) / 2;// (ост. стоим. всего имущества юзера / 2)
        return Long.valueOf(userMoney) + sellSum;
    }

}
