package com.kozak.triangles.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kozak.triangles.data.CityAreasTableData;
import com.kozak.triangles.entity.ConstructionProject;
import com.kozak.triangles.entity.LandLot;
import com.kozak.triangles.entity.Property;
import com.kozak.triangles.entity.Transaction;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.exception.MoneyNotEnoughException;
import com.kozak.triangles.model.LandLotsInfo;
import com.kozak.triangles.repository.ConstructionProjectRep;
import com.kozak.triangles.repository.LandLotRepository;
import com.kozak.triangles.repository.PropertyRep;
import com.kozak.triangles.repository.TransactionRep;
import com.kozak.triangles.service.LandLotService;
import com.kozak.triangles.util.CommonUtil;
import com.kozak.triangles.util.Ksyusha;

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

    private static final String MONEY_NOT_ENOUGH_TO_BUY_LAND_LOT = "Не хватает денег на покупку нового участка. Цена участка = %s";
    private static final long BASE_LAND_LOT_PRICE = 1000;
    private static final int PRICE_COEF = 100;

    @Override
    public int getCountOfLandLot(long userId, CityArea cityArea) {
        return landLotRepository.getLandLot(userId, cityArea).getLotCount();
    }

    @Override
    public void addLandLots(Long userId, CityArea cityArea, int count) {
        LandLot landLot = landLotRepository.getLandLot(userId, cityArea);
        int newLotCount = landLot.getLotCount() + count;
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
            String priceFormatted = CommonUtil.moneyFormat(price) + " &tridot;";
            throw new MoneyNotEnoughException(String.format(MONEY_NOT_ENOUGH_TO_BUY_LAND_LOT, priceFormatted));
        }
        return price;
    }

    @Override
    public void buyOneLandLot(Long userId, CityArea cityArea) throws MoneyNotEnoughException {
        // проверка, что денег достаточно
        long price = getNextLandLotPrice(userId, cityArea); // получить цену на следующий участок в конкретном районе
        long userSolvency = CommonUtil.getSolvency(transactionRep, prRep, userId); // состоятельность пользователя
        if (price > userSolvency) {
            String priceFormatted = CommonUtil.moneyFormat(price) + " &tridot;";
            throw new MoneyNotEnoughException(String.format(MONEY_NOT_ENOUGH_TO_BUY_LAND_LOT, priceFormatted));
        }

        // снять деньги
        String descr = "Район: " + cityArea;
        long userMoney = Long.parseLong(transactionRep.getUserBalance(userId));
        Transaction tr = new Transaction(descr, new Date(), price, TransferType.SPEND, userId, userMoney - price,
                ArticleCashFlow.LAND_LOTS_BUY);
        transactionRep.addTransaction(tr);

        // добавить пользователю участок в конкретном районе
        addLandLots(userId, cityArea, 1);
    }

    @Override
    public List<LandLotsInfo> getLandLotInfo(Long userId, CityArea cityArea) {
        List<LandLotsInfo> result = new ArrayList<>();

        // добавить информацию об имущества
        for (Property property : propertyRepository.cityAreaProperties(userId, cityArea)) {
            result.add(new LandLotsInfo(property.getId(), property.getName(), property.isOnSale()));
        }
        // добавить информацию об объектах строительства
        for (ConstructionProject project : constrRep.getCityAreaUserConstrProject(userId, cityArea)) {
            double completePercent = CommonUtil.numberRound(project.getCompletePerc(), 2);
            result.add(new LandLotsInfo(project.getId(), completePercent));
        }
        return result;
    }

}
