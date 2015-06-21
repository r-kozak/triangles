package com.kozak.triangles.repositories;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.Vmap;
import com.kozak.triangles.interfaces.Consts;
import com.kozak.triangles.utils.DateUtils;

@Repository
@Transactional
public class VmapRep {
    @PersistenceContext
    public EntityManager em;

    /**
     * @return дата следующей генерации предложений на рынке имущества
     */
    public Date nextProposeGen() {
        String hql = "select from vmap as vmap where vmap.name = :name";
        Query query = em.createQuery(hql)
                .setParameter("name", Consts.NEXT_RE_PROPOSE);

        Date result = (Date) query.getSingleResult();
        if (result == null) {
            result = initNextProposeGen();
        }
        return result;
    }

    /**
     * создает константу со следю датой генерации имущества,
     * если та не существует
     */
    private Date initNextProposeGen() {
        Date date = new Date();

        Vmap vm = new Vmap();
        vm.setName(Consts.NEXT_RE_PROPOSE);
        vm.setValue(DateUtils.dateToString(date));

        em.persist(vm);

        return date;
    }
}
