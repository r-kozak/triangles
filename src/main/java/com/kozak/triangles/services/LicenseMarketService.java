package com.kozak.triangles.services;

import java.util.List;

import com.kozak.triangles.entities.LicenseMarket;
import com.kozak.triangles.entities.User;
import com.kozak.triangles.models.Requirement;

public interface LicenseMarketService {

	/**
	 * @return построен ли магазин у конкретного пользователя
	 */
	boolean isMarketBuilt(int userId);

	/**
	 * @return магазин лицензий конкретного пользователя
	 */
	LicenseMarket getLicenseMarket(Integer userId);

	/**
	 * @return список требований (выполненные и невыполненные) для постройки магазина лицензий конкретным пользователем
	 */
	List<Requirement> computeBuildRequirements(User user);

}
