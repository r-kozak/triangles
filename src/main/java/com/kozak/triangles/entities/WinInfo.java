package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kozak.triangles.enums.WinArticle;

/**
 * Статья с информацией о выигрыше в разрезе по всем статьям выигрыша
 * 
 * @author Roman: 13 вер. 2015 17:02:06
 */
@Entity
@Table(name = "LotteryInfo")
public class WinInfo extends BaseEntity {

    // id пользователя, чья информация
    @Column(name = "USER_ID")
    private long userId;

    // статья выигрыша (деньги, повышение уровня, предсказание, имущество...)
    @Column(name = "ARTICLE")
    @Enumerated(EnumType.STRING)
    private WinArticle article;

    // количество, которое осталось от выигрыша к получению (еще можно взять)
    @Column(name = "REMAINING_AMOUNT")
    private int remainingAmount;

    public WinInfo() {
    }

    public WinInfo(long userId, WinArticle article, int remainingAmount) {

        this.userId = userId;
        this.article = article;
        this.remainingAmount = remainingAmount;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public WinArticle getArticle() {
        return article;
    }

    public void setArticle(WinArticle article) {
        this.article = article;
    }

    public int getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(int remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

}
