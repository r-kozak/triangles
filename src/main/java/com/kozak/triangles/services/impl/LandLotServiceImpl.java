package com.kozak.triangles.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kozak.triangles.entities.LandLot;
import com.kozak.triangles.enums.CityAreas;
import com.kozak.triangles.repositories.LandLotRepository;
import com.kozak.triangles.services.LandLotService;

@Service
public class LandLotServiceImpl implements LandLotService {

    @Autowired
    private LandLotRepository landLotRepository;

    @Override
    public int getCountOfLandLot(long userId, CityAreas cityArea) {
        return landLotRepository.getLandLot(userId, cityArea).getLotCount();
    }

    @Override
    public void addOneLandLot(Long userId, CityAreas cityArea) {
        LandLot landLot = landLotRepository.getLandLot(userId, cityArea);
        int newLotCount = landLot.getLotCount() + 1;
        landLot.setLotCount(newLotCount);
        landLotRepository.addUpdateLandLot(landLot);
    }

}
