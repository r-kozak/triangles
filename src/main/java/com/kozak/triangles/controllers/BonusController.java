package com.kozak.triangles.controllers;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.User;
import com.kozak.triangles.utils.ResponseUtil;

@SessionAttributes("user")
@Controller
@RequestMapping(value = "/bonus")
public class BonusController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BonusController.class);

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> isBonusAvailable(User user) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("available", true);
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

}
