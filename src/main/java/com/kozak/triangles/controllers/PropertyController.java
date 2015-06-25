package com.kozak.triangles.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.User;
import com.kozak.triangles.repositories.BuildingDataRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.utils.ModelCreator;

@SessionAttributes("user")
@RequestMapping(value = "/property")
@Controller
public class PropertyController {
    private TransactionRep trRep;
    private BuildingDataRep buiDataRep;

    @Autowired
    public PropertyController(TransactionRep trRep, BuildingDataRep buiDataRep) {
        this.trRep = trRep;
        this.buiDataRep = buiDataRep;
    }

    @RequestMapping(method = RequestMethod.GET)
    String propertyGET(@ModelAttribute("user") User user, Model model) {
        model = ModelCreator.addBalance(model, trRep.getUserBalance(user.getId()));
        return "property";
    }

    @RequestMapping(value = "/r-e-market", method = RequestMethod.GET)
    String realEstMarket(@ModelAttribute("user") User user, Model model) {
        model = ModelCreator.addBalance(model, trRep.getUserBalance(user.getId()));
        model.addAttribute("proposals", buiDataRep.getREProposalsList());
        return "remarket";
    }
}
