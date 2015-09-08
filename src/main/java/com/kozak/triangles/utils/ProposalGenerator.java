package com.kozak.triangles.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.enums.CityAreasT;

/**
 * генератор предложений на рынке имущества (RealEstateProposal)
 * 
 * @author Roman: 21 июня 2015 г. 15:08:20
 */
public class ProposalGenerator {

    private Random random = null; // класс Рандома

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

            switch (rd) {
            case 7:
                bd = data.get("STALL"); // build data - get STALL
                break;
            case 6:
                bd = data.get("VILLAGE_SHOP"); // build data - get VILLAGE_SHOP
                break;
            case 5:
                bd = data.get("STATIONER_SHOP"); // build data - get STATIONER_SHOP
                break;
            }

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
