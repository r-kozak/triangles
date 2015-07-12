package com.kozak.triangles.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.AbstrTest;
import com.kozak.triangles.entities.Test1;

@Repository
@Transactional
public class TestRep {
    @PersistenceContext
    public EntityManager em;

    public void addTest(AbstrTest test) {
	em.persist(test);
    }

    public AbstrTest updateTest(AbstrTest test) {
	return em.merge(test);
    }

    public List<Test1> selectT1() {
	String hql = "from Test1 as t";
	Query query = em.createQuery(hql);
	List<Test1> list = query.getResultList();
	return list;
    }
}
