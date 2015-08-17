package com.kozak.triangles.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.utils.Cooker;

public class StartInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    UserRep userRep;
    @Autowired
    HttpSession session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	String userLogin = Cooker.getCookieByName(request, "userLogin");
	if (userLogin != null) {
	    session.setAttribute("user", userRep.getCurrentUserByLogin(userLogin));
	    response.sendRedirect(request.getContextPath() + "/home");
	    return false;
	}
	return super.preHandle(request, response, handler);
    }
}
