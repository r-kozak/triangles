package com.kozak.triangles.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.CommBuildData;
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
                .setParameter("buildType", type);

        return (CommBuildData) query.getSingleResult();
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
}
