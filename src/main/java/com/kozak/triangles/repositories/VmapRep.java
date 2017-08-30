package com.kozak.triangles.repositories;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.Vmap;
import com.kozak.triangles.utils.Constants;
import com.kozak.triangles.utils.DateUtils;

@Repository
@Transactional
public class VmapRep {
    @PersistenceContext
    public EntityManager em;

    /**
     * @return дата следующей генерации предложений на рынке имущества
     */
    public Vmap getNextProposalGeneration() {
        String hql = "from vmap as vmap where vmap.name = :name";
        Query query = em.createQuery(hql).setParameter("name", Constants.NEXT_RE_PROPOSE);

        Vmap result = null;
        try {
            result = (Vmap) query.getSingleResult();
        } catch (NoResultException e) {
            result = initNextProposeGen();
        }
        return result;
    }

    /**
     * создает строку с константой след. даты генерации имущества,
     * !!! если та не существует !!!
     */
    private Vmap initNextProposeGen() {
        String newValue = DateUtils.dateToString(new Date());

        Vmap vm = new Vmap();
        vm.setName(Constants.NEXT_RE_PROPOSE);
        vm.setValue(newValue);

        em.persist(vm);

        return vm;
    }

    /**
     * обновляет любую константу с Vmap
     */
    public void updateVmapRow(Vmap vm) {
        em.merge(vm);
    }

}
