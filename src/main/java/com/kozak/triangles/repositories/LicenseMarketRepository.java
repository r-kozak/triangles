package com.kozak.triangles.repositories;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Hibernate;
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

    public void updateMarket(LicenseMarket market) {
        em.merge(market);
    }

    /**
     * Позволяет получить магазин лицензий по Id его владельца.
     */
    public LicenseMarket getLicenseMarketByUserId(long userId, boolean isLoadConsignments) {
        String hql = "from LicenseMarket where userId = :userId";
        Query query = em.createQuery(hql).setParameter("userId", userId);
        try {
            // магазин лицензий у пользователя может быть только один
            LicenseMarket market = (LicenseMarket) query.getSingleResult();
            if (isLoadConsignments) {
                Hibernate.initialize(market.getLicensesConsignments());
            }
            return market;
        } catch (NoResultException e) {
            return null;
        }
    }

}
