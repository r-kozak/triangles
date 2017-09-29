package com.kozak.triangles.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kozak.triangles.data.CityAreasTableData;
import com.kozak.triangles.data.LicensesTableData;
import com.kozak.triangles.data.TradeBuildingsTableData;
import com.kozak.triangles.entity.ConstructionProject;
import com.kozak.triangles.entity.LicenseMarket;
import com.kozak.triangles.entity.Property;
import com.kozak.triangles.entity.RealEstateProposal;
import com.kozak.triangles.entity.Transaction;
import com.kozak.triangles.entity.User;
import com.kozak.triangles.entity.UserLicense;
import com.kozak.triangles.entity.Vmap;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.model.TradeBuilding;
import com.kozak.triangles.util.CommonUtil;
import com.kozak.triangles.util.Constants;
import com.kozak.triangles.util.DateUtils;
import com.kozak.triangles.util.PropertyUtil;
import com.kozak.triangles.util.ProposalGenerator;
import com.kozak.triangles.util.Random;

@SessionAttributes("user")
@Controller
public class HomeController extends BaseController {

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    String homePage(User user, Model model) throws InterruptedException {
        if (user == null) {
            return "redirect:/";
        }
        // получить пользователя со всеми данными
        user = userRep.getCurrentUserByLogin(user.getLogin().toLowerCase());
        model.addAttribute("user", user); // чтобы в след. действиях был уже юзер со всеми полями

        // set currUser lastEnter
        user.setLastEnter(new Date());
        userRep.updateUser(user);

        long userId = user.getId();

        checkFirstTime(user); // проверка, первый ли вход в игру (вообще)
        giveDailyBonusAndLotteryTickets(user); // начисление ежедневного бонуса
        licenseMarketService.processSoldLicenses(userId); // списывает партии проданных лицензий
        giveCreditDeposit(userId); // начисление кредита/депозита
        manageREMarketProposals(userId); // очистить-добавить предложения на глобальный рынок недвижимости
        PropertyUtil.profitCalculation(userId, prRep); // начисление прибыли по имуществу пользователя
        propertyDepreciation(userId); // начисление амортизации

        // начислить проценты завершенности для всех объектов строительства
        List<ConstructionProject> constrProjects = consProjectRep.getUserConstructProjects(userId);
        BuildingController.computeAndSetCompletePercent(constrProjects, consProjectRep);

        // проверить окончилась ли лицензия
        User userWithLicense = userRep.getUserWithLicense(userId); // пользователь с лицензиями
        UserLicense userLicense = userWithLicense.getUserLicense();
        Date licenseExpireDate = userLicense.getLossDate(); // дата окончания лицензии
        // если кончилась - назначить новую и получить ее
        licenseExpireDate = BuildingController.checkLicenseExpire(licenseExpireDate, userRep, userId);

        // количество завершенных строительных проектов пользователя
        long countCompletedProj = consProjectRep.getCountOfUserCompletedConstrProject(userId);
        model.addAttribute("countCompletedProj", countCompletedProj);
        if (countCompletedProj > 0) {
            // если есть завершенные, то на домашней странице покажем их количество (напр.: 2/5)
            long totalProjCount = consProjectRep.getCountOfUserConstrProject(userId);

            model.addAttribute("totalProjCount", totalProjCount);
        } else {
            // иначе - покажем таймер, сколько осталось до ближайшей эксплуатации
            model.addAttribute("toExploitation", consProjectRep.getNextExploitation(userId));
        }

        // количество начатых строительных проектов сегодня
        model.addAttribute("startedToConstructToday", consProjectRep.getCountOfStartedProjectsToday(userId));

        model = addMoneyInfoToModel(model, user);
        model.addAttribute("rePrCo", realEstateProposalRep.allPrCount(false, userId)); // колво предложений на рынке имущества
        model.addAttribute("newRePrCo", realEstateProposalRep.allPrCount(true, userId)); // новых предложений на рын.имущ.
        model.addAttribute("ready", prRep.allPrCount(userId, true, false)); // колво готовых к сбору дохода
        model.addAttribute("comPrCount", prRep.allPrCount(userId, false, false)); // всего имущества
        model.addAttribute("nextProfit", prRep.getMinNextProfit(userId)); // дата следующей прибыли
        model.addAttribute("needRepair", prRep.allPrCount(userId, false, true)); // скольким имуществам нужен ремонт
        model.addAttribute("licenseLevel", userLicense.getLicenseLevel()); // уровень лицензии
        model.addAttribute("licenseExpire", licenseExpireDate); // окончание лицензии
        model.addAttribute("ticketsCount", user.getLotteryTickets()); // количество лотерейных билетов
        model.addAttribute("userLogin", user.getLogin()); // логин пользователя
        model.addAttribute("constructionLimitPerDay", Constants.CONSTRUCTION_LIMIT_PER_DAY); // лимит постройки зданий в день, шт
        model.addAttribute("lotteryGamesLimit", Constants.LOTTERY_GAMES_LIMIT_PER_DAY); // лимит на количество розыграшей в день
        model.addAttribute("playsCountToday", lotteryRep.countOfPlaysToday(userId)); // количество розыграшей сегодня

        // информация по магазину лицензий
        LicenseMarket licenseMarket = licenseMarketService.getLicenseMarket(userId, true);
        model.addAttribute("licenseMarket", licenseMarket); // магазин лицензий
        if (licenseMarket != null) {
            // может ли магазин функционировать
            model.addAttribute("licenseMarketActive", licenseMarketService.isMarketCanFunction(userId));

            // если есть партии лицензий на продаже
            if (!licenseMarket.getLicensesConsignments().isEmpty()) {
                // количество лицензий на продаже
                model.addAttribute("sellLicensesCount", licenseMarket.getLicensesConsignments().size());
                // дата ближайшей продажи лицензий
                model.addAttribute("toLicenseSell", licenseMarket.getLicensesConsignments().get(0).getSellDate());
            }
        }
        
        // наполнить модель информацией по участках в каждом районе (занято и всего участков)
        model = addLandLotsInfoToModel(model, userId);


        // СТАТИСТИКА
        model.addAttribute("profitSum", trRep.getSumByTransfType(userId, TransferType.PROFIT)); // прибыль всего
        model.addAttribute("profitFromProp", trRep.getSumByAcf(userId, ArticleCashFlow.LEVY_ON_PROPERTY));
        model.addAttribute("profitFromPropSell", trRep.getSumByAcf(userId, ArticleCashFlow.SELL_PROPERTY));
        model.addAttribute("profitDB", trRep.getSumByAcf(userId, ArticleCashFlow.DAILY_BONUS));
        model.addAttribute("profitDep", trRep.getSumByAcf(userId, ArticleCashFlow.DEPOSIT));
        model.addAttribute("profitDomi", trRep.getSumByAcf(userId, ArticleCashFlow.DOMINANT_TO_TRIAN));
        model.addAttribute("profitLoto", trRep.getSumByAcf(userId, ArticleCashFlow.LOTTERY_WINNINGS));
        model.addAttribute("profitFromLicensesSell", trRep.getSumByAcf(userId, ArticleCashFlow.SELL_LICENSE));

        model.addAttribute("spendSum", trRep.getSumByTransfType(userId, TransferType.SPEND)); // расход всего
        model.addAttribute("spendCr", trRep.getSumByAcf(userId, ArticleCashFlow.CREDIT));
        model.addAttribute("spendBuyPr", trRep.getSumByAcf(userId, ArticleCashFlow.BUY_PROPERTY));
        model.addAttribute("spendRepair", trRep.getSumByAcf(userId, ArticleCashFlow.PROPERTY_REPAIR));
        model.addAttribute("spendUpCash", trRep.getSumByAcf(userId, ArticleCashFlow.UP_CASH_LEVEL));
        model.addAttribute("spendUpLevel", trRep.getSumByAcf(userId, ArticleCashFlow.UP_PROP_LEVEL));
        model.addAttribute("spendLoto", trRep.getSumByAcf(userId, ArticleCashFlow.LOTTERY_TICKETS_BUY));
        model.addAttribute("spendLicenseBuy", trRep.getSumByAcf(userId, ArticleCashFlow.BUY_LICENSE));
        model.addAttribute("spendConstructProperty", trRep.getSumByAcf(userId, ArticleCashFlow.CONSTRUCTION_PROPERTY));
        model.addAttribute("spendWithdraw", trRep.getSumByAcf(userId, ArticleCashFlow.WITHDRAW));
        return "index/home";
    }

