<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Рынок недвижимости</title>

<head>
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
	<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
	<script type="text/javascript">
		$(function () {
			$("#price-slider").slider({
				range: true,
				min: <c:out value='${reps.priceMin}'/>,
				max: <c:out value='${reps.priceMax}'/>,
				values: [<c:out value='${reps.priceFrom}'/>, <c:out value='${reps.priceTo}'/>],
				slide: function (event, ui) {
					$("#pr_from").val(ui.values[0]);
					$("#pr_to").val(ui.values[1]);
					$("#pr_lab_fr").val(ui.values[0]);
					$("#pr_lab_to").val(ui.values[1]);
				}
			});
			$("#pr_lab_fr").val($("#price-slider").slider("values", 0));
			$("#pr_lab_to").val($("#price-slider").slider("values", 1));
		});
	</script>
</head>
<t:template>
	<!-- Задний прозрачный фон-->
	<div id="wrap"></div>

<t:menu>
	<form:form id="searchForm" method="GET" commandName="reps">
		<div id="searchWrap">
		<div id="menuTitle">Поиск</div>
			<fieldset id = "searchBlock">
			<legend>Разм. начало</legend>
				<div id="searchEl">
					<div id="nadp">Начало:</div> <form:input class="dateEl" type="date" path="appearDateFrom"/>
				</div>
				<div id="searchEl">
					<div id="nadp">Конец:</div> <form:input class="dateEl" type="date" path="appearDateTo"/>
				</div>
			</fieldset>
			
			<fieldset id = "searchBlock">
			<legend>Разм. конец</legend>
				<div id="searchEl">
					<div id="nadp">Начало:</div> <form:input class="dateEl" type="date" path="lossDateFrom"/>
				</div>
				<div id="searchEl">
					<div id="nadp">Конец:</div> <form:input class="dateEl" type="date" path="lossDateTo"/>
				</div>
			</fieldset>
			
			<fieldset id = "searchBlock"> 
			<legend>Район</legend>
				<div id="searchEl">
					<form:checkboxes path="areas" items="${areas}"/>      
				</div>
			</fieldset>
			
			<fieldset id = "searchBlock"> 
			<legend>Тип</legend>
				<div id="searchEl">
					<form:checkboxes path="types" items="${types}"/>      
				</div>
			</fieldset>
			
			<fieldset id = "searchBlock">
			<legend>Цена, &tridot;</legend>
				<div id="searchEl">
					<input type="text" class="value_lab" id="pr_lab_fr" readonly>
					<input type="text" class="value_lab" id="pr_lab_to" readonly style="float:right; text-align:right">
					<div class="slider" id="price-slider"></div>
					
					<form:input id="pr_from" class="textInp2" hidden="true" path="priceFrom"></form:input>
					<form:input id="pr_to" class="textInp2"  hidden="true" path="priceTo"></form:input>
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
</t:menu>

	<div class="content">
		<div class="tranBlock">
			<h1 align="center">Рынок недвижимости</h1>

			<c:if test="${empty proposals && marketEmpty}">
				<div class="noData">
					Предложений нет. Вернитесь домой и приходите через минуту. 
					<a href="${pageContext.request.contextPath}/home">ДОМОЙ</a>
				</div>
			</c:if>
			<c:if test="${empty proposals && !marketEmpty}">
				<div class = "noData">Поиск не дал результатов. Попробуйте задать другие параметры.</div>
			</c:if>

			<c:if test="${!empty proposals}">
				<table class="beaTable">
					<tr>
						<td>Тип</td>
						<td>Район города</td>
						<td>Размещение</td>
						<td>Конец размещения</td>
						<td>Цена, &tridot;</td>
						<td>Купить</td>
					</tr>
				<c:forEach items="${proposals}" var="prop">
					<tr>
						<c:choose>
							<c:when test="${prop.commBuildingType == 'STALL'}">
								<td>Киоск</td>
							</c:when>
							<c:when test="${prop.commBuildingType == 'VILLAGE_SHOP'}">
								<td>Сельский магазин</td>
							</c:when>
							<c:when test="${prop.commBuildingType == 'STATIONER_SHOP'}">
								<td>Магазин канцтоваров</td>
							</c:when>
							<c:otherwise>
								<td>${prop.commBuildingType}</td>
							</c:otherwise>
						</c:choose>

						<c:choose>
							<c:when test="${prop.cityArea == 'GHETTO'}">
								<td>Гетто</td>
							</c:when>
							<c:when test="${prop.cityArea == 'OUTSKIRTS'}">
								<td>Окраина</td>
							</c:when>
							<c:when test="${prop.cityArea == 'CHINATOWN'}">
								<td>Чайнатаун</td>
							</c:when>
							<c:when test="${prop.cityArea == 'CENTER'}">
								<td>Центр</td>
							</c:when>
							<c:otherwise>
								<td>${prop.cityArea}</td>
							</c:otherwise>
						</c:choose>

						<td><fmt:formatDate value="${prop.appearDate}"
								pattern="dd-MM-yyyy HH:mm" /></td>
						<td><fmt:formatDate value="${prop.lossDate}"
								pattern="dd-MM-yyyy HH:mm" /></td>

						<td><fmt:formatNumber type="number" maxFractionDigits="3"
								value="${prop.purchasePrice}" /></td>
						<td><a class="support-hover"
							href="${pageContext.request.contextPath}/property/buy/${prop.id}">
								<p class="button small bRed">
									<span>BUY</span>
								</p>
								<span class="tip">Купить</span>
						</a></td>
					</tr>
				</c:forEach>
			</table>
			<div class="pagination">
				<ul>${tagNav}</ul>
			</div>
			</c:if>
		</div>
	</div>
	<div id="popup">
		Поздравляем с покупкой!
		<c:choose>
			<c:when test="${popup == true}">
				<script>
					(function popUp() {
						$("#wrap").css({
							"display" : "block"
						});
						$("#popup")
								.css(
										{
											"left" : (window.screen.availWidth - 600) / 2,
											"top" : (window.screen.availHeight - 110) / 2 - 150,
											"display" : "block"
										});
					})();
					setTimeout(function() {
						$('#popup').fadeOut(1500)
					}, 1500);
					setTimeout(function() {
						$('#wrap').fadeOut(1500)
					}, 1500);
				</script>
			</c:when>
		</c:choose>
	</div>
</t:template>