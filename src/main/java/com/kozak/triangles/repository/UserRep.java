package com.kozak.triangles.repository;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entity.User;

@Repository
@Transactional
public class UserRep {
    @PersistenceContext
    public EntityManager em;

    public void addUser(User user) {
        user.setLogin(user.getLogin().trim());
        em.persist(user);
    }

    public void updateUser(User user) {
        em.merge(user);
    }

    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() {
        String hql = "FROM User";
        Query query = em.createQuery(hql);
        return query.getResultList();
    }

    public User getCurrentUserByLogin(String userLogin) {
        String hql = "FROM User WHERE lower(login) like :userLogin";
        Query query = em.createQuery(hql).setParameter("userLogin", userLogin.toLowerCase());
        return (User) query.getSingleResult();
    }

    public int countActiveUsers() {
        String hql = "SELECT count(id) FROM User WHERE lastEnter >= :twoWeeksAgo";

        Calendar twa = Calendar.getInstance();
        twa.add(Calendar.DATE, -14);
        Query query = em.createQuery(hql).setParameter("twoWeeksAgo", twa, TemporalType.DATE);

        return Integer.valueOf(query.getSingleResult().toString());
    }

    @SuppressWarnings("unchecked")
    public List<Long> getActiveUsersIds() {
        String hql = "SELECT id FROM User WHERE lastEnter >= :twoWeeksAgo";

        Calendar twa = Calendar.getInstance();
        twa.add(Calendar.DATE, -14);
        Query query = em.createQuery(hql).setParameter("twoWeeksAgo", twa, TemporalType.DATE);

        return query.getResultList();
    }

    /**
     * Получить юзера по зашифрованному логину и паролю (нужно для входа по куки)
     * 
     * @param ul
     *            - зашифрованный логин юзера
     * @param up
     *            - пароль юзера
     * @return юзер
     */
    public User getUserByEncrLoginAndPassword(String ul, String up) {
        String hql = "FROM User where encrLogin like :ul and password like :up";
        Query query = em.createQuery(hql).setParameter("ul", ul).setParameter("up", up);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * получает юзера по ID
     */
    public User find(long userId) {
        return em.find(User.class, userId);
    }

    /**
     * получает доминантность юзера
     */
    public int getUserDomi(long userId) {
        return find(userId).getDomi();
    }

    /**
     * Получает пользователя по ID и инициализирует лицензии
     * 
     * @param userId
     * @return
     */
    public User getUserWithLicense(long userId) {
        User user = em.find(User.class, userId);
        Hibernate.initialize(user.getUserLicense());
        return user;
    }

    /**
     * Получить рейтинг пользователей
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getUserRating() {
        String hql = "SELECT login, domi from User ORDER BY Domi DESC";
        return em.createQuery(hql).getResultList();
    }
}
