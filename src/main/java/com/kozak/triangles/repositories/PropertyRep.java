package com.kozak.triangles.repositories;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

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
     * добавляет новое имущество пользователя
     */
    public void addProperty(Property prop) {
        em.persist(prop);
    }

    public void updateProperty(Property prop) {
        em.merge(prop);
    }

    /**
     * Получает сумму остаточной стоимости всего имущества конкретного пользователя
     * 
     * @param userId
     *            id пользователя, данные которого нужно получить
     */
    public long getSellingSumAllPropByUser(int userId) {
        String hql = "SELECT sum(sellingPrice) FROM Property as pr WHERE pr.userId = :userId";
        Query query = em.createQuery(hql).setParameter("userId", userId);

        Long result = (Long) query.getSingleResult();
        if (result == null) {
            return 0;
        }
        return result;
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
        Query query = em.createQuery(hql).setParameter("userId", userId);

        return Long.valueOf(query.getSingleResult().toString());
    }

    /**
     * 
     * @return список имущества пользователя для отображения на странице имущества
     */
    @SuppressWarnings("unchecked")
    public List<Property> getPropertyList(int page, int userId) {
        String hql = "FROM Property as pr WHERE pr.userId = :userId ORDER BY pr.cash";
        Query query = em.createQuery(hql).setParameter("userId", userId);

        int firstResult = (page - 1) * Consts.ROWS_ON_PAGE;
        query.setFirstResult(firstResult);
        query.setMaxResults(Consts.ROWS_ON_PAGE);

        return query.getResultList();
    }

    /**
     * @param userId
     *            пользователь
     * @param id
     *            id имущества
     * @return конкретное имущество конкретного пользователя
     */
    public Property getSpecificProperty(int userId, int id) {
        String hql = "FROM Property as pr WHERE pr.userId = :userId and pr.id = :id";
        Query query = em.createQuery(hql).setParameter("userId", userId).setParameter("id", id);
        Property result = (Property) query.getSingleResult();

        return result;
    }

    /**
     * 
     * @return список имущества валидного имущества пользователя, nextProfit которого <= тек. даты
     */
    @SuppressWarnings("unchecked")
    public List<Property> getPropertyListForProfit(int userId) {
        String hql = "FROM Property as pr WHERE valid = true and pr.userId = :userId and "
                + "pr.nextProfit <= :currDate";

        Query query = em.createQuery(hql).setParameter("userId", userId)
                .setParameter("currDate", new Date(), TemporalType.TIMESTAMP);

        return query.getResultList();
    }
}
