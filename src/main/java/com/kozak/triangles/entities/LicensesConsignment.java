package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	@Column(name = "consignment_id", unique = true, nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "market_id", nullable = false)
	private LicenseMarket licenseMarket; // магазин лицензий

	@Column(name = "count_on_sell")
	private int countOnSell; // количество на продаже

	@Column(name = "profit")
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
