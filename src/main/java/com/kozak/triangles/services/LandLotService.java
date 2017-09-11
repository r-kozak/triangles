package com.kozak.triangles.services;

import com.kozak.triangles.enums.CityArea;

public interface LandLotService {

    /**
     * @param userId
     * @param cityArea
     *            - район
     * @return количество участков земли пользователя в конкретном районе
     */
    int getCountOfLandLot(long userId, CityArea cityArea);

    /**
     * Добавляет пользователю участок в конкретном районе
     * 
     * @param userId
     * @param cityArea
     */
    void addOneLandLot(Long userId, CityArea cityArea);

    /**
     * Занятым участок считается тогда, когда на нем уже есть имущество или имущество еще находится в процессе постройки
     * 
     * @param userId
     * @param cityArea
     * @return количество ЗАНЯТЫХ участков земли пользователя в конкретном районе
     */
    long getBusyLandLotsCount(long userId, CityArea cityArea);

    /**
     * @param userId
     * @param сityArea
     * @return количество ДОСТУПНЫХ (не занятых) участков земли пользователя в конкретном районе
     */
    long getAvailableLandLotsCount(long userId, CityArea cityArea);

    /**
     * @param userId
     * @param cityArea
     * @return цену следующего участка в конкретном районе и для конкретного пользователя
     */
    long getNextLandLotPrice(long userId, CityArea cityArea);

}
