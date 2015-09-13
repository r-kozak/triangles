package com.kozak.triangles.controllers;

import java.util.Date;

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

import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.search.LotterySearch;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.utils.Consts;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@Controller
@RequestMapping(value = "/lottery")
public class LotteryController extends BaseController {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String buildingPage(Model model, User user, LotterySearch ls) {
        if (ls.isNeedClear())
            ls.clear();
        model.addAttribute("ls", ls);

        int userId = user.getId();
        String userBalance = trRep.getUserBalance(userId);
        int userDomi = userRep.getUserDomi(userId);
        model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId), userDomi);
        model.addAttribute("articles", SearchCollections.getLotteryArticles()); // статьи выигрыша
        model.addAttribute("ticketsCount", user.getLotteryTickets()); // количество лотерейных билетов

        return "lottery";
    }

    /**
     * Метод покупки лотерейных билетов. Устанавливает новое значение лотерейных билетов, снимает деньги со счета за
     * покупку.
     * 
     * @param count
     *            - количество для покупки (1, 10, 50)
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/buy-tickets", method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
    public @ResponseBody ResponseEntity<String> preBuildProperty(@RequestParam("count") int count, User user) {

        JSONObject resultJson = new JSONObject();
        int userId = user.getId();

        long userMoney = Long.parseLong(trRep.getUserBalance(userId));
        long userSolvency = Util.getSolvency(trRep, prRep, userId); // состоятельность пользователя

        // проверки на правильность количества покупаемых билетов
        if (count != 1 || count != 10 || count != 50) {
            Util.putErrorMsg(resultJson, "Такое количество недоступно для покупок");
        } else {
            // цена за один
            int priceToOneTicket = 0;
            if (count == 1) {
                priceToOneTicket = Consts.LOTTERY_TICKETS_PRICE[0];
            } else if (count == 10) {
                priceToOneTicket = Consts.LOTTERY_TICKETS_PRICE[1];
            } else if (count == 50) {
                priceToOneTicket = Consts.LOTTERY_TICKETS_PRICE[2];
            }

            // сумма покупки
            long purchaseSum = priceToOneTicket * count;

            // не хватает денег
            if (userSolvency < purchaseSum) {
                Util.putErrorMsg(resultJson, "Сумма покупки <b>(" + purchaseSum
                        + "&tridot;)</b> слишком большая. Вам не хватает денег.");
            } else {
                // установка пользователю нового количества лотерейных билетов
                user.setLotteryTickets(user.getLotteryTickets() + count);
                userRep.updateUser(user);

                // снятие денег со счета
                String descr = String.format("Покупка лотерейных билетов, %s штук", count);
                long newBalance = userMoney - purchaseSum;

                Transaction tr = new Transaction(descr, new Date(), purchaseSum, TransferT.SPEND, userId,
                        newBalance, ArticleCashFlowT.LOTTERY_TICKETS_BUY);
                trRep.addTransaction(tr);

                // добавить информацию о новом значении баланса, состоятельности, количества билетов
                resultJson.put("newBalance", newBalance);
                resultJson.put("newSolvency", Util.getSolvency(String.valueOf(newBalance), prRep, userId));
                resultJson.put("newTickets", user.getLotteryTickets());
            }
        }

        System.err.println(resultJson.toJSONString());
        String json = resultJson.toJSONString();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
    }
}
