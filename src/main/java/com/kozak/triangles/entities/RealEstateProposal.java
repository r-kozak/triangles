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

import com.kozak.triangles.enums.buildings.CommBuildingsT;

/**
 * Предложения на рынке недвижимости
 * 
 * экземпляр этой сущности - это предложение на рынке имущества
 * 
 * commBuildingType - тип коммерческого здания здания (Киоск, Маркет, ...)
 * appearDate - дата появления на рынке
 * lossDate - дата ухода с рынка
 * purchasePrice - цена покупки
 * valid - предложение еще действительно
 * 
 * @author Roman: 12 июня 2015 г. 22:25:55
 */
@Entity(name = "re_proposal")
@Table(name = "re_proposal")
public class RealEstateProposal {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Column(name = "valid")
    private boolean valid = true;

    public RealEstateProposal(CommBuildingsT commBuildingType, Date appearDate, Date lossDate, long purchasePrice) {
        this.commBuildingType = commBuildingType;
        this.appearDate = appearDate;
        this.lossDate = lossDate;
        this.purchasePrice = purchasePrice;
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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
