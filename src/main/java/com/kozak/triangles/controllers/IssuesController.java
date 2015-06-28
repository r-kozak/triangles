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
import com.kozak.triangles.utils.TagCreator;
import com.kozak.triangles.utils.Util;

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
        Integer page = Util.getPageNumber(request);

        Long transCount = trRep.allTrCount(user.getId());
        int lastPageNumber = (int) (transCount / Consts.ROWS_ON_PAGE)
                + ((transCount % Consts.ROWS_ON_PAGE != 0) ? 1 : 0);
        List transacs = trRep.transList(page, user.getId());

        model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
        model.addAttribute("transacs", transacs);
        model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, contextPath + "/transactions?", page));

        return "transactions";
    }
}
