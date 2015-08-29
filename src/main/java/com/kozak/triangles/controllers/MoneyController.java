package com.kozak.triangles.controllers;

import java.text.ParseException;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.search.TransactSearch;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@Controller
public class MoneyController {
    @Autowired
    private TransactionRep trRep;
    @Autowired
    private PropertyRep prRep;
    @Autowired
    private UserRep userRep;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/buy-triangles", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> jqueryBuyTriangles(@RequestParam("count") int count,
	    @RequestParam("action") String action, User user) {

	JSONObject resultJson = new JSONObject();
	resultJson.put("message", action + count);

	String json = resultJson.toJSONString();
	System.out.println(json);

	HttpHeaders responseHeaders = new HttpHeaders();
	responseHeaders.add("Content-Type", "application/json; charset=utf-8");
	return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    String transactionsGET(Model model, User user, TransactSearch ts) throws ParseException {
	if (ts.isNeedClear())
	    ts.clear();
	model.addAttribute("ts", ts);

	int page = Integer.parseInt(ts.getPage());
	// результат с БД [количество всего; транзакции с учетом пагинации]
	List<Object> dbResult = trRep.transList(page, user.getId(), ts, /* ts.isShowAll() */true);

	// Long transCount = Long.valueOf(dbResult.get(0).toString());

	// int lastPageNumber = 1;
	// if (!ts.isShowAll()) { // если показать транзакции НЕ ВСЕ (с пагинацией_
	// lastPageNumber = (int) (transCount / Consts.ROWS_ON_PAGE)
	// + ((transCount % Consts.ROWS_ON_PAGE != 0) ? 1 : 0);
	// }
	List<Transaction> transacs = (List<Transaction>) dbResult.get(1);

	// total sum
	long totalSum = 0;
	for (Transaction tr : transacs) {
	    totalSum += tr.getSum();
	}

	int userId = user.getId();
	String userBalance = trRep.getUserBalance(userId);
	int userDomi = userRep.getUserDomi(userId);
	model = Util.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId), userDomi);
	model.addAttribute("transacs", transacs);
	// model.addAttribute("tagNav", TagCreator.tagNav(lastPageNumber, page));
	model.addAttribute("articles", SearchCollections.getArticlesCashFlow());
	model.addAttribute("transfers", SearchCollections.getTransferTypes());
	model.addAttribute("totalSum", totalSum);

	model.addAttribute("userBal", Long.parseLong(userBalance)); // прибыль всего
	model.addAttribute("profit", trRep.getSumByTransfType(userId, TransferT.PROFIT)); // прибыль всего
	model.addAttribute("spend", trRep.getSumByTransfType(userId, TransferT.SPEND)); // расход всего

	return "transactions";
    }

    /**
     * повышение доминантности пользователя
     * 
     * @param count
     *            - число, на которое нужно повысить доминантность
     * @param userId
     *            - id юзера, доминантность которого будет повышаться
     */
    public static void upUserDomi(int count, int userId, UserRep userRep) {
	changeUserDomi(count, userId, userRep, true);
    }

    /**
     * понижение доминантности пользователя
     * 
     * @param count
     *            - число, на которое нужно уменьшить доминантность
     * @param userId
     *            - id юзера, доминантность которого будет понижаться
     */
    public static void downUserDomi(int count, int userId, UserRep userRep) {
	changeUserDomi(count, userId, userRep, false);
    }

    /**
     * изменение значения доминантности пользователя
     * 
     * @param count
     *            - число, на которое нужно повысить или уменьшить доминантность
     * @param userId
     *            - id юзера, доминантность которого будет понижаться
     * @param userRep
     * @param isUpDomi
     *            - это повышение или понижение
     */
    private static void changeUserDomi(int count, int userId, UserRep userRep, boolean isUpDomi) {
	User user = userRep.find(userId);
	int newDomi = 0;
	if (isUpDomi) {
	    newDomi = user.getDomi() + count;
	} else {
	    newDomi = user.getDomi() - count;
	}
	user.setDomi(newDomi);
	userRep.updateUser(user);
    }
}
