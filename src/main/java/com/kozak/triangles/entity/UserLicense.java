package com.kozak.triangles.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "UserLicense")
public class UserLicense extends BaseEntity {

    // уровень лицензии
    @Column(name = "LICENSE_LEVEL")
    private byte licenseLevel = 1;

    // дата окончания лицензии
    @Column(name = "LOSS_DATE")
    private Date lossDate = new Date();

    public UserLicense() {
    }

    public byte getLicenseLevel() {
        return licenseLevel;
    }

    public void setLicenseLevel(byte licenseLevel) {
        this.licenseLevel = licenseLevel;
    }

    public Date getLossDate() {
        return lossDate;
    }

    public void setLossDate(Date lossDate) {
        this.lossDate = lossDate;
    }
}
