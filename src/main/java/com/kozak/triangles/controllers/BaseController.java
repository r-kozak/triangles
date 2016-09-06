package com.kozak.triangles.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.kozak.triangles.data.TradeBuildingsTableData;
import com.kozak.triangles.entities.TradeBuilding;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.enums.LotteryArticles;
import com.kozak.triangles.repositories.ConstructionProjectRep;
import com.kozak.triangles.repositories.LotteryRep;
import com.kozak.triangles.repositories.MessageRep;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.RealEstateProposalRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.repositories.VmapRep;
import com.kozak.triangles.utils.CommonUtil;
import com.kozak.triangles.utils.ResponseUtil;

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

	protected Model addMoneyInfoToModel(Model model, User user) {
		Integer userId = user.getId();
		int userDomi = userRep.getUserDomi(userId);
		String userBalance = trRep.getUserBalance(userId);
		Long userSolvency = CommonUtil.getSolvency(userBalance, prRep, userId);
		return ResponseUtil.addMoneyInfoToModel(model, userBalance, userSolvency, userDomi);
	}

	/**
	 * @return переданную модель, но с информацией о количестве лицензий разных уровней
	 */
	protected Model addLicenseCountInfoToModel(Model model, int userId) {
		long lic2Count = lotteryRep.getPljushkiCountByArticle(userId, LotteryArticles.LICENSE_2);
		model.addAttribute("lic2Count", lic2Count); // количество лицензий 2 ур

		long lic3Count = lotteryRep.getPljushkiCountByArticle(userId, LotteryArticles.LICENSE_3);
		model.addAttribute("lic3Count", lic3Count); // количество лицензий 3 ур

		long lic4Count = lotteryRep.getPljushkiCountByArticle(userId, LotteryArticles.LICENSE_4);
		model.addAttribute("lic4Count", lic4Count);// количество лицензий 4 ур

		return model;
	}
}
