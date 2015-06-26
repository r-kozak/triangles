package com.kozak.triangles.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.User;
import com.kozak.triangles.interfaces.Consts;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.utils.Util;
import com.kozak.triangles.utils.TagCreator;

@SessionAttributes("user")
@Controller
public class IssuesController {
    private UserRep userRep;
    private TransactionRep trRep;

    @Autowired
    public IssuesController(UserRep userRepository, TransactionRep transactRepository) {
        this.userRep = userRepository;
        this.trRep = transactRepository;
    }

    @RequestMapping(value = "/issues", method = RequestMethod.GET)
    String propertyGET() {
        return "issues";
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    String transactionsGET(Model model, User user, HttpServletRequest request) {
        String contextPath = request.getContextPath();

        Integer page = 1;
        if (request.getParameterValues("page") != null) {
            page = Integer.parseInt(request.getParameterValues("page")[0]);
        }

        Long transCount = trRep.allTrCount(user.getId());
        int lastPageNumber = (int) (transCount / Consts.TRANS_ON_PAGE)
                + ((transCount % Consts.TRANS_ON_PAGE != 0) ? 1 : 0);
        List transacs = trRep.transList(page, user.getId());

        model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
        model.addAttribute("transacs", transacs);
        model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, contextPath + "/transactions?", page));

        return "transactions";
    }
}
