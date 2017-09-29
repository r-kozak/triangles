package com.kozak.triangles.win_service.impl;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kozak.triangles.exceptions.BonusIsNotAvailableException;
import com.kozak.triangles.model.WinDataModel;
import com.kozak.triangles.repository.UserRep;
import com.kozak.triangles.util.Random;
import com.kozak.triangles.win_service.BonusService;
import com.kozak.triangles.win_service.WinService;

@Service
public class BonusServiceImpl implements BonusService {

    @Autowired
    private UserRep userRep;
    @Autowired
    private WinService winService;

    Map<Long, Queue<WinDataModel>> usersBonuses = new ConcurrentHashMap<>();

    @Override
    public boolean isBonusAvailable(long userId) {
        Queue<WinDataModel> userWinModels = usersBonuses.get(userId);
        return userWinModels != null && !userWinModels.isEmpty(); // bonus available if models is NOT empty
    }

    @Override
    public void takeBonus(long userId) throws BonusIsNotAvailableException {
        if (!isBonusAvailable(userId)) {
            throw new BonusIsNotAvailableException();
        }

        Queue<WinDataModel> userWinModels = usersBonuses.get(userId);
        WinDataModel winModel = userWinModels.poll();

    }

    /**
     * Каждые 2 минуты есть шанс в 40% на генерацию бонуса для пользователя.
     * Генерирует бонус для всех активных пользователей, если у них еще нет доступного бонуса.
     */
    @Scheduled(fixedDelay = 2 * 60 * 1000) // 2 минуты
    private void generateBonus() {
        List<Long> activeUsersIds = userRep.getActiveUsersIds(); // получить активных пользователей
        
        for (Long userId : activeUsersIds) {
            // для каждого активного пользователя сгенерировать бонус с вероятностью 40%

            int random = (int) Random.generateRandNum(1, 100);
            if (random <= 40 && !isBonusAvailable(userId)) {
                Queue<WinDataModel> userWinModels = usersBonuses.get(userId);
                if (userWinModels == null) {
                    userWinModels = new ConcurrentLinkedQueue<>();
                }
                // сгенерировать рандомный выигрыш и добавить его, как бонус
                userWinModels.add(winService.generateRandomWinData());
            }
        }
    }
}
