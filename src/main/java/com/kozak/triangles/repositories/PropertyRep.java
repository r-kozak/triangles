package com.kozak.triangles.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kozak.triangles.entities.Property;
import com.kozak.triangles.enums.CityAreas;
import com.kozak.triangles.enums.TradeBuildingsTypes;
import com.kozak.triangles.search.TradePropertySearch;
import com.kozak.triangles.utils.Constants;

/**
 * репозиторий имущества пользователя
 * 
 * @author Roman: 26 июня 2015 г. 21:30:21
 */
@Repository
@Transactional
public class PropertyRep {
    @PersistenceContext
    public EntityManager em;

    /**
     * добавляет новое имущество пользователя
     */
    public void addProperty(Property prop) {
        em.persist(prop);
    }

    public void updateProperty(Property prop) {
        em.merge(prop);
    }

    public void removeProperty(Property prop) {
        em.remove(getPropertyById(prop.getId()));
    }

    /**
     * Получает сумму остаточной стоимости всего имущества конкретного пользователя
     * 
     * @param userId
     *            id пользователя, данные которого нужно получить
     */
    public long getSellingSumAllPropByUser(int userId) {
        String hql = "SELECT sum(sellingPrice) FROM Property as pr WHERE pr.userId = :userId";
        Query query = em.createQuery(hql).setParameter("userId", userId);

        Long result = (Long) query.getSingleResult();
        if (result == null) {
            return 0;
        }
        return result;
    }

    /**
     * нужно для пагинации
     * 
     * @param userId
     * @param ready
     *            - подсчет готовых к сбору дохода (касса > 0)
     * @return количество имущества пользователя
     */
    public Long allPrCount(int userId, boolean ready, boolean needRepair) {
        String hql = "SELECT count(id) FROM Property as pr WHERE pr.userId = :userId"
                + (ready ? " and pr.cash > 0" : "") + (needRepair ? " and pr.depreciationPercent > 0" : "");

        Query query = em.createQuery(hql).setParameter("userId", userId);
        return Long.valueOf(query.getSingleResult().toString());
    }

    @SuppressWarnings("unchecked")
    public List<Property> getPropertyWithNotEmptyCash(int userId) {
        String hql = "FROM Property as pr WHERE pr.userId = :userId and pr.cash > 0";
        Query query = em.createQuery(hql).setParameter("userId", userId);
        return query.getResultList();
    }

    /**
     * 
     * @param rowsOnPage
     * @return список имущества пользователя для отображения на странице имущества
     */
    public List<Object> getPropertyList(int userId, TradePropertySearch tps, int rowsOnPage) {
        String hql00 = "SELECT count(id) ";
        String hql0 = "FROM Property as pr WHERE pr.userId = :userId";
        String hql1 = "";
        String hql2 = " ORDER BY pr.cash DESC, pr.nextProfit";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);

        // name filter
        if (!tps.getName().isEmpty()) {
            hql1 += " and lower(pr.name) like :name";
            params.put("name", "%" + tps.getName().toLowerCase() + "%");
        }

        // state filter
        String state = tps.getState();
        if (!state.isEmpty()) {
            if (state.equals("on_sale")) {
                hql1 += " and onSale = true";
            } else if (state.equals("not_on_sale")) {
                hql1 += " and onSale = false";
            }
        }

        // types filter
        List<TradeBuildingsTypes> types = tps.getTypes(); // типы из формы
        if (types != null && !types.isEmpty()) {
			hql1 += " and pr.tradeBuildingType IN (:types)";
            params.put("types", types);
        }

        // area filter
        List<CityAreas> areas = tps.getAreas(); // типы из формы
        if (areas != null && !areas.isEmpty()) {
            hql1 += " and pr.cityArea IN (:areas)";
            params.put("areas", areas);
        }

        // selling price filter
        long priceFrom = tps.getSellPriceFrom();
        long priceTo = tps.getSellPriceTo();
        if (priceFrom > 0 && priceTo > 0) {
            hql1 += " and pr.sellingPrice between :priceFrom AND :priceTo";
            params.put("priceFrom", priceFrom);
            params.put("priceTo", priceTo);
        }

        // depreciation percent filter
        double percFrom = tps.getDepreciationFrom();
        double percTo = tps.getDepreciationTo();
        if (percTo > 0) {
            hql1 += " and pr.depreciationPercent between :percFrom AND :percTo";
            params.put("percFrom", percFrom);
            params.put("percTo", percTo);
        }

        List<Object> result = new ArrayList<Object>(2); // результат

        // получение общего кол-ва с заданными параметрами
        Query query = em.createQuery(hql00 + hql0 + hql1);
        // установка параметров
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        result.add(query.getSingleResult());

        // получение полного списка
        query = em.createQuery(hql0 + hql1 + hql2);
        // установка параметров
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        // пагинация
        int page = Integer.parseInt(tps.getPage());
        int firstResult = (page - 1) * rowsOnPage;
        query.setFirstResult(firstResult);
        query.setMaxResults(rowsOnPage);

        result.add(query.getResultList());

