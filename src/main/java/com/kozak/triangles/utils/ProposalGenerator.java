package com.kozak.triangles.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.RealEstateProposal;

/**
 * генератор предложений на рынке имущества (RealEstateProposal)
 * 
 * @author Roman: 21 июня 2015 г. 15:08:20
 */
public class ProposalGenerator {

    /**
     * количество предложений расчитывается от количества активных пользователей
     * на каждого пользователя может генериться от 1 до 4 имуществ
     * 
     * @param usersCount
     */
    public List<RealEstateProposal> generateProposalsREMarket(int usersCount, ArrayList<CommBuildData> data) {
        ArrayList<RealEstateProposal> result = new ArrayList<RealEstateProposal>();

        // коэф. колво имуществ на одного пользователя
        int k = (int) generateRandNum(1, 4);

        int propCount = usersCount * k;
        for (int i = 0; i < propCount; i++) {
            CommBuildData bd = null;

            // бросок кости
            int rd = diceRoll();
            if (rd == 7) {
                bd = data.get(0); // build data - get STALL
            } else if (rd == 6) {
                bd = data.get(1); // build data - get VILLAGE_SHOP
            } else if (rd == 5) {
                bd = data.get(2); // build data - get STATIONER_SHOP
            }
            if (bd != null) {
                RealEstateProposal propos = generateProposal(bd);
                result.add(propos);
            }
        }
        return result;
    }

    /**
     * генератор случайного числа
     * 
     * @param aStart
     *            - начало диапазона
     * @param aEnd
     *            - конец диапазона
     * @return случайное число из диапазона
     */
    private long generateRandNum(Number start, Number end) {
        long aStart = start.longValue();
        long aEnd = end.longValue();
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        aStart *= 100;
        aEnd = aEnd * 100 + 100;

        Random random = new Random();
        // get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * random.nextDouble());
        long randomNumber = (fraction + aStart);

        return randomNumber / 100;
    }

    /**
     * бросок костей
     */
    private int diceRoll() {
        long a = generateRandNum(1, 6);
        long b = generateRandNum(1, 6);

        return (int) (a + b);

    }

    /**
     * генерирует предложение имущества на рынке
     * 
     * @param bd
     *            - данные о конкретном виде имущества(о киоске, о ларьке)
     * @return
     */
    private RealEstateProposal generateProposal(CommBuildData bd) {
        Calendar lossDate = Calendar.getInstance();
        lossDate.add(Calendar.DATE, (int) generateRandNum(bd.getRemTermMin(), bd.getRemTermMax()));

        long price = generateRandNum(bd.getPurchasePriceMin(), bd.getPurchasePriceMax());

        RealEstateProposal propos = new RealEstateProposal(bd.getBuildType(), new Date(), lossDate.getTime(),
                price);
        return propos;
    }

}
