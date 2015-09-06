package com.kozak.triangles.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kozak.triangles.repositories.UserRep;

@SessionAttributes("user")
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    UserRep userRep;
    @Autowired
    HttpSession session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // User user = (User) session.getAttribute("user");
        // if (user == null || user.getLogin() == null) {
        // String ul = Cooker.getCookieByName(request, "ul");
        // String up = Cooker.getCookieByName(request, "up");
        // if (ul != null) {
        // user = userRep.getUserByEncrLoginAndPassword(ul, up);
        // if (user != null) {
        // session.setAttribute("user", user);
        // response.sendRedirect(request.getContextPath() + "/home");
        // return true;
        // }
        // }
        // response.sendRedirect(request.getContextPath() + "/");
        // return false;
        // }
        return super.preHandle(request, response, handler);
    }
}
