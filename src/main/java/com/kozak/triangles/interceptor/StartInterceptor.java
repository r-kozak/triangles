package com.kozak.triangles.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kozak.triangles.entity.User;
import com.kozak.triangles.repository.UserRep;
import com.kozak.triangles.util.Cooker;

public class StartInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    UserRep userRep;
    @Autowired
    HttpSession session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ul = Cooker.getCookieByName(request, "ul");
        String up = Cooker.getCookieByName(request, "up");
        if (ul != null && up != null) {
            User user = userRep.getUserByEncrLoginAndPassword(ul, up);
            if (user != null) {
                session.setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/home");
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }
}