    @RequestMapping(value = "/rating", method = RequestMethod.GET)
    String ratingPage(User user, Model model) {
        model = addMoneyInfoToModel(model, user);
        List<Object[]> users = userRep.getUserRating();
        model.addAttribute("users", users);
        return "rating";
    }

    /**
     * wiki
     */
    @RequestMapping(value = "/wiki", method = RequestMethod.GET)
    String wiki(User user, Model model) {
        model = addMoneyInfoToModel(model, user);

        // данные имущества
        model.addAttribute("tradeBuildingsData", TradeBuildingsTableData.getTradeBuildingsDataList());
        // мин и макс частота генерации предложений на рынке
        model.addAttribute("frp_min", Constants.FREQ_RE_PROP_MIN);
        model.addAttribute("frp_max", Constants.FREQ_RE_PROP_MAX);
        // районы города
        model.addAttribute("gp", CityAreasTableData.getCityAreaPercent(CityArea.GHETTO));
        model.addAttribute("op", CityAreasTableData.getCityAreaPercent(CityArea.OUTSKIRTS));
        model.addAttribute("chp", CityAreasTableData.getCityAreaPercent(CityArea.CHINATOWN));
        model.addAttribute("cep", CityAreasTableData.getCityAreaPercent(CityArea.CENTER));
        // проценты типов строителей
        model.addAttribute("builders", Constants.BUILDERS_COEF);
        // цены и сроки лицензий на строительство
        model.addAttribute("licPrices", LicensesTableData.getLicensePricesTable().values().toArray());
        model.addAttribute("licTerms", LicensesTableData.getLicenseTermsTable().values().toArray());
        // ставки кредита и депозита
        model.addAttribute("cr_rate", Constants.CREDIT_RATE);
        model.addAttribute("dep_rate", Constants.DEPOSIT_RATE);
        // ежедневный бонус
        model.addAttribute("dailyBonus", Constants.DAILY_BONUS_SUM);
        // коэф-ты уменьшения сумм
        model.addAttribute("kdr", Constants.K_DECREASE_REPAIR);
        model.addAttribute("kdp", Constants.K_DECREASE_PROP_L);
        model.addAttribute("kdc", Constants.K_DECREASE_CASH_L);
        // максимальные уровни имущества и кассы
        model.addAttribute("max_prop_lev", Constants.MAX_PROP_LEVEL);
        model.addAttribute("max_cash_lev", Constants.MAX_CASH_LEVEL);
        // цены на лотерейные билеты
        model.addAttribute("ticketsPrice", Constants.LOTTERY_TICKETS_PRICE);
        // лимиты на доминантность и цена билета при ежедневном начислении билетов
        model.addAttribute("domiTicketPrice", Constants.DAILY_TICKETS_FROM_DOMI_K);
        model.addAttribute("domiLimit", Constants.DOMI_LOTTERY_LIMIT);
        model.addAttribute("constructionLimit", Constants.CONSTRUCTION_LIMIT_PER_DAY);
        // магазин лицензий
        model.addAttribute("maxLicMarketLevel", LicenseMarket.MAX_LEVEL);
        model.addAttribute("basePriceLicMarketBuild", LicenseMarket.BASE_PRICE);
        model.addAttribute("hoursToSellLic2", LicensesTableData.getHoursToSellLicenses(2));
        model.addAttribute("hoursToSellLic3", LicensesTableData.getHoursToSellLicenses(3));
        model.addAttribute("hoursToSellLic4", LicensesTableData.getHoursToSellLicenses(4));
        // лимит на количество розыграшей в день
        model.addAttribute("lotteryGamesLimit", Constants.LOTTERY_GAMES_LIMIT_PER_DAY);

        return "wiki";
    }

