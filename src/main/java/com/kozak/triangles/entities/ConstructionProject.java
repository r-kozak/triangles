package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.kozak.triangles.enums.BuildersType;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TradeBuildingType;
import com.kozak.triangles.utils.PropertyUtil;

/**
 * Объект строительства
 * 
 * экземпляр этой сущности - это имущество в процессе стройки
 */
@Entity(name = "ConstructionProject")
@Table(name = "CONSTRUCTION_PROJECT")
public class ConstructionProject extends BaseEntity {

    // наименование имущества
    @Column(name = "NAME")
    private String name;

    // владелец имущества
    @Column(name = "USER_ID")
    private long userId;

    // тип - КИОСК, СЕЛЬСКИЙ МАГАЗИН ...
    @Column(name = "BUILDING_TYPE")
    @Enumerated(EnumType.STRING)
    private TradeBuildingType buildingType;

    // дата начала стройки
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    private Date startDate;

    // дата конца стройки
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FINISH_DATE")
    private Date finishDate;

    // район города
    @Column(name = "CITY_AREA")
    @Enumerated(EnumType.STRING)
    private CityArea cityArea;

    // тип строителей
    @Column(name = "BUILDERS_TYPE")
    private BuildersType buildersType;

    // процент завершенности строительства
    @Column(name = "COMPLETE_PERC")
    private float completePerc;

    public ConstructionProject() {
    }

    public ConstructionProject(TradeBuildingType tradeBuildingType, Date finishDate, CityArea cityArea,
            BuildersType buildersType, long userId) {
        this.startDate = new Date();
        this.name = PropertyUtil.generatePropertyName(tradeBuildingType, cityArea);

        this.buildingType = tradeBuildingType;
        this.finishDate = finishDate;
        this.cityArea = cityArea;
        this.buildersType = buildersType;
        this.userId = userId;
    }

    public TradeBuildingType getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(TradeBuildingType buildingType) {
        this.buildingType = buildingType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public CityArea getCityArea() {
        return cityArea;
    }

    public void setCityArea(CityArea cityArea) {
        this.cityArea = cityArea;
    }

    public float getCompletePerc() {
        return completePerc;
    }

    public void setCompletePerc(float completePerc) {
        this.completePerc = completePerc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BuildersType getBuildersType() {
        return buildersType;
    }

    public void setBuildersType(BuildersType buildersType) {
        this.buildersType = buildersType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}
