package com.kozak.triangles.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kozak.triangles.data.CityAreasTableData;
import com.kozak.triangles.entities.LandLot;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.repositories.ConstructionProjectRep;
import com.kozak.triangles.repositories.LandLotRepository;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.services.LandLotService;
import com.kozak.triangles.utils.Ksyusha;

@Service
public class LandLotServiceImpl implements LandLotService {

    @Autowired
    private LandLotRepository landLotRepository;
    @Autowired
    private PropertyRep propertyRepository;
    @Autowired
    private ConstructionProjectRep constructionProjectRep;

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
        int propertiesCount = propertyRepository.cityAreaProperties(userId, CityArea.OUTSKIRTS).size();
        long constructProjectsCount = constructionProjectRep.getCountOfUserConstrProject(userId, cityArea);
        return propertiesCount + constructProjectsCount;
    }

    @Override
    public long getAvailableLandLotsCount(long userId, CityArea cityArea) {
        // количество участков всего - количество занятых участков
        return getCountOfLandLot(userId, cityArea) - getBusyLandLotsCount(userId, cityArea);
    }

    @Override
    public long getNextLandLotPrice(long userId, CityArea cityArea) {
        // хочет иметь участков = количество участков в конкретном районе + 1
        int nextCount = landLotRepository.getLandLot(userId, cityArea).getLotCount() + 1;

        // расчитать цену следующего участка
        double unCoefSquare = Math.pow(Ksyusha.computeCoef(nextCount), 2);
        long coefs = Math.round(PRICE_COEF * unCoefSquare); // перемножить коэф. участков с универсальным коэф.
        long price = nextCount * (BASE_LAND_LOT_PRICE + (nextCount * coefs));

        // добавить процент района
        price += price * CityAreasTableData.getCityAreaPercent(cityArea) / 100;

        return price;
    }

}
