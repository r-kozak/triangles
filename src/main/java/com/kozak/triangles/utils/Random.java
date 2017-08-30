package com.kozak.triangles.utils;

import java.security.SecureRandom;

public class Random {
    /**
     * генератор случайного числа включая начальное и конечное число.
     * 
     * @param aStart
     *            - начало диапазона
     * @param aEnd
     *            - конец диапазона
     * @return случайное число из диапазона
     */
    public static long generateRandNum(Number start, Number end) {
        long aStart = start.longValue();
        long aEnd = end.longValue();
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        aStart *= 100;
        aEnd = aEnd * 100 + 100;

        SecureRandom random = new SecureRandom();
        // get the range, casting to long to avoid overflow problems
        long range = aEnd - aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * random.nextDouble());
        long randomNumber = (fraction + aStart);

        return randomNumber / 100;
    }

    /**
     * бросок костей
     */
    public static int diceRoll() {
        long a = generateRandNum(1, 6);
        long b = generateRandNum(1, 6);

        return (int) (a + b);
    }

    /**
     * генерирует строку со случайным набором символов
     * 
     * @param length
     */
    public static String getHash(int length) {
        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }

}
