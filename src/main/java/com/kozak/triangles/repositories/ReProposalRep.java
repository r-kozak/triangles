package com.kozak.triangles.repositories;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.interfaces.Consts;

/**
 * Репозиторий данных о предложениях на рынке недвижимости
 * 
 * @author Roman: 21 июня 2015 г. 12:19:45
 */
@Repository
@Transactional
public class ReProposalRep {
    @PersistenceContext
    public EntityManager em;

    /**
     * 
     * @return список с валидными предложениями на рынке недвижимости
     */
    @SuppressWarnings("unchecked")
    public List<RealEstateProposal> getREProposalsList(int page) {
	String hql = "from re_proposal as rep where rep.valid = true ORDER BY rep.commBuildingType";
	Query query = em.createQuery(hql);

	int firstResult = (page - 1) * Consts.ROWS_ON_PAGE;
	query.setFirstResult(firstResult);
	query.setMaxResults(Consts.ROWS_ON_PAGE);

	return query.getResultList();
    }

    /**
     * устаревшими считаются все валидные предложения, lossDate (дата ухода) которых меньше текущей
     * 
     * @return список с устаревшими предложениями на рынке недвижимости
     */
    @SuppressWarnings("unchecked")
    public List<RealEstateProposal> getOutdatedProposals() {
	String hql = "from re_proposal as rep where rep.valid = true and rep.lossDate < :now ";
	Query query = em.createQuery(hql).setParameter("now", new Date());

	return query.getResultList();
    }

    /**
     * Добавляет объект предложения недвижимости
     * 
     * @param prop
     *            RealEstateProposal, которое нужно добавить
     */
    public void addREproposal(RealEstateProposal prop) {
	em.persist(prop);
    }

    /**
     * Обновляет объект предложения недвижимости
     * 
     * @param prop
     *            - RealEstateProposal, которое нужно обновить
     */
    public void updateREproposal(RealEstateProposal prop) {
	em.merge(prop);
    }

    /**
     * 
     * @param id
     *            id предложения
     * @return предложение с указанным id
     */
    public RealEstateProposal getREProposalById(int id) {
	return em.find(RealEstateProposal.class, id);
    }

    /**
     * получает количество всех предложений на рынке имущества или только количество новых предложений
     * 
     * @param countOfNew
     *            - признак, получать все или только новые
     * @return
     */
    public Long allPrCount(boolean countOfNew) {
	String hql = "select count(id) FROM re_proposal as rep where rep.valid = true";
	Query query = em.createQuery(hql);

	if (countOfNew) {
	    Calendar yestC = Calendar.getInstance();
	    yestC.add(Calendar.DATE, -1);
	    Date yest = yestC.getTime();

	    hql += " and rep.appearDate > :yest";

	    query = em.createQuery(hql);
	    query.setParameter("yest", yest, TemporalType.TIMESTAMP);
	}
	return Long.valueOf(query.getSingleResult().toString());
    }
}
