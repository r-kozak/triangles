package com.kozak.triangles.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.Messages;

@Repository
@Transactional
public class MessageRep {
    @PersistenceContext
    public EntityManager em;

    /**
     * добавляет сообщение
     */
    public void addMsg(Messages msg) {
        em.persist(msg);
    }

    /**
     * удаляет сообщение
     */
    public void removeMsg(long id) {
        em.remove(em.find(Messages.class, id));
    }

    @SuppressWarnings("unchecked")
    public List<Messages> getAllMsgs() {
        Query query = em.createQuery("from Messages ORDER BY id DESC");
        return query.getResultList();
    }

    public Messages getMsgById(long id) {
        return em.find(Messages.class, id);
    }
}
