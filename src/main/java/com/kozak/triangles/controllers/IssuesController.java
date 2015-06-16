package com.kozak.triangles.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.User;
import com.kozak.triangles.repositories.TransactionRepository;
import com.kozak.triangles.repositories.UserRepository;
import com.kozak.triangles.utils.ModelCreator;

@SessionAttributes("user")
@Controller
public class IssuesController {
    private UserRepository userRepository;
    private TransactionRepository transactRepository;

    @Autowired
    public IssuesController(UserRepository userRepository, TransactionRepository transactRepository) {
        this.userRepository = userRepository;
        this.transactRepository = transactRepository;
    }

    @RequestMapping(value = "/issues", method = RequestMethod.GET)
    String propertyGET() {
        return "issues";
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    String transactionsGET(Model model, User user) {

        model = ModelCreator.addBalance(model, transactRepository.getUserBalance(user.getId()));
        model.addAttribute("transacs", transactRepository.getAllUserTransactions(user.getId()));

        return "transactions";
    }

}
