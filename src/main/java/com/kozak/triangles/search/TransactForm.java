package com.kozak.triangles.search;

import java.util.List;

public class TransactForm {

    private boolean profit;
    private boolean spend;
    private String dateFrom;
    private String dateTo;
    private List<String> articles;
    private boolean needClear;

    public void clear() {
	this.profit = false;
	this.spend = false;
	this.dateFrom = "";
	this.dateTo = "";
	if (articles != null) {
	    this.articles.clear();
	}
	this.needClear = false;
    }

    public boolean isNeedClear() {
	return needClear;
    }

    public void setNeedClear(boolean needClear) {
	this.needClear = needClear;
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

    public List<String> getArticles() {
	return articles;
    }

    public void setArticles(List<String> articles) {
	this.articles = articles;
    }

    public boolean isProfit() {
	return profit;
    }

    public void setProfit(boolean profit) {
	this.profit = profit;
    }

    public boolean isSpend() {
	return spend;
    }

    public void setSpend(boolean spend) {
	this.spend = spend;
    }

}
