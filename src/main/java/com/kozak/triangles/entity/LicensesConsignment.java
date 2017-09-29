package com.kozak.triangles.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Партия лицензий на продаже
 * 
 * @author Roman
 */
@Entity
@Table(name = "LicensesConsignment")
public class LicensesConsignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARKET_ID", nullable = false)
    private LicenseMarket licenseMarket; // магазин лицензий

    @Column(name = "LICENSE_LEVEL")
    private byte licenseLevel; // тип, уровень лицензий (2-4)

    @Column(name = "COUNT_ON_SELL")
    private int countOnSell; // количество на продаже

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SELL_DATE")
    private Date sellDate; // дата продажи партии лицензий

    @Column(name = "PROFIT")
    private long profit; // сумма прибыли от продажи лицензий

    public LicensesConsignment() {
    }

    public LicenseMarket getLicenseMarket() {
        return licenseMarket;
    }

    public void setLicenseMarket(LicenseMarket licenseMarket) {
        this.licenseMarket = licenseMarket;
    }

    public int getCountOnSell() {
        return countOnSell;
    }

    public void setCountOnSell(int countOnSell) {
        this.countOnSell = countOnSell;
    }

    public long getProfit() {
        return profit;
    }

    public void setProfit(long totalProfit) {
        this.profit = totalProfit;
    }

    public byte getLicenseLevel() {
        return licenseLevel;
    }

    public void setLicenseLevel(byte licenseLevel) {
        this.licenseLevel = licenseLevel;
    }

    public Date getSellDate() {
        return sellDate;
    }

    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }

}
