package com.kozak.triangles.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kozak.triangles.entities.User;

public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null || !user.isAuthenticated()) {
                throw new Exception();
            }
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }
        return super.preHandle(request, response, handler);
    }
}
