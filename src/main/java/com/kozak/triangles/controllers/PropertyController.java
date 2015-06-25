package com.kozak.triangles.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.RealEstateProposal;
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

    @RequestMapping(value = "/buy/{id}", method = RequestMethod.GET)
    String buyProperty(@PathVariable int id, User user, Model model) {
        RealEstateProposal prop = buiDataRep.getREProposalById(id);

        if (prop == null || !prop.isValid()) {
            model.addAttribute("errorMsg",
                    "Вы не успели. Имущество уже было куплено кем-то. Попробуйте купить что-нибудь другое.");
            return "error";
        } else {
            Long userMoney = Long.parseLong(trRep.getUserBalance(user.getId()));
            // TODO посчитать (ост. стоим. всего имущества / 2)
            long ostStoimost = 0;

            if (userMoney <= 0) {
                model.addAttribute("errorMsg",
                        "Ваш баланс равен или меньше нуля. Покупка невозможна. Продайте что-нибудь или заработайте денег.");
                return "error";
            } else if (userMoney >= prop.getPurchasePrice()) {
                // TODO покупка состоялась
            } else if (userMoney + ostStoimost >= prop.getPurchasePrice()) {
                // TODO спросить о кредите
            } else if (userMoney + ostStoimost < prop.getPurchasePrice()) {
                // TODO нала и ост стоим ваш имущ / 2 не хватает для залога кредита
            }
        }

        return null;
    }
}
