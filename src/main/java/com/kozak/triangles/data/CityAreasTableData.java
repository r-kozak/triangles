package com.kozak.triangles.data;

import java.util.HashMap;
import java.util.Map;

import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TradeBuildingType;

public class CityAreasTableData {

	// ставки процентов районов города
	private static final int GHETTO_PERCENT = 0;
	private static final int OUTSKIRTS_PERCENT = 5;
	private static final int CHINATOWN_PERCENT = 15;
	private static final int CENTER_PERCENT = 30;

	// процентные ставки, относящиеся к конкретному району города
	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	private static Map<CityArea, Integer> cityAreasPercents = new HashMap() {
		{
			put(CityArea.GHETTO, GHETTO_PERCENT);
			put(CityArea.OUTSKIRTS, OUTSKIRTS_PERCENT);
			put(CityArea.CHINATOWN, CHINATOWN_PERCENT);
			put(CityArea.CENTER, CENTER_PERCENT);
		}
	};

	// короткие имена для районов города
	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	private static Map<TradeBuildingType, String> shortTradeBuildingsNames = new HashMap() {
		{
			put(CityArea.GHETTO, "GH");
			put(CityArea.OUTSKIRTS, "OT");
			put(CityArea.CHINATOWN, "CN");
			put(CityArea.CENTER, "CR");
		}
	};

	/**
	 * @param cityArea
	 *            - район города
	 * @return процентную ставку района города
	 */
	public static int getCityAreaPercent(CityArea cityArea) {
		return cityAreasPercents.get(cityArea);
	}

	public static String getShortCityAreaName(CityArea cityArea) {
		return shortTradeBuildingsNames.get(cityArea);
	}
}
