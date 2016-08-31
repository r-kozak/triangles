package com.kozak.triangles.data;

import java.util.HashMap;
import java.util.Map;

import com.kozak.triangles.enums.CityAreas;
import com.kozak.triangles.enums.TradeBuildingsTypes;

public class CityAreasTableData {

	// ставки процентов районов города
	private static final int GHETTO_PERCENT = 0;
	private static final int OUTSKIRTS_PERCENT = 5;
	private static final int CHINATOWN_PERCENT = 15;
	private static final int CENTER_PERCENT = 30;

	// процентные ставки, относящиеся к конкретному району города
	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	private static Map<CityAreas, Integer> cityAreasPercents = new HashMap() {
		{
			put(CityAreas.GHETTO, GHETTO_PERCENT);
			put(CityAreas.OUTSKIRTS, OUTSKIRTS_PERCENT);
			put(CityAreas.CHINATOWN, CHINATOWN_PERCENT);
			put(CityAreas.CENTER, CENTER_PERCENT);
		}
	};

	// короткие имена для районов города
	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	private static Map<TradeBuildingsTypes, String> shortTradeBuildingsNames = new HashMap() {
		{
			put(CityAreas.GHETTO, "GH");
			put(CityAreas.OUTSKIRTS, "OT");
			put(CityAreas.CHINATOWN, "CN");
			put(CityAreas.CENTER, "CR");
		}
	};

	/**
	 * @param cityArea
	 *            - район города
	 * @return процентную ставку района города
	 */
	public static int getCityAreaPercent(CityAreas cityArea) {
		return cityAreasPercents.get(cityArea);
	}

	public static String getShortCityAreaName(CityAreas cityArea) {
		return shortTradeBuildingsNames.get(cityArea);
	}
}
