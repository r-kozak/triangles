package com.kozak.triangles.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.Property;
import com.kozak.triangles.interfaces.Consts;

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
        String hql = "SELECT sum(sellingPrice) FROM Property as pr WHERE pr.userId = :userId";
        Query query = em.createQuery(hql)
                .setParameter("userId", userId);

        Long result = (Long) query.getSingleResult();
        if (result == null) {
            return 0;
        }
        return result;
    }

    /**
     * добавляет новое имущество пользователя
     */
    public void addProperty(Property prop) {
        em.persist(prop);
    }

    /**
     * нужно для пагинации
     * 
     * @param userId
     * 
     * @return количество имущества пользователя
     */
    public Long allPrCount(int userId) {
        String hql = "SELECT count(*) FROM Property as pr WHERE pr.userId = :userId";
        Query query = em.createQuery(hql)
                .setParameter("userId", userId);

        return Long.valueOf(query.getSingleResult().toString());
    }

    /**
     * 
     * @return список имущества пользователя
     */
    public List getPropertyList(int page, int userId) {
        String hql = "FROM Property as pr WHERE pr.userId = :userId ORDER BY pr.cash";
        Query query = em.createQuery(hql)
                .setParameter("userId", userId);

        int firstResult = (page - 1) * Consts.ROWS_ON_PAGE;
        query.setFirstResult(firstResult);
        query.setMaxResults(Consts.ROWS_ON_PAGE);

        return query.getResultList();
    }
}
