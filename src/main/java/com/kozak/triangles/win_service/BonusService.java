package com.kozak.triangles.win_service;

import com.kozak.triangles.exception.BonusIsNotAvailableException;
import com.kozak.triangles.exception.WinHandlingException;
import com.kozak.triangles.model.WinDataModel;

public interface BonusService {

    /**
     * @param userId
     * @return true, если бонус для пользователя доступен, иначе - false
     */
    boolean isBonusAvailable(long userId);

    /**
     * Удаляет бонус из списка доступных. При этом начисляет бонус пользователю (дает деньги, добавляет имущество или другое).
     * 
     * @param userId
     * @throws BonusIsNotAvailableException
     *             - если у пользователя нет доступных бонусов
     * @throws WinHandlingException
     */
    WinDataModel takeBonus(long userId) throws BonusIsNotAvailableException, WinHandlingException;
}
