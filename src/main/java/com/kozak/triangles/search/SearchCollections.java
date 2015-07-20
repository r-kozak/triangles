package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;

public class SearchCollections {
    // transaction search
    public static List<ArticleCashFlowT> getArticlesCashFlow() {
        List<ArticleCashFlowT> articles = new ArrayList<ArticleCashFlowT>();
        for (ArticleCashFlowT a : ArticleCashFlowT.values()) {
            articles.add(a);
        }
        return articles;
    }

    public static List<TransferT> getTransferTypes() {
        List<TransferT> transfers = new ArrayList<TransferT>();
        transfers.add(TransferT.PROFIT);
        transfers.add(TransferT.SPEND);
        return transfers;
    }

    // commercial property search
    public static List<CommBuildingsT> getCommBuildTypes() {
        List<CommBuildingsT> types = new ArrayList<CommBuildingsT>();
        for (CommBuildingsT a : CommBuildingsT.values()) {
            types.add(a);
        }
        return types;
    }

    // real estate market search
    public static List<CityAreasT> getCityAreas() {
        List<CityAreasT> types = new ArrayList<CityAreasT>();
        for (CityAreasT a : CityAreasT.values()) {
            types.add(a);
        }
        return types;
    }
}
