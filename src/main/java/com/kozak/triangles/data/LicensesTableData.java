package com.kozak.triangles.data;

import java.util.HashMap;
import java.util.Map;

public class LicensesTableData {

	/**
	 * Таблица хранит данные, сколько часов продаются лицензии разных уровней. 
	 * Ключ - уровень лицензии, значение - количество часов. 
	 */
	@SuppressWarnings("serial")
	private static Map<Integer, Integer> hoursToSell = new HashMap<Integer, Integer>() {
		{
			put(2, 1);
			put(3, 2);
			put(4, 5);
		}
	};

	/**
	 * Таблица хранит данные, сколько стоят лицензии разных уровней.
	 * Ключ - уровень лицензии, значение - цена.
	 */
	@SuppressWarnings("serial")
	private static Map<Byte, Integer> licensePrices = new HashMap<Byte, Integer>() {
		{
			put((byte) 1, 0);
			put((byte) 2, 16000);
			put((byte) 3, 32000);
			put((byte) 4, 64000);
		}
	};

	/**
	 * Таблица хранит данные, сколько действуют лицензии разных уровней.
	 * Ключ - уровень лицензии, значение - срок действия, дней.
	 */
	@SuppressWarnings("serial")
	private static Map<Byte, Integer> licenseTerms = new HashMap<Byte, Integer>() {
		{
			put((byte) 1, 10);
			put((byte) 2, 7);
			put((byte) 3, 4);
			put((byte) 4, 2);
		}
	};

	/**
	 * @param licensesLevel
	 *            уровень лицензий
	 * @return сколько часов продаются лицензии конкретного уровня
	 */
	public static Integer getHoursToSellLicenses(int licensesLevel) {
		return hoursToSell.get(licensesLevel);
	}
	
	/**
	 * @param licensesLevel
	 * @return цену лицензии конкретного уровня
	 */
	public static Integer getLicensePrice(byte licensesLevel) {
		return licensePrices.get(licensesLevel);
	}

	/**
	 * @return все данные по ценам лицензий всех уровней
	 */
	public static Map<Byte, Integer> getLicensePricesTable() {
		return licensePrices;
	}

	/**
	 * @param licensesLevel
	 * @return срок действия лицензии конкретного уровня
	 */
	public static Integer getLicenseTerm(byte licensesLevel) {
		return licenseTerms.get(licensesLevel);
	}

	/**
	 * @return все данные по срокам действий лицензий всех уровней
	 */
	public static Map<Byte, Integer> getLicenseTermsTable() {
		return licenseTerms;
	}
}
