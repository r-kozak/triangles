package com.kozak.triangles.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;

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

    private Random random = null; // класс Рандома

    // массив с наименованиями имущества на основании вероятность выпадения (описание выше)
    // 0-й и 1-й элемент никогда не должен заполняться т.к. не могут выпасти 2 кости с такой комбинацией
    public static final CommBuildingsT[] PROPERTY_PROBABILITY =
    { null, null, null,
            CommBuildingsT.CANDY_SHOP,
            CommBuildingsT.BOOK_SHOP,
            CommBuildingsT.STATIONER_SHOP,
            CommBuildingsT.VILLAGE_SHOP,
            CommBuildingsT.STALL,
            null, null,
            CommBuildingsT.LITTLE_SUPERMARKET,
            CommBuildingsT.MIDDLE_SUPERMARKET,
            CommBuildingsT.BIG_SUPERMARKET
    };

    /**
     * количество предложений расчитывается от количества активных пользователей
     * на каждого пользователя может генериться от 1 до 4 имуществ
     * 
     * @param usersCount
     */
    public ArrayList<RealEstateProposal> generateProposalsREMarket(int usersCount, Map<String, CommBuildData> data) {
        ArrayList<RealEstateProposal> result = new ArrayList<RealEstateProposal>();

        // коэф. колво имуществ на одного пользователя
        int k = (int) getRandom().generateRandNum(1, 4);

        int propCount = usersCount * k;
        for (int i = 0; i < propCount; i++) {
            CommBuildData bd = null;

            // бросок кости
            int rd = getRandom().diceRoll();
            String buildTypeName = (PROPERTY_PROBABILITY[rd] != null) ? PROPERTY_PROBABILITY[rd].name() : null;
            bd = data.get(buildTypeName); // build data

            if (bd != null) {
                RealEstateProposal propos = generateProposal(bd);
                result.add(propos);
            }
        }
        return result;
    }

    /**
     * генерирует предложение имущества на рынке
     * 
     * @param bd
     *            - данные о конкретном виде имущества(о киоске, о ларьке)
     * @return
     */
    private RealEstateProposal generateProposal(CommBuildData bd) {
        // generate lossDate
        Calendar lossDate = Calendar.getInstance();
        lossDate.add(Calendar.DATE, (int) getRandom().generateRandNum(bd.getRemTermMin(), bd.getRemTermMax()));

        long price = getRandom().generateRandNum(bd.getPurchasePriceMin(), bd.getPurchasePriceMax());

        // генерируем район города и увеличиваем цену в зависимости от района
        int rd = getRandom().diceRoll();// бросок кости
        CityAreasT area = null;

        switch (rd) {
        case 2:
        case 12:
            area = CityAreasT.CENTER;
            break;
        case 3:
        case 4:
        case 11:
            area = CityAreasT.CHINATOWN;
            break;
        case 5:
        case 9:
        case 10:
            area = CityAreasT.OUTSKIRTS;
            break;
        default:
            area = CityAreasT.GHETTO;
            break;
        }
        price += price * Util.getAreaPercent(area) / 100;

        RealEstateProposal propos = new RealEstateProposal(bd.getCommBuildType(), lossDate.getTime(),
                price, area);
        return propos;
    }

    /**
     * генерирует следующую дату для генерации предложений на рынке недвижимости
     * 
     * @return возвращает следующую дату в виде строки для последующей вставки в Vmap модель и обновления модели
     */
    public String generateNEXT_RE_PROPOSE() {
        int countToAdd = (int) getRandom().generateRandNum(Consts.FREQ_RE_PROP_MIN, Consts.FREQ_RE_PROP_MAX);

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, countToAdd);

        return DateUtils.dateToString(now.getTime());
    }

    /**
     * @return экземпляр класса Рандом
     */
    private Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }
}
