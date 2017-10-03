package com.kozak.triangles.util;

/**
 * @author Roman: 13 июня 2015 г. 12:18:06
 */
public final class Constants {
    // стартовая сумма, с которой игрой начинает игру
    public static final long GAME_START_BALANCE = 17000;

    // ЛОГИН АДМИНА
    public static final String ADMIN_LOGIN = "admin";

    // кредитные и депозитные ставки (месячная, НЕ %)
    public static final double CREDIT_RATE = 20.0 / 100;
    public static final double DEPOSIT_RATE = 15.0 / 100;

    // daily bonus by day number
    public static final int[] DAILY_BONUS_SUM = { 0, 125, 250, 500, 1000, 2000, 3000, 5000, 8000, 12000, 17000 };

    // количество элементов на страницу
    public static final int ROWS_ON_PAGE = 12;

    // частота появления предложений на рынке недвижимости - мин, макс (ДНЕЙ)
    public static final int FREQ_RE_PROP_MIN = 1;
    public static final int FREQ_RE_PROP_MAX = 4;

    // название поля с датой следующего предложения на рынке недвижимости
    public static final String NEXT_RE_PROPOSE = "NEXT_RE_PROPOSE_DATE";

    // проценты типов строителей - влияют на скорость постройки здания
    public static final float[] BUILDERS_COEF = { (float) 0.5, 1, (float) 1.5 };

    public static final int K_DECREASE_REPAIR = 2; // коэф. уменьшения суммы ремонта
    public static final int K_DECREASE_CASH_L = 10; // коэф. уменьшения суммы поднятия уровня кассы
    public static final int K_DECREASE_PROP_L = 8; // коэф. уменьшения суммы поднятия уровня имущества

    // максимальные уровни !!!!!!!!!!!!!! ПРИ - ↑ - ДОБАВИТЬ КОЕФ. В !!! UNIVERS_K !!!
    public static final int MAX_CASH_LEVEL = 20; // кассы
    public static final int MAX_PROP_LEVEL = 15; // имущества

    // коэфициенты доминантности
    public static final double K_PROP_LEVEL_DOMI = 1.5; // коэф. умножения уровня имущества при его повышении
    public static final int K_DOMI_BUY_PROP = 10; // коэф. - доминантность при покупке имущества

    // цена одного очка доминантности (учавствует при обмене на деньги)
    public static final int DOMI_PRICE = 100;
    public static final int DAILY_TICKETS_FROM_DOMI_K = 20; // сколько очков доминантности стоит один лотерейный билет

    // цены на лотерейные билеты, в зависимости от покупаемого количества
    public static final int[] LOTTERY_TICKETS_PRICE = { 500, 475, 450 };

    // WORK WITH RECAPTHA
    public static final String RECAPTHA_URL = "https://www.google.com/recaptcha/api/siteverify";
    public static final String RECAPTCHA_SECRET = "6Ldw3yUTAAAAAFW2H4BLqrA2wuMPNNDNj3LEQuye";

    // длина сообщения из беседки с погрешностью на пробелы и др. знаки
    public static final int MSG_LEN = 600;

    // очки доминантности, что учитываются при начислении ежедневных лотерейных билетов
    public static final int DOMI_LOTTERY_LIMIT = 50000;

    // лимит на постройку зданий в день, шт
    public static final int CONSTRUCTION_LIMIT_PER_DAY = 10;

    // коэфициент умножения индекса типа строения при постройке здания. Нужен для начисления доминантности
    public static final int K_ADD_DOMI_FOR_BUILDING = 5;

    // лимит розыгрышей лотереи в день
    public static final int LOTTERY_GAMES_LIMIT_PER_DAY = 3000;
}
