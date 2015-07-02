package com.kozak.triangles.repositories;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.User;

@Repository
@Transactional
public class UserRep {
    @PersistenceContext
    public EntityManager em;

    public void addUser(User user) {
        em.persist(user);
    }

    public void updateUser(User user) {
        em.merge(user);
    }

    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() {
        String hql = "from User";
        Query query = em.createQuery(hql);
        return query.getResultList();
    }

    public User getCurrentUserByLogin(String userLogin) {
        String hql = "from User as user where login = :userLogin";
        Query query = em.createQuery(hql).setParameter("userLogin", userLogin);
        return (User) query.getSingleResult();
    }

    public int countActiveUsers() {
        String hql = "select count(*) FROM User as u where u.lastEnter >= :twoWeeksAgo";

        Calendar twa = Calendar.getInstance();
        twa.add(Calendar.DATE, -14);

        Query query = em.createQuery(hql).setParameter("twoWeeksAgo", twa, TemporalType.DATE);

        return Integer.valueOf(query.getSingleResult().toString());
    }

}
