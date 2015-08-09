<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Транзакции</title>

<link rel='stylesheet' href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/css/bootstrap.min.css'>

<t:template>
<t:menu>
	<form:form id="searchForm" name="searchForm" method="GET" commandName="ts">
		<div id="searchWrap">
		<div id="menuTitle">Поиск</div>
			<fieldset id = "searchBlock">
			<legend>Период</legend>
				<div id="searchEl">
					<div id="nadp">Начало:</div> <form:input class="dateEl" type="date" path="dateFrom"/>
				</div>
				<div id="searchEl">
					<div id="nadp">Конец:</div> <form:input class="dateEl" type="date" path="dateTo"/>
				</div>
			</fieldset>

			<fieldset id="searchBlock">
			<legend>Движение</legend>
				<form:select path="transfer" id="selectEl">    
					<form:option value="NONE" label="--- Выбрать ---"/>
			   		<form:options items="${transfers}" />
				</form:select>
			</fieldset>

			<fieldset id="searchBlock">
			<legend>Статьи затрат</legend>
				<div id="searchEl">
					<form:checkboxes path="articles" items="${articles}"/>      
				</div>
			</fieldset>

			<form:checkbox id="needClear" path="needClear" hidden="true"/>
			<input id="page" path="page" name="page" value="1" hidden="true">
			<form:input id="showAll" path="showAll" name="showAll" value="" hidden="true"></form:input>
		</div>
		<div id="searchEl">
			<input id="searchSubmit" type="submit" name="submit1" value="Искать">
			<input id="searchSubmit" class="submClear" title="Очистить" type="button" value="&#10008;" onclick="document.getElementById('needClear').checked = true; document.getElementById('searchForm').submit();"/>
		</div>
	</form:form>
</t:menu>


		<div class="tranBlock">

				<c:if test="${!empty transacs}">
				<div id="actionBlock">
					<a class="support-hover" onclick="document.searchForm.showAll.value='true'; document.searchForm.submit();" >
						<p class="button small bRed"><span>Показать все</span></p> <span class="tip">Показать все транзакции с текущими фильтрами</span>
					</a>
				</div>
<div class="panel panel-default">
	<div class="panel-heading"><h4>Транзакции</h4></div>
	  <div class="panel-body">
	    <p>Это транзакции</p>
	  </div>
			<table class="table">
				<tr>
					<td>Дата</td>
					<td>Статья затрат</td>
					<td>Описание</td>
					<td>Прих./Расх.</td>
					<td>Сумма</td>
					<td>Баланс</td>
				</tr>
					<c:forEach items="${transacs}" var="transac">
						<tr>
							<td><fmt:formatDate value="${transac.transactDate}" pattern="dd-MM-yyyy HH:mm:ss" /></td>
							
							<c:choose>
								<c:when test="${transac.articleCashFlow == 'DAILY_BONUS'}">
									<td style="text-align:left">Ежедневный бонус</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'CREDIT'}">
									<td style="text-align:left">Кредит</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'DEPOSIT'}">
									<td style="text-align:left">Депозит</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'LEVY_ON_PROPERTY'}">
									<td style="text-align:left">Сбор с имущества</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'BUY_PROPERTY'}">
									<td style="text-align:left">Покупка имущества</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'PROPERTY_REPAIR'}">
									<td style="text-align:left">Ремонт имущества</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'UP_CASH_LEVEL'}">
									<td style="text-align:left">Улучшение кассы</td>
								</c:when>
								<c:otherwise>
									<td>${transac.articleCashFlow}</td>
								</c:otherwise>
							</c:choose>
							
							<td style="text-align:left">${transac.description}</td>
							
							<c:choose>
								<c:when test="${transac.transferType == 'PROFIT'}">
									<td>Приход</td>
								</c:when>
								<c:when test="${transac.transferType == 'SPEND'}">
									<td>Расход</td>
								</c:when>
								<c:otherwise>
									<td>${transac.transferType}</td>
								</c:otherwise>
							</c:choose>
							<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${transac.sum}"/></td>
							<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${transac.balance}"/></td>
						</tr>
					</c:forEach>
					<tr>
						<td colspan=4 style="text-align: left; font-weight:bold;">Всего:</td>
						<td style="font-weight:bold;"><fmt:formatNumber type="number" maxFractionDigits="3" value="${totalSum}"/></td>
						<td>―</td>
					</tr>
				</c:if>
			</table>
		</div>
	
			<div class="pagination">
				<ul>${tagNav}</ul>
			</div>
		</div>

	
	<script type="text/javascript" src="webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="webjars/jquery/2.1.4/jquery.min.js"></script>
</t:template>