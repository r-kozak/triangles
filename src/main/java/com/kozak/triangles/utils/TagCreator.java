package com.kozak.triangles.utils;

import java.util.List;

import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.search.SearchCollections;

/**
 * Created by Roman on 07.04.2015 19:42.
 */

public class TagCreator {

    /**
     * Формирование тега <select> для выбора пользователем района города перед началом постройки здания. Тег формируется
     * в зависимости от уровня лицензии пользователя.
     * 
     * @param userLicenseLevel
     *            - уровень лицензии пользователя на стройку имущества
     * @return тег
     */
    public static String cityAreaTag(byte userLicenseLevel) {
        String result = "<select id=\"city_area\" class=\"form-control\">";

        List<CityAreasT> areas = SearchCollections.getCityAreas();
        for (int i = 0; i < areas.size(); i++) {
            if (userLicenseLevel - 1 >= i) {
                result += "<option selected=\"selected\">" + areas.get(i) + "</option>";
            }
        }
        result += "</select>";
        return result;
    }
}
