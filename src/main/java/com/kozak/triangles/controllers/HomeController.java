package com.kozak.triangles.controllers;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import com.kozak.triangles.other.DateUtils;
import com.kozak.triangles.repositories.TransactionRepository;
import com.kozak.triangles.repositories.UserRepository;

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

		int currUserId = userRepository.getCurrentUserId(user.getLogin());
		checkFirstTime(currUserId); // проверка, первый ли вход в игру (вообще)
		giveDailyBonus(currUserId); // начисление ежедневного бонуса
		giveCreditDeposit(currUserId); // начисление кредита/депозита
		levyOnProperty(currUserId); // сбор средств с имущества, где есть кассир
		salaryPayment(currUserId); //выдача зп работникам
	
		// output balance
		NumberFormat formatter = NumberFormat.getInstance(new Locale("ru"));
		String balance = formatter.format(Long.valueOf(transactRepository.getUserBalance(currUserId)));
		model.addAttribute("balance", balance);

		return "home";
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

	private void giveCreditDeposit(int currUserId) {
		// TODO Auto-generated method stub
		
		// начисляется каждые 30 дней - для этого
		// 1. получить последнюю дату начисления кредита/депозита
		// 2. если дата >= 30 дней - циклом начислить К/Д (колво итераций = количество дней / 30)
		// * подумать над ставками
	}

	@RequestMapping(value = "/property", method = RequestMethod.GET)
	String propertyGET() {
		return "property";
	}

	@RequestMapping(value = "/bank", method = RequestMethod.GET)
	String bankGET() {
		return "bank";
	}

	@RequestMapping(value = "/relations", method = RequestMethod.GET)
	String relationsGET() {
		return "relations";
	}

	@RequestMapping(value = "/entertainment", method = RequestMethod.GET)
	String entertainmentGET() {
		return "entertainment";
	}

	// other methods

	/**
	 * проверка, первый ли раз вошли в игру.
	 * если первый - добавляем начальную транзакцию пользователю
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

			Transaction t = new Transaction("Начальный капитал", yest.getTime(), 17000, TransferType.PROFIT,
					currUserId, 17000, ArticleCashFlow.DAILY_BONUS);
			transactRepository.addTransaction(t);
		}
	}

	/**
	 * начисление дневного бонуса.
	 * 
	 * @param currUserId
	 */
	private void giveDailyBonus(int currUserId) {
		// get user transactions
		List<Transaction> userTransactions = transactRepository.getDailyBonusUserTransactions(currUserId);
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
			int oldBalance = userTransactions.get(userTransactions.size() - 1).getBalance();
			Transaction t = new Transaction(description, now, bonusSum, TransferType.PROFIT, currUserId, oldBalance
					+ bonusSum, ArticleCashFlow.DAILY_BONUS);
			transactRepository.addTransaction(t);
		}
	}

	private int getBonusSum(int i) {
		final int FIRST_DAY = 20;
		final int SECOND_DAY = 50;
		final int THIRD_DAY = 200;
		final int FOURTH_DAY = 400;
		final int FIFTH_DAY = 1000;

		switch (i) {
		case 1:
			return FIRST_DAY;
		case 2:
			return SECOND_DAY;
		case 3:
			return THIRD_DAY;
		case 4:
			return FOURTH_DAY;
		case 5:
			return FIFTH_DAY;
		default:
			return 0;
		}
	}

	/**
	 * вычисляет, сколько строк в списке транзакций пользователя
	 * нужно в цикле при начислении ежедневного бонуса
	 * для перебора и сравнения текущей даты с предыдущими
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
