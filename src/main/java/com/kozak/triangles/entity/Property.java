package com.kozak.triangles.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TradeBuildingType;
import com.kozak.triangles.model.TradeBuilding;
import com.kozak.triangles.util.DateUtils;

/**
 * Имущество
 * 
 * конкретный экземпляр имущества, которым кто-то владеет
 * 
 * cityArea - район города, где находится здание
 * level - уровень здания, влияет на прибыль
 * depreciationPercent - процент износа или амортизации, возрастает каждый месяц, при достижении 100 - valid = false
 * cash - количество денег в кассе
 * cashLevel - уровень кассы cashCapacity - текущая вместимость кассы
 * purchaseDate - дата покупки
 * initialCost - начальная стоимость или цена покупки
 * selling price - остаточная стоимость (цена продажи) на текущий момент
 * valid - если false - не приносит прибыль, не начисляется износ
 * userId - владелец имущества name - наименование, может меняться пользователем
 * nextProfit - дата следующего начисления прибыли
 * nextDepreciation - следующее начисление амортизации
 * 
 * @author Roman: 12 июня 2015 г. 20:22:47
 */

@Entity
@Table(name = "Property")
public class Property extends BaseEntity implements JSONAware {

    @Column(name = "USER_ID")
    private long userId;

    @Column(name = "CITY_AREA")
    @Enumerated(EnumType.STRING)
    private CityArea cityArea;

    @Column(name = "LEVEL")
    private int level;

    @Column(name = "DEPRECIATION_PERCENT")
    private double depreciationPercent;

    @Column(name = "VALID")
    private boolean valid;

    @Column(name = "CASH")
    private long cash;

    @Column(name = "CASH_LEVEL")
    private int cashLevel;

    @Column(name = "CASH_CAPACITY")
    private long cashCapacity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PURCHASE_DATE")
    private Date purchaseDate;

    @Column(name = "INITIAL_COST")
    private long initialCost;

    @Column(name = "SELLING_PRICE")
    private long sellingPrice;

    @Column(name = "NAME")
    private String name;

    @Column(name = "BUILD_TYPE")
    @Enumerated(EnumType.STRING)
    private TradeBuildingType tradeBuildingType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NEXT_PROFIT")
    private Date nextProfit;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NEXT_DEPR")
    private Date nextDepreciation;

    // признак - на продаже имущество или нет
    @Column(name = "ON_SALE")
    private boolean onSale;

    // //////////////////////////////////////////////

    public Property() {
    }

    public Property(TradeBuilding data, long userId, CityArea cityArea, Date purchaseDate, long initialCost, String name) {

        Date now = new Date();

        this.level = 0;
        this.depreciationPercent = 0;
        this.valid = true;
        this.cash = 0;
        this.cashLevel = 0;
        this.name = name;
        this.nextProfit = DateUtils.addDays(now, 1);
        this.nextDepreciation = DateUtils.addDays(now, 7); // + 7 days

        this.cashCapacity = data.getCashCapacity().get(0);
        this.tradeBuildingType = data.getTradeBuildingType();
        this.userId = userId;
        this.cityArea = cityArea;
        this.purchaseDate = purchaseDate;
        this.initialCost = initialCost;
        this.sellingPrice = initialCost; // тоже initialCost
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public CityArea getCityArea() {
        return cityArea;
    }

    public void setCityArea(CityArea cityArea) {
        this.cityArea = cityArea;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getDepreciationPercent() {
        return depreciationPercent;
    }

    public void setDepreciationPercent(double depreciationPercent) {
        this.depreciationPercent = depreciationPercent;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public long getCash() {
        return cash;
    }

    public void setCash(long cash) {
        this.cash = cash;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public long getInitialCost() {
        return initialCost;
    }

    public void setInitialCost(long initialCost) {
        this.initialCost = initialCost;
    }

    public long getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(long sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TradeBuildingType getTradeBuildingType() {
        return tradeBuildingType;
    }

    public void setTradeBuildingType(TradeBuildingType tradeBuildingType) {
        this.tradeBuildingType = tradeBuildingType;
    }

    public int getCashLevel() {
        return cashLevel;
    }

    public void setCashLevel(int cashLevel) {
        this.cashLevel = cashLevel;
    }

    public long getCashCapacity() {
        return cashCapacity;
    }

    public void setCashCapacity(long cashCapacity) {
        this.cashCapacity = cashCapacity;
    }

    public Date getNextProfit() {
        return nextProfit;
    }

    public void setNextProfit(Date nextProfit) {
        this.nextProfit = nextProfit;
    }

    public Date getNextDepreciation() {
        return nextDepreciation;
    }

    public void setNextDepreciation(Date nextDepreciation) {
        this.nextDepreciation = nextDepreciation;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("id", getId());
        obj.put("name", name);
        obj.put("level", level);
        obj.put("cashLevel", cashLevel);
        return obj.toString();
    }
}
