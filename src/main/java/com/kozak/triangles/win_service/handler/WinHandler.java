package com.kozak.triangles.win_service.handler;

import java.util.List;

import com.kozak.triangles.exception.WinHandlingException;
import com.kozak.triangles.model.WinDataModel;

public interface WinHandler {

    /**
     * Обрабатывает модели и начисляет пользователю выигрышы
     * 
     * @param winModels
     *            модели с выигрышами
     * @throws WinHandlingException
     */
    void handle(List<WinDataModel> winModels, long userId) throws WinHandlingException;

}
