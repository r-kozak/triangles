package com.kozak.triangles.data;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.model.WinDataModel;

public class WinsTableData {

    @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
    private static List<WinDataModel> winData = new ArrayList() {
        {
            add(new WinDataModel(0, WinArticle.TRIANGLES, 100));
            add(new WinDataModel(10001, WinArticle.LOTTERY_TICKET, 2));
            add(new WinDataModel(15001, WinArticle.TRIANGLES, 25));
            add(new WinDataModel(25001, WinArticle.TRIANGLES, 100));
            add(new WinDataModel(50001, WinArticle.STALL, 1));
            add(new WinDataModel(51001, WinArticle.VILLAGE_SHOP, 1));
            add(new WinDataModel(51501, WinArticle.TRIANGLES, 400));
            add(new WinDataModel(60001, WinArticle.STATIONER_SHOP, 1));
            add(new WinDataModel(61001, WinArticle.STALL, 1));
            add(new WinDataModel(61201, WinArticle.TRIANGLES, 15));
            add(new WinDataModel(61401, WinArticle.TRIANGLES, 150));
            add(new WinDataModel(65001, WinArticle.TRIANGLES, 20));
            add(new WinDataModel(70001, WinArticle.TRIANGLES, 10));
            add(new WinDataModel(71001, WinArticle.VILLAGE_SHOP, 1));
            add(new WinDataModel(71101, WinArticle.STATIONER_SHOP, 1));
            add(new WinDataModel(71201, WinArticle.TRIANGLES, 5));
            add(new WinDataModel(72001, WinArticle.PROPERTY_UP, 1));
            add(new WinDataModel(72601, WinArticle.PROPERTY_UP, 2));
            add(new WinDataModel(72701, WinArticle.CASH_UP, 2));
            add(new WinDataModel(72801, WinArticle.PROPERTY_UP, 3));
            add(new WinDataModel(72851, WinArticle.CASH_UP, 3));
            add(new WinDataModel(72901, WinArticle.TRIANGLES, 50));
            add(new WinDataModel(72301, WinArticle.CASH_UP, 1));
            add(new WinDataModel(78001, WinArticle.PREDICTION, 1));
            add(new WinDataModel(80001, WinArticle.TRIANGLES, 10));
            add(new WinDataModel(82001, WinArticle.LOTTERY_TICKET, 5));
            add(new WinDataModel(83001, WinArticle.LICENSE_4, 1));
            add(new WinDataModel(83201, WinArticle.TRIANGLES, 500));
            add(new WinDataModel(83501, WinArticle.TRIANGLES, 1000000));
            add(new WinDataModel(83505, WinArticle.TRIANGLES, 500000));
            add(new WinDataModel(83515, WinArticle.TRIANGLES, 250000));
            add(new WinDataModel(83530, WinArticle.TRIANGLES, 100000));
            add(new WinDataModel(83555, WinArticle.TRIANGLES, 25000));
            add(new WinDataModel(83600, WinArticle.TRIANGLES, 10000));
            add(new WinDataModel(83650, WinArticle.TRIANGLES, 5000));
            add(new WinDataModel(84001, WinArticle.TRIANGLES, 2500));
            add(new WinDataModel(85001, WinArticle.TRIANGLES, 250));
            add(new WinDataModel(95001, WinArticle.LICENSE_2, 1));
            add(new WinDataModel(96001, WinArticle.LICENSE_3, 1));
            add(new WinDataModel(96501, WinArticle.LOTTERY_TICKET, 3));
            add(new WinDataModel(96601, WinArticle.LAND_LOT_GHETTO, 1));
            add(new WinDataModel(96611, WinArticle.LAND_LOT_OUTSKIRTS, 1));
            add(new WinDataModel(96621, WinArticle.LAND_LOT_CHINATOWN, 1));
            add(new WinDataModel(96625, WinArticle.LAND_LOT_CENTER, 1));
            add(new WinDataModel(96628, WinArticle.TRIANGLES, 500));
        }
    };

    public static List<WinDataModel> getWinData() {
        return winData;
    }

}
