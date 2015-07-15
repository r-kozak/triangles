package com.kozak.triangles.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.interfaces.Consts;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.search.TransactForm;
import com.kozak.triangles.utils.TagCreator;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@Controller
public class IssuesController {
    @Autowired
    private TransactionRep trRep;

    @RequestMapping(value = "/issues", method = RequestMethod.GET)
    String propertyGET() {
        return "issues";
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    String transactionsGET(Model model, User user, HttpServletRequest request, TransactForm tf) {
        String contextPath = request.getContextPath();
        Integer page = Util.getPageNumber(request);

        Long transCount = trRep.allTrCount(user.getId());
        int lastPageNumber = (int) (transCount / Consts.ROWS_ON_PAGE)
                + ((transCount % Consts.ROWS_ON_PAGE != 0) ? 1 : 0);
        List<Transaction> transacs = trRep.transList(page, user.getId());

        model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
        model.addAttribute("transacs", transacs);
        model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, contextPath + "/transactions?", page));

        // TODO test search
        if (tf.isNeedClear()) {
            tf.clear();
        }
        model.addAttribute("tf", tf);

        List<String> articles = new ArrayList<String>();
        for (ArticleCashFlowT a : ArticleCashFlowT.values()) {
            articles.add(a.name());
        }

        // test transfer
        Map<String, Object> mo = new HashMap<String, Object>();
        tf.setCompanyName("The Selected Company");
        mo.put("tf", tf);

        List<String> companyNames = new ArrayList<String>();
        companyNames.add("First Company Name");
        companyNames.add("The Selected Company");
        companyNames.add("Last Company Name");

        mo.put("companyNames", companyNames);

        Map<String, Map<String, Object>> modelForView = new HashMap<String, Map<String, Object>>();
        model.addAttribute("vars", mo);
        //
        model.addAttribute("articles", articles);
        // //

        return "transactions";
    }
}
