package com.kozak.triangles.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@SessionAttributes("user")
@Controller
public class EntertainmentController {

    @RequestMapping(value = "/entertainment", method = RequestMethod.GET)
    String entertainmentGET(Model model) {
	return "entertainment";
    }
}
