package com.kozak.triangles.entity;

import java.util.Date;

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
public class LotteryInfo extends BaseEntity {

    // id пользователя, чья информация
    @Column(name = "USER_ID")
    private long userId;

    // дата операции
    @Column(name = "DATE")
    private Date date;

    // статья выигрыша (деньги, повышение уровня, предсказание, имущество...)
    @Column(name = "ARTICLE")
    @Enumerated(EnumType.STRING)
    private WinArticle article;

    // количество выигранного ИЛИ id предсказания (мудрости)
    @Column(name = "COUNT")
    private int count;

    // количество потраченных билетов на это
    @Column(name = "TICKET_COUNT")
    private int ticketCount;

    public LotteryInfo() {
    }

    public LotteryInfo(long userId, WinArticle article, int count, int ticketCount, Date date) {
        this.userId = userId;
        this.article = article;
        this.count = count;
        this.ticketCount = ticketCount;
        this.date = date;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public int getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

}
