package com.kozak.triangles.controllers;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.LicenseMarket;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.models.Requirement;
import com.kozak.triangles.services.LicenseMarketService;
import com.kozak.triangles.utils.ResponseUtil;

@SessionAttributes("user")
@Controller
public class ServiceBuildingController extends BaseController {

    private static final String ERROR_OCCURRED_WHILE_LEVEL_UP = "Не все требования соблюдены для повышения уровня или достигнут последний уровень.";
    private static final String MAX_MARKET_LEVEL_ACHIEVED = "Максимальный уровень достигнут.";

    @Autowired
    private LicenseMarketService licenseMarketService;

    /**
     * @return страницу со списком служебного имущества
     */
    @RequestMapping(value = "/service-buildings", method = RequestMethod.GET)
    public String getServiceBuildingsPage(Model model, User user) {
        model = addMoneyInfoToModel(model, user);
        long userId = user.getId();
        boolean licenseMarketActive = licenseMarketService.isMarketBuilt(userId)
                && licenseMarketService.isMarketCanFunction(userId);
        model.addAttribute("licenseMarketActive", licenseMarketActive);
        return "service_buildings/list";
    }

    /**
     * @return страницу с магазином лицензий
     */
    @RequestMapping(value = "/license-market", method = RequestMethod.GET)
    public String getLicenseMarketPage(Model model, User user) {
        long userId = user.getId();

        boolean isMarketBuilt = licenseMarketService.isMarketBuilt(userId); // построен ли магазин
        model.addAttribute("marketBuilt", isMarketBuilt);
        model.addAttribute("marketLevelMax", LicenseMarket.MAX_LEVEL);

        if (isMarketBuilt) {
            // если магазин построен, тогда
            licenseMarketService.processSoldLicenses(userId); // обработать проданные лицензии (начислить деньги, удалить партию)

            // передать на страницу текущий уровень магазина
            LicenseMarket market = licenseMarketService.getLicenseMarket(userId, true);
            model.addAttribute("marketLevel", market.getLevel());
            model.addAttribute("licensesConsignments", market.getLicensesConsignments()); // список лицензий на продаже
            model = addLicenseCountInfoToModel(model, userId); // количество лицензий разных уровней, доступных для продажи

            // может ли магазин функционировать
            boolean isMarketCanFunction = licenseMarketService.isMarketCanFunction(userId);
            model.addAttribute("isMarketCanFunction", isMarketCanFunction);
            if (isMarketCanFunction) {
                // можно ли продавать лицензии разных уровней. Можно, если выполнены разные требования
                model.addAttribute("requirementToSellLic2", licenseMarketService.isLicensesCanBeSold(2, userId)); // уровня 2
                model.addAttribute("requirementToSellLic3", licenseMarketService.isLicensesCanBeSold(3, userId)); // уровня 3
                model.addAttribute("requirementToSellLic4", licenseMarketService.isLicensesCanBeSold(4, userId)); // уровня 4
            } else {
                // магазин не может функционировать, вычислить требования для восстановления функций
                List<Requirement> requirements = licenseMarketService.computeFunctionRequirements(userId);
                model.addAttribute("requirementsToFunction", requirements);
            }
        } else {
            // если магазин не построен, передать на страницу требования (выполненные и не выполненные)
            List<Requirement> requirements = licenseMarketService.computeBuildRequirements(userId);
            model.addAttribute("requirementsToBuild", requirements);

            // выполнены ли все требования для постройки магазина
            model.addAttribute("isMarketCanBeBuilt", licenseMarketService.isMarkerCanBeBuilt(userId));
        }
        model = addMoneyInfoToModel(model, user);
        return "service_buildings/license_market";
    }

    /**
     * Построить магазин
     */
    @RequestMapping(value = "/build-license-market", method = RequestMethod.GET)
    public String buildLicenseMarket(Model model, User user) {
        long userId = user.getId();
        licenseMarketService.buildNewLicenseMarket(userId);

        return "redirect:/license-market";
    }

    /**
     * Подтвердить продажу лицензий
     */
    @RequestMapping(value = "/confirm-license-selling", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> confirmLicenseSelling(@RequestParam("licenseCount") int licenseCount,
            @RequestParam("licenseLevel") byte licenseLevel, User user) {

        long userId = user.getId();
        JSONObject resultJson = licenseMarketService.confirmLicenseSelling(licenseCount, licenseLevel, userId);
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * Получить требования для повышения уровня имущества
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/license-market/pre-level-up", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getRequirementsForLicenseMarketLevelUp(Model model, User user) {
        long userId = user.getId();

        JSONObject resultJson = new JSONObject();
        if (!licenseMarketService.isMaxLevelAchieved(userId)) {
            List<Requirement> requirementsForLevelUp = licenseMarketService.computeRequirementsForLevelUp(userId);
            resultJson.put("requirements", requirementsForLevelUp);
        } else {
            ResponseUtil.putErrorMsg(resultJson, MAX_MARKET_LEVEL_ACHIEVED); // достигнут последний уровень магазина
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * Получить требования для повышения уровня имущества
     */
    @RequestMapping(value = "/license-market/confirm-level-up", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> confirmLicenseMarketLevelUp(Model model, User user) {
        long userId = user.getId();
        JSONObject resultJson = new JSONObject();
        boolean upped = licenseMarketService.upLicenseMarketLevel(userId);
        if (!upped) {
            ResponseUtil.putErrorMsg(resultJson, ERROR_OCCURRED_WHILE_LEVEL_UP); // возникла ошибка
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

}
