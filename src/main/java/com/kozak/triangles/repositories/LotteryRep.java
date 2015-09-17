package com.kozak.triangles.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.LotteryInfo;
import com.kozak.triangles.entities.Predictions;
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
        em.persist(lInfo);
    }

    public void updateLotoInfo(LotteryInfo lInfo) {
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
        long countOfPredictions = getPljushkiCountByArticle(userId, LotteryArticles.PREDICTION);
        return countOfPredictions > 0;
    }

    /**
     * @return все ID c таблицы предсказаний
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getAllPredictionIDs() {
        return em.createQuery("SELECT id FROM Predictions ORDER BY id ASC").getResultList();
    }

    /**
     * Получает предсказание по id
     */
    public Predictions getPredictionById(int id) {
        return em.find(Predictions.class, id);
    }

    /**
     * Получает информацию о всех выигрышах пользователя.
     */
    @SuppressWarnings("unchecked")
    public List<LotteryInfo> getLotteryStory(int userId) {
        String hql = "FROM LotteryInfo WHERE userId = ?0 ORDER BY id DESC";
        return em.createQuery(hql).setParameter(0, userId).getResultList();
    }

    /**
     * Получает количество неиспользованных плюшек в разрезе статьи (Повышение уровня, Лицензии на строительство)
     * 
     * @param userId
     * @param article
     * @return
     */
    public long getPljushkiCountByArticle(int userId, LotteryArticles article) {
        String hql = "SELECT sum(remainingAmount) from LotteryInfo WHERE userId = ?0 AND article = ?1 AND remainingAmount > 0";
        Query query = em.createQuery(hql);
        query.setParameter(0, userId);
        query.setParameter(1, article);

        return (long) query.getSingleResult();
    }

    /**
     * Получает предсказание пользователя, если оно у него есть
     * 
     * @param userId
     * @return
     */
    public LotteryInfo getUserPrediction(int userId) throws NoResultException {
        LotteryInfo result = getLotteryInfoByArticle(userId, LotteryArticles.PREDICTION);
        if (result == null) {
            throw new NoResultException();
        }
        return result;
    }

    public LotteryInfo getUserLicenses(int userId, LotteryArticles levelArticle) {
        return getLotteryInfoByArticle(userId, levelArticle);
    }

    @SuppressWarnings("unchecked")
    private LotteryInfo getLotteryInfoByArticle(int userId, LotteryArticles article) {
        String hql = "from LotteryInfo WHERE userId = ?0 AND article = ?1 AND remainingAmount > 0 ORDER BY id ASC";
        Query query = em.createQuery(hql);
        query.setParameter(0, userId);
        query.setParameter(1, article);

        query.setMaxResults(1);

        List<LotteryInfo> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }
}
