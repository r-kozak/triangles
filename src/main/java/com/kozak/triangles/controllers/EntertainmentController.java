package com.kozak.triangles.controllers;

import java.util.Calendar;

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
	Calendar to = Calendar.getInstance();
	to.add(Calendar.SECOND, 3);
	model.addAttribute("to", to.getTimeInMillis());

	return "timer";
	// return "entertainment";
    }
}
