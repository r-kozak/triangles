package com.kozak.triangles.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.User;

@SessionAttributes("user")
@Controller
public class ServiceController extends BaseController {

	/**
	 * @return страницу со списком служебного имущества
	 */
	@RequestMapping(value = "/service-buildings", method = RequestMethod.GET)
	public String getServiceBuildingsPage(Model model, User user) {
		model = addMoneyInfoToModel(model, user);
		return "service_buildings/list";
	}

	@RequestMapping(value = "/license-market", method = RequestMethod.GET)
	public String getLicenseMarketPage(Model model, User user) {
		model = addMoneyInfoToModel(model, user);
		return "service_buildings/license_market";
	}

}
