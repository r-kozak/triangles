package com.kozak.triangles.repositories;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.WinInfo;
import com.kozak.triangles.enums.WinArticle;

@Repository
@Transactional
public class WinRepository {

    @PersistenceContext
    public EntityManager em;

    public WinInfo addUpdateWinInfo(WinInfo winInfo) {
        return em.merge(winInfo);
    }

    /**
     * @param userId
     * @param article
     * @return запись с остатками по определенной статье выигрыша у конкретного пользователя
     */
    public WinInfo getWinInfoByIdAndArticle(long userId, WinArticle article) {
        String hql = "FROM WinInfo WHERE userId = ?0 AND article = ?1";
        Query query = em.createQuery(hql).setParameter(0, userId).setParameter(1, article);

        WinInfo result = null;
        try {
            result = (WinInfo) query.getSingleResult();
        } catch (NoResultException e) {
            // если сущности еще нет (для этого пользователя, по этой статье) - создать её
            result = addUpdateWinInfo(new WinInfo(userId, article, 0));
        }
        return result;
    }

}
