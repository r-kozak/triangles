package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;

import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;
import com.kozak.triangles.utils.DateUtils;

/**
 * Имущество
 * 
 * конкретный экземпляр имущества, которым кто-то владеет
 * 
 * cityArea - район города, где находится здание level - уровень здания, влияет на прибыль
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
public class Property {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "cityArea")
    @Enumerated(EnumType.STRING)
    private CityAreasT cityArea;

    @Column(name = "level")
    private int level;

    @Column(name = "depreciation_percent")
    private double depreciationPercent;

    @Column(name = "valid")
    private boolean valid;

    @Column(name = "cash")
    private long cash;

    @Column(name = "cash_level")
    private int cashLevel;

    @Column(name = "cash_capacity")
    private long cashCapacity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Column(name = "initial_cost")
    private long initialCost;

    @Column(name = "selling_price")
    private long sellingPrice;

    @Column(name = "name")
    @Length(min = 3, max = 25, message = "Длина наименования может быть 3-25 символов!")
    private String name;

    @Column(name = "build_type")
    @Enumerated(EnumType.STRING)
    private CommBuildingsT commBuildingType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_profit")
    private Date nextProfit;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_depr")
    private Date nextDepreciation;

    // //////////////////////////////////////////////

    public Property() {
    }

    public Property(CommBuildData data, int userId, CityAreasT cityArea, Date purchaseDate, long initialCost) {
	Date now = new Date();

	this.level = 0;
	this.depreciationPercent = 0;
	this.valid = true;
	this.cash = 0;
	this.cashLevel = 0;
	this.name = "no_name";
	this.nextProfit = DateUtils.getPlusDay(now, 1);
	this.nextDepreciation = DateUtils.getPlusDay(now, 7); // + 7 days

	this.cashCapacity = data.getCashCapacity().get(0);
	this.commBuildingType = data.getCommBuildType();
	this.userId = userId;
	this.cityArea = cityArea;
	this.purchaseDate = purchaseDate;
	this.initialCost = initialCost;
	this.sellingPrice = initialCost;
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

    public CommBuildingsT getCommBuildingType() {
	return commBuildingType;
    }

    public void setCommBuildingType(CommBuildingsT commBuildingType) {
	this.commBuildingType = commBuildingType;
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
}
