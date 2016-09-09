package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "UserLicense")
public class UserLicense {
    @Id
    @GeneratedValue
    private int id;

    // уровень лицензии
	@Column(name = "LICENSE_LEVEL")
    private byte licenseLevel = 1;

    // дата окончания лицензии
	@Column(name = "LOSS_DATE")
    private Date lossDate = new Date();

    public UserLicense() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
