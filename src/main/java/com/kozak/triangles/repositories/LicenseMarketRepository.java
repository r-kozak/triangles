package com.kozak.triangles.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.LicenseMarket;

@Repository
@Transactional
public class LicenseMarketRepository {

	@PersistenceContext
	public EntityManager em;

	public void addLicenseMarket(LicenseMarket licenseMarket) {
		em.persist(licenseMarket);
	}

	/**
	 * Позволяет получить магазин лицензий по Id его владельца.
	 */
	public LicenseMarket getLicenseMarketByUserId(int userId) {
		String hql = "from LicenseMarket where userId = :userId";
		Query query = em.createQuery(hql).setParameter("userId", userId);
		return (LicenseMarket) query.getSingleResult(); // магазин лицензий у пользователя может быть только один
	}
}
