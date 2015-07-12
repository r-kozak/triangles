package com.kozak.triangles.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.Test1;
import com.kozak.triangles.repositories.TestRep;

@SessionAttributes("user")
@Controller
public class RelationsController {
    @Autowired
    TestRep testRep;

    @RequestMapping(value = "/relations", method = RequestMethod.GET)
    String relationsGET() {
	List<Test1> t1 = testRep.selectT1();
	System.err.println(t1);
	return "relations";
    }
}
