package com.kozak.triangles.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

}
