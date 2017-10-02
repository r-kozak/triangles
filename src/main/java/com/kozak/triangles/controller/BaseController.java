package com.kozak.triangles.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.kozak.triangles.data.TradeBuildingsTableData;
import com.kozak.triangles.entity.Transaction;
import com.kozak.triangles.entity.User;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.model.TradeBuilding;
import com.kozak.triangles.repository.ConstructionProjectRep;
import com.kozak.triangles.repository.LotteryRep;
import com.kozak.triangles.repository.MessageRep;
import com.kozak.triangles.repository.PropertyRep;
import com.kozak.triangles.repository.RealEstateProposalRep;
import com.kozak.triangles.repository.TransactionRep;
import com.kozak.triangles.repository.UserRep;
import com.kozak.triangles.repository.VmapRep;
import com.kozak.triangles.service.LandLotService;
import com.kozak.triangles.service.LicenseMarketService;
import com.kozak.triangles.util.CommonUtil;
import com.kozak.triangles.util.ResponseUtil;
import com.kozak.triangles.win_service.BonusService;
import com.kozak.triangles.win_service.LotteryService;
import com.kozak.triangles.win_service.WinService;

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
    @Autowired
    protected LicenseMarketService licenseMarketService;
    @Autowired
    protected LandLotService landLotService;
    @Autowired
    protected WinService winService;
    @Autowired
    protected LotteryService lotteryService;
    @Autowired
    protected BonusService bonusService;

    protected Model addMoneyInfoToModel(Model model, User user) {
        Long userId = user.getId();
        int userDomi = userRep.getUserDomi(userId);
        String userBalance = trRep.getUserBalance(userId);
        Long userSolvency = CommonUtil.getSolvency(userBalance, prRep, userId);
        return ResponseUtil.addMoneyInfoToModel(model, userBalance, userSolvency, userDomi);
    }

    /**
     * @return переданную модель, но с информацией о количестве лицензий разных уровней
     */
    protected Model addLicenseCountInfoToModel(Model model, long userId) {
        long lic2Count = winService.getRemainingAmount(userId, WinArticle.LICENSE_2);
        model.addAttribute("lic2Count", lic2Count); // количество лицензий 2 ур

        long lic3Count = winService.getRemainingAmount(userId, WinArticle.LICENSE_3);
        model.addAttribute("lic3Count", lic3Count); // количество лицензий 3 ур

        long lic4Count = winService.getRemainingAmount(userId, WinArticle.LICENSE_4);
        model.addAttribute("lic4Count", lic4Count);// количество лицензий 4 ур

        return model;
    }

    /**
     * @return переданную модель, но наполненную информацией по участкам земли (сколько занято и сколько всего) в разрезе
     *         конкретного района
     */
    protected Model addLandLotsInfoToModel(Model model, long userId) {
        // информация по участкам земли в разных районах - сколько занято и сколько всего
        model.addAttribute("landLotGhettoBusy", landLotService.getBusyLandLotsCount(userId, CityArea.GHETTO)); // занятые участки
        model.addAttribute("landLotOutskirtsBusy", landLotService.getBusyLandLotsCount(userId, CityArea.OUTSKIRTS));
        model.addAttribute("landLotChinatownBusy", landLotService.getBusyLandLotsCount(userId, CityArea.CHINATOWN));
        model.addAttribute("landLotCenterBusy", landLotService.getBusyLandLotsCount(userId, CityArea.CENTER));
        model.addAttribute("landLotGhettoTotal", landLotService.getCountOfLandLot(userId, CityArea.GHETTO)); // сколько всего
        model.addAttribute("landLotOutskirtsTotal", landLotService.getCountOfLandLot(userId, CityArea.OUTSKIRTS));
        model.addAttribute("landLotChinatownTotal", landLotService.getCountOfLandLot(userId, CityArea.CHINATOWN));
        model.addAttribute("landLotCenterTotal", landLotService.getCountOfLandLot(userId, CityArea.CENTER));

        return model;
    }

    /**
     * @return последнюю дату начисленного кредита или депозита конкретному пользователю
     */
    protected Date getLastCreditOrDepositDate(long userId) {
        // получение транзакций пользователя для получения последней даты начисления
        List<Transaction> userTransactionsCr = trRep.getUserTransactionsByType(userId, ArticleCashFlow.CREDIT);
        List<Transaction> userTransactionsDep = trRep.getUserTransactionsByType(userId, ArticleCashFlow.DEPOSIT);

        // получение дат кредита и депозита, после чего взятие последней
        Date lastTransactionDateCr = userTransactionsCr.get(userTransactionsCr.size() - 1).getTransactDate();
        Date lastTransactionDateDep = userTransactionsDep.get(userTransactionsDep.size() - 1).getTransactDate();
        Date lastTransactionDate = (lastTransactionDateCr.after(lastTransactionDateDep)) ? lastTransactionDateCr
                : lastTransactionDateDep;

        return lastTransactionDate;
    }
}
