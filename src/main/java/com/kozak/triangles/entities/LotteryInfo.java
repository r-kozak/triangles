package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
    private int description;

    // дата операции
    @Column(name = "DATE")
    private Date date = new Date();

    // статья выигрыша (деньги, повышение уровня, предсказание, имущество...)
    @Column(name = "ARTICLE")
    private LotteryArticles article;

    // количество выигранного
    @Column(name = "COUNT")
    private int count;

    // валидная запись или нет (если юзер забрал выигрыш)
    @Column(name = "VALID")
    private boolean valid;

    public LotteryInfo() {
    }

    public LotteryInfo(int userId, int description, LotteryArticles article, int count) {
        this.userId = userId;
        this.description = description;
        this.article = article;
        this.count = count;
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

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
