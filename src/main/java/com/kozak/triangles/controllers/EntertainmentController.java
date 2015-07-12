package com.kozak.triangles.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.Test1;
import com.kozak.triangles.entities.Test2;
import com.kozak.triangles.repositories.TestRep;

@SessionAttributes("user")
@Controller
public class EntertainmentController {

    @Autowired
    TestRep testRep;

    @RequestMapping(value = "/entertainment", method = RequestMethod.GET)
    String entertainmentGET(Model model) {
	Test1 t1 = new Test1();
	t1.setAt1(11);
	t1.setAt2(23);
	t1.setT1(523);
	testRep.addTest(t1);

	List<Test2> list = new ArrayList<Test2>(2);
	Test2 t2 = new Test2();
	t2.setAt1(44);
	t2.setAt2(55);
	t2.setT2(66);
	t2.setTest1(t1);
	testRep.addTest(t2);

	Test2 t22 = new Test2();
	t22.setAt1(44);
	t22.setAt2(55);
	t22.setT2(66);
	t22.setTest1(t1);
	testRep.addTest(t22);

	list.add(t2);
	list.add(t22);

	t1.setTest2(list);
	testRep.updateTest(t1);

	return "timer";
    }
}
