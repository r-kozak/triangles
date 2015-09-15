package com.kozak.triangles.utils;

import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kozak.triangles.repositories.PropertyRep;

public class ResponseUtil {
    public static ResponseEntity<String> getResponseEntity(JSONObject resultJson) {
        System.err.println(resultJson.toJSONString());
        String json = resultJson.toJSONString();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
    }

    /**
     * добавление сообщения об ошибке в JSON
     */
    @SuppressWarnings("unchecked")
    public static void putErrorMsg(JSONObject resultJson, String msg) {
        resultJson.put("error", true);
        resultJson.put("message", msg);
    }

    /**
     * добавляет информацию о балансе в модель JSON
     * 
     * @param sum
     *            - сумма операции
     */
    @SuppressWarnings("unchecked")
    public static void addBalanceData(JSONObject resultJson, long sum, long userMoney, int userId, PropertyRep prRep) {
        resultJson.put("changeBal", "-" + sum);
        resultJson.put("newBalance", Util.moneyFormat(userMoney - sum));
        resultJson.put("newSolvency",
                Util.moneyFormat(Util.getSolvency(String.valueOf(userMoney - sum), prRep, userId)));
    }
}
