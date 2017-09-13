package com.kozak.triangles.controllers;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.User;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.exceptions.MoneyNotEnoughException;
import com.kozak.triangles.utils.ResponseUtil;

@SessionAttributes("user")
@Controller
public class LandLotController extends BaseController {

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/land-lot/price", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getLandLotPrice(User user, @RequestParam("city_area") CityArea cityArea) {
        JSONObject resultJson = new JSONObject();

        Long userId = user.getId();
        
        try {
            // получить цену на следующий участок в конкретном районе
            long nextLandLotPrice = landLotService.getNextLandLotPrice(userId, cityArea);

            resultJson.put("price", nextLandLotPrice);
            resultJson.put("cityArea", cityArea.toString());
        } catch (MoneyNotEnoughException e) {
            ResponseUtil.putErrorMsg(resultJson, e.getMessage()); // не хватает денег на покупку участка
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    @RequestMapping(value = "/land-lot/buy", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> buyLandLot(User user, @RequestParam("city_area") CityArea cityArea) {
        JSONObject resultJson = new JSONObject();

        Long userId = user.getId();

        try {
            landLotService.buyOneLandLot(userId, cityArea);
        } catch (MoneyNotEnoughException e) {
            ResponseUtil.putErrorMsg(resultJson, e.getMessage()); // не хватает денег на покупку участка
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }
}
