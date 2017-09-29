package com.kozak.triangles.repository;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entity.LotteryInfo;
import com.kozak.triangles.enums.WinArticle;
import com.kozak.triangles.search.LotterySearch;
import com.kozak.triangles.util.Constants;
import com.kozak.triangles.util.DateUtils;

@Repository
@Transactional
public class LotteryRep {
    @PersistenceContext
    public EntityManager em;

    /**
     * Добавляет информацию о выигрыше в таблицу данных.
     * 
     * @param lInfo
     *            - сущность информации о выигрыше
     */
    public void addLotoInfo(LotteryInfo lInfo) {
        em.persist(lInfo);
    }

    /**
     * Получает информацию о всех выигрышах пользователя.
     * 
     * @throws ParseException
     */
    public List<Object> getLotteryStory(long userId, LotterySearch ls) throws ParseException {
        String hql00 = "SELECT count(id) ";
        String hql0 = "FROM LotteryInfo WHERE userId = :userId";
        String hql1 = "";
        String hql2 = " ORDER BY id DESC";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);

        // date filter
        hql1 += " and date between :dateFrom and :dateTo";
        Date dateFrom = DateUtils.getStartDateForQuery(ls.getDateFrom());
        Date dateTo = DateUtils.getEndDateForQuery(ls.getDateTo());
        params.put("dateFrom", dateFrom);
        params.put("dateTo", dateTo);

        // lottery articles filter
        List<WinArticle> articles = ls.getArticles(); // статьи из формы
        if (articles != null && !articles.isEmpty()) {
            hql1 += " and article IN (:la)";
            params.put("la", articles);
        }

        ArrayList<Object> result = new ArrayList<Object>();

        // подсчет общего количества элементов, учитывая заданные параметры
        Query query = em.createQuery(hql00 + hql0 + hql1);
        // установка параметров
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        result.add(query.getSingleResult());

        // получение сущностей
        query = em.createQuery(hql0 + hql1 + hql2);
        // установка параметров
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        int page = Integer.parseInt(ls.getPage());
        int firstResult = (page - 1) * Constants.ROWS_ON_PAGE;
        query.setFirstResult(firstResult);
        query.setMaxResults(Constants.ROWS_ON_PAGE);

        result.add(query.getResultList());

        return result;
    }

    public int countOfPlaysToday(long userId) {
        String hql = "SELECT sum(ticketCount) FROM LotteryInfo WHERE userId = ?0 AND date between ?1 and ?2";

        Query query = em.createQuery(hql);
        query.setParameter(0, userId);

        Date currDate = new Date();
        query.setParameter(1, DateUtils.getStart(currDate));
        query.setParameter(2, DateUtils.getEnd(currDate));
        try {
            Long result = (Long) query.getSingleResult();
            if (result == null) {
                return 0;
            }
            return new BigDecimal(result).intValueExact();
        } catch (NoResultException e) {
            return 0;
        }
    }

}
