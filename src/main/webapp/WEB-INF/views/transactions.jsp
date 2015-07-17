<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Транзакции</title>

<style>
#searchBlock {
	padding: 5;
	margin-top: 10px; 
}
#searchForm {
	margin-top: 50;
}
#searchEl{
	margin-top:5;
}
#searchEl span{
	display: block;
	margin-top:5;
}
#searchEl span label{
	margin-left:5;
}
.date{
	width: 125;
	font-size:12px;
}
#nadp {
	width:55;
	display: inline-block;
}
#searchSubmit {
  height: 28;
  width: 155;
  background: transparent;
  border: 3px solid rgb(110, 110, 110);
  margin-top: 10;
  font-weight: bold;
  font-size: 15;
  display: inline-block;
}
.submClear {
	 width: 40 !important;
	 vertical-align: bottom;
}
.submClear:hover {
	 color: red !important;
}
#searchSubmit:hover {
  border-color: black;
  color: red;
  cursor: pointer; 
}
legend {
  color:red
}
#transfer {
  width: 184;
  height: 30;
  font-size: 17;
  margin: 10 0 10 0;
}
</style>

<script>
    function submForm(obj) {
        document.getElementById('page').value = obj.getAttribute("value");
        document.getElementById('searchForm').submit();
        return false;
    }
</script>

<t:template>
	<div id="menu">
	<div id="menuTitle">Меню</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property">Упр. имуществом</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/r-e-market">Рынок</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/commerc-pr">Моё коммерческое</a>
		</div>

	<form:form id="searchForm" method="GET" commandName="ts">
		<div id="searchWrap">
		<div id="menuTitle">Поиск</div>
			<fieldset id = "searchBlock">
			<legend>Период</legend>
				<div id="searchEl">
					<div id="nadp">Начало:</div> <form:input class="date" type="date" path="dateFrom"/>
				</div>
				<div id="searchEl">
					<div id="nadp">Конец:</div> <form:input class="date" type="date" path="dateTo"/>
				</div>
			</fieldset>

			<fieldset id="searchBlock">
			<legend>Движение</legend>
				<form:select path="transfer" id="transfer">    
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
			<input id="page" path="page" name="page" value="1" hidden="true" >
		</div>
		<div id="searchEl">
			<input id="searchSubmit" type="submit" name="submit1" value="Искать">
			<input id="searchSubmit" class="submClear" title="Очистить" type="button" value="&#10008;" onclick="document.getElementById('needClear').checked = true; document.getElementById('searchForm').submit();"/>
		</div>
	</form:form>
</div>
	<div class="content">
		<div class="tranBlock">
		<h1 align="center">Транзакции</h1>
				<c:if test="${!empty transacs}">
			<table class="beaTable">
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
									<td>Ежедневный бонус</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'CREDIT'}">
									<td>Кредит</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'DEPOSIT'}">
									<td>Депозит</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'LEVY_ON_PROPERTY'}">
									<td>Сбор с имущества</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'BUY_PROPERTY'}">
									<td>Покупка имущества</td>
								</c:when>
								<c:otherwise>
									<td>${transac.articleCashFlow}</td>
								</c:otherwise>
							</c:choose>
							
							<td>${transac.description}</td>
							
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
	
			<div class="pagination">
				<ul>${tagNav}</ul>
			</div>
		</div>
	</div>
</t:template>