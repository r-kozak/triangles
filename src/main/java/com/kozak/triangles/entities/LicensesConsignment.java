package com.kozak.triangles.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
public class LicensesConsignment {

	@Id
	@GeneratedValue
	@Column(name = "CONSIGNMENT_ID", unique = true, nullable = false)
	private int id;

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
	private int profit; // сумма прибыли от продажи лицензий

	public LicensesConsignment() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getProfit() {
		return profit;
	}

	public void setProfit(int profit) {
		this.profit = profit;
	}

}
