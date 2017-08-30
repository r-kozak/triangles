package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.TransferTypes;

public class TransactSearch extends AbstractSearch {

    private String dateFrom = "";
    private String dateTo = "";
    private List<ArticleCashFlow> articles = new ArrayList<ArticleCashFlow>();
    private TransferTypes transfer;
    private String description = "";

    @Override
    public void clear() {
        super.clear();
        this.dateFrom = "";
        this.dateTo = "";
        articles = new ArrayList<ArticleCashFlow>();
        this.transfer = null;
        this.description = "";
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

    public List<ArticleCashFlow> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleCashFlow> articles) {
        this.articles = articles;
    }

    public void setTransfer(TransferTypes transfer) {
        this.transfer = transfer;
    }

    public TransferTypes getTransfer() {
        return transfer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
