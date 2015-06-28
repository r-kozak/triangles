package com.kozak.triangles.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.entities.Vmap;
import com.kozak.triangles.enums.ArticleCashFlowT;
import com.kozak.triangles.enums.TransferT;
import com.kozak.triangles.enums.buildings.BuildingsT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;
import com.kozak.triangles.interfaces.Consts;
import com.kozak.triangles.repositories.BuildingDataRep;
import com.kozak.triangles.repositories.ReProposalRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.repositories.UserRep;
import com.kozak.triangles.repositories.VmapRep;
import com.kozak.triangles.utils.DateUtils;
import com.kozak.triangles.utils.ProposalGenerator;
import com.kozak.triangles.utils.Util;

@SessionAttributes("user")
@Controller
public class HomeController {
    @Autowired
    private UserRep userRep;
    @Autowired
    private TransactionRep trRep;
    @Autowired
    private BuildingDataRep buiDataRep;
    @Autowired
    private VmapRep vmRep;
    @Autowired
    private ReProposalRep rePrRep;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    String homeGET(User user, Model model) throws InterruptedException {
        if (user == null) {
            return "redirect:/";
        }
        User currUser = userRep.getCurrentUserByLogin(user.getLogin());
        // set currUser lastEnter
        currUser.setLastEnter(new Date());
        userRep.updateUser(currUser);

        int currUserId = currUser.getId();
        user.setId(currUserId);

        buildDataInit(); // инициализируем данные по строениям для каждого типа
        checkFirstTime(currUserId); // проверка, первый ли вход в игру (вообще)
        giveDailyBonus(currUserId); // начисление ежедневного бонуса
        giveCreditDeposit(currUserId); // начисление кредита/депозита
        manageREMarketProposals(); // очистить-добавить предложения на глобальный рынок недвижимости
        levyOnProperty(currUserId); // сбор средств с имущества, где есть кассир
        salaryPayment(currUserId); // выдача зп работникам

        model = Util.addBalanceToModel(model, trRep.getUserBalance(currUserId));

        return "home";
    }

    /**
     * Первоначальное добавление данных о каждом типе имущества в БД.
     * Нужно для дальнейшего получения данных и генерации предложений на рынке
     * 
     * После добавления нового типа (CommBuildingsT) необходимо его добавить и здесь
     */
    private void buildDataInit() {
        CommBuildData data = null;
        BuildingsT superTYPE = BuildingsT.TRADING;

        // init STALL
        CommBuildingsT TYPE = CommBuildingsT.STALL;
        if (buiDataRep.getCommBuildDataByType(TYPE) == null) {
            data = new CommBuildData(3, 6, 4500, 5500, TYPE, superTYPE, 1, 1, 2);
            buiDataRep.addCommBuildingData(data);
        }

        // init VILLAGE_SHOP
        TYPE = CommBuildingsT.VILLAGE_SHOP;
        if (buiDataRep.getCommBuildDataByType(TYPE) == null) {
            data = new CommBuildData(2, 10, 10000, 15000, TYPE, superTYPE, 2, 2, 3);
            buiDataRep.addCommBuildingData(data);
        }

        // init STATIONER_SHOP
        TYPE = CommBuildingsT.STATIONER_SHOP;
        if (buiDataRep.getCommBuildDataByType(TYPE) == null) {
            data = new CommBuildData(5, 12, 17000, 30000, TYPE, superTYPE, 3, 1, 4);
            buiDataRep.addCommBuildingData(data);
        }
    }

    /**
     * Управляет предложениями на рынке недвижимости
     * 
     * очищает рынок от устаревших предложений, а затем
     * генерирует и добавляет новые предложения на рынок недвижимости,
     * генерирует след. дату генерации предложений
     */
    private void manageREMarketProposals() {
        clearREMarket(); // очищает рынок от устаревших предложений

        int activeUsers = userRep.countActiveUsers();
        boolean marketEmpty = rePrRep.getREProposalsList(1).isEmpty();

        Vmap vm = vmRep.getNextProposeGen(); // получаем экземпляр константы (след. даты генерации предложений)
        Date nrep = DateUtils.stringToDate(vm.getValue()); // берем из нее значение даты
        boolean dateCome = (new Date().after(nrep)); // пришла след. дата генерации предложений

        if (marketEmpty || dateCome) {
            // генерируем предложения
            HashMap<String, CommBuildData> mapData = new HashMap<String, CommBuildData>();

            ArrayList<CommBuildData> data = (ArrayList<CommBuildData>) buiDataRep.getCommBuildDataList();
            for (CommBuildData d : data) {
                mapData.put(d.getBuildType().name(), d);
            }

            ProposalGenerator pg = new ProposalGenerator();
            ArrayList<RealEstateProposal> result = pg.generateProposalsREMarket(activeUsers, mapData);
            for (RealEstateProposal prop : result) {
                rePrRep.addREproposal(prop);
            }
        }

        // если пришла дата след. генерации, значит нужно генерить новую
        if (dateCome) {
            generateNewNextDate(vm); // генерация новой даты NextREproposal (константа с Vmap)
        }
    }

