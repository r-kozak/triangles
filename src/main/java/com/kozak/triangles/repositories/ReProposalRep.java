package com.kozak.triangles.repositories;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.enums.CityAreasT;
import com.kozak.triangles.enums.buildings.CommBuildingsT;
import com.kozak.triangles.interfaces.Consts;
import com.kozak.triangles.search.RealEstateProposalsSearch;
import com.kozak.triangles.utils.DateUtils;

/**
 * Репозиторий данных о предложениях на рынке недвижимости
 * 
 * @author Roman: 21 июня 2015 г. 12:19:45
 */
@Repository
@Transactional
public class ReProposalRep {
    @PersistenceContext
    public EntityManager em;

    /**
     * 
     * @return список с валидными предложениями на рынке недвижимости
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    public List<Object> getREProposalsList(int page, RealEstateProposalsSearch reps) throws ParseException {
	String hql0 = "from re_proposal as rep where rep.valid = true";
	String hql1 = "";
	String hql2 = " ORDER BY rep.commBuildingType";

	Map<String, Object> params = new HashMap<String, Object>();

	// date filter
	hql1 += " and rep.appearDate between :appearDateFrom and :appearDateTo";
	Date appearDateFrom = DateUtils.getStartDateForQuery(reps.getAppearDateFrom());
	Date appearDateTo = DateUtils.getEndDateForQuery(reps.getAppearDateTo());
	params.put("appearDateFrom", appearDateFrom);
	params.put("appearDateTo", appearDateTo);

	hql1 += " and rep.lossDate between :lossDateFrom and :lossDateTo";
	Date lossDateFrom = DateUtils.getStartDateForQuery(reps.getLossDateFrom());
	Date lossDateTo = DateUtils.getEndDateForQuery(reps.getLossDateTo());
	params.put("lossDateFrom", lossDateFrom);
	params.put("lossDateTo", lossDateTo);

	// TODO area filter
	List<CityAreasT> areas = reps.getAreas(); // типы из формы
	if (areas != null && !areas.isEmpty()) {
	    hql1 += " and rep.cityArea IN (:areas)";
	    params.put("areas", areas);
	}

	// types filter
	List<CommBuildingsT> types = reps.getTypes(); // типы из формы
	if (types != null && !types.isEmpty()) {
	    hql1 += " and rep.commBuildingType IN (:types)";
	    params.put("types", types);
	}

	// price filter
	long priceFrom = reps.getPriceFrom();
	long priceTo = reps.getPriceTo();
	if (priceFrom > 0 && priceTo > 0) {
	    hql1 += " and rep.purchasePrice between :priceFrom AND :priceTo";
	    params.put("priceFrom", priceFrom);
	    params.put("priceTo", priceTo);
	}

	Query query = em.createQuery(hql0 + hql1 + hql2);
	// установка параметров
	for (Map.Entry<String, Object> entry : params.entrySet()) {
	    query.setParameter(entry.getKey(), entry.getValue());
	}

	List<Object> result = new ArrayList<Object>(2); // результат
	int totalCount = query.getResultList().size();// общее количество транзакций для пагинации

	int firstResult = (page - 1) * Consts.ROWS_ON_PAGE;
	query.setFirstResult(firstResult);
	query.setMaxResults(Consts.ROWS_ON_PAGE);

	result.add(totalCount);
	result.add(query.getResultList());

	return result;
    }

    /**
     * устаревшими считаются все валидные предложения, lossDate (дата ухода) которых меньше текущей
     * 
     * @return список с устаревшими предложениями на рынке недвижимости
     */
    @SuppressWarnings("unchecked")
    public List<RealEstateProposal> getOutdatedProposals() {
	String hql = "from re_proposal as rep where rep.valid = true and rep.lossDate < :now ";
	Query query = em.createQuery(hql).setParameter("now", new Date());

	return query.getResultList();
    }

    /**
     * Добавляет объект предложения недвижимости
     * 
     * @param prop
     *            RealEstateProposal, которое нужно добавить
     */
    public void addREproposal(RealEstateProposal prop) {
	em.persist(prop);
    }

    /**
     * Обновляет объект предложения недвижимости
     * 
     * @param prop
     *            - RealEstateProposal, которое нужно обновить
     */
    public void updateREproposal(RealEstateProposal prop) {
	em.merge(prop);
    }

    /**
     * 
     * @param id
     *            id предложения
     * @return предложение с указанным id
     */
    public RealEstateProposal getREProposalById(int id) {
	return em.find(RealEstateProposal.class, id);
    }

    /**
     * получает количество всех валидных предложений на рынке имущества или только количество новых предложений
     * 
     * @param countOfNew
     *            - признак, получать все или только новые
     * @return
     */
    public Long allPrCount(boolean countOfNew) {
	String hql = "select count(id) FROM re_proposal as rep where rep.valid = true";
	Query query = em.createQuery(hql);

	if (countOfNew) {
	    Calendar yestC = Calendar.getInstance();
	    yestC.add(Calendar.DATE, -1);
	    Date yest = yestC.getTime();

	    hql += " and rep.appearDate > :yest";

	    query = em.createQuery(hql);
	    query.setParameter("yest", yest, TemporalType.TIMESTAMP);
	}
	return Long.valueOf(query.getSingleResult().toString());
    }

    /**
     * значения диапазонов: мин и макс значений цены покупки имущества
     * 
     * @param userId
     * @return
     */
    public List<Object> getRangeValues() {
	List<Object> result = new ArrayList<Object>(2); // результат

	String suff = "FROM re_proposal as rep where valid = true";

	String minPrHql = "Select min(purchasePrice)" + suff;
	String maxPrHql = "Select max(purchasePrice)" + suff;

	Long min = (Long) em.createQuery(minPrHql).getSingleResult();
	Long max = (Long) em.createQuery(maxPrHql).getSingleResult();
	result.add(min == null ? 0 : min);
	result.add(max == null ? 0 : max);

	return result;
    }
}
