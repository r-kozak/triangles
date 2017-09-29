package com.kozak.triangles.repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entity.Transaction;
import com.kozak.triangles.enums.ArticleCashFlow;
import com.kozak.triangles.enums.TransferType;
import com.kozak.triangles.search.TransactSearch;
import com.kozak.triangles.util.Constants;
import com.kozak.triangles.util.DateUtils;

@Repository
@Transactional
public class TransactionRep {
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
     * @return all current user transactions where Article of cash flow = some_type
     */
    @SuppressWarnings("unchecked")
    public List<Transaction> getUserTransactionsByType(long userId, ArticleCashFlow acf) {
        String hql = "from Transac as tr where tr.userId = :userId and tr.articleCashFlow = :acf ORDER BY id";
        Query query = em.createQuery(hql).setParameter("userId", userId).setParameter("acf", acf);
        return query.getResultList();
    }

    public long getSumByAcf(long userId, ArticleCashFlow acf) {
        String hql = "SELECT sum(summa) from Transac as tr where tr.userId = :userId and tr.articleCashFlow = :acf";
        Query query = em.createQuery(hql).setParameter("userId", userId).setParameter("acf", acf);
        Long result = (Long) query.getSingleResult();
        if (result != null) {
            return result;
        } else {
            return 0;
        }
    }

    public long getSumByTransfType(long userId, TransferType transfT) {
        String hql = "SELECT sum(summa) from Transac as tr where tr.userId = :userId and tr.transferType = :trt";
        Query query = em.createQuery(hql).setParameter("userId", userId).setParameter("trt", transfT);
        return Long.valueOf(query.getSingleResult().toString());
    }

    public String getUserBalance(long userId) {
        Query query = em.createQuery("Select balance from Transac as tr where tr.userId = :userId order by id DESC")
                .setParameter("userId", userId);
        query.setMaxResults(1);
        return query.getSingleResult().toString();
    }

    /**
     * возвращает список транзакций пользователя с учетом страницы, на которую он перешел
     * 
     * @param page
     *            - текущая страница, на которую перешел пользователь
     * @param userId
     * @return
     * @throws ParseException
     */
    public List<Object> getTransactionsList(long userId, TransactSearch ts) throws ParseException {
        String hql00 = "SELECT count(id) ";
        String hql01 = "SELECT sum(summa) ";
        String hql0 = "from Transac as tr where tr.userId = :userId";
        String hql1 = "";
        String hql2 = " order by id DESC";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);

        // description filter
        if (!ts.getDescription().isEmpty()) {
            hql1 += " and lower(tr.description) like :description";
            params.put("description", "%" + ts.getDescription().toLowerCase() + "%");
        }

        // date filter
        hql1 += " and tr.transactDate between :dateFrom and :dateTo";
        Date dateFrom = DateUtils.getStartDateForQuery(ts.getDateFrom());
        Date dateTo = DateUtils.getEndDateForQuery(ts.getDateTo());
        params.put("dateFrom", dateFrom);
        params.put("dateTo", dateTo);

        // transfer type filter
        TransferType trType = ts.getTransfer();
        if (trType != null && trType != TransferType.NONE) {
            hql1 += " and tr.transferType = :trType";
            params.put("trType", trType);
        }

        // articles cash flow filter
        List<ArticleCashFlow> articles = ts.getArticles(); // статьи из формы
        if (articles != null && !articles.isEmpty()) {
            hql1 += " and tr.articleCashFlow IN (:acf)";
            params.put("acf", articles);
        }

        List<Object> result = new ArrayList<>(3); // результат

        // подсчет общего количества элементов, учитывая заданные параметры
        Query query = em.createQuery(hql00 + hql0 + hql1);
        // установка параметров
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        result.add(query.getSingleResult());

        // подсчет общей суммы транзакций с заданными параметрами
        query = em.createQuery(hql01 + hql0 + hql1);
        // установка параметров
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        result.add(query.getSingleResult());

        // получение транзакций, учитывая параметры и пагинацию
        query = em.createQuery(hql0 + hql1 + hql2);
        // установка параметров
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        int page = Integer.parseInt(ts.getPage());
        int firstResult = (page - 1) * Constants.ROWS_ON_PAGE;
        query.setFirstResult(firstResult);
        query.setMaxResults(Constants.ROWS_ON_PAGE);

        result.add(query.getResultList());

        return result;
    }
}
