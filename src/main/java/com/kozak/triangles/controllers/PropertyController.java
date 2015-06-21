package com.kozak.triangles.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.User;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.utils.ModelCreator;

@SessionAttributes("user")
@Controller
public class PropertyController {
    private UserRep userRepository;
    private TransactionRep transactRepository;

    @Autowired
    public PropertyController(UserRep userRepository, TransactionRep transactRepository) {
        this.userRepository = userRepository;
        this.transactRepository = transactRepository;
    }

    @RequestMapping(value = "/property", method = RequestMethod.GET)
    String propertyGET(@ModelAttribute("user") User user, Model model) {
        model = ModelCreator.addBalance(model, transactRepository.getUserBalance(user.getId()));
        return "property";
    }

}
