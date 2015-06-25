package com.kozak.triangles.repositories;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.CommBuildData;
import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.enums.buildings.CommBuildingsT;

/**
 * Репозиторий данных о строениях
 * 
 * @author Roman: 21 июня 2015 г. 12:19:45
 */
@Repository
@Transactional
public class BuildingDataRep {
    @PersistenceContext
    public EntityManager em;

    // /////////// CommBuildingData
    public void addCommBuildingData(CommBuildData data) {
        em.persist(data);
    }

    public CommBuildData getCommBuildDataByType(CommBuildingsT type) {
        String hql = "from CommBuildData as cbd where cbd.buildType = :type";

        Query query = em.createQuery(hql)
                .setParameter("type", type);

        try {
            return (CommBuildData) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @return список с типами зданий и их данными
     */
    public List<?> getCommBuildDataList() {
        String hql = "from CommBuildData as cbd ORDER BY cbd.buildType";
        Query query = em.createQuery(hql);

        return query.getResultList();
    }

    // //////////// RealEstateProposal

    /**
     * 
     * @return список с валидными предложениями на рынке недвижимости
     */
    public List getREProposalsList() {
        String hql = "from re_proposal as rep where rep.valid = :valid ORDER BY rep.commBuildingType";
        Query query = em.createQuery(hql)
                .setParameter("valid", true);

        return query.getResultList();
    }

    /**
     * устаревшими считаются все валидные предложения, lossDate (дата ухода) которых меньше текущей
     * 
     * @return список с устаревшими предложениями на рынке недвижимости
     */
    public List getOutdatedProposals() {
        String hql = "from re_proposal as rep where rep.valid = :valid and rep.lossDate < :now ";
        Query query = em.createQuery(hql)
                .setParameter("valid", true)
                .setParameter("now", new Date());

        return query.getResultList();
    }

    /**
     * Обновляет объект предложения недвижимости
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
        String hql = "from re_proposal as rep where rep.id = :id ";
        Query query = em.createQuery(hql)
                .setParameter("id", id);

        RealEstateProposal result = null;
        try {
            result = (RealEstateProposal) query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return result;
    }

}
