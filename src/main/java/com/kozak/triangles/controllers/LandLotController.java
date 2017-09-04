package com.kozak.triangles.controllers;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.utils.ResponseUtil;

@RestController()
public class LandLotController extends BaseController {

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/land-lot/price", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getLandLotPrice(@RequestParam("city_area") CityArea cityArea) {
        JSONObject resultJson = new JSONObject();

        // TODO compute correct price
        resultJson.put("price", 321L);
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }
}
