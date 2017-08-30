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

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.RealEstateProposal;
import com.kozak.triangles.enums.CityAreas;
import com.kozak.triangles.enums.TradeBuildingsTypes;
import com.kozak.triangles.search.RealEstateProposalsSearch;
import com.kozak.triangles.utils.DateUtils;

/**
 * Репозиторий данных о предложениях на рынке недвижимости
 * 
 * @author Roman: 21 июня 2015 г. 12:19:45
 */
@Repository
@Transactional
public class RealEstateProposalRep {
    @PersistenceContext
    public EntityManager em;

    /**
     * 
     * @return список с валидными предложениями на рынке недвижимости
     * @throws ParseException
     */
    public List<Object> getREProposalsList(int page, RealEstateProposalsSearch reps, long userId) throws ParseException {
        String hql0 = "from re_proposal as rep where rep.valid = true";
        String hql1 = "";
        String hql2 = " ORDER BY rep.tradeBuildingType";

        Map<String, Object> params = new HashMap<String, Object>();

        // получить ID имущества пользователя, которые он выставил на продажу, чтобы не учитывать их
        List<Integer> propsOnSale = this.getPropertyIdsOnSale(userId);
        if (!propsOnSale.isEmpty()) {
            hql0 += " and rep.usedId not in (:propsOnSale)";
            params.put("propsOnSale", propsOnSale);
        }

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

        // area filter
        List<CityAreas> areas = reps.getAreas(); // типы из формы
        if (areas != null && !areas.isEmpty()) {
            hql1 += " and rep.cityArea IN (:areas)";
            params.put("areas", areas);
        }

        // types filter
        List<TradeBuildingsTypes> types = reps.getTypes(); // типы из формы
        if (types != null && !types.isEmpty()) {
            hql1 += " and rep.tradeBuildingType IN (:types)";
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

        // пагинация не нужна
        // int firstResult = (page - 1) * Consts.ROWS_ON_PAGE;
        // query.setFirstResult(firstResult);
        // query.setMaxResults(Consts.ROWS_ON_PAGE);

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
    public void addRealEstateProposal(RealEstateProposal prop) {
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
    public Long allPrCount(boolean countOfNew, long userId) {
        // параметры, которые потом установятся запросу
        Map<String, Object> params = new HashMap<String, Object>();

        String hql = "select count(id) FROM re_proposal as rep where rep.valid = true";

        // получить ID имущества пользователя, которые он выставил на продажу, чтобы не учитывать их
        List<Integer> propsOnSale = this.getPropertyIdsOnSale(userId);
        if (!propsOnSale.isEmpty()) {
            hql += " and rep.usedId not in (:propsOnSale)";
            params.put("propsOnSale", propsOnSale);
        }

        if (countOfNew) {
            Calendar yestC = Calendar.getInstance();
            yestC.add(Calendar.DATE, -1);
            Date yest = yestC.getTime();

            hql += " and rep.appearDate > :yest";
            params.put("yest", yest);
        }

        Query query = em.createQuery(hql);
        // установка параметров
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return Long.valueOf(query.getSingleResult().toString());
    }

    /**
     * значения диапазонов: мин и макс значений цены покупки имущества
     * 
     * @param userId
     * @return
     */
    public List<Object> getRangeValues(long userId) {
        Map<String, Object> params = new HashMap<String, Object>();

        String suff = "FROM re_proposal as rep where valid = true";

        // получить ID имущества пользователя, которые он выставил на продажу, чтобы не учитывать их
        List<Integer> propsOnSale = this.getPropertyIdsOnSale(userId);
        if (!propsOnSale.isEmpty()) {
            suff += " and rep.usedId not in (:propsOnSale)";
            params.put("propsOnSale", propsOnSale);
        }

        ArrayList<Object> result = new ArrayList<Object>(2);

        // строки запроса
        String minPrHql = "Select min(purchasePrice)" + suff;
        String maxPrHql = "Select max(purchasePrice)" + suff;

        // запросы
        Query queryMin = em.createQuery(minPrHql);
        Query queryMax = em.createQuery(maxPrHql);

        // установка параметров
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            queryMin.setParameter(entry.getKey(), entry.getValue());
            queryMax.setParameter(entry.getKey(), entry.getValue());
        }

        Long min = (Long) queryMin.getSingleResult();
        Long max = (Long) queryMax.getSingleResult();

        result.add(min == null ? 0 : min);
        result.add(max == null ? 0 : max);

        return result;
    }

    public void removeReProposalByUsedId(long usedId) {
        RealEstateProposal toRemove = this.getProposalByUsedId(usedId);
        this.em.remove(toRemove);
    }

    public RealEstateProposal getProposalByUsedId(long usedId) {
        String hql = "from re_proposal as rep where usedId = :usedId";
        Query query = this.em.createQuery(hql).setParameter("usedId", usedId);
        return (RealEstateProposal) query.getSingleResult();
    }

    /**
     * получить ID имущества пользователя, которые он выставил на продажу, чтобы не учитывать их
     * при переходе на рынок
     * 
     * @param userId
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getPropertyIdsOnSale(long userId) {
        String hql = "Select id from Property as pr where pr.onSale = true and pr.userId = :userId";
        Query query = this.em.createQuery(hql).setParameter("userId", userId);

        return query.getResultList();
    }

    public void removeReProposalById(long id) {
        em.remove(em.find(RealEstateProposal.class, id));
    }
}
