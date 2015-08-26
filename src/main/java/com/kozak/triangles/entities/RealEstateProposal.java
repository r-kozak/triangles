package com.kozak.triangles.entities;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;
import com.kozak.triangles.repositories.BuildingDataRep;
import com.kozak.triangles.utils.DateUtils;
import com.kozak.triangles.utils.SingletonData;

/**
 * Предложения на рынке недвижимости
 * 
 * экземпляр этой сущности - это предложение на рынке имущества
 * 
 * commBuildingType - тип коммерческого здания здания (Киоск, Маркет, ...)
 * appearDate - дата появления на рынке
 * lossDate - дата ухода с рынка
 * purchasePrice - цена покупки
 * cityArea - район здания
 * valid - предложение еще действительно
 * 
 * @author Roman: 12 июня 2015 г. 22:25:55
 */
@Entity(name = "re_proposal")
@Table(name = "re_proposal")
public class RealEstateProposal {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name = "building_type")
    @Enumerated(EnumType.STRING)
    private CommBuildingsT commBuildingType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "appear_date")
    private Date appearDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "loss_date")
    private Date lossDate;

    @Column(name = "purchase_price")
    private long purchasePrice;

    @Column(name = "cityArea")
    @Enumerated(EnumType.STRING)
    private CityAreasT cityArea;

    @Column(name = "valid")
    private boolean valid = true;

    // id имущества, которое продается (0, если новое)
    @Column(name = "used_id")
    private int usedId;

    // уровень имущества (0, если новое)
    @Column(name = "prop_level")
    private int propLevel;

    // уровень кассы имущества (0, если новое)
    @Column(name = "cash_level")
    private int cashLevel;

    // процент износа имущества
    @Column(name = "depreciation")
    private double depreciation;

    public RealEstateProposal(CommBuildingsT commBuildingType, Date lossDate, long purchasePrice,
            CityAreasT cityArea) {
        this.commBuildingType = commBuildingType;
        this.appearDate = new Date();
        this.lossDate = lossDate;
        this.purchasePrice = purchasePrice;
        this.cityArea = cityArea;
    }

    /**
     * конструктор вызывается при формировании предложения на рынке когда пользователь продает имущество
     */
    public RealEstateProposal(Property prop, BuildingDataRep buiDataRep) {
        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);
        CommBuildData propData = mapData.get(prop.getCommBuildingType().name());
        int countDaysOnMarket = Math.round((propData.getRemTermMin() + propData.getRemTermMax()) / 2);

        this.commBuildingType = prop.getCommBuildingType();
        this.appearDate = new Date();

        this.lossDate = DateUtils.getPlusDay(new Date(), countDaysOnMarket);
        this.purchasePrice = prop.getSellingPrice();
        this.cityArea = prop.getCityArea();
        this.usedId = prop.getId();
        this.propLevel = prop.getLevel();
        this.cashLevel = prop.getCashLevel();
        this.depreciation = prop.getDepreciationPercent();
    }

    public RealEstateProposal() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CommBuildingsT getCommBuildingType() {
        return commBuildingType;
    }

    public void setCommBuildingType(CommBuildingsT commBuildingType) {
        this.commBuildingType = commBuildingType;
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

    public CityAreasT getCityArea() {
        return cityArea;
    }

    public void setCityArea(CityAreasT cityArea) {
        this.cityArea = cityArea;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getUsedId() {
        return usedId;
    }

    public void setUsedId(int usedId) {
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
