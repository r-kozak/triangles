package com.kozak.triangles.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Cooker {
    /**
     * @param response
     *            для добавления в него Cookies
     * @param time
     *            (-1 - до закрытия браузера, 0 - удалить немедленно, >0 - время в секундах, сколько будут жить Cookies)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int time) {
        Cookie c = new Cookie(name, value);
        c.setMaxAge(time);
        response.addCookie(c);
    }

    public static String getCookieByName(HttpServletRequest request, String cName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
