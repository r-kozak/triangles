package com.kozak.triangles.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.repositories.TransactionRepository;
import com.kozak.triangles.repositories.UserRepository;

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

}
