package com.kozak.triangles.models;

import java.util.ArrayList;
import java.util.List;

import com.kozak.triangles.enums.TradeBuildingType;
import com.kozak.triangles.utils.Constants;
import com.kozak.triangles.utils.Ksyusha;

/**
 * Торговое строение
 * 
 * экземпляр класса - данные какого-либо конкретного вида имущества (киоск, магазин...)
 * 
 ********
 * ПОСЛЕ ДОБАВЛЕНИЯ НОВОГО ТИПА ПРОИНИТИТЬ ЕГО В: com.kozak.triangles.data.TradeBuildingsTableData.tradeBuildingsData
 * 
 * @author Roman: 20 июня 2015 г. 12:13:58
 */

public class TradeBuilding {

    private int paybackPeriodMin; // период окупаемости ОТ, недель
    private int paybackPeriodMax; // период окупаемости ДО, недель
    private long purchasePriceMin; // цена покупки min, в triangles
    private long purchasePriceMax; // цена покупки max, в triangles
    private int usefulLife; // срок полезного использования, в 2 раза больше периода окупаемости, недель
    private int profitMin; // прибыль в день min, в triangles
    private int profitMax; // прибыль в день max, в triangles
    private int marketTermMin; // срок, сколько предложение будет находиться на рынке min, в днях
    private int marketTermMax; // срок, сколько предложение будет находиться на рынке max, в днях
    private List<Long> cashCapacity; // вместимость кассы на разных уровнях, {на уровне 0, ..., на уровне n}
    private TradeBuildingType tradeBuildingType; // тип торгового имущества
    private int buildTime; // buildTime - время постройки (при скорости 100%), дней

    public TradeBuilding(int paybackPeriodMin, int paybackPeriodMax, long purchasePriceMin, long purchasePriceMax,
            TradeBuildingType tradeBuildingType, int buildTime, int marketTermMin, int marketTermMax) {

        this.paybackPeriodMin = paybackPeriodMin;
        this.paybackPeriodMax = paybackPeriodMax;
        this.purchasePriceMin = purchasePriceMin;
        this.purchasePriceMax = purchasePriceMax;

        this.usefulLife = paybackPeriodMax * 2;

        this.profitMin = (int) (purchasePriceMin / (paybackPeriodMax * 7));
        this.profitMax = (int) (purchasePriceMin / (paybackPeriodMin * 7));

        this.cashCapacity = new ArrayList<>();
        for (int i = 1; i < Constants.MAX_CASH_LEVEL + 2; i++) {
            this.cashCapacity.add(Math.round(profitMax * Ksyusha.computeCoef(i)));
        }

        this.tradeBuildingType = tradeBuildingType;
        this.buildTime = buildTime;
        this.marketTermMin = marketTermMin;
        this.marketTermMax = marketTermMax;
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

    public int getMarketTermMin() {
        return marketTermMin;
    }

    public void setMarketTermMin(int marketTermMin) {
        this.marketTermMin = marketTermMin;
    }

    public int getMarketTermMax() {
        return marketTermMax;
    }

    public void setMarketTermMax(int marketTermMax) {
        this.marketTermMax = marketTermMax;
    }

    public List<Long> getCashCapacity() {
        return cashCapacity;
    }

    public void setCashCapacity(List<Long> cashCapacity) {
        this.cashCapacity = cashCapacity;
    }

    public TradeBuildingType getTradeBuildingType() {
        return tradeBuildingType;
    }

    public void setTradeBuildingType(TradeBuildingType tradeBuildingType) {
        this.tradeBuildingType = tradeBuildingType;
    }

    public int getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(int buildTime) {
        this.buildTime = buildTime;
    }

}
