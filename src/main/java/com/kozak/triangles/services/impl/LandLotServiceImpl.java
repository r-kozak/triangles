package com.kozak.triangles.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kozak.triangles.data.CityAreasTableData;
import com.kozak.triangles.entities.ConstructionProject;
import com.kozak.triangles.entities.LandLot;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.exceptions.MoneyNotEnoughException;
import com.kozak.triangles.models.LandLotsInfo;
import com.kozak.triangles.repositories.ConstructionProjectRep;
import com.kozak.triangles.repositories.LandLotRepository;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.services.LandLotService;
import com.kozak.triangles.utils.CommonUtil;
import com.kozak.triangles.utils.Ksyusha;

@Service
public class LandLotServiceImpl implements LandLotService {

    @Autowired
    private LandLotRepository landLotRepository;
    @Autowired
    private PropertyRep propertyRepository;
    @Autowired
    private ConstructionProjectRep constructionProjectRep;
    @Autowired
    private TransactionRep transactionRep;
    @Autowired
    private PropertyRep prRep;
    @Autowired
    private ConstructionProjectRep constrRep;

    private static final String MONEY_NOT_ENOUGH_TO_BUY_LAND_LOT = "Не хватает денег на покупку нового участка. Цена участка = %d";
    private static final long BASE_LAND_LOT_PRICE = 1000;
    private static final int PRICE_COEF = 300;

    @Override
    public int getCountOfLandLot(long userId, CityArea cityArea) {
        return landLotRepository.getLandLot(userId, cityArea).getLotCount();
    }

    @Override
    public void addOneLandLot(Long userId, CityArea cityArea) {
        LandLot landLot = landLotRepository.getLandLot(userId, cityArea);
        int newLotCount = landLot.getLotCount() + 1;
        landLot.setLotCount(newLotCount);
        landLotRepository.addUpdateLandLot(landLot);
    }

    @Override
    public long getBusyLandLotsCount(long userId, CityArea cityArea) {
        int propertiesCount = propertyRepository.cityAreaProperties(userId, cityArea).size();
        long constructProjectsCount = constructionProjectRep.getCountOfUserConstrProject(userId, cityArea);
        return propertiesCount + constructProjectsCount;
    }

    @Override
    public long getAvailableLandLotsCount(long userId, CityArea cityArea) {
        // количество участков всего - количество занятых участков
        return getCountOfLandLot(userId, cityArea) - getBusyLandLotsCount(userId, cityArea);
    }

    @Override
    public long getNextLandLotPrice(long userId, CityArea cityArea) throws MoneyNotEnoughException {
        // хочет иметь участков = количество участков в конкретном районе + 1
        int nextCount = landLotRepository.getLandLot(userId, cityArea).getLotCount() + 1;

        // расчитать цену следующего участка
        double unCoefSquare = Math.pow(Ksyusha.computeCoef(nextCount), 2);
        long coefs = Math.round(PRICE_COEF * unCoefSquare); // перемножить коэф. участков с универсальным коэф.
        long price = nextCount * (BASE_LAND_LOT_PRICE + (nextCount * coefs));

        // добавить процент района
        price += price * CityAreasTableData.getCityAreaPercent(cityArea) / 100;

        long userSolvency = CommonUtil.getSolvency(transactionRep, prRep, userId); // состоятельность пользователя
        if (price > userSolvency) {
            throw new MoneyNotEnoughException(String.format(MONEY_NOT_ENOUGH_TO_BUY_LAND_LOT, price));
        }
        return price;
    }

    @Override
    public void buyOneLandLot(Long userId, CityArea cityArea) throws MoneyNotEnoughException {
        // проверка, что денег достаточно
        long price = getNextLandLotPrice(userId, cityArea); // получить цену на следующий участок в конкретном районе
        long userSolvency = CommonUtil.getSolvency(transactionRep, prRep, userId); // состоятельность пользователя
        if (price > userSolvency) {
            throw new MoneyNotEnoughException(String.format(MONEY_NOT_ENOUGH_TO_BUY_LAND_LOT, price));
        }

        // снять деньги
        String descr = "Покупка участка в районе: " + cityArea;
        long userMoney = Long.parseLong(transactionRep.getUserBalance(userId));
        Transaction tr = new Transaction(descr, new Date(), price, TransferType.SPEND, userId, userMoney - price,
                ArticleCashFlow.LAND_LOTS_BUY);
        transactionRep.addTransaction(tr);

        // добавить пользователю участок в конкретном районе
        addOneLandLot(userId, cityArea);
    }

    @Override
    public List<LandLotsInfo> getLandLotInfo(Long userId, CityArea cityArea) {
        List<LandLotsInfo> result = new ArrayList<>();

        // добавить информацию об имущества
        for (Property property : propertyRepository.cityAreaProperties(userId, cityArea)) {
            result.add(new LandLotsInfo(property.getId(), property.getName()));
        }
        // добавить информацию об объектах строительства
        for (ConstructionProject project : constrRep.getCityAreaUserConstrProject(userId, cityArea)) {
            result.add(new LandLotsInfo(project.getId(), project.getCompletePerc()));
        }
        return result;
    }

}
