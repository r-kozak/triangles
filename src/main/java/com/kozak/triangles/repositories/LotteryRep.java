package com.kozak.triangles.repositories;

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

import com.kozak.triangles.entities.LotteryInfo;
import com.kozak.triangles.entities.Predictions;
import com.kozak.triangles.entities.WinningsData;
import com.kozak.triangles.enums.LotteryArticles;
import com.kozak.triangles.search.LotterySearch;
import com.kozak.triangles.utils.Constants;
import com.kozak.triangles.utils.DateUtils;

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

    public void updateLotoInfo(LotteryInfo lInfo) {
        em.merge(lInfo);
    }

    /**
     * @return данные о всех вариантах выигрыша в лотерею
     */
    @SuppressWarnings("unchecked")
    public List<WinningsData> getWinnignsData() {
        return em.createQuery("from WinningsData").getResultList();
    }

    /**
     * Проверяет, есть ли у пользователя выигранные, не просмотренные предсказания
     */
    public boolean isUserHasPrediction(int userId) {
        long countOfPredictions = getPljushkiCountByArticle(userId, LotteryArticles.PREDICTION);
        return countOfPredictions > 0;
    }

    /**
     * @return все ID c таблицы предсказаний
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getAllPredictionIDs() {
        return em.createQuery("SELECT id FROM Predictions ORDER BY id ASC").getResultList();
    }

    /**
     * Получает предсказание по id
     */
    public Predictions getPredictionById(int id) {
        return em.find(Predictions.class, id);
    }

    /**
     * Получает информацию о всех выигрышах пользователя.
     * 
     * @throws ParseException
     */
    public List<Object> getLotteryStory(int userId, LotterySearch ls) throws ParseException {
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
        List<LotteryArticles> articles = ls.getArticles(); // статьи из формы
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

    /**
     * Получает количество неиспользованных плюшек в разрезе статьи (Повышение уровня, Лицензии на строительство)
     * 
     * @param userId
     * @param article
     * @return
     */
    public long getPljushkiCountByArticle(int userId, LotteryArticles article) {
        String hql = "SELECT sum(remainingAmount) from LotteryInfo WHERE userId = ?0 AND article = ?1 AND remainingAmount > 0";
        Query query = em.createQuery(hql);
        query.setParameter(0, userId);
        query.setParameter(1, article);

        try {
            return (long) query.getSingleResult();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Получает предсказание пользователя, если оно у него есть
     * 
     * @param userId
     * @return
     */
    public LotteryInfo getUserPrediction(int userId) throws NoResultException {
        LotteryInfo result = getLotteryInfoByArticle(userId, LotteryArticles.PREDICTION);
        if (result == null) {
            throw new NoResultException();
        }
        return result;
    }

    /**
     * Получает последнюю запись с выигранными лицензиями учитывая уровень лицензии.
     * 
     * @param levelArticle
     *            - статья лицензии
     */
    public LotteryInfo getUserLicenses(int userId, LotteryArticles levelArticle) {
        return getLotteryInfoByArticle(userId, levelArticle);
    }

    /**
     * Получает запись с выигранными плюшками повышения уровня имущества.
     */
    public LotteryInfo getUserUpPropLevel(int userId) {
        return getLotteryInfoByArticle(userId, LotteryArticles.PROPERTY_UP);
    }

    /**
     * Получает запись с выигранными плюшками повышения уровня кассы имущества.
     */
    public LotteryInfo getUserUpCashLevel(int userId) {
        return getLotteryInfoByArticle(userId, LotteryArticles.CASH_UP);
    }

	/**
	 * @param userId
	 * @param article
	 * @return список выигранного в лотерею с определенной статьей. Например вернет все Лицензии 2-го уровня, где остаток > 0.
	 */
	@SuppressWarnings("unchecked")
	public List<LotteryInfo> getLotteryInfoListByArticle(int userId, LotteryArticles article) {
		Query query = createQueryForLotteryInfoByArticle(userId, article);
		return query.getResultList();
	}

	public int countOfPlaysToday(int userId) {
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

    @SuppressWarnings("unchecked")
    private LotteryInfo getLotteryInfoByArticle(int userId, LotteryArticles article) {
		Query query = createQueryForLotteryInfoByArticle(userId, article);
        query.setMaxResults(1);

        List<LotteryInfo> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

	/**
	 * Создается запрос на выборку из базы данных списка остатков, выигранных в лото, где оставшееся количество > 0 и статья
	 * выигрыша в лото соответствует переданному параметру.
	 * 
	 * @param userId
	 * @param article
	 *            статья выигрыша в лото
	 * @return запрос
	 */
	private Query createQueryForLotteryInfoByArticle(int userId, LotteryArticles article) {
		String hql = "from LotteryInfo WHERE userId = ?0 AND article = ?1 AND remainingAmount > 0 ORDER BY id ASC";
		Query query = em.createQuery(hql);
		query.setParameter(0, userId);
		query.setParameter(1, article);
		return query;
	}
}
