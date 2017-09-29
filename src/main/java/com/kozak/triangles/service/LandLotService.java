package com.kozak.triangles.service;

import java.util.List;

import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.exceptions.MoneyNotEnoughException;
import com.kozak.triangles.model.LandLotsInfo;

public interface LandLotService {

    /**
     * @param userId
     * @param cityArea
     *            - район
     * @return количество участков земли пользователя в конкретном районе
     */
    int getCountOfLandLot(long userId, CityArea cityArea);

    /**
     * Добавляет БЕСПЛАТНО пользователю участки в конкретном районе
     * 
     * @param userId
     * @param cityArea
     * @param count
     *            - количество участков, что нужно добавить
     */
    void addLandLots(Long userId, CityArea cityArea, int count);

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
     * @throws MoneyNotEnoughException
     *             - если цена участка больше, чем состоятельность пользователя
     */
    long getNextLandLotPrice(long userId, CityArea cityArea) throws MoneyNotEnoughException;

    /**
     * Покупка пользователем одного участка в конкретном районе
     * 
     * @throws MoneyNotEnoughException
     *             - если цена участка больше, чем состоятельность пользователя
     */
    void buyOneLandLot(Long userId, CityArea cityArea) throws MoneyNotEnoughException;

    /**
     * @return каким построенным имуществом или объектами строительства заняты участки в конкретном районе
     */
    List<LandLotsInfo> getLandLotInfo(Long userId, CityArea cityArea);

}
