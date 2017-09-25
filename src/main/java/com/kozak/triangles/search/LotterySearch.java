package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.WinArticle;

public class LotterySearch extends AbstractSearch {

    private String dateFrom = "";
    private String dateTo = "";
    private List<WinArticle> articles = new ArrayList<WinArticle>();

    public void clear() {
        super.clear();
        this.dateFrom = "";
        this.dateTo = "";
        articles = new ArrayList<WinArticle>();
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

    public List<WinArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<WinArticle> articles) {
        this.articles = articles;
    }
}
