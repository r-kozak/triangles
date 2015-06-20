package com.kozak.triangles.interfaces.build_data;

/**
 * свойства (данные) имущества КИОСК (STALL)
 * 
 * ********* CommercBuildDataAbstr **********
 * 
 * usefulLife - срок полезного использования, в 2 раза больше периода окупаемости
 * profit - прибыль в день {min, max}
 * cashCapacity - вместимость кассы на разных уровнях, {на ур 0, на ур 1, ...}
 * 
 * ******************************************
 * 
 * paybackPeriod - период окупаемости {min, max}, недель
 * purchasePrice - цена покупки {min, max}
 * buildTime - время постройки (при скорости 100%), недель
 * depreciationRange - процент, на который может возрости процент износа
 * 
 * @author Roman: 20 июня 2015 г. 12:13:58
 */
public abstract class StallData extends CommercBuildDataAbstr {

    int[] paybackPeriod = { 3, 6 };

    long[] purchasePrice = { 4500, 5500 };

    int buildTime = 1;

}
