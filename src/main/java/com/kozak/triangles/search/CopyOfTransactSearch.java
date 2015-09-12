package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.LotteryArticles;

public class CopyOfTransactSearch extends AbstractSearch {

    private String dateFrom = "";
    private String dateTo = "";
    private List<LotteryArticles> articles = new ArrayList<LotteryArticles>();

    public void clear() {
        super.clear();
        this.dateFrom = "";
        this.dateTo = "";
        articles = new ArrayList<LotteryArticles>();
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public List<LotteryArticles> getArticles() {
        return articles;
    }

    public void setArticles(List<LotteryArticles> articles) {
        this.articles = articles;
    }
}
