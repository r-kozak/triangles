package com.kozak.triangles.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * репозиторий имущества пользователя
 * 
 * @author Roman: 26 июня 2015 г. 21:30:21
 */
@Repository
@Transactional
public class PropertyRep {
    @PersistenceContext
    public EntityManager em;

    /**
     * Получает сумму остаточной стоимости всего имущества конкретного пользователя
     * 
     * @param userId
     *            id пользователя, данные которого нужно получить
     */
    public long getSellingSumAllPropByUser(int userId) {
        String hql = "select sum(sellingPrice) from Property as pr where pr.userId = :userId";
        Query query = em.createQuery(hql)
                .setParameter("userId", userId);

        Long result = (Long) query.getSingleResult();
        if (result == null) {
            return 0;
        }
        return result;
    }
}
