package com.kozak.triangles.services;

import java.util.List;

import org.json.simple.JSONObject;

import com.kozak.triangles.entities.LicenseMarket;
import com.kozak.triangles.models.Requirement;

public interface LicenseMarketService {

	/**
	 * @return построен ли магазин у конкретного пользователя
	 */
	boolean isMarketBuilt(int userId);

	/**
	 * @param isLoadConsignments
	 *            возвращать магазин с партиями лицензий на продаже или без них
	 * @return магазин лицензий конкретного пользователя с партиями лицензий на продаже или без них
	 */
	LicenseMarket getLicenseMarket(Integer userId, boolean isLoadConsignments);

	/**
	 * @return список требований (выполненные и невыполненные) для постройки магазина лицензий конкретным пользователем
	 */
	List<Requirement> computeBuildRequirements(int userId);

	/**
	 * @return список требований (выполненные и невыполненные) чтобы магазин лицензий мог функционировать
	 */
	List<Requirement> computeFunctionRequirements(int userId);

	/**
	 * @return true, если магазин может быть построен, иначе - false
	 */
	boolean isMarkerCanBeBuilt(int userId);

	/**
	 * @return может ли магазин лицензий функционировать. Для функционирования необходимо выполнить требования
	 */
	boolean isMarketCanFunction(int userId);

	/**
	 * создает новый магазин лицензий для пользователя
	 */
	void buildNewLicenseMarket(Integer userId);

	/**
	 * Списывает проданные лицензии. При списании начисляет деньги пользователю, удаляет проданную партию из базы.
	 */
	void processSoldLicenses(Integer userId);

	/**
	 * @param licenseLevel
	 *            - уровень лицензии
	 * 
	 * @return Requirement (требование), где значение поля сarriedOut - true, если все требования удовлетворены и лицензии
	 *         определенного уровня могут быть проданы, иначе - значение поля сarriedOut = false. Значение поля description в
	 *         любом случае будет содержать описание требования.
	 * 
	 *         Требования заключаются в том, что уровень магазина лицензий должен соответствовать уровню продаваемых лицензий.
	 * 
	 *         Уровень лицензий | Минимальный уровень магазина
	 *         -------------------------------------------------
	 *         2 | 2
	 *         3 | 3
	 *         4 | 4
	 */
	Requirement isLicensesCanBeSold(int licensesLevel, int userId);

	/**
	 * Подтверждает продажу лицензий. При этом списывает количество лицензий с остатков, что было выиграно в лото и создает партию
	 * лицензий на продаже. Если лицензий на остатках не достаточно, тогда в Json строке возвращается ошибка.
	 * 
	 * @param licenseCount
	 *            - количество лицензий для продажи
	 * @param licenseLevel
	 *            - уровень лицензий
	 * @param userId
	 * @param resultJson
	 * @return
	 */
	JSONObject confirmLicenseSelling(int licenseCount, byte licenseLevel, Integer userId, JSONObject resultJson);

}
