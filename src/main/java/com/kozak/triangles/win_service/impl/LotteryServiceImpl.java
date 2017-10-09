package com.kozak.triangles.win_service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.kozak.triangles.entity.User;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.exception.LotteryPlayException;
import com.kozak.triangles.exception.WinHandlingException;
import com.kozak.triangles.model.WinDataModel;
import com.kozak.triangles.repository.LotteryRep;
import com.kozak.triangles.repository.UserRep;
import com.kozak.triangles.service.LandLotService;
import com.kozak.triangles.util.Constants;
import com.kozak.triangles.win_service.LotteryService;
import com.kozak.triangles.win_service.WinService;
import com.kozak.triangles.win_service.handler.LotteryWinHandler;
import com.kozak.triangles.win_service.handler.WinHandler;

@Service
public class LotteryServiceImpl implements LotteryService {

    @Autowired
    private UserRep userRep;
    @Autowired
    private LotteryRep lotteryRep;
    @Autowired
    private WinService winService;
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private LandLotService landLotService;

    @Override
    public void playLoto(int count, long userId) throws LotteryPlayException, WinHandlingException {
        User user = userRep.find(userId);
        int userTickets = user.getLotteryTickets();

        // проверки на правильность количества покупаемых билетов
        if (userTickets == 0) {
            throw new LotteryPlayException("У вас нет билетов. Купите или ждите зачисления за очки доминантности.");
        } else if (lotteryRep.countOfPlaysToday(userId) >= Constants.LOTTERY_GAMES_LIMIT_PER_DAY) {
            throw new LotteryPlayException("Вы исчерпали суточный лимит розыгрышей лотереи. Приходите завтра.");
        } else if (count != 1 && count != 5 && count != 10 && count != 0) {
            throw new LotteryPlayException("Игра в лото на такое количество билетов недоступна.");
        } else {
            int gamesCount = computeGamesCount(userTickets, count, userId); // вычислить количество игр

            // сыграть в игру на кол-во билетов и обработать выигрышы
            List<WinDataModel> lotoResult = generatePlayLotoResult(gamesCount, userId);
            WinHandler winHandler = appContext.getBean(LotteryWinHandler.class);
            winHandler.handle(lotoResult, userId);

            // снять билеты у пользователя
            user = userRep.find(userId); // снова получить пользователя, т.к. в процессе игры его данные могут измениться
            user.setLotteryTickets(user.getLotteryTickets() - gamesCount);
            userRep.updateUser(user);
        }
    }

    /**
     * Вычисляет, на сколько билетов нужно сыграть в лото.
     * 
     * @param userTickets
     *            - колво билетов пользователя
     * @param countUserWant
     *            - количество, на которое он хочет играть
     */
    private int computeGamesCount(int userTickets, int countUserWant, long userId) {
        int gamesCount = countUserWant; // количество игр в лото

        // если билетов меньше, чем пользователь хочет потратить на игру ИЛИ он хочет играть на все
        if (userTickets < countUserWant || countUserWant == 0) {
            gamesCount = userTickets; // количество игр будет ограниченно билетами
        }

        // вычислить, сколько осталось розыгрышей на сегодня, учитывая лимит на количество игр в день
        int remainToPlayToday = Constants.LOTTERY_GAMES_LIMIT_PER_DAY - lotteryRep.countOfPlaysToday(userId);
        if (gamesCount > remainToPlayToday) {
            // если количество игр больше, чем осталось сыграть сегодня, то ограничим остаточным количеством
            gamesCount = remainToPlayToday;
        }

        return gamesCount;
    }

    /**
     * разыгрывает лотерейные билеты
     * 
     * @param ticketsCount
     *            - количество билетов к розыгрышу
     * @param userId
     * @return сгруппированный результат розыгрышей
     */
    private List<WinDataModel> generatePlayLotoResult(int ticketsCount, long userId) {
        List<WinDataModel> playResult = new ArrayList<>();

        boolean userHasPrediction = winService.isUserHasPrediction(userId); // есть непросмотренные предсказания
        // имущество можно выиграть только в районе Гетто. Взять количество доступных участков
        long availableLandLotsCount = landLotService.getAvailableLandLotsCount(userId, CityArea.GHETTO);

        WinDataModel winDataModel = null; // временные данные результата одного розыгрыша
        // цикл - это розыгрыш одного билета
        for (int i = 0; i < ticketsCount; i++) {
            winDataModel = winService.generateRandomWinData(); // данные о выигрыше
            WinArticle article = winDataModel.getArticle();

            // если выиграл предсказание
            if (article.equals(WinArticle.PREDICTION)) {
                // если есть непросмотренные предсказания - не считать этот розыгрыш
                if (userHasPrediction) {
                    i--;
                    continue;
                } else {
                    // иначе - посчитать этот розыгрыш, но теперь у юзера уже есть непросмотренные предсказания
                    userHasPrediction = true;
                }
            }
            // если выиграл имущество - проверить, есть ли для него участок
            if (article.equals(WinArticle.STALL) || article.equals(WinArticle.VILLAGE_SHOP)
                    || article.equals(WinArticle.STATIONER_SHOP)) {

                if (availableLandLotsCount <= 0) {
                    // участков не осталось совсем
                    i--;
                    continue;
                } else {
                    int wonPropertyCount = winDataModel.getCount(); // кол-во выигранного имущества
                    if (availableLandLotsCount < wonPropertyCount) {
                        // участков меньше, чем выиграно имущества
                        wonPropertyCount = (int) availableLandLotsCount;
                        winDataModel.setCount((int) availableLandLotsCount);
                    }
                    availableLandLotsCount -= wonPropertyCount; // уменьшить кол-во доступных участков
                }
            }
            // увеличить кол-во доступных участков, если выиграл участок
            // имущество можно выигрывать только в районе Гетто, значит брать во внимание только такие участки
            if (article.equals(WinArticle.LAND_LOT_GHETTO)) {
                availableLandLotsCount += winDataModel.getCount();
            }

            playResult.add(winDataModel);
        }
        return playResult;
    }

}
