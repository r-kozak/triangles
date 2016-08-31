package com.kozak.triangles.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.kozak.triangles.data.TradeBuildingsTableData;
import com.kozak.triangles.entities.TradeBuilding;
import com.kozak.triangles.repositories.ConstructionProjectRep;
import com.kozak.triangles.repositories.LotteryRep;
import com.kozak.triangles.repositories.MessageRep;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.RealEstateProposalRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.repositories.VmapRep;

public abstract class BaseController {
	// получить данные всех торговых строений
	Map<Integer, TradeBuilding> tradeBuildingsData = TradeBuildingsTableData.getTradeBuildingsDataMap();

    @Autowired
    protected UserRep userRep;
    @Autowired
    protected TransactionRep trRep;
    @Autowired
    protected VmapRep vmRep;
    @Autowired
    protected RealEstateProposalRep realEstateProposalRep;
    @Autowired
    protected PropertyRep prRep;
    @Autowired
    protected ConstructionProjectRep consProjectRep;
    @Autowired
    protected LotteryRep lotteryRep;
    @Autowired
    protected MessageRep msgRep;
}
