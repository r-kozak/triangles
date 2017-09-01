package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.LotteryArticle;

public class LotterySearch extends AbstractSearch {

    private String dateFrom = "";
    private String dateTo = "";
    private List<LotteryArticle> articles = new ArrayList<LotteryArticle>();

    public void clear() {
        super.clear();
        this.dateFrom = "";
        this.dateTo = "";
        articles = new ArrayList<LotteryArticle>();
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

    public List<LotteryArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<LotteryArticle> articles) {
        this.articles = articles;
    }
}
