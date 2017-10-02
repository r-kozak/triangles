package com.kozak.triangles.enums;

/**
 * типы движений денежных средств: приход, расход
 * 
 * @author Roman: 06 июня 2015 г. 15:34:31
 */
public enum TransferType {
    NONE(""), PROFIT("+"), SPEND("-");

    private String sign;

    TransferType(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

}