    /**
     * еженедельное начисление амортизации. Есть основной % износа и доп. % износа. Доп. плюсуется или минусуется от основного.
     * 
     * Основной = 100 / срок полезного использования имущества. Доп = Основной * РАНД(1, 30) / 100;
     * 
     * @param currUserId
     */
    private void propertyDepreciation(long userId) {
        // получить валидное имущество пользователя, у которого nD <= тек.дата
        ArrayList<Property> properties = (ArrayList<Property>) prRep.getPropertyListForProfit(userId, false);

        // для каждого имущества
        for (Property p : properties) {
            TradeBuilding data = tradeBuildingsData.get(p.getTradeBuildingType().ordinal());

            int deprSum = 0;
            double deprPerc = 0;

            // расчитать, за сколько недель нужно насчитать прибыль
            Date d1 = p.getNextDepreciation();
            Date d2 = new Date();
            int dayBetw = DateUtils.daysBetween(d1, d2) + 1;
            int calcC = (dayBetw / 7) + 1; // количество начислений

            for (int i = 0; i < calcC; i++) {
                // будем (+ || -) доп. процент износа
                boolean plus = (Random.generateRandNum(0, 1) == 0) ? true : false;

                // ОСНОВНОЙ % за неделю
                double mainPerc = 100 / data.getUsefulLife();

                // ДОП % (расч. от основного)
                double additPerc = mainPerc * (int) Random.generateRandNum(1, 30) / 100;

                // приплюсовать или отминусовать ПР от ПИ
                if (plus) {
                    mainPerc += additPerc;
                } else {
                    mainPerc -= additPerc;
                }

                // вычислить недельную сумму износа
                deprSum += p.getInitialCost() * mainPerc / 100;
                deprPerc += mainPerc;
            }

            // вычислить новую цену продажи
            long newSellingPrice = p.getSellingPrice() - deprSum;
            newSellingPrice = newSellingPrice < 0 ? 0 : newSellingPrice; // нельзя, чтобы цена продажи была меньше 0

            // вычислить новый процент износа
            double newDepreciationPercent = CommonUtil.numberRound(p.getDepreciationPercent() + deprPerc, 2);
            newDepreciationPercent = newDepreciationPercent > 100 ? 100 : newDepreciationPercent; // не больше 100

            // установить новые значения
            p.setNextDepreciation(DateUtils.addDays(new Date(), 7 - (dayBetw % 7)));
            p.setSellingPrice(newSellingPrice);
            p.setDepreciationPercent(newDepreciationPercent);
            if (newDepreciationPercent >= 100) {
                p.setValid(false);
            }
            prRep.updateProperty(p);

            // если имущество на продаже - изменить цену в предложении
            if (p.isOnSale()) {
                // получить предложение с этим имуществом на рынке и установить новую цену
                RealEstateProposal rePr = realEstateProposalRep.getProposalByUsedId(p.getId());
                rePr.setPurchasePrice(p.getSellingPrice());
                realEstateProposalRep.updateREproposal(rePr);
            }
        }
    }

