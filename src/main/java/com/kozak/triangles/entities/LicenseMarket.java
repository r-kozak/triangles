package com.kozak.triangles.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Сущность представляет собой Магазин лицензий, пренадлежащий конкретному пользователю.
 * Магазины лицензий служат для того, чтобы продавать лицензии на строительство разных уровней, которые были выиграны в лотерею.
 * 
 * @author Roman
 */
@Entity
@Table(name = "LicenseMarket")
public class LicenseMarket {

	public static final int START_LEVEL = 1; // уровень после постройки
	public static final int MAX_LEVEL = 10; // максимальный уровень магазина

	/**
	 * базовая стоимость магазина, будет умножаться на универсальный коэфициент (Constants.UNIVERS_K) при строительстве или
	 * повышении уровня магазина
	 */
	public static final int BASE_PRICE = 100_000;

	/**
	 * Количество первых уровней, для которых нет надбавки к стоимости лицензий при их продаже. За каждый уровень после этого
	 * числа начисляется <b>10%</b> к продажной стоимости лицензии.
	 * Допустим, значение этой константы = 4. Если уровень магазина <= 4, значит к продажной стоимости нет никаких надбавок.
	 * Если же, к примеру, уровень магазина = 6, тогда:
	 * (6 - 4) * 10% = 20% - это процент надбавки к стоимости лицензии.
	 */
	public static final int LEVELS_COUNT_WITHOUT_PREMIUM = 4;

    @Id
    @GeneratedValue
    private int id;

	@Column(name = "USER_ID")
	private int userId; // id владельца магазина

	@Column(name = "MARKET_LEVEL")
	private byte level = START_LEVEL; // уровень магазина

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "licenseMarket")
	private List<LicensesConsignment> licensesConsignments = new ArrayList<>(0);

    public LicenseMarket() {
    }

	public LicenseMarket(int userId) {
		this.userId = userId;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	public List<LicensesConsignment> getLicensesConsignments() {
		return licensesConsignments;
	}

	public void setLicensesConsignments(List<LicensesConsignment> licensesConsignments) {
		this.licensesConsignments = licensesConsignments;
	}

}
