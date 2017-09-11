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
import com.kozak.triangles.utils.CommonUtil;
import com.kozak.triangles.utils.ResponseUtil;

@SessionAttributes("user")
@Controller
public class LandLotController extends BaseController {

    private static final String MONEY_NOT_ENOUGH_TO_BUY_LAND_LOT = "Не хватает денег на покупку нового участка. Цена участка = %d";

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/land-lot/price", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getLandLotPrice(User user, @RequestParam("city_area") CityArea cityArea) {
        JSONObject resultJson = new JSONObject();

        Long userId = user.getId();
        
        // получить цену на следующий участок в конкретном районе
        long nextLandLotPrice = landLotService.getNextLandLotPrice(userId, cityArea);
        long userSolvency = CommonUtil.getSolvency(trRep, prRep, userId); // состоятельность пользователя
        
        if (userSolvency >= nextLandLotPrice) {
            resultJson.put("price", nextLandLotPrice);
            resultJson.put("cityArea", cityArea.toString());
        } else {
            String msg = String.format(MONEY_NOT_ENOUGH_TO_BUY_LAND_LOT, nextLandLotPrice);
            ResponseUtil.putErrorMsg(resultJson, msg); // не хватает денег на покупку участка
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }
}
