package com.kozak.triangles.interfaces.build_data;

/**
 * общие данные для всех коммерческих зданий
 * 
 * @author Roman: 20 июня 2015 г. 12:20:49
 */
public abstract class CommercBuildDataAbstr {
    int[] paybackPeriod = {};

    int usefulLife = paybackPeriod[1] * 2;

    long[] purchasePrice = {};

    int[] profit = {
            (int) (purchasePrice[0] / (paybackPeriod[1] * 7)),
            (int) (purchasePrice[0] / (paybackPeriod[0] * 7))
    };

    long[] cashCapacity = {
            profit[1],
            profit[1] * 2,
            profit[1] * 3,
            profit[1] * 5,
            profit[1] * 10
    };
}
