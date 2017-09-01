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
     * @param userId
     * @param cityArea
     * @return количество ЗАНЯТЫХ участков земли пользователя в конкретном районе
     */
    long getBusyLandLotsCount(long userId, CityArea cityArea);

}
