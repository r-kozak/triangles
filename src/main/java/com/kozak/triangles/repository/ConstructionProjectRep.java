package com.kozak.triangles.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entity.ConstructionProject;
import com.kozak.triangles.enums.CityArea;
import com.kozak.triangles.util.DateUtils;

@Repository
@Transactional
public class ConstructionProjectRep {
    @PersistenceContext
    public EntityManager em;

    public void addConstructionProject(ConstructionProject conProject) {
        em.persist(conProject);
    }

    public void updateConstructionProject(ConstructionProject conProject) {
        em.merge(conProject);
    }

    /**
     * @return все проекты строительства в процессе стройки конкретного пользователя
     */
    @SuppressWarnings("unchecked")
    public List<ConstructionProject> getUserConstructProjects(long userId) {
        String hql = "from ConstructionProject where userId = :userId ORDER BY finishDate ASC";
        Query query = em.createQuery(hql).setParameter("userId", userId);
        return query.getResultList();
    }

    /**
     * @return все ЗАВЕРШЕННЫЕ проекты строительства конкретного пользователя
     */
    @SuppressWarnings("unchecked")
    public List<ConstructionProject> getCompletedUserConstructProjects(long userId) {
        String hql = "from ConstructionProject where userId = :userId and completePerc >= 100";
        Query query = em.createQuery(hql).setParameter("userId", userId);
        return query.getResultList();
    }

    /**
     * получить конкретный строительный проект пользователя по ID
     * 
     * @param id
     *            - ID проекта
     * @param userId
     *            - ID пользователя
     */
    public ConstructionProject getUserConstrProjectById(long id, long userId) {
        String hql = "from ConstructionProject WHERE id = ?0 AND userId = ?1";
        Query query = em.createQuery(hql);
        query.setParameter(0, id);
        query.setParameter(1, userId);

        try {
            return (ConstructionProject) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * удаляет проект с базы данных
     * 
     * @param constrProject
     *            - проект к удалению
     */
    public void removeConstrProject(ConstructionProject constrProject) {
        em.remove(em.find(ConstructionProject.class, constrProject.getId()));
    }

    /**
     * получает ближайшую дату принятия строения в эксплуатацию
     * 
     * @param userId
     */
    public Date getNextExploitation(long userId) {
        String hql = "SELECT finishDate FROM ConstructionProject WHERE userId = ?0 ORDER BY finishDate ASC";
        Query query = em.createQuery(hql).setParameter(0, userId);
        query.setMaxResults(1);

        try {
            return (Date) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param userId
     * @return общее количество строительных проектов пользователя
     */
    public long getCountOfUserConstrProject(long userId) {
        String hql = "SELECT count(id) FROM ConstructionProject WHERE userId = ?0";
        Query query = em.createQuery(hql).setParameter(0, userId);
        return (long) query.getSingleResult();
    }

    /**
     * @param userId
     * @param cityArea
     *            район города
     * @return общее количество строительных проектов пользователя в конкретном районе
     */
    public long getCountOfUserConstrProject(long userId, CityArea cityArea) {
        String hql = "SELECT count(id) FROM ConstructionProject WHERE userId = ?0 AND cityArea = ?1";
        Query query = em.createQuery(hql).setParameter(0, userId).setParameter(1, cityArea);
        return (long) query.getSingleResult();
    }

    /**
     * @param userId
     * @param cityArea
     *            район города
     * @return строительные проекты пользователя в конкретном районе
     */
    @SuppressWarnings("unchecked")
    public List<ConstructionProject> getCityAreaUserConstrProject(long userId, CityArea cityArea) {
        String hql = "FROM ConstructionProject WHERE userId = ?0 AND cityArea = ?1";
        Query query = em.createQuery(hql).setParameter(0, userId).setParameter(1, cityArea);
        return query.getResultList();
    }

    /**
     * @param userId
     * @return количество завершенных строительных проектов пользователя
     */
    public long getCountOfUserCompletedConstrProject(long userId) {
        String hql = "SELECT count(id) FROM ConstructionProject WHERE userId = ?0 AND completePerc >= 100";
        Query query = em.createQuery(hql).setParameter(0, userId);
        return (long) query.getSingleResult();
    }

    /**
     * @param userId
     * @return количество начатых строительных проектов пользователя сегодня
     */
    public long getCountOfStartedProjectsToday(long userId) {
        String hql = "SELECT count(id) FROM ConstructionProject WHERE userId = ?0 AND startDate between ?1 and ?2";

        Query query = em.createQuery(hql);
        query.setParameter(0, userId);

        Date currDate = new Date();
        query.setParameter(1, DateUtils.getStart(currDate));
        query.setParameter(2, DateUtils.getEnd(currDate));
        return (long) query.getSingleResult();
    }
}
