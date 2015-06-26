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
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@RequestMapping(value = "/property")
@Controller
public class PropertyController {
    private TransactionRep trRep;
    private BuildingDataRep buiDataRep;
    private PropertyRep prRep;

    @Autowired
    public PropertyController(TransactionRep trRep, BuildingDataRep buiDataRep, PropertyRep prRep) {
        this.trRep = trRep;
        this.buiDataRep = buiDataRep;
        this.prRep = prRep;
    }

    @RequestMapping(method = RequestMethod.GET)
    String propertyGET(@ModelAttribute("user") User user, Model model) {
        model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
        return "property";
    }

    @RequestMapping(value = "/r-e-market", method = RequestMethod.GET)
    String realEstMarket(@ModelAttribute("user") User user, Model model) {
        model = Util.addBalanceToModel(model, trRep.getUserBalance(user.getId()));
        model.addAttribute("proposals", buiDataRep.getREProposalsList());
        return "remarket";
    }

    @RequestMapping(value = "/buy/{prId}", method = RequestMethod.GET)
    String buyProperty(@PathVariable int prId, User user, Model model) {
        RealEstateProposal prop = buiDataRep.getREProposalById(prId);

        if (prop == null || !prop.isValid()) {
            model.addAttribute("errorMsg",
                    "Вы не успели. Имущество уже было куплено кем-то. Попробуйте купить что-нибудь другое.");
            return "error";
        } else {
            Long userMoney = Long.parseLong(trRep.getUserBalance(user.getId()));
            // (ост. стоим. всего имущества / 2)
            long sellSum = prRep.getSellingSumAllPropByUser(user.getId()) / 2;

            // TODO решить с валидностью предложения при начале покупки. и в конце, если покупка отменена
            prop.setValid(false);

            // хватает денег
            if (userMoney >= prop.getPurchasePrice()) {
                model.addAttribute("title", "Покупка за наличные");
                model.addAttribute("percent", Util.getAreaPercent(prop.getCityArea()));
                model.addAttribute("prop", prop);
                model.addAttribute("data", buiDataRep.getCommBuildDataByType(prop.getCommBuildingType()));
                return "apply_buy";
            } else if (userMoney + sellSum >= prop.getPurchasePrice()) { // войдем в минуса
                // TODO спросить о кредите
            } else if (userMoney <= 0) {
                model.addAttribute("errorMsg",
                        "Ваш баланс равен или меньше нуля. Покупка невозможна. Продайте что-нибудь или заработайте денег.");
            } else if (userMoney + sellSum < prop.getPurchasePrice()) {
                model.addAttribute("errorMsg", "Ваша "
                        + "<a href\"${pageContext.request.contextPath}/help\">состоятельность</a> "
                        + "не позволяет вам купить это имущество. Купить что-нибудь другое.");
            }
            return "error";
        }
    }

    @RequestMapping(value = "/buy/{prId}", method = RequestMethod.POST)
    String applyBuy(@ModelAttribute("prId") int prId, @ModelAttribute("action") String action, User user, Model model) {

        return null;
    }
}
