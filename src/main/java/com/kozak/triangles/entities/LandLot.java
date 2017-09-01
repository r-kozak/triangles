package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.kozak.triangles.enums.CityArea;

/**
 * Таблица для хранения информации об участках земли
 * 
 * @author Roman: 30 августа 2017 г.
 */
@Entity
@Table(name = "LAND_LOT")
public class LandLot extends BaseEntity {

    @Column(name = "USER_ID")
    private long userId;

    @Column(name = "CITY_AREA")
    @Enumerated(EnumType.STRING)
    private CityArea cityArea; // район участка земли

    @Column(name = "LOT_COUNT")
    private int lotCount; // количество участков земли

    public LandLot() {
    }

    public LandLot(long userId, CityArea cityArea, int lotCount) {
        this.userId = userId;
        this.cityArea = cityArea;
        this.lotCount = lotCount;
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

    public int getLotCount() {
        return lotCount;
    }

    public void setLotCount(int lotCount) {
        this.lotCount = lotCount;
    }

}
