package com.kozak.triangles.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.User;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@Controller
public class BuildingController extends BaseController {
    @RequestMapping(value = "/building", method = RequestMethod.GET)
    public String buildingPage(Model model, User user) {

        int userId = user.getId();
        String userBalance = trRep.getUserBalance(userId);
        int userDomi = userRep.getUserDomi(userId);
        model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId), userDomi);
        // данные имущества
        model.addAttribute("commBuData", buiDataRep.getCommBuildDataList());

        return "building";
    }

}