    /**
     * генерирует новый экземпляр даты следующей генерации предложений
     * 
     * @param oldValue
     *            старый экземпляр константы для обновления
     */
    private void generateNewNextDate(Vmap oldValue) {
        ProposalGenerator pg = new ProposalGenerator();
        String newDate = pg.generateNEXT_RE_PROPOSE();

        oldValue.setValue(newDate);
        vmRep.updateVmapRow(oldValue);
    }

    /**
     * очищает рынок недвижимости от не актуальных предложений (lossDate которых меньше текущей)
     */
    private void clearREMarket() {
        List<RealEstateProposal> outdated = rePrRep.getOutdatedProposals();
        for (RealEstateProposal rep : outdated) {
            rep.setValid(false);
            rePrRep.updateREproposal(rep);
        }
    }

    /**
     * проверка, первый ли раз вошли в игру. если первый - добавляем начальные транзакции пользователю
     * 
     * @throws InterruptedException
     * 
     */
    private void checkFirstTime(int currUserId) throws InterruptedException {
        // get user transactions
        List<Transaction> userTransactions = trRep.getAllUserTransactions(currUserId);

        // if it's a first time in game - add start transactions for user
        if (userTransactions.isEmpty()) {
            Calendar yest = Calendar.getInstance();
            yest.setTime(new Date());
            yest.add(Calendar.DATE, -1);

            // transaction for DAILY_BONUS
            Transaction firstT = new Transaction("Начальный капитал", yest.getTime(), 17000, TransferT.PROFIT,
                    currUserId, 17000, ArticleCashFlowT.DAILY_BONUS);
            trRep.addTransaction(firstT);

            // transaction for CREDIT_DEPOSIT
            yest.add(Calendar.SECOND, 1);
            firstT = new Transaction("Начальный кредит/депозит", yest.getTime(), 0, TransferT.PROFIT, currUserId,
                    17000, ArticleCashFlowT.CREDIT_DEPOSIT);
            trRep.addTransaction(firstT);
            Thread.sleep(1000);

            // transaction for LEVY_ON_PROPERTY
            yest.add(Calendar.SECOND, 1);
            firstT = new Transaction("Начальный сбор с имущества", yest.getTime(), 0, TransferT.PROFIT, currUserId,
                    17000, ArticleCashFlowT.LEVY_ON_PROPERTY);
            trRep.addTransaction(firstT);
        }
    }

    /**
     * начисление дневного бонуса.
     * 
     * @param currUserId
     */
    private void giveDailyBonus(int currUserId) {
        // get user transactions
        List<Transaction> userTransactions = trRep.getUserTransactionsByType(currUserId,
                ArticleCashFlowT.DAILY_BONUS);
        Date lastTransactionDate = userTransactions.get(userTransactions.size() - 1).getTransactDate();

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        // пользователь сегодня уже получил бонус?
        boolean gotBonusToday = DateUtils.isSameDay(lastTransactionDate, today.getTime());

        if (!gotBonusToday) {
            int thisDayNumber = 0; // номер текущего дня подряд при входе

            // сколько последних дней проверять
            int countLastRows = howMuchLastDaysToCheck(userTransactions);
            for (int i = 1; i < countLastRows; i++) {
                Calendar lastDate = Calendar.getInstance();
                lastDate.setTime(userTransactions.get(userTransactions.size() - i).getTransactDate());

                today.add(Calendar.DATE, -1);

                if (DateUtils.isSameDay(lastDate, today)) {
                    thisDayNumber++;
                } else {
                    break;
                }
            }
            if (thisDayNumber >= 5) {
                thisDayNumber = 0;
            }
            thisDayNumber += 1;

            int bonusSum = getBonusSum(thisDayNumber);
            String description = "Ежедневный бонус (день " + thisDayNumber + "-й)";
            long oldBalance = Long.parseLong(trRep.getUserBalance(currUserId));
            Transaction t = new Transaction(description, new Date(), bonusSum, TransferT.PROFIT, currUserId, oldBalance
                    + bonusSum, ArticleCashFlowT.DAILY_BONUS);
            trRep.addTransaction(t);
        }
    }

