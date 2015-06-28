package com.kozak.triangles.repositories;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
    public List getREProposalsList(int page) {
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
    public List getOutdatedProposals() {
        String hql = "from re_proposal as rep where rep.valid = true and rep.lossDate < :now ";
        Query query = em.createQuery(hql)
                .setParameter("now", new Date());

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
     * нужно для пагинации
     * 
     * @return количество всех валидных предложений на рынке имущества
     */
    public Long allPrCount() {
        String hql = "select count(*) FROM re_proposal as rep where rep.valid = true";
        Query query = em.createQuery(hql);

        return Long.valueOf(query.getSingleResult().toString());
    }
}