    /**
     * Управляет предложениями на рынке недвижимости
     * 
     * очищает рынок от устаревших предложений, а затем генерирует и добавляет новые предложения на рынок недвижимости, генерирует
     * след. дату генерации предложений
     */
    private void manageREMarketProposals(long userId) {
        clearREMarket(); // очищает рынок от устаревших предложений

        int activeUsers = userRep.countActiveUsers();
        boolean marketEmpty = realEstateProposalRep.allPrCount(false, userId) == 0;

        Vmap vm = vmRep.getNextProposalGeneration(); // получаем экземпляр константы (след. даты генерации предложений)
        Date nrep = DateUtils.stringToDate(vm.getValue()); // берем из нее значение даты
        boolean dateCome = (new Date().after(nrep)); // пришла след. дата генерации предложений

        if (marketEmpty || dateCome) {
            // генерируем предложения
            List<RealEstateProposal> proposals = null;
            do {
                proposals = new ProposalGenerator().generateMarketProposals(activeUsers);
            } while (proposals.isEmpty());

            for (RealEstateProposal proposal : proposals) {
                realEstateProposalRep.addRealEstateProposal(proposal);
            }
        }
        // если пришла дата след. генерации, значит нужно генерить новую
        if (dateCome) {
            generateNewNextDate(vm); // генерация новой даты NextRealEstateProposal (константа с Vmap)
        }
    }