    /**
     * начисление кредита/депозита начисляется каждый месяц ставки описаны в интерфейсе Consts
     * 
     * @param currUserId
     */
    private void giveCreditDeposit(int currUserId) {
        // get user transactions
        List<Transaction> userTransactions = trRep.getUserTransactionsByType(currUserId,
                ArticleCashFlowT.CREDIT_DEPOSIT);

        Date lastTransactionDate = userTransactions.get(userTransactions.size() - 1).getTransactDate();

        int daysBetween = DateUtils.daysBetween(lastTransactionDate, new Date());
        if (daysBetween > 0) {
            int countI = daysBetween / 30;
            for (int i = 0; i < countI; i++) {
                long userBalance = Long.parseLong(trRep.getUserBalance(currUserId));
                double rate = (userBalance > 0 ? Consts.DEPOSIT_RATE : Consts.CREDIT_RATE);
                TransferT transferType = (userBalance > 0 ? TransferT.PROFIT : TransferT.SPEND);
                long sum = (long) (userBalance * rate);
                long newBalance = userBalance + sum;

                // date for description
                Calendar c1 = Calendar.getInstance();
                // отнимаем необходимое количество дней, например с последнего начисления прошло 64 дня
                // начисляем за 2 месяца
                // итерация 1: тек дата - ((2 - 0) * 30) + 4 = 64. дата с 13.04.15 по 12.05.15
                // итерация 2: тек дата - ((2 - 1) * 30) + 4 = 34. дата с 12.05.15 по 11.06.15
                c1.add(Calendar.DATE, -((countI - i) * 30 + daysBetween % 30));
                Date dateFrom = c1.getTime();

                Calendar c2 = Calendar.getInstance();
                c2 = c1;
                c2.add(Calendar.DATE, 30);
                Date dateTo = c2.getTime();
                // //

                String description = (userBalance > 0 ? "Начислено депозит за: %td.%tm.%ty - %td.%tm.%ty"
                        : "Начислено кредит за: %td.%tm.%ty - %td.%tm.%ty");
                description = String.format(description, dateFrom, dateFrom, dateFrom, dateTo, dateTo, dateTo);

                Transaction cdTr = new Transaction(description, new Date(), sum, transferType, currUserId, newBalance,
                        ArticleCashFlowT.CREDIT_DEPOSIT);
                trRep.addTransaction(cdTr);
            }
        }
    }

    private void salaryPayment(int currUserId) {
        // TODO Auto-generated method stub

        // выдача зарплаты кассирам, продавцам
    }

    private void levyOnProperty(int currUserId) {
        // TODO Auto-generated method stub

        // для каждого коммерческого имущества юзера
        // если у имущества есть кассир - тогда собираем прибыль
    }

    /**
     * 
     * @param i
     *            - day number
     * @return sum of day bonus by day number
     */
    private int getBonusSum(int i) {
        switch (i) {
        case 1:
            return Consts.FIRST_DAY;
        case 2:
            return Consts.SECOND_DAY;
        case 3:
            return Consts.THIRD_DAY;
        case 4:
            return Consts.FOURTH_DAY;
        case 5:
            return Consts.FIFTH_DAY;
        default:
            return 0;
        }
    }

    /**
     * вычисляет, сколько строк в списке транзакций пользователя нужно в цикле при начислении ежедневного бонуса для
     * перебора и сравнения текущей даты с предыдущими
     * 
     * @param userTransactions
     *            - список транзакций пользователя
     * @return - количество последних строк для цикла
     */
    private int howMuchLastDaysToCheck(List<Transaction> userTransactions) {
        int countLastRows = 1;
        if (userTransactions.size() >= 5) {
            countLastRows = 6;
        } else if (!userTransactions.isEmpty()) {
            countLastRows = userTransactions.size() + 1;
        }
        return countLastRows;
    }
}
