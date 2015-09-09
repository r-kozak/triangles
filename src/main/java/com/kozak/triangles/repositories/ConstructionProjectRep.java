package com.kozak.triangles.repositories;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.ConstructionProject;

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
     * @return все проекты строительства в процессе стройки
     */
    @SuppressWarnings("unchecked")
    public List<ConstructionProject> getAllConstructProjects() {
        String hql = "from ConstructionProject";
        Query query = em.createQuery(hql);
        return query.getResultList();
    }

    /**
     * @return все проекты строительства в процессе стройки конкретного пользователя
     */
    @SuppressWarnings("unchecked")
    public List<ConstructionProject> getUserConstructProjects(int userId) {
        String hql = "from ConstructionProject where userId = :userId ORDER BY finishDate ASC";
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
    public ConstructionProject getUserConstrProjectById(int id, int userId) {
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
    public Date getNextExploitation(int userId) {
        String hql = "SELECT finishDate FROM ConstructionProject WHERE userId = ?0 ORDER BY finishDate ASC";
        Query query = em.createQuery(hql).setParameter(0, userId);
        query.setMaxResults(1);

        try {
            return (Date) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