    /**
     * генерирует новый экземпляр даты следующей генерации предложений
     * 
     * @param oldValue
     *            старый экземпляр константы для обновления
     */
    private void generateNewNextDate(Vmap oldValue) {
        String newDate = new ProposalGenerator().computeNextGeneratingDate();
        oldValue.setValue(newDate);
        vmRep.updateVmapRow(oldValue);
    }

    /**
     * очищает рынок недвижимости от не актуальных предложений (lossDate которых меньше текущей)
     */
    private void clearREMarket() {
        List<RealEstateProposal> outdated = realEstateProposalRep.getOutdatedProposals();
        for (RealEstateProposal rep : outdated) {
            // если это б/у имущество - начислить деньги продавцу
            if (rep.getUsedId() != 0) {
                PropertyUtil.buyUsedProperty(rep, new Date(), 0, prRep, trRep);
            }
            // удалить устаревшее, обработанное предложение
            realEstateProposalRep.removeReProposalById(rep.getId());
        }
    }

    /**
     * проверка, первый ли раз вошли в игру. если первый - добавляем начальные транзакции пользователю
     * 
     * @throws InterruptedException
     */
    private void checkFirstTime(User user) throws InterruptedException {
        Date lastBonus = user.getLastBonus();
        if (lastBonus == null) {
            Date yest = DateUtils.getYesterday();

            // upd user
            user.setLastBonus(yest);
            user.setDayNumber(0);
            userRep.updateUser(user);

            Long userId = user.getId();

            // transaction for DAILY_BONUS
            Transaction firstT = new Transaction("Начальный капитал", yest, Constants.GAME_START_BALANCE, TransferType.PROFIT,
                    userId, Constants.GAME_START_BALANCE, ArticleCashFlow.DAILY_BONUS);
            trRep.addTransaction(firstT);

            // transaction for DEPOSIT
            firstT = new Transaction("Начальный депозит", yest, 0, TransferType.PROFIT, userId,
                    Constants.GAME_START_BALANCE, ArticleCashFlow.DEPOSIT);
            trRep.addTransaction(firstT);

            // transaction for CREDIT
            firstT = new Transaction("Начальный кредит", yest, 0, TransferType.SPEND, userId, Constants.GAME_START_BALANCE,
                    ArticleCashFlow.CREDIT);
            trRep.addTransaction(firstT);

            // transaction for LEVY_ON_PROPERTY
            firstT = new Transaction("Начальный сбор с имущества", yest, 0, TransferType.PROFIT, userId,
                    Constants.GAME_START_BALANCE, ArticleCashFlow.LEVY_ON_PROPERTY);
            trRep.addTransaction(firstT);

            // дать участки пользователю
            landLotService.addLandLots(userId, CityArea.GHETTO, 2);
            landLotService.addLandLots(userId, CityArea.OUTSKIRTS, 1);
        }
    }

