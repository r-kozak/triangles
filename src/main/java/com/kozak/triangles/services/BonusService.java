package com.kozak.triangles.services;

import com.kozak.triangles.exceptions.BonusIsNotAvailableException;

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
     */
    void takeBonus(long userId) throws BonusIsNotAvailableException;
}
