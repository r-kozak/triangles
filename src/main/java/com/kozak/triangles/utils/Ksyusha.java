package com.kozak.triangles.utils;

/**
 * @author Ксюшенька, 11-09-2017
 */
public class Ksyusha {

    private static final double F = 0.05;

    /**
     * Универсальные коэфициенты могут быть нужны для расчета вместимости кассы, начисления прибыли, повышения уровня имущества и
     * кассы, стоимости участков земли и т.д.
     * 
     * Вычисляет универсальный коэфициент по формуле:
     * n = m((m-1) * f) + 1,
     * где: f = 0.05
     * 
     * Последовательность коэфициентов: 1, 1.1, 1.3, 1.6, 2.0, 2.5, 3.1, 3.8, ...
     * 
     * @param m
     *            - порядковый номер числа в последовательности
     * @return n универсальный коэфициент, число, вычисленное по формуле. Если m = 4, то результат = 1,6 (4-е число из
     *         последовательности).
     */
    public static double computeCoef(int m) {
        return (m * ((m - 1) * F)) + 1;
    }
}
