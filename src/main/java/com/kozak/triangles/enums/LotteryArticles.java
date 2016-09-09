package com.kozak.triangles.enums;

import com.kozak.triangles.exceptions.NoSuchLicenseLevelException;

/**
 * Деньги, Повышение уровня имущества, Повышение уровня кассы имущества, Лицензия на строительство ур2, Лицензия на
 * строительство ур3, Лицензия на строительство ур4, Предсказание, Киоск, Сельский магазин, Магазин канцтоваров
 * 
 */
public enum LotteryArticles {

	TRIANGLES, 
	PROPERTY_UP, 
	CASH_UP, 
	LICENSE_2, 
	LICENSE_3, 
	LICENSE_4, 
	PREDICTION, 
	STALL, 
	VILLAGE_SHOP, 
	STATIONER_SHOP;

	private static final String LICENSE_ARTICLE_PREFIX = "LICENSE_";

	public static LotteryArticles getLicenseArticleByLevel(int licenseLevel) throws NoSuchLicenseLevelException {
		LotteryArticles result = LotteryArticles.valueOf(LICENSE_ARTICLE_PREFIX + licenseLevel);
		if (result == null) {
			throw new NoSuchLicenseLevelException("Такого уровня лицензий не существует!");
		}
		return result;
	}
}
