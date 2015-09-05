package com.kozak.triangles.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.repositories.UserRep;

@SessionAttributes("user")
@Controller
public class BuildingController {
    private UserRep userRepository;

    @RequestMapping(value = "/building", method = RequestMethod.GET)
    public String buildingPage(Model model) {
        return "building";
    }

}
