package com.kozak.triangles.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.User;

@Repository
@Transactional
public class UserRepository {
	@PersistenceContext
	public EntityManager em;

	public void addUser(User user) {
		em.merge(user);
	}

	public List<User> getAllUsers() {
		String hql = "from User";
		Query query = em.createQuery(hql);
		return query.getResultList();
	}

	public int getCurrentUserId(String userLogin) {
		String hql = "Select id from User as user where login = :userLogin";
		Query query = em.createQuery(hql).setParameter("userLogin", userLogin);
		return (Integer) query.getSingleResult();
	}
}
