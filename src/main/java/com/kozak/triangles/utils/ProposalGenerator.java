package com.kozak.triangles.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.kozak.triangles.data.CityAreasTableData;
import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.entities.TradeBuilding;
import com.kozak.triangles.enums.CityAreas;
import com.kozak.triangles.enums.TradeBuildingsTypes;

/**
 * генератор предложений на рынке имущества (RealEstateProposal)
 * 
 * вероятность выпадения числа:
 * 
 * число | комбинации
 * *** 2 | 1-1
 * *** 3 | 1-2 2-1
 * *** 4 | 1-3 3-1 2-2
 * *** 5 | 2-3 3-2 1-4 4-1
 * *** 6 | 2-4 4-2 1-5 5-1 3-3
 * *** 7 | 1-6 6-1 2-5 5-2 3-4 4-3
 * *** 8 | 2-6 6-2 4-4 3-5 5-3
 * *** 9 | 3-6 6-3 4-5 5-4
 * ** 10 | 4-6 6-4 5-5
 * ** 11 | 5-6 6-5
 * ** 12 | 6-6
 * 
 * @author Roman: 21 июня 2015 г. 15:08:20
 */
public class ProposalGenerator {

	// минимальное и максимальное количество генерируемых имуществ на одного активного пользователя (ОТ и ДО)
	private static int GENERATING_PROPERTIES_COUNT_MIN = 1;
	private static int GENERATING_PROPERTIES_COUNT_MAX = 5;

    // массив с наименованиями имущества на основании вероятность выпадения (описание выше)
    // 0-й и 1-й элемент никогда не должен заполняться т.к. не могут выпасти 2 кости с такой комбинацией
	public static final TradeBuildingsTypes[] PROPERTY_PROBABILITY = { 
			null, /* 0 */
			null, /* 1 */
			TradeBuildingsTypes.MALL, /* 2 */
			TradeBuildingsTypes.RESTAURANT, /* 3 */
			TradeBuildingsTypes.MIDDLE_SUPERMARKET, /* 4 */
			TradeBuildingsTypes.CANDY_SHOP, /* 5 */
			TradeBuildingsTypes.VILLAGE_SHOP, /* 6 */
			TradeBuildingsTypes.STALL, /* 7 */
			TradeBuildingsTypes.STATIONER_SHOP, /* 8 */
			TradeBuildingsTypes.BOOK_SHOP, /* 9 */
			TradeBuildingsTypes.LITTLE_SUPERMARKET, /* 10 */
			TradeBuildingsTypes.BIG_SUPERMARKET, /* 11 */
			TradeBuildingsTypes.CINEMA /* 12 */
		};

    /**
	 * генерирует предложения на рынке имущества
	 * количество предложений расчитывается от количества активных пользователей
	 * на каждого пользователя может генериться от 1 до 5 имуществ
	 * 
	 * @param usersCount
	 */
	public List<RealEstateProposal> generateMarketProposals(int usersCount, Map<Integer, TradeBuilding> data) {
		List<RealEstateProposal> result = new ArrayList<>();

		// коэф. кол-во имуществ на одного пользователя
		int k = (int) Random.generateRandNum(GENERATING_PROPERTIES_COUNT_MIN, GENERATING_PROPERTIES_COUNT_MAX);

        int proposalsCount = usersCount * k;
        for (int i = 0; i < proposalsCount; i++) {
			int rd = Random.diceRoll(); // бросок кости

			// при броске костей НЕ должно выпасть 0 и 1, соответственно этого: { PROPERTY_PROBABILITY[rd] == null }
			// никогда не будет, но все равно проверим
			Integer buildTypeOrdinal = (PROPERTY_PROBABILITY[rd] != null) ? PROPERTY_PROBABILITY[rd].ordinal() : null;
			if (buildTypeOrdinal != null) {
				TradeBuilding buildingData = data.get(buildTypeOrdinal);
				RealEstateProposal proposal = generateProposal(buildingData);
				result.add(proposal);
            }
        }
        return result;
    }

    /**
	 * генерирует предложение имущества на рынке
	 * 
	 * @param buildingData
	 *            - данные о конкретном виде имущества(о киоске, о магазине и т.д.)
	 * @return
	 */
    private RealEstateProposal generateProposal(TradeBuilding buildingData) {
        // generate lossDate
        Calendar lossDate = Calendar.getInstance();
		lossDate.add(Calendar.DATE, (int) Random.generateRandNum(buildingData.getMarketTermMin(), buildingData.getMarketTermMax()));

		long price = Random.generateRandNum(buildingData.getPurchasePriceMin(), buildingData.getPurchasePriceMax());

        // генерируем район города и увеличиваем цену в зависимости от района
		int rd = Random.diceRoll();// бросок кости
        CityAreas area = null;

        switch (rd) {
        case 2:
        case 12:
            area = CityAreas.CENTER;
            break;
        case 3:
        case 4:
        case 11:
            area = CityAreas.CHINATOWN;
            break;
        case 5:
        case 9:
        case 10:
            area = CityAreas.OUTSKIRTS;
            break;
        default:
            area = CityAreas.GHETTO;
            break;
        }
		price += price * CityAreasTableData.getCityAreaPercent(area) / 100;

		RealEstateProposal proposal = new RealEstateProposal(buildingData.getTradeBuildingType(), lossDate.getTime(), price, area);
        return proposal;
    }

    /**
     * генерирует следующую дату для генерации предложений на рынке недвижимости
     * 
     * @return возвращает следующую дату в виде строки для последующей вставки в Vmap модель и обновления модели
     */
    public String computeNextGeneratingDate() {
		int daysCount = (int) Random.generateRandNum(Constants.FREQ_RE_PROP_MIN, Constants.FREQ_RE_PROP_MAX);

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, daysCount);

        return DateUtils.dateToString(now.getTime());
    }

}