    /**
     * начисление дневного бонуса.
     * 
     * @param currUserId
     */
    private void giveDailyBonusAndLotteryTickets(User user) {
        // максимальный номер дня при начислении бонуса
        final int MAX_DAY_NUMBER = Constants.DAILY_BONUS_SUM.length - 1;

        Date lastBonus = user.getLastBonus();
        int dayNumber = user.getDayNumber();

        // пользователь сегодня уже получил бонус?
        boolean gotBonusToday = DateUtils.isSameDay(lastBonus, new Date());

        if (!gotBonusToday) {
            int daysBetw = DateUtils.daysBetween(DateUtils.getStart(lastBonus), DateUtils.getStart(new Date()));

            if (dayNumber == MAX_DAY_NUMBER || daysBetw > 1) {
                dayNumber = 1;
            } else {
                dayNumber++;
            }

            // добавляем транзакцию
            int bonusSum = Constants.DAILY_BONUS_SUM[dayNumber];
            String description = "День " + dayNumber + "-й";
            long oldBalance = Long.parseLong(trRep.getUserBalance(user.getId()));
            Transaction t = new Transaction(description, new Date(), bonusSum, TransferType.PROFIT, user.getId(),
                    oldBalance + bonusSum, ArticleCashFlow.DAILY_BONUS);
            trRep.addTransaction(t);

            // ежедневное начисление бесплатных лотерейных билетов
            int dailyTicketsCount = 0; // сколько начислить билетов
            int userDomi = user.getDomi();

            if (userDomi >= Constants.DOMI_LOTTERY_LIMIT)
                userDomi = Constants.DOMI_LOTTERY_LIMIT; // ограничение доминантности для начисления билетов

            dailyTicketsCount = userDomi / Constants.DAILY_TICKETS_FROM_DOMI_K;
            user.setLotteryTickets(user.getLotteryTickets() + dailyTicketsCount);

            // обновляем данные юзера
            user.setLastBonus(new Date());
            user.setDayNumber(dayNumber);
            userRep.updateUser(user);
        }
    }

    /**
     * начисление кредита/депозита начисляется каждый месяц ставки описаны в интерфейсе Consts
     * 
     * @param currUserId
     */
    private void giveCreditDeposit(long currUserId) {
        // получение последней даты начисления кредита или депозита
        Date lastTransactionDate = getLastCreditOrDepositDate(currUserId);

        int daysBetween = DateUtils.daysBetween(lastTransactionDate, new Date());
        if (daysBetween >= 30) {
            long userBalance = Long.parseLong(trRep.getUserBalance(currUserId));
            double rate = (userBalance > 0 ? Constants.DEPOSIT_RATE : Constants.CREDIT_RATE);
            TransferType transferType = (userBalance > 0 ? TransferType.PROFIT : TransferType.SPEND);
            ArticleCashFlow acf = (userBalance > 0 ? ArticleCashFlow.DEPOSIT : ArticleCashFlow.CREDIT);

            int iCount = daysBetween / 30;
            for (int i = 0; i < iCount; i++) {
                // date for description
                Calendar c1 = Calendar.getInstance();
                // отнимаем необходимое количество дней, например с последнего начисления прошло 64 дня
                // начисляем за 2 месяца
                // итерация 1: тек дата - ((2 - 0) * 30) + 4 = 64. дата с 13.04.15 по 12.05.15
                // итерация 2: тек дата - ((2 - 1) * 30) + 4 = 34. дата с 12.05.15 по 11.06.15
                c1.add(Calendar.DATE, -((iCount - i) * 30 + daysBetween % 30));

                Calendar c2 = Calendar.getInstance();
                c2 = c1;
                Calendar cTemp = (Calendar) c1.clone(); // для описания
                c2.add(Calendar.DATE, 30);
                // //

                String description = (userBalance > 0 ? "Начислено депозит за: %td.%tm.%ty - %td.%tm.%ty"
                        : "Начислено кредит за: %td.%tm.%ty - %td.%tm.%ty");
                cTemp.add(Calendar.DATE, 1);
                Date dateFrom = cTemp.getTime();
                Date dateTo = c2.getTime();
                description = String.format(description, dateFrom, dateFrom, dateFrom, dateTo, dateTo, dateTo);

                long sum = Math.round(userBalance * rate);
                userBalance += sum; // добавляет или отнимает, если сумма отрицательная
                Transaction tr = new Transaction(description, dateTo, Math.abs(sum), transferType, currUserId, userBalance, acf);
                trRep.addTransaction(tr);
            }
        }
    }

}
