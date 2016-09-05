package com.kozak.triangles.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

	public static final int MAX_LEVEL = 10; // максимальный уровень магазина

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

	@Column(name = "user_id")
	private int userId; // id владельца магазина

    @Column(name = "license_level")
	private byte level = 1; // уровень магазина

    public LicenseMarket() {
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

}
