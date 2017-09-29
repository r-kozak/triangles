package com.kozak.triangles.model;

import com.kozak.triangles.enums.WinArticle;

public class WinDataModel {

    // рандомный номер, который выпал - С
    private int randomNumFrom;

    // статья выигрыша (деньги, повышение уровня, предсказание, имущество...)
    private WinArticle article;

    // количество выигранного
    private int count;

    public WinDataModel(int randomNumFrom, WinArticle article, int count) {
        this.randomNumFrom = randomNumFrom;
        this.article = article;
        this.count = count;
    }

    public int getRandomNumFrom() {
        return randomNumFrom;
    }

    public void setRandomNumFrom(int randomNumFrom) {
        this.randomNumFrom = randomNumFrom;
    }

    public WinArticle getArticle() {
        return article;
    }

    public void setArticle(WinArticle article) {
        this.article = article;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
