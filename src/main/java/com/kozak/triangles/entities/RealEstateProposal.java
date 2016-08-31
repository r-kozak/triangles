package com.kozak.triangles.entities;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.kozak.triangles.data.TradeBuildingsTableData;
import com.kozak.triangles.enums.CityAreas;
import com.kozak.triangles.enums.TradeBuildingsTypes;
import com.kozak.triangles.utils.DateUtils;

/**
 * Предложения на рынке недвижимости
 * 
 * экземпляр этой сущности - это предложение на рынке имущества
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
	private TradeBuildingsTypes tradeBuildingType; // тип торгового здания (Киоск, Маркет, ...)

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "appear_date")
	private Date appearDate; // дата появления на рынке

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "loss_date")
	private Date lossDate; // дата ухода с рынка

    @Column(name = "purchase_price")
	private long purchasePrice; // цена покупки

    @Column(name = "cityArea")
    @Enumerated(EnumType.STRING)
	private CityAreas cityArea; // район здания

    @Column(name = "valid")
	private boolean valid = true; // предложение еще действительно

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

	public RealEstateProposal(TradeBuildingsTypes tradeBuildingType, Date lossDate, long purchasePrice, CityAreas cityArea) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public TradeBuildingsTypes getTradeBuildingType() {
		return tradeBuildingType;
	}

	public void setTradeBuildingType(TradeBuildingsTypes tradeBuildingType) {
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

    public CityAreas getCityArea() {
        return cityArea;
    }

    public void setCityArea(CityAreas cityArea) {
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
