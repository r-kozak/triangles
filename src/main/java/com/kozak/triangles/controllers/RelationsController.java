package com.kozak.triangles.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@SessionAttributes("user")
@Controller
public class RelationsController {

    @RequestMapping(value = "/relations", method = RequestMethod.GET)
    String relationsGET() {
        return "relations";
    }

}
