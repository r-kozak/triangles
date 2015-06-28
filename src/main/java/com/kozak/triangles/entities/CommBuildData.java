package com.kozak.triangles.entities;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kozak.triangles.enums.buildings.BuildingsT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;

/**
 * Данные вида имущества
 * 
 * экземпляр сущности - данные какого-либо конкретного вида имущества (киоск, магазин...)
 * 
 * usefulLife - срок полезного использования, в 2 раза больше периода окупаемости
 * profit - прибыль в день {min, max}
 * cashCapacity - вместимость кассы на разных уровнях, {на ур 0, на ур 1, ...}
 * paybackPeriod - период окупаемости {min, max}, недель
 * purchasePrice - цена покупки {min, max}
 * buildTime - время постройки (при скорости 100%), недель
 * commBuildType - тип строения (киоск, магазин)
 * buildType - супер-тип строения (торговое, производственное...)
 * remTerm - срок, сколько предложение будет находиться на рынке (мин, макс), в днях
 * 
 ********
 * ПОСЛЕ ДОБАВЛЕНИЯ НОВОГО ТИПА ПРОИНИТИТЬ ЕГО В:
 * com.kozak.triangles.controllers.HomeController.buildDataInit()
 * 
 * @author Roman: 20 июня 2015 г. 12:13:58
 */

@Entity(name = "CommBuildData")
@Table(name = "CommBuildData")
public class CommBuildData {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "payback_period_min")
    int paybackPeriodMin;

    @Column(name = "payback_period_max")
    int paybackPeriodMax;

    @Column(name = "purchase_price_min")
    long purchasePriceMin;

    @Column(name = "purchase_price_max")
    long purchasePriceMax;

    @Column(name = "useful_life")
    int usefulLife;

    @Column(name = "profit_min")
    int profitMin;

    @Column(name = "profit_max")
    int profitMax;

    @Column(name = "remTerm_min")
    int remTermMin;

    @Column(name = "remTerm_max")
    int remTermMax;

    @ElementCollection
    @Column(name = "cash_capacity")
    ArrayList<Long> cashCapacity;

    @Column(name = "comm_build_type")
    @Enumerated(EnumType.STRING)
    CommBuildingsT commBuildType;

    @Column(name = "build_type")
    @Enumerated(EnumType.STRING)
    BuildingsT buildType;

    @Column(name = "build_time")
    int buildTime;

    public CommBuildData() {
    }

    public CommBuildData(int paybackPeriodMin, int paybackPeriodMax, long purchasePriceMin, long purchasePriceMax,
            CommBuildingsT commBuildType, BuildingsT buildType, int buildTime, int remTermMin, int remTermMax) {

        this.paybackPeriodMin = paybackPeriodMin;
        this.paybackPeriodMax = paybackPeriodMax;
        this.purchasePriceMin = purchasePriceMin;
        this.purchasePriceMax = purchasePriceMax;

        this.usefulLife = paybackPeriodMax * 2;

        this.profitMin = (int) (purchasePriceMin / (paybackPeriodMax * 7));
        this.profitMax = (int) (purchasePriceMin / (paybackPeriodMin * 7));

        ArrayList<Long> cashList = new ArrayList<Long>(5);
        cashList.add((long) profitMax);
        cashList.add((long) profitMax * 2);
        cashList.add((long) profitMax * 3);
        cashList.add((long) profitMax * 4);
        cashList.add((long) profitMax * 5);

        this.cashCapacity = cashList;

        this.commBuildType = commBuildType;
        this.buildType = buildType;

        this.buildTime = buildTime;

        this.remTermMin = remTermMin;
        this.remTermMax = remTermMax;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getPaybackPeriodMin() {
        return paybackPeriodMin;
    }

    public void setPaybackPeriodMin(int paybackPeriodMin) {
        this.paybackPeriodMin = paybackPeriodMin;
    }

    public int getPaybackPeriodMax() {
        return paybackPeriodMax;
    }

    public void setPaybackPeriodMax(int paybackPeriodMax) {
        this.paybackPeriodMax = paybackPeriodMax;
    }

    public long getPurchasePriceMin() {
        return purchasePriceMin;
    }

    public void setPurchasePriceMin(long purchasePriceMin) {
        this.purchasePriceMin = purchasePriceMin;
    }

    public long getPurchasePriceMax() {
        return purchasePriceMax;
    }

    public void setPurchasePriceMax(long purchasePriceMax) {
        this.purchasePriceMax = purchasePriceMax;
    }

    public int getUsefulLife() {
        return usefulLife;
    }

    public void setUsefulLife(int usefulLife) {
        this.usefulLife = usefulLife;
    }

    public int getProfitMin() {
        return profitMin;
    }

    public void setProfitMin(int profitMin) {
        this.profitMin = profitMin;
    }

    public int getProfitMax() {
        return profitMax;
    }

    public void setProfitMax(int profitMax) {
        this.profitMax = profitMax;
    }

    public ArrayList<Long> getCashCapacity() {
        return cashCapacity;
    }

    public void setCashCapacity(ArrayList<Long> cashCapacity) {
        this.cashCapacity = cashCapacity;
    }

    public int getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(int buildTime) {
        this.buildTime = buildTime;
    }

    public int getRemTermMin() {
        return remTermMin;
    }

    public void setRemTermMin(int remTermMin) {
        this.remTermMin = remTermMin;
    }

    public int getRemTermMax() {
        return remTermMax;
    }

    public void setRemTermMax(int remTermMax) {
        this.remTermMax = remTermMax;
    }

    public CommBuildingsT getCommBuildType() {
        return commBuildType;
    }

    public void setCommBuildType(CommBuildingsT commBuildType) {
        this.commBuildType = commBuildType;
    }

    public BuildingsT getBuildType() {
        return buildType;
    }

    public void setBuildType(BuildingsT buildType) {
        this.buildType = buildType;
    }

}
