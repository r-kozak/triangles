package com.kozak.triangles.services;

import com.kozak.triangles.enums.CityAreas;

public interface LandLotService {

    /**
     * @param userId
     * @param cityArea
     *            - район
     * @return количество участков земли пользователя в конкретном районе
     */
    int getCountOfLandLot(long userId, CityAreas cityArea);

    /**
     * Добавляет пользователю участок в конкретном районе
     * 
     * @param userId
     * @param cityArea
     */
    void addOneLandLot(Long userId, CityAreas cityArea);

}
