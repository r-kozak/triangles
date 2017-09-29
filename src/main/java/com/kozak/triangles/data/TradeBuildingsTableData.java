package com.kozak.triangles.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.kozak.triangles.enums.TradeBuildingType;
import com.kozak.triangles.model.TradeBuilding;

public class TradeBuildingsTableData {

	private static final String UNDEFINED_TYPE_NAME = "UNDEFINED_TYPE";

	// структура данных из данными о торговых строениях
	// ключом является ordinal (порядковый индекс типа строения в ENUM) для правильной сортировки значений
	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	private static Map<Integer, TradeBuilding> tradeBuildingsData = new HashMap() {
		{
			put(TradeBuildingType.STALL.ordinal(), new TradeBuilding(3, 6, 4500, 5500, TradeBuildingType.STALL, 1, 1, 2));
			put(TradeBuildingType.VILLAGE_SHOP.ordinal(), new TradeBuilding(2, 10, 10000, 15000, TradeBuildingType.VILLAGE_SHOP, 2, 2, 3));
			put(TradeBuildingType.STATIONER_SHOP.ordinal(), new TradeBuilding(5, 12, 17000, 30000, TradeBuildingType.STATIONER_SHOP, 3, 1, 4));
			put(TradeBuildingType.BOOK_SHOP.ordinal(), new TradeBuilding(2, 4, 30000, 40000, TradeBuildingType.BOOK_SHOP, 4, 1, 2));
			put(TradeBuildingType.CANDY_SHOP.ordinal(), new TradeBuilding(3, 7, 40000, 50000, TradeBuildingType.CANDY_SHOP, 5, 1, 5));
			put(TradeBuildingType.LITTLE_SUPERMARKET.ordinal(), new TradeBuilding(4, 5, 70000, 100000, TradeBuildingType.LITTLE_SUPERMARKET, 6, 2, 6));
			put(TradeBuildingType.MIDDLE_SUPERMARKET.ordinal(), new TradeBuilding(5, 7, 120000, 150000, TradeBuildingType.MIDDLE_SUPERMARKET, 7, 3, 7));
			put(TradeBuildingType.BIG_SUPERMARKET.ordinal(), new TradeBuilding(4, 7, 150000, 200000, TradeBuildingType.BIG_SUPERMARKET, 8, 4, 8));
			put(TradeBuildingType.RESTAURANT.ordinal(), new TradeBuilding(5, 8, 200000, 280000, TradeBuildingType.RESTAURANT, 9, 3, 5));
			put(TradeBuildingType.CINEMA.ordinal(), new TradeBuilding(6, 8, 280000, 380000, TradeBuildingType.CINEMA, 10, 2, 4));
			put(TradeBuildingType.MALL.ordinal(), new TradeBuilding(3, 6, 380000, 500000, TradeBuildingType.MALL, 11, 6, 10));
		}
	};

	// короткие имена для типов торговых имуществ
	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	private static Map<TradeBuildingType, String> shortTradeBuildingsNames = new HashMap() {
		{
			put(TradeBuildingType.STALL, "STL");
			put(TradeBuildingType.VILLAGE_SHOP, "VSH");
			put(TradeBuildingType.STATIONER_SHOP, "SSH");
			put(TradeBuildingType.BOOK_SHOP, "BSH");
			put(TradeBuildingType.CANDY_SHOP, "CSH");
			put(TradeBuildingType.LITTLE_SUPERMARKET, "LST");
			put(TradeBuildingType.MIDDLE_SUPERMARKET, "MST");
			put(TradeBuildingType.BIG_SUPERMARKET, "BST");
			put(TradeBuildingType.RESTAURANT, "RST");
			put(TradeBuildingType.CINEMA, "CNM");
			put(TradeBuildingType.MALL, "MALL");
		}
	};

	// граничные значение влияния типа имущества на уровень доминантности. Означает, к примеру, что если при повышении уровня
	// Киоска (STALL) у пользователя уровень доминантности = 1000 (>= граничного значения для Киоска), то его доминантность при
	// этом не будет повышена, но если доминантность меньше граничного значения - доминантность повышена будет.
	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	private static Map<TradeBuildingType, Integer> boundaryValuesForDomiUp = new HashMap() {
		{
			put(TradeBuildingType.STALL, 1000);
			put(TradeBuildingType.VILLAGE_SHOP, 1500);
			put(TradeBuildingType.STATIONER_SHOP, 3000);
			put(TradeBuildingType.BOOK_SHOP, 5000);
			put(TradeBuildingType.CANDY_SHOP, 7500);
			put(TradeBuildingType.LITTLE_SUPERMARKET, 15000);
			put(TradeBuildingType.MIDDLE_SUPERMARKET, 30000);
			put(TradeBuildingType.BIG_SUPERMARKET, 50000);
			put(TradeBuildingType.RESTAURANT, 100000);
			put(TradeBuildingType.CINEMA, 300000);
			put(TradeBuildingType.MALL, 1000000);
		}
	};

    /**
	 * @return - данные о всех торговых строениях в виде списка
	 */
	public static Collection<TradeBuilding> getTradeBuildingsDataList() {
		return tradeBuildingsData.values();
    }

    /**
	 * @return map c данными о торговых строениях (TradeBuildingsTypes).
	 *         Ключ - ordinal у Enum элемента TradeBuildingsTypes
	 *         Значение - елемент с данными об имуществе
	 */
	public static Map<Integer, TradeBuilding> getTradeBuildingsDataMap() {
		return tradeBuildingsData;
    }

	public static TradeBuilding getTradeBuildingDataByName(String typeName) {
		for (TradeBuilding tradeBuilding : getTradeBuildingsDataList()) {
			if (tradeBuilding.getTradeBuildingType().name().equals(typeName)) {
				return tradeBuilding;
			}
		}
		return null;
	}

	/**
	 * @param tradeBuildingType
	 *            тип имущества
	 * @return короткое наименование торгового имущества по его типу
	 */
	public static String getShortTradeBuildingName(TradeBuildingType tradeBuildingType) {
		String shortName = shortTradeBuildingsNames.get(tradeBuildingType);
		if (shortName == null) {
			return UNDEFINED_TYPE_NAME;
		}
		return shortName;
	}

	/**
	 * @param buildingType
	 *            тип торгового имущества
	 * @return граничное значение влияния типа имущества на уровень доминантности при повышении его уровня или уровня его кассы
	 */
	public static Integer getBoundaryValueForDomiUp(TradeBuildingType buildingType) {
		return boundaryValuesForDomiUp.get(buildingType);
	}
}
