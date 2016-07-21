package com.kozak.triangles.controllers;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.search.TransactSearch;
import com.kozak.triangles.utils.Consts;
import com.kozak.triangles.utils.ResponseUtil;
import com.kozak.triangles.utils.TagCreator;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@Controller
public class MoneyController extends BaseController {

    /**
     * функция обмена доминантности на деньги
     * 
     * @param count
     *            - количество [500, 5000]
     * @param action
     *            - [info, confirm] - получение информации или подтверждение обмена
     * @return json строку с
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/buy-triangles", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> jqueryBuyTriangles(@RequestParam("count") int count,
            @RequestParam("action") String action, User user) {

        JSONObject resultJson = new JSONObject();

        // признаки правильности запроса
        boolean correctAction = action.equals("info") || action.equals("confirm"); // корректное действие
        boolean correctCount = count == 100 || count == 1000; // корректное количество для обмена

        if (!correctAction || !correctCount) {
            ResponseUtil.putErrorMsg(resultJson, "Ошибка запроса :(");
        } else {
            int userId = user.getId();
            int userDomi = userRep.getUserDomi(userId);
            int needDomi = count / Consts.DOMI_PRICE; // сколько нужно доминантности для обмена

            if (userDomi < needDomi) {
                ResponseUtil.putErrorMsg(resultJson,
                        String.format("Ошибка. Не хватает очков доминантности для обмена (остаток: %s).", userDomi));
            } else {
                if (action.equals("info")) {
                    resultJson.put("message", String.format(
                            "Вы точно желаете обменять %s очков доминантности на %s&tridot;?", needDomi, count));
                    resultJson.put("domiEnough", true);
                } else if (action.equals("confirm")) {
                    // установить новую доминантность
                    user.setDomi(userDomi - needDomi);
                    userRep.updateUser(user);

                    // начислить деньги за обмен
                    String desc = String.format("Обмен %s очков доминантности на %s&tridot;", needDomi, count);
                    long balance = Long.valueOf(trRep.getUserBalance(userId));

                    Transaction t = new Transaction(desc, new Date(), count, TransferT.PROFIT, userId, balance + count,
                            ArticleCashFlowT.DOMINANT_TO_TRIAN);
                    trRep.addTransaction(t);
                }
            }
        }

        String json = resultJson.toJSONString();
        System.out.println(json);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    String transactionsGET(Model model, User user, TransactSearch ts, HttpServletRequest req) throws ParseException {
        if (ts.isNeedClear())
            ts.clear();
        model.addAttribute("ts", ts);

        int userId = user.getId();

        // результат с БД [количество всего; общая сумма; транзакции с учетом пагинации]
        List<Object> dbResult = trRep.transList(userId, ts);
        long itemsCount = (long) dbResult.get(0);
        int totalPages = (int) (itemsCount / Consts.ROWS_ON_PAGE) + ((itemsCount % Consts.ROWS_ON_PAGE != 0) ? 1 : 0);

        if (totalPages > 1) {
            int currPage = Integer.parseInt(ts.getPage());
            String paginationTag = TagCreator.paginationTag(totalPages, currPage, req);
            model.addAttribute("paginationTag", paginationTag);
        }

        String userBalance = trRep.getUserBalance(userId);
        int userDomi = userRep.getUserDomi(userId);
        model = ResponseUtil.addMoneyInfoToModel(model, userBalance, Util.getSolvency(userBalance, prRep, userId), userDomi);
        model.addAttribute("totalSum", dbResult.get(1));
        model.addAttribute("transacs", dbResult.get(2));
        model.addAttribute("articles", SearchCollections.getArticlesCashFlow());
        model.addAttribute("transfers", SearchCollections.getTransferTypes());

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
        if (isUpDomi) {
            user.setDomi(user.getDomi() + count);
        } else {
            user.setDomi(user.getDomi() - count);
        }
        userRep.updateUser(user);
    }
}
