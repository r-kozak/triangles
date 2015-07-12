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
     * @param ready
     *            - подсчет готовых к сбору дохода (касса > 0)
     * @return количество имущества пользователя
     */
    public Long allPrCount(int userId, boolean ready, boolean needRepair) {
	String hql = "SELECT count(id) FROM Property as pr WHERE pr.userId = :userId"
		+ (ready ? " and pr.cash > 0" : "") + (needRepair ? " and pr.depreciationPercent > 0" : "");

	Query query = em.createQuery(hql).setParameter("userId", userId);
	return Long.valueOf(query.getSingleResult().toString());
    }

    /**
     * 
     * @return список имущества пользователя для отображения на странице имущества
     */
    @SuppressWarnings("unchecked")
    public List<Property> getPropertyList(int page, int userId) {
	String hql = "FROM Property as pr WHERE pr.userId = :userId ORDER BY pr.cash DESC";
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
     * @return список валидного имущества пользователя, nextProfit которого <= тек. даты
     */
    @SuppressWarnings("unchecked")
    public List<Property> getPropertyListForProfit(int userId, boolean isProfit) {
	// это начисление прибыли или износа
	String field = (isProfit) ? "nextProfit" : "nextDepreciation";
	String hql = "FROM Property as pr WHERE valid = true and pr.userId = :userId and "
		+ "pr." + field + " <= :currDate";

	Query query = em.createQuery(hql).setParameter("userId", userId)
		.setParameter("currDate", new Date(), TemporalType.TIMESTAMP);

	return query.getResultList();
    }

    /**
     * получает дату самого ближайшей прибыли с имущества
     * 
     * @param userId
     * @return дату прибыли
     */
    public Date getMinNextProfit(int userId) {
	String hql = "SELECT min(nextProfit) FROM Property as pr WHERE pr.userId = :userId";
	Query query = em.createQuery(hql).setParameter("userId", userId);
	return (Date) query.getSingleResult();
    }
}
