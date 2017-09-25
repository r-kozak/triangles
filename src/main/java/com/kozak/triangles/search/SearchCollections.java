package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.enums.TradeBuildingType;
import com.kozak.triangles.enums.TransferType;

public class SearchCollections {
    // transaction search
    public static List<ArticleCashFlow> getArticlesCashFlow() {
        List<ArticleCashFlow> articles = new ArrayList<ArticleCashFlow>();
        for (ArticleCashFlow a : ArticleCashFlow.values()) {
            articles.add(a);
        }
        return articles;
    }

    public static List<TransferType> getTransferTypes() {
        List<TransferType> transfers = new ArrayList<TransferType>();
        transfers.add(TransferType.PROFIT);
        transfers.add(TransferType.SPEND);
        return transfers;
    }

    // trade property search
    public static List<TradeBuildingType> getTradeBuildingsTypes() {
        List<TradeBuildingType> types = new ArrayList<>();
        for (TradeBuildingType a : TradeBuildingType.values()) {
            types.add(a);
        }
        return types;
    }

    // real estate market search
    public static List<CityArea> getCityAreas() {
        List<CityArea> types = new ArrayList<CityArea>();
        for (CityArea a : CityArea.values()) {
            types.add(a);
        }
        return types;
    }

    // lottery search
    public static List<WinArticle> getLotteryArticles() {
        List<WinArticle> types = new ArrayList<WinArticle>();
        for (WinArticle a : WinArticle.values()) {
            types.add(a);
        }
        return types;
    }

    public static Object getRowCount() {
        return Arrays.asList(25, 50, 100);
    }
}
