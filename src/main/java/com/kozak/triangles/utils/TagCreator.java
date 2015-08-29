package com.kozak.triangles.utils;

/**
 * Created by Roman on 07.04.2015 19:42.
 */

public class TagCreator {

    public static String tagNav(int lastPageNumber, int currPage) {
	StringBuilder sb = new StringBuilder();
	if (lastPageNumber < 12) {
	    for (int i = 1; i <= lastPageNumber; i++) {
		sb = generateTagNav(sb, currPage, i);
	    }
	} else if (currPage <= 5) {
	    for (int i = 1; i <= currPage + 2; i++) {
		sb = generateTagNav(sb, currPage, i);
	    }
	    sb.append(" ... ");
	    for (int i = lastPageNumber - 2; i <= lastPageNumber; i++) {
		sb = generateTagNav(sb, currPage, i);
	    }
	} else if (currPage > 5 && currPage <= lastPageNumber - 5) {
	    for (int i = 1; i <= 3; i++) {
		sb = generateTagNav(sb, currPage, i);
	    }
	    sb.append(" ... ");
	    for (int i = currPage - 1; i <= currPage + 1; i++) {
		sb = generateTagNav(sb, currPage, i);
	    }
	    sb.append(" ... ");
	    for (int i = lastPageNumber - 2; i <= lastPageNumber; i++) {
		sb = generateTagNav(sb, currPage, i);
	    }
	} else if (currPage > lastPageNumber - 5) {
	    for (int i = 1; i <= 3; i++) {
		sb = generateTagNav(sb, currPage, i);
	    }
	    sb.append(" ... ");
	    for (int i = (3 + currPage) / 2 - 1; i <= (3 + currPage) / 2 + 1; i++) {
		sb = generateTagNav(sb, currPage, i);
	    }
	    sb.append(" ... ");
	    for (int i = lastPageNumber - 4; i <= lastPageNumber; i++) {
		sb = generateTagNav(sb, currPage, i);
	    }
	}

	return sb.toString();
    }

    private static StringBuilder generateTagNav(StringBuilder sb, int currPage, int i) {
	sb.append("<li>");

	sb.append("<a href");
	if (i == currPage) {
	    sb.append(" class=\"whitesquareactive\"");
	} else {
	    sb.append(" class=\"whitesquare\"");
	}
	sb.append("onclick=\"setPage(this); return false;\" value=\"" + i + "\"");
	sb.append(">");
	sb.append(i);
	sb.append("</a>");

	sb.append("</li>");
	return sb;
    }
}
