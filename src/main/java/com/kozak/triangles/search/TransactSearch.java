package com.kozak.triangles.search;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.TransferT;

public class TransactSearch extends AbstractSearch {

    private String dateFrom = "";
    private String dateTo = "";
    private List<ArticleCashFlowT> articles = new ArrayList<ArticleCashFlowT>();
    private TransferT transfer;

    public void clear() {
	setNeedClear(false);
	this.dateFrom = "";
	this.dateTo = "";
	articles = new ArrayList<ArticleCashFlowT>();
	this.transfer = null;
	setPage("1");
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

    public List<ArticleCashFlowT> getArticles() {
	return articles;
    }

    public void setArticles(List<ArticleCashFlowT> articles) {
	this.articles = articles;
    }

    public void setTransfer(TransferT transfer) {
	this.transfer = transfer;
    }

    public TransferT getTransfer() {
	return transfer;
    }
}
