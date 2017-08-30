package com.kozak.triangles.models;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class Requirement implements JSONAware {
    private boolean carriedOut; // выполнено
    private String description;

    public Requirement(boolean carriedOut, String description) {
        this.carriedOut = carriedOut;
        this.description = description;
    }

    public boolean isCarriedOut() {
        return carriedOut;
    }

    public void setCarriedOut(boolean carriedOut) {
        this.carriedOut = carriedOut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("carriedOut", carriedOut);
        obj.put("description", description);
        return obj.toString();
    }

}
