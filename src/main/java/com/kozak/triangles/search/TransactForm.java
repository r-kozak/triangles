package com.kozak.triangles.search;

import java.util.List;

public class TransactForm {

    private String dateFrom;
    private String dateTo;
    private List<String> articles;
    private String transfer;
    private boolean needClear;
    private String page = "1";

    public String getPage() {
	return page;
    }

    public void setPage(String page) {
	this.page = page;
    }

    public void clear() {
	this.dateFrom = "";
	this.dateTo = "";
	if (articles != null) {
	    this.articles.clear();
	}
	this.needClear = false;
	this.transfer = "NONE";
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

    public void setTransfer(String transfer) {
	this.transfer = transfer;
    }

    public String getTransfer() {
	return transfer;
    }
}
