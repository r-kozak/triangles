package com.kozak.triangles.controllers;

import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.utils.DateUtils;
import com.kozak.triangles.utils.SingletonData;
import com.kozak.triangles.utils.TagCreator;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@Controller
@RequestMapping(value = "/building")
public class BuildingController extends BaseController {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String buildingPage(Model model, User user) {

        int userId = user.getId();
        String userBalance = trRep.getUserBalance(userId);
        int userDomi = userRep.getUserDomi(userId);
        model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId), userDomi);

        model.addAttribute("commBuData", SingletonData.getCommBuildDataArray(buiDataRep)); // данные всех имуществ

        return "building";
    }

    /**
     * Вызывается перед началом стройки, чтобы пользователь мог выбрать район, посмотреть цену, дату приема в
     * эксплуатацию, баланс после утверждения проекта и т.д.
     * 
     * @param buiType
     *            - тип строения, который пользователь выбрал для строительства
     * @param user
     *            - пользователь
     * @return - информация о строительном проекте
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/pre-build", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> preBuildProperty(@RequestParam("buiType") String buiType, User user) {

        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

        JSONObject resultJson = new JSONObject();
        int userId = user.getId();

        long userMoney = Long.parseLong(trRep.getUserBalance(userId));
        long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя
        byte userLicenseLevel = userRep.getUserLicenseLevel(userId);

        CommBuildData dataOfBuilding = mapData.get(buiType); // данные конкретного типа имущества (здания)
        if (dataOfBuilding == null) {
            Util.putErrorMsg(resultJson, "Нет такого типа имущества.");
        } else {
            long priceOfBuilt = dataOfBuilding.getPurchasePriceMin(); // цена постройки = минимальная цена покупки
            if (userSolvency < priceOfBuilt) { // не хватает денег
                Util.putErrorMsg(resultJson, "Не хватает денег на постройку. Ваш максимум: <b>" + userSolvency + "</b>");
            } else {
                Date exploitation = DateUtils.getPlusDay(new Date(), dataOfBuilding.getBuildTime());

                // тег с районом города для выбора пользователем
                resultJson.put("buiType", buiType);
                resultJson.put("cityAreaTag", TagCreator.cityAreaTag(userLicenseLevel));
                resultJson.put("price", "Цена постройки: <b>" + priceOfBuilt + "&tridot;</b>"); // цена постройки
                resultJson.put("balanceAfter", "Баланс после постройки: <b>" + (userMoney - priceOfBuilt)
                        + "&tridot;</b>"); // баланс после постройки
                resultJson.put("exploitation", "Дата приема в эксплуатацию: <b>" + DateUtils.dateToString(exploitation)
                        + "</b>");
            }
        }
        System.err.println(resultJson.toJSONString());
        String json = resultJson.toJSONString();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/confirm-build", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> confirmBuildProperty(@RequestParam("buiType") String buiType,
            @RequestParam("cityArea") String cityArea, User user) {

        // получить данные всех коммерческих строений
        HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(buiDataRep);

        JSONObject resultJson = new JSONObject();
        int userId = user.getId();

        long userMoney = Long.parseLong(trRep.getUserBalance(userId));
        long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя
        byte userLicenseLevel = userRep.getUserLicenseLevel(userId);

        CommBuildData dataOfBuilding = mapData.get(buiType); // данные конкретного типа имущества (здания)

        if (dataOfBuilding == null) { // проверка на тип имущества
            Util.putErrorMsg(resultJson, "Нет такого типа имущества.");
        } else {
            long priceOfBuilt = dataOfBuilding.getPurchasePriceMin(); // цена постройки = минимальная цена покупки
            if (userSolvency < priceOfBuilt) { // не хватает денег
                Util.putErrorMsg(resultJson, "Не хватает денег на постройку. Ваш максимум: <b>" + userSolvency + "</b>");
            } else {
                // проверка можно ли строить в районе, что выбрал пользователь
                boolean cityAreaError = userLicenseLevel - 1 < CityAreasT.valueOf(cityArea).ordinal();
                if (cityAreaError) {
                    Util.putErrorMsg(resultJson,
                            "Ваша лицензия не позволяет строить в выбранном районе. Уровень лицензии: "
                                    + userLicenseLevel);
                } else {
                    // TODO создать модель имущества в процессе стройки

                }
            }
        }
        System.err.println(resultJson.toJSONString());
        String json = resultJson.toJSONString();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
    }
}
