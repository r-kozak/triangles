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

import com.kozak.triangles.enums.BuildersTypes;
import com.kozak.triangles.enums.CityAreas;
import com.kozak.triangles.enums.TradeBuildingsTypes;
import com.kozak.triangles.utils.PropertyUtil;

/**
 * Объект строительства
 * 
 * экземпляр этой сущности - это имущество в процессе стройки
 */
@Entity(name = "ConstructionProject")
@Table(name = "CONSTRUCTION_PROJECT")
public class ConstructionProject {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    // наименование имущества
    @Column(name = "NAME")
    private String name;

    // владелец имущества
    @Column(name = "userId")
    private int userId;

    // тип - КИОСК, СЕЛЬСКИЙ МАГАЗИН ...
    @Column(name = "BUILDING_TYPE")
    @Enumerated(EnumType.STRING)
    private TradeBuildingsTypes buildingType;

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
    private CityAreas cityArea;

    // тип строителей
    @Column(name = "buildersType")
    private BuildersTypes buildersType;

    // процент завершенности строительства
    @Column(name = "COMPLETE_PERC")
    private float completePerc;

    public ConstructionProject() {
    }

	public ConstructionProject(TradeBuildingsTypes tradeBuildingType, Date finishDate, CityAreas cityArea,
            BuildersTypes buildersType, int userId) {
        this.startDate = new Date();
		this.name = PropertyUtil.generatePropertyName(tradeBuildingType, cityArea);

		this.buildingType = tradeBuildingType;
        this.finishDate = finishDate;
        this.cityArea = cityArea;
        this.buildersType = buildersType;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TradeBuildingsTypes getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(TradeBuildingsTypes buildingType) {
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

    public CityAreas getCityArea() {
        return cityArea;
    }

    public void setCityArea(CityAreas cityArea) {
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

    public BuildersTypes getBuildersType() {
        return buildersType;
    }

    public void setBuildersType(BuildersTypes buildersType) {
        this.buildersType = buildersType;
    }
}
