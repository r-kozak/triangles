package com.kozak.triangles.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.repositories.TestRep;
import com.kozak.triangles.search.TransactForm;

@SessionAttributes("user")
@Controller
public class EntertainmentController {

    @Autowired
    TestRep testRep;

    @RequestMapping(value = "/entertainment", method = RequestMethod.GET)
    String entertainmentGET(Model model, TransactForm tf) {
	// TransactForm tf = new TransactForm();
	// tf.setProfit(true);

	// List<String> preCheckedVals = new ArrayList<String>();
	// preCheckedVals.add("CREDIT");
	// tf.setArticles(preCheckedVals);
	if (tf.isNeedClear()) {
	    tf.clear();
	}
	model.addAttribute("tf", tf);

	List<String> articles = new ArrayList<String>();
	for (ArticleCashFlowT a : ArticleCashFlowT.values()) {
	    articles.add(a.name());
	}

	model.addAttribute("articles", articles);

	return "entertainment";
    }

    @RequestMapping(value = "/entertainment", method = RequestMethod.POST)
    String entertainmentPOST(Model model, TransactForm tf) {
	model.addAttribute("tf", tf);

	List<String> articles = new ArrayList<String>();
	for (ArticleCashFlowT a : ArticleCashFlowT.values()) {
	    articles.add(a.name());
	}

	model.addAttribute("articles", articles);
	return "entertainment";
    }
}
