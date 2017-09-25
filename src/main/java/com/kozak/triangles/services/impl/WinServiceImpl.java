package com.kozak.triangles.services.impl;

import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kozak.triangles.data.WinsTableData;
import com.kozak.triangles.entities.WinInfo;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.exceptions.NoPredictionException;
import com.kozak.triangles.models.WinDataModel;
import com.kozak.triangles.repositories.WinRepository;
import com.kozak.triangles.services.WinService;

@Service
public class WinServiceImpl implements WinService {

    private static TreeMap<Integer, WinDataModel> winningsData = null;

    @Autowired
    private WinRepository winRepository;

    /**
     * получает данные с базы данных информацию о всех вариантах выигрыша и заполняет ними дерево, которое потом может
     * использоваться для получение данных о выигрыше или бонусе.
     * 
     * @return - дерево с данными.
     */
    @Override
    public TreeMap<Integer, WinDataModel> getWinningsData() {
        if (winningsData == null) {
            winningsData = new TreeMap<Integer, WinDataModel>();

            List<WinDataModel> tableData = WinsTableData.getWinData();
            // заполнение дерево данными из базы
            for (WinDataModel dataModel : tableData) {
                winningsData.put(dataModel.getRandomNumFrom(), dataModel);
            }
        }
        return winningsData;
    }

    @Override
    public void addAmountOfWin(long userId, WinArticle article, int amount) {
        WinInfo info = winRepository.getWinInfoByIdAndArticle(userId, article);
        info.setRemainingAmount(info.getRemainingAmount() + amount);
        winRepository.addUpdateWinInfo(info);
    }

    @Override
    public void addUserPrediction(long userId, Integer predictionId) {
        WinInfo info = winRepository.getWinInfoByIdAndArticle(userId, WinArticle.PREDICTION);
        info.setRemainingAmount(predictionId); // да, в этом поле хранится ID-шник
        winRepository.addUpdateWinInfo(info);
    }

    @Override
    public boolean isUserHasPrediction(long userId) {
        WinInfo info = winRepository.getWinInfoByIdAndArticle(userId, WinArticle.PREDICTION);
        return info.getRemainingAmount() > 0; // да, в этом поле хранится ID-шник и, если он есть, значит -- есть и предсказание
    }

    @Override
    public Integer takeUserPredictionId(long userId) throws NoPredictionException {
        WinInfo info = winRepository.getWinInfoByIdAndArticle(userId, WinArticle.PREDICTION);
        Integer result = info.getRemainingAmount(); // да, в этом поле хранится ID-шник
        if (result == 0) {
            throw new NoPredictionException();
        }
        // списать предсказание с остатков
        info.setRemainingAmount(0);
        winRepository.addUpdateWinInfo(info);

        return result;
    }

    @Override
    public long getRemainingAmount(long userId, WinArticle article) {
        return winRepository.getWinInfoByIdAndArticle(userId, article).getRemainingAmount();
    }

    @Override
    public void takeAmount(long userId, WinArticle article, int amount) {
        WinInfo info = winRepository.getWinInfoByIdAndArticle(userId, article);
        info.setRemainingAmount(info.getRemainingAmount() - amount);
        winRepository.addUpdateWinInfo(info);
    }
}
