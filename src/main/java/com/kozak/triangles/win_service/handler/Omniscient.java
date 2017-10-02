package com.kozak.triangles.win_service.handler;

import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kozak.triangles.data.PredictionsTableData;
import com.kozak.triangles.util.Random;
import com.kozak.triangles.win_service.WinService;

/**
 * Всезнающий, живущий в недрах программного кода, который может открыть пользователю свою мудрость
 * 
 * @author Roman: 20 вер. 2015 22:56:49
 */
@Service
public class Omniscient {

    @Autowired
    private WinService winService;

    /**
     * Открыть пользователю мудрость или дать предсказание.
     */
    public void generatePredictionToUser(long userId) {
        TreeSet<Integer> allPredIDs = PredictionsTableData.getPredictionsIds(); // все ID предсказаний
        Integer lastPredId = allPredIDs.last(); // последний ID из предсказаний

        // cгенерить ID предсказания (мудрости), которое точно есть в базе
        Integer predictionId = null;
        do {
            predictionId = (int) Random.generateRandNum(1, lastPredId);
        } while (!allPredIDs.contains(predictionId));

        // вместо remainingAmount устанавливается ID предсказания, чтобы таким образом его хранить
        winService.addUserPrediction(userId, predictionId);
    }
}