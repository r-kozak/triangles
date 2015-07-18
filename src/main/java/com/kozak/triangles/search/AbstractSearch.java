package com.kozak.triangles.search;


public class AbstractSearch {

    private boolean needClear;
    private String page = "1";

    public void clear() {
	this.needClear = false;
	page = "1";
    }

    public String getPage() {
	return page;
    }

    public void setPage(String page) {
	this.page = page;
    }

    public boolean isNeedClear() {
	return needClear;
    }

    public void setNeedClear(boolean needClear) {
	this.needClear = needClear;
    }
}
