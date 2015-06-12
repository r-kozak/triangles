package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kozak.triangles.enums.CityAreas;

/**
 * Киоск
 * 
 * paybackPeriod - период окупаемости {min, max}, недель
 * purchasePrice - цена покупки
 * usefulLife - срок полезного использования, в 2 раза больше периода окупаемости
 * profit - прибыль в день {min, max}
 * cashCapacity - вместимость кассы на разных уровнях, {на ур 0, на ур 1, ...}
 * buildTime - время постройки (при скорости 100%), недель
 * depreciationRange - процент, на который может возрости процент износа
 * 
 * cityArea - район города, где находится здание
 * level - уровень здания, влияет на прибыль
 * depreciationPercent - процент износа или амортизации, возрастает каждый месяц, при достижении 100 - valid = false
 * cash - количество денег в кассе
 * valid - если false - не приносит прибыль, не начисляется износ
 * 
 * 
 * @author Roman: 12 июня 2015 г. 20:22:47
 */

@Entity
@Table(name = "Stall")
public class Stall {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "cityArea")
	@Enumerated(EnumType.STRING)
	private CityAreas cityArea;

	@Column(name = "level")
	private int level;

	@Column(name = "depreciation_percent")
	private int depreciationPercent;

	@Column(name = "valid")
	private boolean valid;

	@Column(name = "cash")
	private int cash;

	// //////////////////////////////////////////////
	@Transient
	private int[] paybackPeriod = { 3, 6 };

	@Transient
	private int purchasePrice = 5000;

	@Transient
	private int usefulLife = paybackPeriod[1] * 2;

	@Transient
	private int[] profit = { purchasePrice / (paybackPeriod[1] * 7), purchasePrice / (paybackPeriod[0] * 7) };

	@Transient
	private int[] cashCapacity = { profit[1], profit[1] * 2, profit[1] * 3, profit[1] * 5, profit[1] * 10 };

	@Transient
	private int buildTime = 1;

	public Stall(CityAreas cityArea, int level, int depreciationPercent, boolean valid, int cash) {
		this.cityArea = cityArea;
		this.level = level;
		this.depreciationPercent = depreciationPercent;
		this.valid = valid;
		this.cash = cash;
	}

	public Stall() {
	}

	public int[] getPaybackPeriod() {
		return paybackPeriod;
	}

	public void setPaybackPeriod(int[] paybackPeriod) {
		this.paybackPeriod = paybackPeriod;
	}

	public int getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(int purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public int getUsefulLife() {
		return usefulLife;
	}

	public void setUsefulLife(int usefulLife) {
		this.usefulLife = usefulLife;
	}

	public int[] getProfit() {
		return profit;
	}

	public void setProfit(int[] profit) {
		this.profit = profit;
	}

	public int[] getCashCapacity() {
		return cashCapacity;
	}

	public void setCashCapacity(int[] cashCapacity) {
		this.cashCapacity = cashCapacity;
	}

	public int getBuildTime() {
		return buildTime;
	}

	public void setBuildTime(int buildTime) {
		this.buildTime = buildTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CityAreas getCityArea() {
		return cityArea;
	}

	public void setCityArea(CityAreas cityArea) {
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

}
