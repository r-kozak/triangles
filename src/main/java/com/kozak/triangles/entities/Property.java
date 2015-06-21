package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.kozak.triangles.enums.CityAreasT;

/**
 * Имущество
 * 
 * конкретный экземпляр имущества, которым кто-то владеет
 * 
 * cityArea - район города, где находится здание
 * level - уровень здания, влияет на прибыль
 * depreciationPercent - процент износа или амортизации, возрастает каждый месяц, при достижении 100 - valid = false
 * cash - количество денег в кассе
 * purchaseDate - дата покупки
 * initialCost - начальная стоимость или цена покупки
 * selling price - остаточная стоимость (цена продажи) на текущий момент
 * valid - если false - не приносит прибыль, не начисляется износ
 * userId - владелец имущества
 * 
 * @author Roman: 12 июня 2015 г. 20:22:47
 */

@Entity
@Table(name = "Property")
public class Property {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "cityArea")
    @Enumerated(EnumType.STRING)
    private CityAreasT cityArea;

    @Column(name = "level")
    private int level;

    @Column(name = "depreciation_percent")
    private int depreciationPercent;

    @Column(name = "valid")
    private boolean valid;

    @Column(name = "cash")
    private int cash;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Column(name = "initial_cost")
    int initialCost;

    @Column(name = "selling_price")
    long sellingPrice;

    // //////////////////////////////////////////////

    public Property() {
    }

    public Property(Integer id, int userId, CityAreasT cityArea, int level, int depreciationPercent, boolean valid,
            int cash, Date purchaseDate, int initialCost, long sellingPrice) {
        this.id = id;
        this.userId = userId;
        this.cityArea = cityArea;
        this.level = level;
        this.depreciationPercent = depreciationPercent;
        this.valid = valid;
        this.cash = cash;
        this.purchaseDate = purchaseDate;
        this.initialCost = initialCost;
        this.sellingPrice = sellingPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public CityAreasT getCityArea() {
        return cityArea;
    }

    public void setCityArea(CityAreasT cityArea) {
        this.cityArea = cityArea;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDepreciationPercent() {
        return depreciationPercent;
    }

    public void setDepreciationPercent(int depreciationPercent) {
        this.depreciationPercent = depreciationPercent;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getInitialCost() {
        return initialCost;
    }

    public void setInitialCost(int initialCost) {
        this.initialCost = initialCost;
    }

    public long getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(long sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

}
