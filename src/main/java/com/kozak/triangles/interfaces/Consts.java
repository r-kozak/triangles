package com.kozak.triangles.interfaces;

/**
 * Ставки
 * 
 * кредит (месячная, не %), депозит (месячная, не %)
 * 
 * @author Roman: 13 июня 2015 г. 12:18:06
 */
public interface Consts {
    // кредитные и депозитные ставки
    double CREDIT_RATE = 7.66 / 100;
    double DEPOSIT_RATE = 3.375 / 100;

    // daily bonus by day number
    int FIRST_DAY = 20;
    int SECOND_DAY = 50;
    int THIRD_DAY = 200;
    int FOURTH_DAY = 400;
    int FIFTH_DAY = 1000;

    // количество элементов на страницу
    int ROWS_ON_PAGE = 12;

    // частота появления предложений на рынке недвижимости - мин, макс (ДНЕЙ)
    int FREQ_RE_PROP_MIN = 1;
    int FREQ_RE_PROP_MAX = 7;

    // название поля с датой следующего предложения на рынке недвижимости
    String NEXT_RE_PROPOSE = "NEXT_RE_PROPOSE_DATE";

    // ставки процентов районов города
    int CENTER_P = 30;
    int CHINA_P = 15;
    int OUTSKIRTS_P = 5;
    int GHETTO_P = 0;

    // коэфициенты вместимости кассы
    double CASH_CAP_L1 = 1.1;
    double CASH_CAP_L2 = 1.3;
    double CASH_CAP_L3 = 1.6;
    double CASH_CAP_L4 = 2.0;
    double CASH_CAP_L5 = 2.5;

    // проценты типов строителей
    int GASTARBEITER_B = 50;
    int UKRAINIAN_B = 100;
    int GERMANY_B = 150;

    // цены лицензий на строительство
    int LI_PR_2 = 16000;
    int LI_PR_3 = 20000;
    int LI_PR_4 = 24000;

    // сроки действия лицензий на строительство, недель
    int LI_TERM_2 = 8;
    int LI_TERM_3 = 4;
    int LI_TERM_4 = 2;
}
