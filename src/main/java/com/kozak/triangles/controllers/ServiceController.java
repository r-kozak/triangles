package com.kozak.triangles.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.LicenseMarket;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.models.Requirement;
import com.kozak.triangles.services.LicenseMarketService;

@SessionAttributes("user")
@Controller
public class ServiceController extends BaseController {

	@Autowired
	private LicenseMarketService licenseMarketService;

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
		Integer userId = user.getId();

		boolean isMarketBuilt = licenseMarketService.isMarketBuilt(userId); // построен ли магазин
		model.addAttribute("marketBuilt", isMarketBuilt);
		model.addAttribute("marketLevelMax", LicenseMarket.MAX_LEVEL);

		if (isMarketBuilt) {
			// если магазин построен, тогда передать на страницу
			model.addAttribute("marketLevel", licenseMarketService.getLicenseMarket(userId)); // текущий уровень магазина
			// TODO
			// общее количество лицензий на продаже

			// количество лицензий разных уровней, доступных для продажи
			model = addLicenseCountInfoToModel(model, userId);
		} else {
			// если магазин не построен, передать на страницу требования (выполненные и не выполненные)
			List<Requirement> requirements = licenseMarketService.computeBuildRequirements(user);
			model.addAttribute("requirementsToBuild", requirements);
		}
		return "service_buildings/license_market";
	}

}
