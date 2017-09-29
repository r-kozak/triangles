package com.kozak.triangles.service;

import java.util.List;

import org.json.simple.JSONObject;

import com.kozak.triangles.entity.LicenseMarket;
import com.kozak.triangles.model.Requirement;

public interface LicenseMarketService {

    /**
     * @return построен ли магазин у конкретного пользователя
     */
    boolean isMarketBuilt(long userId);

    /**
     * @param isLoadConsignments
     *            возвращать магазин с партиями лицензий на продаже или без них
     * @return магазин лицензий конкретного пользователя с партиями лицензий на продаже или без них
     */
    LicenseMarket getLicenseMarket(long userId, boolean isLoadConsignments);

    /**
     * @return список требований (выполненные и невыполненные) для постройки магазина лицензий конкретным пользователем
     */
    List<Requirement> computeBuildRequirements(long userId);

    /**
     * @return список требований (выполненные и невыполненные) чтобы магазин лицензий мог функционировать
     */
    List<Requirement> computeFunctionRequirements(long userId);

    /**
     * @return true, если магазин может быть построен, иначе - false
     */
    boolean isMarkerCanBeBuilt(long userId);

    /**
     * @return может ли магазин лицензий функционировать. Для функционирования необходимо выполнить требования
     */
    boolean isMarketCanFunction(long userId);

    /**
     * создает новый магазин лицензий для пользователя
     */
    void buildNewLicenseMarket(long userId);

    /**
     * Списывает проданные лицензии. При списании начисляет деньги пользователю, удаляет проданную партию из базы.
     */
    void processSoldLicenses(long userId);

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
    Requirement isLicensesCanBeSold(int licensesLevel, long userId);

    /**
     * Подтверждает продажу лицензий. При этом списывает количество лицензий с остатков, что было выиграно в лото или получено в
     * качестве бонуса и создает партию лицензий на продаже. Если лицензий на остатках не достаточно, тогда в Json строке
     * возвращается ошибка.
     * 
     * @param licenseCount
     *            - количество лицензий для продажи
     * @param licenseLevel
     *            - уровень лицензий
     * @param userId
     * @param resultJson
     * @return
     */
    JSONObject confirmLicenseSelling(int licenseCount, byte licenseLevel, long userId);

    /**
     * Возвращает список требований для повышения Магазина лицензий
     */
    List<Requirement> computeRequirementsForLevelUp(long userId);

    /**
     * @return достигнут ли последний уровень
     */
    boolean isMaxLevelAchieved(long userId);

    /**
     * @return все ли требования удовлетворены для повышения уровня Магазина лицензий
     */
    boolean isPossibleToUpMarketLevel(long userId);

    /**
     * Повышает уровень Магазина лицензий, если это возможно
     * 
     * @return true - если уровень магазина возможно повысить и он был повышен.
     */
    boolean upLicenseMarketLevel(long userId);

}
