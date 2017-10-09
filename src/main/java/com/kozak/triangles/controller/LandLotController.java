package com.kozak.triangles.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entity.User;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.exception.MoneyNotEnoughException;
import com.kozak.triangles.model.LandLotsInfo;
import com.kozak.triangles.util.CommonUtil;
import com.kozak.triangles.util.ResponseUtil;

@SessionAttributes("user")
@Controller
@RequestMapping("/land-lot")
public class LandLotController extends BaseController {

    /**
     * @return цену на следующий участок земли в конкретном районе для конкретного пользователя
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/price", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getLandLotPrice(User user, @RequestParam("city_area") CityArea cityArea) {
        JSONObject resultJson = new JSONObject();

        try {
            // получить цену на следующий участок в конкретном районе
            long nextLandLotPrice = landLotService.getNextLandLotPrice(user.getId(), cityArea);

            resultJson.put("price", CommonUtil.moneyFormat(nextLandLotPrice));
            resultJson.put("cityArea", cityArea.toString());
        } catch (MoneyNotEnoughException e) {
            ResponseUtil.putErrorMsg(resultJson, e.getMessage()); // не хватает денег на покупку участка
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * Позволяет купить участок земли в конкретном районе для конкретного пользователя.
     * 
     * @throws MoneyNotEnoughException
     *             если у пользователя недостаточно денег
     */
    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> buyLandLot(User user, @RequestParam("city_area") CityArea cityArea) {
        JSONObject resultJson = new JSONObject();

        try {
            landLotService.buyOneLandLot(user.getId(), cityArea);
        } catch (MoneyNotEnoughException e) {
            ResponseUtil.putErrorMsg(resultJson, e.getMessage()); // не хватает денег на покупку участка
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * @return каким построенным имуществом или объектами строительства заняты участки в конкретном районе
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getLandLotInfo(User user, @RequestParam("city_area") CityArea cityArea) {
        JSONObject resultJson = new JSONObject();

        List<LandLotsInfo> info = landLotService.getLandLotInfo(user.getId(), cityArea);
        resultJson.put("info", info);
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }
}
