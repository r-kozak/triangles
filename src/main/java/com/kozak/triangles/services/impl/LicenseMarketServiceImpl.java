package com.kozak.triangles.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kozak.triangles.entities.LicenseMarket;
import com.kozak.triangles.entities.Property;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.enums.CityAreas;
import com.kozak.triangles.enums.TradeBuildingsTypes;
import com.kozak.triangles.models.Requirement;
import com.kozak.triangles.repositories.LicenseMarketRepository;
import com.kozak.triangles.repositories.PropertyRep;
import com.kozak.triangles.repositories.TransactionRep;
import com.kozak.triangles.services.LicenseMarketService;
import com.kozak.triangles.utils.CommonUtil;
import com.kozak.triangles.utils.Constants;

@Service
public class LicenseMarketServiceImpl implements LicenseMarketService {

	private static final int LEVEL_OF_STATIONER_SHOP = 10;
	private static final int DOMI_COUNT_TO_BUILD = 1500;
	private static final String DOMI_REQUIREMENT_DESCRIPTION = DOMI_COUNT_TO_BUILD + " доминантности.";

	@Autowired
	private LicenseMarketRepository licenseMarketRepository;

	@Autowired
	private PropertyRep propertyRep;

	@Autowired
	private TransactionRep transactionRep;

	@Override
	public boolean isMarketBuilt(int userId) {
		// если магазин есть в базе данных (!=null) - значит он построен
		return licenseMarketRepository.getLicenseMarketByUserId(userId) != null;
	}

	@Override
	public LicenseMarket getLicenseMarket(Integer userId) {
		return licenseMarketRepository.getLicenseMarketByUserId(userId);
	}

	@Override
	public List<Requirement> computeBuildRequirements(User user) {
		List<Requirement> result = new ArrayList<>();

		// требование, что у пользователя достаточное доминантности для постройки
		Requirement domiRequirement = new Requirement(user.getDomi() >= DOMI_COUNT_TO_BUILD, DOMI_REQUIREMENT_DESCRIPTION);
		result.add(domiRequirement);

		// требование, что у пользователя достаточно Магазинов канцтоваров для постройки
		// Для постройки нужно иметь 1 магазин канцтоваров, 10-го уровня, в центре
		List<Property> stationerShops = propertyRep.getPropertyListWithParams(user.getId(), TradeBuildingsTypes.STATIONER_SHOP,
				CityAreas.CENTER, LEVEL_OF_STATIONER_SHOP, null);
		String shopsDescription = String.format("Необходимо %d магазинов канцтоваров (%d-го уровня, в центре)",
				LicenseMarket.START_LEVEL, LEVEL_OF_STATIONER_SHOP);
		Requirement stationerShopRequirement = new Requirement(stationerShops.size() >= LicenseMarket.START_LEVEL,
				shopsDescription);
		result.add(stationerShopRequirement);

		// требование, что состоятельность пользователя позволяет построить магазин
		long requiredSum = (long) (LicenseMarket.BASE_PRICE * Constants.UNIVERS_K[LicenseMarket.START_LEVEL]);
		long userSolvency = CommonUtil.getSolvency(transactionRep, propertyRep, user.getId()); // состоятельность пользователя
		String moneyDescription = String.format("Цена постройки <b>%d &tridot;</b>. Необходимо иметь на счету.", requiredSum);
		Requirement moneyRequirement = new Requirement(userSolvency >= requiredSum, moneyDescription);
		result.add(moneyRequirement);

		return result;
	}

}