        return result;
    }

    /**
     * @param userId
     *            пользователь
     * @param id
     *            id имущества
     * @return конкретное имущество конкретного пользователя
     */
    public Property getSpecificProperty(int userId, int id) {
        String hql = "FROM Property as pr WHERE pr.userId = :userId and pr.id = :id";
        Query query = em.createQuery(hql).setParameter("userId", userId).setParameter("id", id);
        Property result = null;
        try {
            result = (Property) query.getSingleResult();
        } catch (NoResultException ignored) {
        }
        return result;
    }

    /**
     * @param isProfit
     *            признак начисления прибыли (если true, иначе - начисление износа)
     * @return список валидного имущества пользователя, nextProfit которого <= тек. даты
     */
    @SuppressWarnings("unchecked")
    public List<Property> getPropertyListForProfit(int userId, boolean isProfit) {
        // это начисление прибыли или износа
        String field = (isProfit) ? "nextProfit" : "nextDepreciation";
        String hql = "FROM Property as pr WHERE valid = true and pr.userId = :userId and " + "pr." + field
                + " <= :currDate";

        Query query = em.createQuery(hql).setParameter("userId", userId)
                .setParameter("currDate", new Date(), TemporalType.TIMESTAMP);

        return query.getResultList();
    }

    /**
     * получает дату самого ближайшей прибыли с имущества
     * 
     * @param userId
     * @return дату прибыли
     */
    public Date getMinNextProfit(int userId) {
        String hql = "SELECT min(nextProfit) FROM Property as pr WHERE pr.userId = :userId";
        Query query = em.createQuery(hql).setParameter("userId", userId);
        return (Date) query.getSingleResult();
    }

    /**
     * значения диапазонов: мин и макс значений цены продажи имущества мин и макс значений процента износа имущества
     * 
     * @param userId
     * @return
     */
    public List<Object> getRangeValues(int userId) {
        List<Object> result = new ArrayList<Object>(4); // результат

        String suff = "FROM Property as pr WHERE pr.userId = ?0";

        String minPrHql = "Select min(sellingPrice)" + suff;
        String maxPrHql = "Select max(sellingPrice)" + suff;
        String minDpHql = "Select min(depreciationPercent)" + suff;
        String maxDpHql = "Select max(depreciationPercent)" + suff;

        result.add(em.createQuery(minPrHql).setParameter(0, userId).getSingleResult());
        result.add(em.createQuery(maxPrHql).setParameter(0, userId).getSingleResult());
        result.add(em.createQuery(minDpHql).setParameter(0, userId).getSingleResult());
        result.add(em.createQuery(maxDpHql).setParameter(0, userId).getSingleResult());

        return result;
    }

    public Property getPropertyById(int id) {
        return em.find(Property.class, id);
    }

    /**
     * @param obj
     *            - [prop || cash] (имущество или касса)
     * @return список имущества, уровень которого или уровень кассы которого можно повысить. Можно повысить уровень,
     *         если его значение меньше, чем максимальная установленая планка
     */
    @SuppressWarnings("unchecked")
    public List<Property> getToLevelUpForPljushki(String obj, int userId) {
        String field = "";
        int maxLevel = 0;
        if (obj.equals("prop")) {
            field = "level";
            maxLevel = Constants.MAX_PROP_LEVEL;
        } else if (obj.equals("cash")) {
            field = "cashLevel";
            maxLevel = Constants.MAX_CASH_LEVEL;
        }
        String hql = String.format("FROM Property WHERE userId = ?0 AND %s < %s", field, maxLevel);

        return em.createQuery(hql).setParameter(0, userId).getResultList();
    }

	/**
	 * @param userId
	 *            - id владельца имущества
	 * @param type
	 *            - тип имущества (Киоск, Сельский магазин, ...), может быть null, тогда этот параметр не учитывается
	 * @param cityArea
	 *            - район города
	 * @param level
	 *            - уровень имущества, может быть null, тогда этот параметр не учитывается
	 * @param cashLevel
	 *            - уровень кассы имущества, может быть null, тогда этот параметр не учитывается
	 * @param active
	 *            - активное имущество считается тогда, когда процент износа в нем меньше 100%
	 * @return список имущества пользователя, учитывая указанные параметры
	 */
	@SuppressWarnings("unchecked")
	public List<Property> getPropertyListWithParams(Integer userId, TradeBuildingsTypes type, CityAreas cityArea, Integer level,
			Integer cashLevel, boolean active) {

		String hql = "from Property as pr WHERE userId = :userId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);

		// установка параметра Типа
		if (type != null) {
			hql += " and pr.tradeBuildingType = :type";
			params.put("type", type);
		}
		// установка параметра Района города
		if (cityArea != null) {
			hql += " and pr.cityArea = :cityArea";
			params.put("cityArea", cityArea);
		}
		// установка параметра Уровня имущества
		if (level != null) {
			hql += " and pr.level = :level";
			params.put("level", level);
		}
		// установка параметра Уровня кассы имущества
		if (cashLevel != null) {
			hql += " and pr.cashLevel = :cashLevel";
			params.put("cashLevel", cashLevel);
		}
		// установка параметра процента износа (показатель активности имущества)
		if (active) {
			hql += " and pr.depreciationPercent < 100";
		}

		Query query = em.createQuery(hql);
		// установка параметров
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();
	}
}
