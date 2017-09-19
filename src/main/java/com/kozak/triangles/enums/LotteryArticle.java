package com.kozak.triangles.enums;

import com.kozak.triangles.exceptions.NoSuchLicenseLevelException;

/**
 * Деньги, Повышение уровня имущества, Повышение уровня кассы имущества, Лицензия на строительство ур2, Лицензия на
 * строительство ур3, Лицензия на строительство ур4, Предсказание, Киоск, Сельский магазин, Магазин канцтоваров, Земельные участки
 * в разных районах
 * 
 */
public enum LotteryArticle {

	TRIANGLES, 
	PROPERTY_UP, 
	CASH_UP, 
	LICENSE_2, 
	LICENSE_3, 
	LICENSE_4, 
	PREDICTION, 
	STALL, 
	VILLAGE_SHOP, 
	STATIONER_SHOP,
    LAND_LOT_GHETTO,
    LAND_LOT_OUTSKIRTS,
    LAND_LOT_CHINATOWN,
    LAND_LOT_CENTER;

    private static final String LICENSE_ARTICLE_PREFIX = "LICENSE_";

    public static LotteryArticle getLicenseArticleByLevel(int licenseLevel) throws NoSuchLicenseLevelException {
        LotteryArticle result = LotteryArticle.valueOf(LICENSE_ARTICLE_PREFIX + licenseLevel);
        if (result == null) {
            throw new NoSuchLicenseLevelException("Такого уровня лицензий не существует!");
        }
        return result;
    }
}
