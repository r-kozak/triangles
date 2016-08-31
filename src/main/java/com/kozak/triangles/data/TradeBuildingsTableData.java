package com.kozak.triangles.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.kozak.triangles.entities.TradeBuilding;
import com.kozak.triangles.enums.TradeBuildingsTypes;

public class TradeBuildingsTableData {

	private static final String UNDEFINED_TYPE_NAME = "UNDEFINED_TYPE";

	// структура данных из данными о торговых строениях
	// ключом является ordinal (порядковый индекс типа строения в ENUM) для правильной сортировки значений
	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	private static Map<Integer, TradeBuilding> tradeBuildingsData = new HashMap() {
		{
			put(TradeBuildingsTypes.STALL.ordinal(), new TradeBuilding(3, 6, 4500, 5500, TradeBuildingsTypes.STALL, 1, 1, 2));
			put(TradeBuildingsTypes.VILLAGE_SHOP.ordinal(), new TradeBuilding(2, 10, 10000, 15000, TradeBuildingsTypes.VILLAGE_SHOP, 2, 2, 3));
			put(TradeBuildingsTypes.STATIONER_SHOP.ordinal(), new TradeBuilding(5, 12, 17000, 30000, TradeBuildingsTypes.STATIONER_SHOP, 3, 1, 4));
			put(TradeBuildingsTypes.BOOK_SHOP.ordinal(), new TradeBuilding(2, 4, 30000, 40000, TradeBuildingsTypes.BOOK_SHOP, 4, 1, 2));
			put(TradeBuildingsTypes.CANDY_SHOP.ordinal(), new TradeBuilding(3, 7, 40000, 50000, TradeBuildingsTypes.CANDY_SHOP, 5, 1, 5));
			put(TradeBuildingsTypes.LITTLE_SUPERMARKET.ordinal(), new TradeBuilding(4, 5, 70000, 100000, TradeBuildingsTypes.LITTLE_SUPERMARKET, 6, 2, 6));
			put(TradeBuildingsTypes.MIDDLE_SUPERMARKET, new TradeBuilding(5, 7, 120000, 150000, TradeBuildingsTypes.MIDDLE_SUPERMARKET, 7, 3, 7));
			put(TradeBuildingsTypes.BIG_SUPERMARKET.ordinal(), new TradeBuilding(4, 7, 150000, 200000, TradeBuildingsTypes.BIG_SUPERMARKET, 8, 4, 8));
			put(TradeBuildingsTypes.RESTAURANT.ordinal(), new TradeBuilding(5, 8, 200000, 280000, TradeBuildingsTypes.RESTAURANT, 9, 3, 5));
			put(TradeBuildingsTypes.CINEMA.ordinal(), new TradeBuilding(6, 8, 280000, 380000, TradeBuildingsTypes.CINEMA, 10, 2, 4));
			put(TradeBuildingsTypes.MALL.ordinal(), new TradeBuilding(3, 6, 380000, 500000, TradeBuildingsTypes.MALL, 11, 6, 10));
		}
	};

	// короткие имена для типов торговых имуществ
	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	private static Map<TradeBuildingsTypes, String> shortTradeBuildingsNames = new HashMap() {
		{
			put(TradeBuildingsTypes.STALL, "STL");
			put(TradeBuildingsTypes.VILLAGE_SHOP, "VSH");
			put(TradeBuildingsTypes.STATIONER_SHOP, "SSH");
			put(TradeBuildingsTypes.BOOK_SHOP, "BSH");
			put(TradeBuildingsTypes.CANDY_SHOP, "CSH");
			put(TradeBuildingsTypes.LITTLE_SUPERMARKET, "LST");
			put(TradeBuildingsTypes.MIDDLE_SUPERMARKET, "MST");
			put(TradeBuildingsTypes.BIG_SUPERMARKET, "BST");
			put(TradeBuildingsTypes.RESTAURANT, "RST");
			put(TradeBuildingsTypes.CINEMA, "CNM");
			put(TradeBuildingsTypes.MALL, "MALL");
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
	public static String getShortTradeBuildingName(TradeBuildingsTypes tradeBuildingType) {
		String shortName = shortTradeBuildingsNames.get(tradeBuildingType);
		if (shortName == null) {
			return UNDEFINED_TYPE_NAME;
		}
		return shortName;
	}
}
