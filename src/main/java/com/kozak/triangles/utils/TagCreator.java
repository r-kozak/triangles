package com.kozak.triangles.utils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.kozak.triangles.enums.CityAreas;
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

        List<CityAreas> areas = SearchCollections.getCityAreas();
        for (int i = 0; i < areas.size(); i++) {
            if (userLicenseLevel - 1 >= i) {
                result += "<option selected=\"selected\">" + areas.get(i) + "</option>";
            }
        }
        result += "</select>";
        return result;
    }

    public static String paginationTag(int lastPageNumber, int currPage, HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        // append previous page arrow
        int prevPage = currPage - 1;
        String pageSuff = (prevPage == 0) ? "#" : "?page=" + prevPage;
        String href = req.getRequestURI() + pageSuff;

        String onClick = "onclick=\"setPage(this); return false;\" value=\""
                + ((prevPage == 0) ? 1 : prevPage) + "\"";
        sb.append(String.format("<li %s><a href=\"%s\" %s>&laquo;</a></li>",
                ((currPage == 1) ? "class=\"disabled\"" : ""), href, onClick));

        if (lastPageNumber < 12) {
            for (int i = 1; i <= lastPageNumber; i++) {
                sb = generateOneButton(sb, currPage, i, req);
            }
        } else if (currPage <= 5) {
            for (int i = 1; i <= currPage + 2; i++) {
                sb = generateOneButton(sb, currPage, i, req);
            }
            sb.append("<li><a href=\"#\">...</a></li>");
            for (int i = lastPageNumber - 2; i <= lastPageNumber; i++) {
                sb = generateOneButton(sb, currPage, i, req);
            }
        } else if (currPage > 5 && currPage <= lastPageNumber - 5) {
            for (int i = 1; i <= 3; i++) {
                sb = generateOneButton(sb, currPage, i, req);
            }
            sb.append("<li><a href=\"#\">...</a></li>");
            for (int i = currPage - 1; i <= currPage + 1; i++) {
                sb = generateOneButton(sb, currPage, i, req);
            }
            sb.append("<li><a href=\"#\">...</a></li>");
            for (int i = lastPageNumber - 2; i <= lastPageNumber; i++) {
                sb = generateOneButton(sb, currPage, i, req);
            }
        } else if (currPage > lastPageNumber - 5) {
            for (int i = 1; i <= 3; i++) {
                sb = generateOneButton(sb, currPage, i, req);
            }
            sb.append("<li><a href=\"#\">...</a></li>");
            for (int i = (3 + currPage) / 2 - 1; i <= (3 + currPage) / 2 + 1; i++) {
                sb = generateOneButton(sb, currPage, i, req);
            }
            sb.append("<li><a href=\"#\">...</a></li>");
            for (int i = lastPageNumber - 4; i <= lastPageNumber; i++) {
                sb = generateOneButton(sb, currPage, i, req);
            }
        }

        // append next page arrow
        String disabled = (currPage == lastPageNumber) ? "class=\"disabled\"" : "";
        int nextPage = currPage + 1;
        String nextSuff = (nextPage > lastPageNumber) ? "?page=" + lastPageNumber : "?page=" + nextPage;

        onClick = "onclick=\"setPage(this); return false;\" value=\""
                + ((nextPage > lastPageNumber) ? lastPageNumber : nextPage) + "\"";
        href = req.getRequestURI() + nextSuff;
        sb.append(String.format("<li %s><a href=\"%s\" %s>&raquo;</a></li>", disabled, href, onClick));

        return sb.toString();
    }

    private static StringBuilder generateOneButton(StringBuilder sb, int currPage, int i, HttpServletRequest req) {

        String active = (i == currPage) ? "class=\"active\"" : "";
        String href = "#";// "\"" + req.getRequestURI() + "?page=" + i + "\"";
        String onClick = "onclick=\"setPage(this); return false;\" value=\"" + i + "\"";

        sb.append(String.format("<li %s><a href=%s %s>%s</a></li>", active, href, onClick, i));

        return sb;
    }
}
