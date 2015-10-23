package com.kozak.triangles.utils;

import java.security.SecureRandom;

import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;

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
    public long generateRandNum(Number start, Number end) {
        long aStart = start.longValue();
        long aEnd = end.longValue();
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        aStart *= 100;
        aEnd = aEnd * 100 + 100;

        SecureRandom random = new SecureRandom();
        // get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * random.nextDouble());
        long randomNumber = (fraction + aStart);

        return randomNumber / 100;
    }

    /**
     * бросок костей
     */
    public int diceRoll() {
        long a = generateRandNum(1, 6);
        long b = generateRandNum(1, 6);

        return (int) (a + b);
    }

    /**
     * генерирует строку со случайным набором символов
     * 
     * @param length
     */
    public String getHash(int length) {
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

    /**
     * Генерирует новое имя для имущества на основании его типа и района размещения
     * 
     * @param commBuildingType
     *            - тип имущества
     * @param cityArea
     *            - район
     * @return строку с именем
     */
    public String generatePropertyName(CommBuildingsT commBuildingType, CityAreasT cityArea) {
        StringBuffer sb = new StringBuffer();

        if (commBuildingType.equals(CommBuildingsT.STALL)) {
            sb.append("STL");
        } else if (commBuildingType.equals(CommBuildingsT.VILLAGE_SHOP)) {
            sb.append("VSH");
        } else if (commBuildingType.equals(CommBuildingsT.STATIONER_SHOP)) {
            sb.append("SSH");
        } else if (commBuildingType.equals(CommBuildingsT.BOOK_SHOP)) {
            sb.append("BSH");
        } else if (commBuildingType.equals(CommBuildingsT.CANDY_SHOP)) {
            sb.append("CSH");
        } else if (commBuildingType.equals(CommBuildingsT.LITTLE_SUPERMARKET)) {
            sb.append("LST");
        } else if (commBuildingType.equals(CommBuildingsT.MIDDLE_SUPERMARKET)) {
            sb.append("MST");
        } else if (commBuildingType.equals(CommBuildingsT.BIG_SUPERMARKET)) {
            sb.append("BST");
        } else {
            sb.append("UNDEF");
        }

        sb.append("_");

        if (cityArea.equals(CityAreasT.GHETTO)) {
            sb.append("GH");
        } else if (cityArea.equals(CityAreasT.OUTSKIRTS)) {
            sb.append("OT");
        } else if (cityArea.equals(CityAreasT.CHINATOWN)) {
            sb.append("CH");
        } else if (cityArea.equals(CityAreasT.CENTER)) {
            sb.append("CR");
        }
        sb.append("-");
        sb.append(getHash(5));

        return sb.toString();
    }
}
