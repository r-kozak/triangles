package com.kozak.triangles.controllers;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
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
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.TransferTypes;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.search.SearchCollections;
import com.kozak.triangles.search.TransactSearch;
import com.kozak.triangles.utils.Constants;
import com.kozak.triangles.utils.DateUtils;
import com.kozak.triangles.utils.ResponseUtil;
import com.kozak.triangles.utils.TagCreator;

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
    public @ResponseBody ResponseEntity<String> buyTriangles(@RequestParam("count") int count,
            @RequestParam("action") String action, User user) {

        JSONObject resultJson = new JSONObject();

        // признаки правильности запроса
        boolean correctAction = action.equals("info") || action.equals("confirm"); // корректное действие
        boolean correctCount = count == 100 || count == 1000; // корректное количество для обмена

        if (!correctAction || !correctCount) {
            ResponseUtil.putErrorMsg(resultJson, "Ошибка запроса :(");
        } else {
            long userId = user.getId();
            int userDomi = userRep.getUserDomi(userId);
            int needDomi = count / Constants.DOMI_PRICE; // сколько нужно доминантности для обмена

            if (userDomi < needDomi) {
                ResponseUtil.putErrorMsg(resultJson,
                        String.format("Ошибка. Не хватает очков доминантности для обмена (остаток: %s).", userDomi));
            } else {
                if (action.equals("info")) {
                    resultJson.put("message",
                            String.format("Вы точно желаете обменять %s очков доминантности на %s&tridot;?", needDomi, count));
                    resultJson.put("domiEnough", true);
                } else if (action.equals("confirm")) {
                    // установить новую доминантность
                    user.setDomi(userDomi - needDomi);
                    userRep.updateUser(user);

                    // начислить деньги за обмен
                    String desc = String.format("Обмен %s очков доминантности на %s&tridot;", needDomi, count);
                    long balance = Long.valueOf(trRep.getUserBalance(userId));

                    Transaction t = new Transaction(desc, new Date(), count, TransferTypes.PROFIT, userId, balance + count,
                            ArticleCashFlow.DOMINANT_TO_TRIAN);
                    trRep.addTransaction(t);
                }
            }
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    String transactionsGET(Model model, User user, TransactSearch ts, HttpServletRequest req) throws ParseException {
        if (ts.isNeedClear())
            ts.clear();
        model.addAttribute("ts", ts);

        long userId = user.getId();

        // результат с БД [количество всего; общая сумма; транзакции с учетом пагинации]
        List<Object> dbResult = trRep.getTransactionsList(userId, ts);
        long itemsCount = (long) dbResult.get(0);
        int totalPages = (int) (itemsCount / Constants.ROWS_ON_PAGE) + ((itemsCount % Constants.ROWS_ON_PAGE != 0) ? 1 : 0);

        if (totalPages > 1) {
            int currPage = Integer.parseInt(ts.getPage());
            String paginationTag = TagCreator.paginationTag(totalPages, currPage, req);
            model.addAttribute("paginationTag", paginationTag);
        }

        // вычисление следующей даты начисления кредита или депозита
        // для этого добавить 29 дней и 12 часов к последнему начислению, именно через это время будет кредит или депозит
        Date lastCreditDeposit = getLastCreditOrDepositDate(userId);
        Date nextCreditDeposit = DateUtils.addDays(lastCreditDeposit, 29);
        nextCreditDeposit = DateUtils.addHours(nextCreditDeposit, 12);

        // вычисление суммы кредита или депозита
        long userBalance = Long.parseLong(trRep.getUserBalance(userId));
        double rate = (userBalance > 0 ? Constants.DEPOSIT_RATE : Constants.CREDIT_RATE);
        long creditDepositSum = Math.round(userBalance * rate);

        model = addMoneyInfoToModel(model, user);
        model.addAttribute("totalSum", dbResult.get(1));
        model.addAttribute("transacs", dbResult.get(2));
        model.addAttribute("articles", SearchCollections.getArticlesCashFlow());
        model.addAttribute("transfers", SearchCollections.getTransferTypes());
        model.addAttribute("nextCreditDeposit", nextCreditDeposit); // следующее начисление кредита/депозита
        model.addAttribute("creditDepositSum", Math.abs(creditDepositSum)); // сумма кредита/депозита
        model.addAttribute("isDeposit", userBalance > 0); // это кредит или депозит

        return "transactions";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/withdraw-money", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> withdrawTriangles(@RequestParam("count") int count, User user) {
        JSONObject resultJson = new JSONObject();

        long userId = user.getId();
        long balance = Long.valueOf(trRep.getUserBalance(userId));
        if (count > 0 && balance > 0 && count <= balance) {
            // вывести деньги со счета
            String desc = String.format("Вывод средств со счета");
            Date transactDate = new Date();
            ArticleCashFlow artCashFlowType = ArticleCashFlow.WITHDRAW;
            Transaction t = new Transaction(desc, transactDate, count, TransferTypes.SPEND, userId, balance - count,
                    artCashFlowType);
            trRep.addTransaction(t);

            // сформировать json ответ
            ResponseUtil.addBalanceData(resultJson, count, balance, userId, prRep);
            resultJson.put("transactDate", DateUtils.dateToString(transactDate));
            resultJson.put("description", desc);
        } else {
            ResponseUtil.putErrorMsg(resultJson,
                    "Сумма вывода и баланс должны быть положительными!<br/> Также сумма вывода должна быть <= суммы баланса!");
        }
        return ResponseUtil.createTypicalResponseEntity(resultJson);
    }

    /**
     * повышение доминантности пользователя
     * 
     * @param count
     *            - число, на которое нужно повысить доминантность
     * @param userId
     *            - id юзера, доминантность которого будет повышаться
     */
    public static void upUserDomi(int count, long userId, UserRep userRep) {
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
    public static void downUserDomi(int count, long userId, UserRep userRep) {
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
    private static void changeUserDomi(int count, long userId, UserRep userRep, boolean isUpDomi) {
        User user = userRep.find(userId);
        if (isUpDomi) {
            user.setDomi(user.getDomi() + count);
        } else {
            user.setDomi(user.getDomi() - count);
        }
        userRep.updateUser(user);
    }

}
