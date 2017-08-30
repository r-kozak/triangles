package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityAreas;
import com.kozak.triangles.enums.LotteryArticles;
import com.kozak.triangles.enums.TradeBuildingsTypes;
import com.kozak.triangles.enums.TransferTypes;

public class SearchCollections {
    // transaction search
    public static List<ArticleCashFlow> getArticlesCashFlow() {
        List<ArticleCashFlow> articles = new ArrayList<ArticleCashFlow>();
        for (ArticleCashFlow a : ArticleCashFlow.values()) {
            articles.add(a);
        }
        return articles;
    }

    public static List<TransferTypes> getTransferTypes() {
        List<TransferTypes> transfers = new ArrayList<TransferTypes>();
        transfers.add(TransferTypes.PROFIT);
        transfers.add(TransferTypes.SPEND);
        return transfers;
    }

    // trade property search
    public static List<TradeBuildingsTypes> getTradeBuildingsTypes() {
        List<TradeBuildingsTypes> types = new ArrayList<>();
        for (TradeBuildingsTypes a : TradeBuildingsTypes.values()) {
            types.add(a);
        }
        return types;
    }

    // real estate market search
    public static List<CityAreas> getCityAreas() {
        List<CityAreas> types = new ArrayList<CityAreas>();
        for (CityAreas a : CityAreas.values()) {
            types.add(a);
        }
        return types;
    }

    // lottery search
    public static List<LotteryArticles> getLotteryArticles() {
        List<LotteryArticles> types = new ArrayList<LotteryArticles>();
        for (LotteryArticles a : LotteryArticles.values()) {
            types.add(a);
        }
        return types;
    }

    public static Object getRowCount() {
        return Arrays.asList(25, 50, 100);
    }
}
