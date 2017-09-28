package com.kozak.triangles.services;

import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.exceptions.NoPredictionException;
import com.kozak.triangles.models.WinDataModel;

/**
 * Сервис для управления информацией по выигрышах в лотерею и выигранными призами
 * 
 * @author Roman
 */
public interface WinService {

    /**
     * У каждого пользователя в базе хранятся данные о выигранном количестве в разрезе статьи. Например:
     * Пользователь -- Статья -- Остаток
     * ======================================================
     * Пользователь1 -- Предсказание -- 1
     * Пользователь1 -- Повышение уровня имущества -- 4
     * Пользователь1 -- Повышение уровня кассы имущества -- 2
     * 
     * Добавляет к остаткам количество выигранного в разрезе статьи выигрыша.
     * 
     * @param userId
     * @param article
     * @param count
     */
    void addAmountOfWin(long userId, WinArticle article, int count);

    /**
     * Сохраняет в базу информацию о выигранном предсказании пользователем
     * 
     * @param userId
     * @param predictionId
     */
    void addUserPrediction(long userId, Integer predictionId);

    /**
     * @param userId
     * @return true, если пользователь имеет выигранное предсказание, иначе - false
     */
    boolean isUserHasPrediction(long userId);

    /**
     * @param userId
     * @return ID предсказания, выигранного пользователем. При этом списывает предсказание с остатков
     * @throws NoPredictionException
     */
    Integer takeUserPredictionId(long userId) throws NoPredictionException;

    /**
     * @param userId
     * @param propertyUp
     * @return количество на остатках в разрезе статьи выигрыша
     */
    long getRemainingAmount(long userId, WinArticle propertyUp);

    /**
     * Списывает количество с остатков по статье выигрыша
     * 
     * @param userId
     * @param article
     * @param amount
     */
    void takeAmount(long userId, WinArticle article, int amount);

    /**
     * @return модель с рандомным выигрышем. При этом, выиграть можно все, что перечисленно в
     *         {@link com.kozak.triangles.enums.WinArticle}
     */
    WinDataModel generateRandomWinData();

}
