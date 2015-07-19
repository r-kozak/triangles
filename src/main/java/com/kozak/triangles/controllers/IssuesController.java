package com.kozak.triangles.controllers;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.interfaces.Consts;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.search.TransactSearch;
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
    String transactionsGET(Model model, User user, TransactSearch ts) throws ParseException {
	if (ts.isNeedClear())
	    ts.clear();
	model.addAttribute("ts", ts);

	int page = Integer.parseInt(ts.getPage());
	// результат с БД [количество всего; транзакции с учетом пагинации]
	List<Object> dbResult = trRep.transList(page, user.getId(), ts);

	Long transCount = Long.valueOf(dbResult.get(0).toString());
	int lastPageNumber = (int) (transCount / Consts.ROWS_ON_PAGE)
		+ ((transCount % Consts.ROWS_ON_PAGE != 0) ? 1 : 0);

	@SuppressWarnings("unchecked")
	List<Transaction> transacs = (List<Transaction>) dbResult.get(1);

	// total sum
	long totalSum = 0;
	for (Transaction tr : transacs) {
	    totalSum += tr.getSum();
	}

	model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
	model.addAttribute("transacs", transacs);
	model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, page));
	model.addAttribute("articles", SearchCollections.getArticlesCashFlow());
	model.addAttribute("transfers", SearchCollections.getTransferTypes());
	model.addAttribute("totalSum", totalSum);

	return "transactions";
    }

}
