package com.kozak.triangles.search;

public abstract class AbstractSearch {

    private boolean needClear;
    private String page = "1";
    private boolean showAll = false;

	public void clear() {
		this.needClear = false;
		this.page = "1";
		this.showAll = false;
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

	public boolean isShowAll() {
		return showAll;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}
}
