package com.kozak.triangles.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.CommBuildData;

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

    /**
     * @return список с типами зданий и их данными
     */
    public List<?> getCommBuildDataList() {
        String hql = "from CommBuildData as cbd ORDER BY cbd.id";
        Query query = em.createQuery(hql);

        return query.getResultList();
    }

    public void removeAllData() {
        for (Object cbd : getCommBuildDataList()) {
            em.remove(cbd);
        }
    }
}
