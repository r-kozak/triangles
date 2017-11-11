package com.kozak.triangles.model;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * Информация
 * 
 * @author Roman
 */
public class LandLotsInfo implements JSONAware {

    /**
     * Имущество функционирует, когда уже достроенно, иначе -- оно в процессе стройки
     */
    private boolean isFunctioningProperty;

    private String propertyName;

    /**
     * Имущество на продаже
     */
    private boolean isOnSale;

    /**
     * ID имущества или объекта строительства
     */
    private Long id;

    /**
     * Для объектов строительства
     */
    private double completedPercent;

    /**
     * Общий конструктор
     * 
     * @param id
     *            ID уже функционирующего имущества
     * @param propertyName
     *            имя имущества
     */
    private LandLotsInfo(Long id, String propertyName) {
        this.id = id;
        this.propertyName = propertyName;
    }

    /**
     * Конструктор для функционирующего имущества
     * 
     * @param id
     *            ID уже функционирующего имущества
     * @param propertyName
     *            имя имущества
     * @param isOnSale
     *            находится ли имущество на продаже
     */
    public LandLotsInfo(Long id, String propertyName, boolean isOnSale) {
        this(id, propertyName);
        this.isFunctioningProperty = true;
        this.isOnSale = isOnSale;
    }

    /**
     * Конструктор для объектов строительства
     * 
     * @param id
     *            ID объекта строительства
     * @param completedPercent
     *            процент постройки
     * @param propertyName
     *            имя имущества, что находится на стадии постройки
     */
    public LandLotsInfo(Long id, String propertyName, double completedPercent) {
        this(id, propertyName);
        this.isFunctioningProperty = false;
        this.completedPercent = completedPercent;
    }


    public boolean isFunctioningProperty() {
        return isFunctioningProperty;
    }

    public void setFunctioningProperty(boolean isFunctioningProperty) {
        this.isFunctioningProperty = isFunctioningProperty;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getCompletedPercent() {
        return completedPercent;
    }

    public void setCompletedPercent(double completedPercent) {
        this.completedPercent = completedPercent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("isFunctioningProperty", isFunctioningProperty);
        obj.put("propertyName", propertyName);
        obj.put("id", id);
        obj.put("isOnSale", isOnSale);
        obj.put("completedPercent", completedPercent);
        return obj.toString();
    }

}
