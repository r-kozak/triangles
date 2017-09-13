package com.kozak.triangles.exceptions;

public class MoneyNotEnoughException extends Exception {

    private static final long serialVersionUID = 7561117388452452001L;

    public MoneyNotEnoughException(String msg) {
        super(msg);
    }

}
