package com.kozak.triangles.entity;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.kozak.triangles.data.TradeBuildingsTableData;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TradeBuildingType;
import com.kozak.triangles.model.TradeBuilding;
import com.kozak.triangles.util.DateUtils;

/**
 * Предложения на рынке недвижимости
 * 
 * экземпляр этой сущности - это предложение на рынке имущества
 * 
 * @author Roman: 12 июня 2015 г. 22:25:55
 */
@Entity(name = "re_proposal")
@Table(name = "re_proposal")
public class RealEstateProposal extends BaseEntity {

    @Column(name = "BUILDING_TYPE")
    @Enumerated(EnumType.STRING)
    private TradeBuildingType tradeBuildingType; // тип торгового здания (Киоск, Маркет, ...)

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "APPEAR_DATE")
    private Date appearDate; // дата появления на рынке

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LOSS_DATE")
    private Date lossDate; // дата ухода с рынка

    @Column(name = "PURCHASE_PRICE")
    private long purchasePrice; // цена покупки

    @Column(name = "CITY_AREA")
    @Enumerated(EnumType.STRING)
    private CityArea cityArea; // район здания

    @Column(name = "VALID")
    private boolean valid = true; // предложение еще действительно

    // id имущества, которое продается (0, если новое)
    @Column(name = "USED_ID")
    private long usedId;

    // уровень имущества (0, если новое)
    @Column(name = "PROP_LEVEL")
    private int propLevel;

    // уровень кассы имущества (0, если новое)
    @Column(name = "CASH_LEVEL")
    private int cashLevel;

    // процент износа имущества
    @Column(name = "DEPRECIATION")
    private double depreciation;

    public RealEstateProposal(TradeBuildingType tradeBuildingType, Date lossDate, long purchasePrice, CityArea cityArea) {
        this.tradeBuildingType = tradeBuildingType;
        this.appearDate = new Date();
        this.lossDate = lossDate;
        this.purchasePrice = purchasePrice;
        this.cityArea = cityArea;
    }

    /**
     * конструктор вызывается при формировании предложения на рынке когда пользователь продает имущество
     */
    public RealEstateProposal(Property property) {
        // получить данные всех торговых строений
        Map<Integer, TradeBuilding> tradeBuildingsData = TradeBuildingsTableData.getTradeBuildingsDataMap();
        TradeBuilding propData = tradeBuildingsData.get(property.getTradeBuildingType().ordinal());
        int countDaysOnMarket = Math.round((propData.getMarketTermMin() + propData.getMarketTermMax()) / 2);

        this.tradeBuildingType = property.getTradeBuildingType();
        this.appearDate = new Date();

        this.lossDate = DateUtils.addDays(new Date(), countDaysOnMarket);
        this.purchasePrice = property.getSellingPrice();
        this.cityArea = property.getCityArea();
        this.usedId = property.getId();
        this.propLevel = property.getLevel();
        this.cashLevel = property.getCashLevel();
        this.depreciation = property.getDepreciationPercent();
    }

    public RealEstateProposal() {
    }

    public TradeBuildingType getTradeBuildingType() {
        return tradeBuildingType;
    }

    public void setTradeBuildingType(TradeBuildingType tradeBuildingType) {
        this.tradeBuildingType = tradeBuildingType;
    }

    public Date getAppearDate() {
        return appearDate;
    }

    public void setAppearDate(Date appearDate) {
        this.appearDate = appearDate;
    }

    public Date getLossDate() {
        return lossDate;
    }

    public void setLossDate(Date lossDate) {
        this.lossDate = lossDate;
    }

    public long getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(long purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public CityArea getCityArea() {
        return cityArea;
    }

    public void setCityArea(CityArea cityArea) {
        this.cityArea = cityArea;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public long getUsedId() {
        return usedId;
    }

    public void setUsedId(long usedId) {
        this.usedId = usedId;
    }

    public double getDepreciation() {
        return depreciation;
    }

    public void setDepreciation(double depreciation) {
        this.depreciation = depreciation;
    }

    public int getPropLevel() {
        return propLevel;
    }

    public void setPropLevel(int propLevel) {
        this.propLevel = propLevel;
    }

    public int getCashLevel() {
        return cashLevel;
    }

    public void setCashLevel(int cashLevel) {
        this.cashLevel = cashLevel;
    }
}
