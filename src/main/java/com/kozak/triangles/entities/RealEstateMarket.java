package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.kozak.triangles.enums.buildings.TradeBuindingTypes;

/**
 * Рынок недвижимости
 * 
 * tradeBuildingType - тип торгового здания (Киоск, Маркет, ...)
 * appearDate - дата появления на рынке
 * lossDate - дата ухода с рынка
 * valid - предложение еще действительно
 * 
 * 
 * 
 * @author Roman: 12 июня 2015 г. 22:25:55
 */
@Entity
@Table(name = "r_e_market")
public class RealEstateMarket {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "building_type")
    @Enumerated(EnumType.STRING)
    private TradeBuindingTypes tradeBuildingType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "appear_date")
    private Date appearDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "loss_date")
    private Date lossDate;

    @Column(name = "valid")
    private boolean valid;

    public RealEstateMarket(TradeBuindingTypes tradeBuildingType, Date appearDate, Date lossDate, boolean valid) {
        this.tradeBuildingType = tradeBuildingType;
        this.appearDate = appearDate;
        this.lossDate = lossDate;
        this.valid = valid;
    }

    public RealEstateMarket() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TradeBuindingTypes getBuildingType() {
        return tradeBuildingType;
    }

    public void setBuildingType(TradeBuindingTypes tradeBuildingType) {
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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
