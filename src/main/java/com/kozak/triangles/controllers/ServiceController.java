package com.kozak.triangles.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ServiceController {

	@RequestMapping(value = "/service-buildings", method = RequestMethod.GET)
	public String getServiceBuildingsPage() {
		return "service_buildings/list";
	}

	@RequestMapping(value = "/license-market", method = RequestMethod.GET)
	public String getLicenseMarketPage() {
		return "service_buildings/license_market";
	}

}
