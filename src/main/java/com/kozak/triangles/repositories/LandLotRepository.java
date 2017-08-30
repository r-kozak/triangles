package com.kozak.triangles.repositories;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.LandLot;
import com.kozak.triangles.enums.CityAreas;

@Repository
@Transactional
public class LandLotRepository {

    @PersistenceContext
    public EntityManager em;

    /**
     * Добавляет или обновляет участок
     * 
     * @param landLot
     * @return добавленный или обновленный участок
     */
    public LandLot addUpdateLandLot(LandLot landLot) {
        return em.merge(landLot);
    }

    public LandLot getLandLot(long userId, CityAreas cityArea) {
        String hql = "FROM LandLot WHERE userId = ?0 AND cityArea = ?1";
        Query query = em.createQuery(hql)
                .setParameter(0, userId)
                .setParameter(1, cityArea);

        LandLot landLot = null;
        try {
            landLot = (LandLot) query.getSingleResult();
        } catch (NoResultException e) {
            // если сущности еще нет (для этого пользователя, в этом районе) - создать её
            landLot = addUpdateLandLot(new LandLot(userId, cityArea, 0));
        }
        return landLot;
    }
}
