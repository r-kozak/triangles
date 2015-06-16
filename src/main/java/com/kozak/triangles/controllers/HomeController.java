package com.kozak.triangles.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.interfaces.Rates;
import com.kozak.triangles.repositories.TransactionRepository;
import com.kozak.triangles.repositories.UserRepository;
import com.kozak.triangles.utils.DateUtils;
import com.kozak.triangles.utils.ModelCreator;

@SessionAttributes("user")
@Controller
public class HomeController {
    private UserRepository userRepository;
    private TransactionRepository transactRepository;

    @Autowired
    public HomeController(UserRepository userRepository, TransactionRepository transactRepository) {
        this.userRepository = userRepository;
        this.transactRepository = transactRepository;
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    String homeGET(User user, Model model) {
        if (user == null) {
            return "redirect:/";
        }
        int currUserId = userRepository.getCurrentUserId(user.getLogin());
        user.setId(currUserId);

        checkFirstTime(currUserId); // проверка, первый ли вход в игру (вообще)
        giveDailyBonus(currUserId); // начисление ежедневного бонуса
        giveCreditDeposit(currUserId); // начисление кредита/депозита
        addProposalREMarket(); // добавляет предложение на глобальный рынок недвижимости
        levyOnProperty(currUserId); // сбор средств с имущества, где есть кассир
        salaryPayment(currUserId); // выдача зп работникам

        model = ModelCreator.addBalance(model, transactRepository.getUserBalance(currUserId));

        return "home";
    }

    private void addProposalREMarket() {
        // TODO Auto-generated method stub

        // рынок глобальный
        // количество предложений зависит от количества активных! пользователей
        // активный пользователь тот - который заходил последние 2 недели
        // на одного пользователя каждые 2-14 дней появляется по имуществу
    }

    /**
     * проверка, первый ли раз вошли в игру. если первый - добавляем начальные транзакции пользователю
     * 
     */
    private void checkFirstTime(int currUserId) {
        // get user transactions
        List<Transaction> userTransactions = transactRepository.getAllUserTransactions(currUserId);

        // if it's a first time in game
        if (userTransactions.isEmpty()) {
            Calendar yest = Calendar.getInstance();
            yest.setTime(new Date());
            yest.add(Calendar.DATE, -1);

            // transaction for DAILY_BONUS
            Transaction firstT = new Transaction("Начальный капитал", yest.getTime(), 17000, TransferType.PROFIT,
                    currUserId, 17000, ArticleCashFlow.DAILY_BONUS);
            transactRepository.addTransaction(firstT);

            // transaction for CREDIT_DEPOSIT
            firstT = new Transaction("Начальный кредит/депозит", yest.getTime(), 0, TransferType.PROFIT, currUserId,
                    17000, ArticleCashFlow.CREDIT_DEPOSIT);
            transactRepository.addTransaction(firstT);

            // transaction for LEVY_ON_PROPERTY
            firstT = new Transaction("Начальный сбор с имущества", yest.getTime(), 0, TransferType.PROFIT, currUserId,
                    17000, ArticleCashFlow.LEVY_ON_PROPERTY);
            transactRepository.addTransaction(firstT);
        }
    }

    /**
     * начисление дневного бонуса.
     * 
     * @param currUserId
     */
    private void giveDailyBonus(int currUserId) {
        // get user transactions
        List<Transaction> userTransactions = transactRepository.getUserTransactionsByType(currUserId,
                ArticleCashFlow.DAILY_BONUS);
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

                System.out.println(DateUtils.isSameDay(lastDate, today));
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
            Date now = new Date();

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = formatter.format(now);

            String description = "Ежедневный бонус за: " + formattedDate;
            long oldBalance = userTransactions.get(userTransactions.size() - 1).getBalance();
            Transaction t = new Transaction(description, now, bonusSum, TransferType.PROFIT, currUserId, oldBalance
                    + bonusSum, ArticleCashFlow.DAILY_BONUS);
            transactRepository.addTransaction(t);
        }
    }

    /**
     * начисление кредита/депозита начисляется каждый месяц ставки описаны в интерфейсе Rates
     * 
     * @param currUserId
     */
    private void giveCreditDeposit(int currUserId) {
        // get user transactions
        List<Transaction> userTransactions = transactRepository.getUserTransactionsByType(currUserId,
                ArticleCashFlow.CREDIT_DEPOSIT);

        Date lastTransactionDate = userTransactions.get(userTransactions.size() - 1).getTransactDate();

        int daysBetween = DateUtils.daysBetween(lastTransactionDate, new Date());
        if (daysBetween > 0) {
            int countI = daysBetween / 30;
            for (int i = 0; i < countI; i++) {
                long userBalance = Long.parseLong(transactRepository.getUserBalance(currUserId));
                double rate = (userBalance > 0 ? Rates.DEPOSIT_RATE : Rates.CREDIT_RATE);
                TransferType transferType = (userBalance > 0 ? TransferType.PROFIT : TransferType.SPEND);
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
                        ArticleCashFlow.CREDIT_DEPOSIT);
                transactRepository.addTransaction(cdTr);
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
            return Rates.FIRST_DAY;
        case 2:
            return Rates.SECOND_DAY;
        case 3:
            return Rates.THIRD_DAY;
        case 4:
            return Rates.FOURTH_DAY;
        case 5:
            return Rates.FIFTH_DAY;
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
