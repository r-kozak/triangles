package com.kozak.triangles.enums;

import com.kozak.triangles.exception.NoSuchLicenseLevelException;

/**
 * Статьи выигрыша, которые используются при игре в лотерею или при начислении бонуса
 * 
 * Деньги, Повышение уровня имущества, Повышение уровня кассы имущества, Лицензия на строительство ур2, Лицензия на
 * строительство ур3, Лицензия на строительство ур4, Предсказание, Киоск, Сельский магазин, Магазин канцтоваров, Земельные участки
 * в разных районах, Лотерейные билеты
 * 
 */
public enum WinArticle {

	TRIANGLES, 
	PROPERTY_UP, 
	CASH_UP, 
	LICENSE_2, 
	LICENSE_3, 
	LICENSE_4, 
	PREDICTION, 
    STALL, // не переименовывать, связанно с TradeBuildingType.STALL
    VILLAGE_SHOP, // не переименовывать, связанно с TradeBuildingType.VILLAGE_SHOP
    STATIONER_SHOP, // не переименовывать, связанно с TradeBuildingType.STATIONER_SHOP
    LAND_LOT_GHETTO,
    LAND_LOT_OUTSKIRTS,
    LAND_LOT_CHINATOWN,
    LAND_LOT_CENTER, 
    LOTTERY_TICKET;

    private static final String LICENSE_ARTICLE_PREFIX = "LICENSE_";

    public static WinArticle getLicenseArticleByLevel(int licenseLevel) throws NoSuchLicenseLevelException {
        WinArticle result = WinArticle.valueOf(LICENSE_ARTICLE_PREFIX + licenseLevel);
        if (result == null) {
            throw new NoSuchLicenseLevelException("Такого уровня лицензий не существует!");
        }
        return result;
    }
}
