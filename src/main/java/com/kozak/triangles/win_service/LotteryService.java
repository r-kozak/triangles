package com.kozak.triangles.win_service;

import com.kozak.triangles.exception.LotteryPlayException;
import com.kozak.triangles.exception.WinHandlingException;

public interface LotteryService {

    /**
     * Игра в лотерею на указанное количество билетов
     * 
     * @param count
     *            количество игр, которое запрашивает пользователь. Может быть (1, 5 - ≤5, 10 - ≤10, 0 - на все)
     * @return сгенерированный список моделей с выигрышами
     * @throws LotteryPlayException
     *             1. У пользователя нет билетов на счету
     *             2. Был исчерпан суточный лимит розыгрышей
     *             3. Было запрошено неверное количество (count)
     *             4. Статья выигрыша не была обработана
     * @throws WinHandlingException
     */
    void playLoto(int count, long userId) throws LotteryPlayException, WinHandlingException;
}
