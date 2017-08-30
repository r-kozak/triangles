package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kozak.triangles.enums.LotteryArticles;

/**
 * Варианты выигрыша
 * 
 */
@Entity
@Table(name = "WinningsData")
public class WinningsData extends BaseEntity {

    // рандомный номер, который выпал - С
    private int randomNumFrom;

    // статья выигрыша (деньги, повышение уровня, предсказание, имущество...)
    @Column(name = "ARTICLE")
    @Enumerated(EnumType.STRING)
    private LotteryArticles article;

    // количество выигранного
    @Column(name = "COUNT")
    private int count;

    public WinningsData() {
    }

    public int getRandomNumFrom() {
        return randomNumFrom;
    }

    public void setRandomNumFrom(int randomNumFrom) {
        this.randomNumFrom = randomNumFrom;
    }

    public LotteryArticles getArticle() {
        return article;
    }

    public void setArticle(LotteryArticles article) {
        this.article = article;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
