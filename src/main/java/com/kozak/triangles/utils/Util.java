package com.kozak.triangles.utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.interfaces.Consts;
import com.kozak.triangles.repositories.BuildingDataRep;
import com.kozak.triangles.repositories.PropertyRep;

public class Util {
    public static Model addBalanceToModel(Model model, String userBalance) {
	String balance = moneyFormat(Long.valueOf(userBalance));
	return model.addAttribute("balance", balance);
    }

    public static String moneyFormat(Number sum) {
	NumberFormat formatter = NumberFormat.getInstance(new Locale("ru"));
	String result = formatter.format(sum);
	return result;
    }

    public static int getAreaPercent(CityAreasT cityArea) {
	switch (cityArea.ordinal()) {
	case 0:
	    return Consts.GHETTO_P;
	case 1:
	    return Consts.OUTSKIRTS_P;
	case 2:
	    return Consts.CHINA_P;
	case 3:
	    return Consts.CENTER_P;
	}
	return 0;
    }

    public static int getPageNumber(HttpServletRequest request) {
	Integer page = 1;
	if (request.getParameterValues("page") != null) {
	    page = Integer.parseInt(request.getParameterValues("page")[0]);
	}

	return page;
    }

    /**
     * начисление прибыли по каждому имуществу пользователя
     * 
     * @param userId
     */
    public static void profitCalculation(int userId, BuildingDataRep bdRep, PropertyRep prRep) {
	// получить данные всех коммерческих строений
	HashMap<String, CommBuildData> mapData = SingletonData.getCommBuildData(bdRep);

	// получить всё валидное имущество, nextProfit которого < тек. даты
	ArrayList<Property> properties = (ArrayList<Property>) prRep.getPropertyListForProfit(userId, true);
	// генератор
	ProposalGenerator pg = new ProposalGenerator();

	// для каждого имущества
	for (Property p : properties) {
	    CommBuildData data = mapData.get(p.getCommBuildingType().toString());

	    long cashCap = p.getCashCapacity(); // получить вместимость кассы
	    long cash = p.getCash(); // получить тек. значение кассы

	    // получить минимальную и максимальную прибыль
	    int pMin = data.getProfitMin();
	    int pMax = data.getProfitMax();

	    // расчитать, за сколько дней нужно насчитать прибыль
	    Date d1 = p.getNextProfit();
	    Date d2 = new Date();
	    int calcC = DateUtils.daysBetween(d1, d2) + 1; // количество начислений

	    for (int i = 0; i < calcC; i++) {
		// згенерить и приплюсовать к кассе значение прибыли !!! учитывая район !!!
		long gen = pg.generateRandNum(pMin, pMax);
		gen += gen * Util.getAreaPercent(p.getCityArea()) / 100;
		cash += gen;
	    }

	    Date dateFrom = d1;// дата, которая станет след.датой прибыли
	    int countOfDay = calcC; // сколько дней прибавлять
	    // если в кассе больше, чем вместимость - сделать, как вместимость
	    if (cash > cashCap) {
		cash = cashCap;
		// касса заполнена. след. прибыль = к сейчас добавить один день
		dateFrom = new Date();
		countOfDay = 1;
	    }

	    // установить значение кассы и дату nextProfit
	    p.setCash(cash);

	    p.setNextProfit(DateUtils.getPlusDay(dateFrom, countOfDay));

	    prRep.updateProperty(p);// обновить имущество
	}
    }
}
