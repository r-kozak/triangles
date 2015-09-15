package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kozak.triangles.enums.LotteryArticles;

/**
 * Статья с информацией о выигрыше в разрезе по всем статьям выигрыша
 * 
 * @author Roman: 13 вер. 2015 17:02:06
 */
@Entity
@Table(name = "LotteryInfo")
public class LotteryInfo {
    @Id
    @GeneratedValue
    private int id;

    // id пользователя, чья информация
    @Column(name = "USER_ID")
    private int userId;

    // описание выигрыша
    @Column(name = "DESCRIPTION")
    private String description;

    // дата операции
    @Column(name = "DATE")
    private Date date = new Date();

    // статья выигрыша (деньги, повышение уровня, предсказание, имущество...)
    @Column(name = "ARTICLE")
    @Enumerated(EnumType.STRING)
    private LotteryArticles article;

    // количество выигранного
    @Column(name = "COUNT")
    private int count;

    // количество потраченных билетов на это
    @Column(name = "TICKET_COUNT")
    private int ticketCount;

    // количество, которое осталось от выигрыша к получению (еще можно взять)
    @Column(name = "REMAINING_AMOUNT")
    private int remainingAmount;

    public LotteryInfo() {
    }

    public LotteryInfo(int userId, String description, LotteryArticles article, int count, int ticketCount,
            int remainingAmount) {

        this.userId = userId;
        this.description = description;
        this.article = article;
        this.count = count;
        this.ticketCount = ticketCount;
        this.remainingAmount = remainingAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public int getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

    public int getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(int remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

}
