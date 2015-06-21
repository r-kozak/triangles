package com.kozak.triangles.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.repositories.UserRep;

@SessionAttributes("user")
@Controller
public class EntertainmentController {
    private UserRep userRepository;
    private TransactionRep transactRepository;

    @Autowired
    public EntertainmentController(UserRep userRepository, TransactionRep transactRepository) {
        this.userRepository = userRepository;
        this.transactRepository = transactRepository;
    }

    @RequestMapping(value = "/entertainment", method = RequestMethod.GET)
    String entertainmentGET() {
        return "entertainment";
    }

}
