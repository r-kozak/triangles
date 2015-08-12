<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Транзакции</title>

<%-- <link rel='stylesheet' href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/css/bootstrap.min.css'> --%>
<%-- <link rel='stylesheet' href='${pageContext.request.contextPath}/webjars/datatables/1.10.7/css/jquery.dataTables.min.css'> --%>

<t:template>
<div class="content">
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
		</div>
		<div id="searchEl">
			<button id="searchSubmit" class="btn btn-primary btn-sm" type="submit" name="submit1">Искать</button>
			<input id="submClear" class="btn btn-danger btn-sm" data-toggle="tooltip" title="Очистить фильтр"  type="button" value="&#10008;" onclick="document.getElementById('needClear').checked = true; document.getElementById('searchForm').submit();"/>
		</div>
	</form:form>
</t:menu>

<div class="tranBlock">
	<c:if test="${!empty transacs}">
				
		<h1 style="text-align:center;">Транзакции</h1>
		<div class="panel panel-default">
			<div class="panel-heading">
			    <button class="btn btn-default" data-toggle="collapse" data-target="#tr_descr" data-toggle="tooltip"
			     title="Показать или скрыть подробное описание раздела Транзакции">Описание</button>
			</div>
			<div class="panel-body collapse" id="tr_descr">
				<p><a href="${pageContext.request.contextPath}/wiki#ba.tr">Транзакции</a> - это раздел, где можно посмотреть все операции, которые 
			    повлияли на размер баланса.</p>	
			</div>
		</div>

		<table id="transTable" class="table table-striped table-bordered">
	      <thead>
			<tr class="info" style="font-weight:bold">
				<td>Дата</td>
				<td>Статья затрат</td>
				<td>Описание</td>
				<td>Прих./Расх.</td>
				<td>Сумма</td>
				<td>Баланс</td>
			</tr>
		  </thead>
		  <tbody>
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
							<c:when test="${transac.articleCashFlow == 'UP_PROP_LEVEL'}">
								<td style="text-align:left">Улучшение имущества</td>
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
			  </tbody>
		</table>
		
		<div class="panel panel-default" style="margin-top:5">
			<div class="panel-body">
			    <p class="text-right">Общая сумма операций по выбранным фильтрам: <b><fmt:formatNumber type="number" maxFractionDigits="3" 
			    value="${totalSum}"/>&tridot;</b></p>
			</div>
		</div>
		
	</c:if>
</div> <!-- tranBlock -->
</div> <!-- content -->


<script type="text/javascript" src="webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript" src="webjars/datatables/1.10.7/js/jquery.dataTables.min.js"></script>

<!-- Сортировка даты -->
<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.4/moment.min.js"></script>
<script type="text/javascript" src="webjars/datatables-plugins/1.10.7/sorting/datetime-moment.js"></script>
	
<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); // для отображения подсказок
    $('[data-toggle="collapse"]').collapse(); // для сворачивания блоков
  
    //красивая табличка
    $.fn.dataTable.moment('DD-MM-YYYY HH:mm:ss'); // сортировка даты в табличке
    $('#transTable').dataTable( { // сделать сортировку, пагинацию, поиск
        "order": [[ 0, "desc" ]]
    } );
});
</script>
</t:template>