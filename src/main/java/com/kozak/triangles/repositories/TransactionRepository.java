package com.kozak.triangles.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.Transaction;
import com.kozak.triangles.enums.ArticleCashFlow;

@Repository
@Transactional
public class TransactionRepository {
	@PersistenceContext
	public EntityManager em;

	public void addTransaction(Transaction transaction) {
		em.merge(transaction);
	}

	/**
	 * 
	 * @param userId
	 *            - id of current user
	 * @return all transactions of current user
	 */
	public List<Transaction> getAllUserTransactions(int userId) {
		String hql = "from Transac as tr "
				+ "where tr.userId = :userId "
				+ "ORDER BY "
				+ "transactDate";
		Query query = em.createQuery(hql)
				.setParameter("userId", userId);
		return query.getResultList();
	}

	/**
	 * 
	 * @param userId
	 * @return all current user transactions where Article of cash flow = DAILY_BONUS
	 */
	public List<Transaction> getDailyBonusUserTransactions(int userId) {
		String hql = "from Transac as tr "
				+ "where tr.userId = :userId and tr.articleCashFlow = :acf "
				+ "ORDER BY "
				+ "transactDate";
		Query query = em.createQuery(hql)
				.setParameter("userId", userId)
				.setParameter("acf", ArticleCashFlow.DAILY_BONUS);
		return query.getResultList();
	}

	public String getUserBalance(int userId) {
		Query query = em.createQuery("Select balance from Transac as tr where tr.userId = :userId "
				+ "order by transactDate DESC").setParameter("userId", userId);
		query.setMaxResults(1);
		return query.getSingleResult().toString();
	}
}
