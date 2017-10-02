package com.kozak.triangles.controller;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entity.User;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.exceptions.BonusIsNotAvailableException;
import com.kozak.triangles.exceptions.WinHandlingException;
import com.kozak.triangles.util.ResponseUtil;

@SessionAttributes("user")
@Controller
@RequestMapping(value = "/bonus")
public class BonusController extends BaseController {

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> isBonusAvailable(User user) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("available", bonusService.isBonusAvailable(user.getId()));
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/take", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> takeBonus(User user) {
        JSONObject resultJson = new JSONObject();
        long userId = user.getId();
        
        long balanceBefore = Long.parseLong(trRep.getUserBalance(userId));
        try {
            resultJson.put("bonus", bonusService.takeBonus(userId));
        } catch (BonusIsNotAvailableException | WinHandlingException e) {
            ResponseUtil.putErrorMsg(resultJson, e.getMessage());
        }
        long balanceAfter = Long.parseLong(trRep.getUserBalance(userId));

        // если баланс изменился, добавить информацию о новом балансе
        if (balanceAfter != balanceBefore) {
            long sum = balanceAfter - balanceBefore;
            ResponseUtil.addBalanceData(resultJson, sum, balanceBefore, userId, prRep, TransferType.PROFIT);
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

}
