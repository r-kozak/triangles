package com.kozak.triangles.repositories;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.LicensesConsignment;

@Repository
@Transactional
public class LicensesConsignmentRepository {

	@PersistenceContext
	public EntityManager em;

	public LicensesConsignment addLicenseConsignment(LicensesConsignment licensesConsignment) {
		return em.merge(licensesConsignment);
	}

	public void removeConsignment(LicensesConsignment licensesConsignment) {
		em.remove(licensesConsignment);
	}

	/**
	 * @return список партий лицензий, где дата продажи меньше текущей
	 */
	@SuppressWarnings("unchecked")
	public List<LicensesConsignment> getSoldConsignments(int userId) {
		String hql = "FROM LicensesConsignment WHERE sellDate < :now AND licenseMarket.userId = :userId";
		Query query = em.createQuery(hql).setParameter("now", new Date()).setParameter("userId", userId);
		return query.getResultList();
	}

}
