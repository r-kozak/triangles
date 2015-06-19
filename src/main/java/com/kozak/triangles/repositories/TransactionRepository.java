package com.kozak.triangles.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.interfaces.Consts;

@Repository
@Transactional
public class TransactionRepository {
    @PersistenceContext
    public EntityManager em;

    public void addTransaction(Transaction transaction) {
        em.persist(transaction);
    }

    public void updateTransaction(Transaction transaction) {
        em.merge(transaction);
    }

    /**
     * 
     * @param userId
     *            - id of current user
     * @return all transactions of current user
     */
    public List<Transaction> getAllUserTransactions(int userId) {
        String hql = "from Transac as tr where tr.userId = :userId ORDER BY transactDate";
        Query query = em.createQuery(hql).setParameter("userId", userId);
        return query.getResultList();
    }

    /**
     * 
     * @param userId
     * @return all current user transactions where Article of cash flow = some_type
     */
    public List<Transaction> getUserTransactionsByType(int userId, ArticleCashFlow acf) {
        String hql = "from Transac as tr where tr.userId = :userId and tr.articleCashFlow = :acf ORDER BY transactDate";
        Query query = em.createQuery(hql).setParameter("userId", userId).setParameter("acf", acf);
        return query.getResultList();
    }

    public String getUserBalance(int userId) {
        Query query = em.createQuery(
                "Select balance from Transac as tr where tr.userId = :userId order by transactDate DESC").setParameter(
                "userId", userId);
        query.setMaxResults(1);
        return query.getSingleResult().toString();
    }

    /**
     * получает количество всех транзакций юзера
     * нужно для пагинации
     * 
     * @param userId
     * @return
     */
    public Long allTrCount(int userId) {
        String hql = "select count(*) FROM Transac as tr where tr.userId = :userId";
        Query query = em.createQuery(hql).setParameter("userId", userId);

        return Long.valueOf(query.getSingleResult().toString());
    }

    /**
     * возвращает список транзакций пользователя с учетом страницы, на которую он перешел
     * 
     * @param page
     *            - текущая страница, на которую перешел пользователь
     * @param userId
     * @return
     */
    public List transList(int page, int userId) {
        String hql = "from Transac as tr where tr.userId = :userId order by transactDate DESC";
        Query query = em.createQuery(hql).setParameter("userId", userId);

        int firstResult = (page - 1) * Consts.TRANS_ON_PAGE;
        query.setFirstResult(firstResult);
        query.setMaxResults(Consts.TRANS_ON_PAGE);

        return query.getResultList();
    }
}
