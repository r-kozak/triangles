package com.kozak.triangles.interfaces;

/**
 * Ставки
 * 
 * кредит (месячная, не %), депозит (месячная, не %)
 * 
 * @author Roman: 13 июня 2015 г. 12:18:06
 */
public interface Rates {
    double CREDIT_RATE = 7.66 / 100;
    double DEPOSIT_RATE = 3.375 / 100;

    // daily bonus by day number
    int FIRST_DAY = 20;
    int SECOND_DAY = 50;
    int THIRD_DAY = 200;
    int FOURTH_DAY = 400;
    int FIFTH_DAY = 1000;

}
