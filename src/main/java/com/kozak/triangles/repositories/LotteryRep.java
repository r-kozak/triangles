package com.kozak.triangles.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.LotteryInfo;
import com.kozak.triangles.entities.WinningsData;
import com.kozak.triangles.enums.LotteryArticles;

@Repository
@Transactional
public class LotteryRep {
    @PersistenceContext
    public EntityManager em;

    /**
     * Добавляет информацию о выигрыше в таблицу данных.
     * 
     * @param lInfo
     *            - сущность информации о выигрыше
     */
    public void addLotoInfo(LotteryInfo lInfo) {
        em.merge(lInfo);
    }

    /**
     * @return данные о всех вариантах выигрыша в лотерею
     */
    @SuppressWarnings("unchecked")
    public List<WinningsData> getWinnignsData() {
        return em.createQuery("from WinningsData").getResultList();
    }

    /**
     * Проверяет, есть ли у пользователя выигранные, не просмотренные предсказания
     */
    public boolean isUserHasPrediction(int userId) {
        String hql = "SELECT count(id) from LotteryInfo WHERE userId = ?0 AND article = ?1 AND remainingAmount > 0";
        Query query = em.createQuery(hql);
        query.setParameter(0, userId);
        query.setParameter(1, LotteryArticles.PREDICTION);

        byte countOfPredictions = (byte) query.getSingleResult();
        return countOfPredictions > 0;
    }
}
