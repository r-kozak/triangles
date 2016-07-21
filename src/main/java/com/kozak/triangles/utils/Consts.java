package com.kozak.triangles.utils;

/**
 * @author Roman: 13 июня 2015 г. 12:18:06
 */
public final class Consts {
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
	public static final int FREQ_RE_PROP_MAX = 7;

	// название поля с датой следующего предложения на рынке недвижимости
	public static final String NEXT_RE_PROPOSE = "NEXT_RE_PROPOSE_DATE";

	// ставки процентов районов города
	public static final int CENTER_P = 30;
	public static final int CHINA_P = 15;
	public static final int OUTSKIRTS_P = 5;
	public static final int GHETTO_P = 0;

	// универсальные коэфициенты вместимости кассы, начисления прибыли, повышения уровня имущества и кассы
	// осторожно! нужно, чтобы элементов было больше хотя бы на один, чем нужно
	public static final double[] UNIVERS_K = { 1, 1.1, 1.3, 1.6, 2.0, 2.5, 3.1, 3.8, 4.6, 5.5, 6.5, 7.6, 8.8, 10.1, 11.5, 13.0,
			14.6, 16.3, 18.1, 20.0 };

	// проценты типов строителей - влияют на скорость постройки здания
	public static final float[] BUILDERS_COEF = { (float) 0.5, 1, (float) 1.5 };

	// цены лицензий на строительство
	public static final int[] LICENSE_PRICE = { 0, 0, 16000, 32000, 64000 };

	// сроки действия лицензий на строительство, дней
	public static final int[] LICENSE_TERM = { 0, 10, 7, 4, 2 };

	public static final int K_DECREASE_REPAIR = 2; // коэф. уменьшения суммы ремонта
	public static final int K_DECREASE_CASH_L = 10; // коэф. уменьшения суммы поднятия уровня кассы
	public static final int K_DECREASE_PROP_L = 8; // коэф. уменьшения суммы поднятия уровня имущества

	// максимальные уровни !!!!!!!!!!!!!! ПРИ - ↑ - ДОБАВИТЬ КОЕФ. В !!! UNIVERS_K !!!
	public static final int MAX_CASH_LEVEL = 10; // кассы
	public static final int MAX_PROP_LEVEL = 10; // имущества

	// коэфициенты доминантности
	public static final double K_PROP_LEVEL_DOMI = 1.5; // коэф. умножения уровня имущества при его повышении
	public static final int K_DOMI_BUY_PROP = 10; // коэф. - доминантность при покупке имущества

	// цена одного очка доминантности (учавствует при обмене на деньги)
	public static final int DOMI_PRICE = 100;
	public static final int DAILY_TICKETS_FROM_DOMI_K = 20; // сколько очков доминантности стоит один лотерейный билет

	// цены на лотерейные билеты, в зависимости от покупаемого количества
	public static final int[] LOTTERY_TICKETS_PRICE = { 500, 475, 450 };

	// нижняя и верхняя граница рандомного числа при генерации лотерейного розыгрыша
	public static final int LOWER_LOTTERY_BOUND = 0;
	public static final int UPPER_LOTTERY_BOUND = 100_000;

	// WORK WITH RECAPTHA
	public static final String RECAPTHA_URL = "https://www.google.com/recaptcha/api/siteverify";
	public static final String RECAPTCHA_SECRET = "6LeKaQ8TAAAAAO9mHKXwvoVVor232VnhAaUXlzmy";

	// длина сообщения из беседки с погрешностью на пробелы и др. знаки
	public static final int MSG_LEN = 600;

	// лимит доминантности при начислении ежедневных лотерейных билетов
	public static final int DOMI_LIMIT = 50000;

	// лимит на постройку зданий в день, шт
	public static final int CONSTRUCTION_LIMIT_PER_DAY = 10;
}
