package com.kozak.triangles.utils;

import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<String> getResponseEntity(JSONObject resultJson) {
        System.err.println(resultJson.toJSONString());
        String json = resultJson.toJSONString();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
    }
}
