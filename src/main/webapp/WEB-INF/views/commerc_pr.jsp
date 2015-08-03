<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
	/*наименование имущества*/
	#name {
		width:185;
	}
</style>

<head>
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
	<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
	<script type="text/javascript">
		$(function () {
			$("#depreciation-slider").slider({
				range: true,
				min: <c:out value='${cps.depreciationMin}'/>,
				max: <c:out value='${cps.depreciationMax}'/>,
				values: [<c:out value='${cps.depreciationFrom}'/>, <c:out value='${cps.depreciationTo}'/>],
				slide: function (event, ui) {
					$("#depr_from").val(ui.values[0]);
					$("#depr_to").val(ui.values[1]); 
					$("#depr_lab_fr").val(ui.values[0]);
					$("#depr_lab_to").val(ui.values[1]);
				}
			});
			$("#depr_lab_fr").val($("#depreciation-slider").slider("values", 0));
			$("#depr_lab_to").val($("#depreciation-slider").slider("values", 1));
		});
		$(function () {
			$("#sell_price-slider").slider({
				range: true,
				min: <c:out value='${cps.sellPriceMin}'/>,
				max: <c:out value='${cps.sellPriceMax}'/>,
				values: [<c:out value='${cps.sellPriceFrom}'/>, <c:out value='${cps.sellPriceTo}'/>],
				slide: function (event, ui) {
					$("#sell_pr_from").val(ui.values[0]);
					$("#sell_pr_to").val(ui.values[1]);
					$("#sell_pr_lab_fr").val(ui.values[0]);
					$("#sell_pr_lab_to").val(ui.values[1]);
				}
			});
			$("#sell_pr_lab_fr").val($("#sell_price-slider").slider("values", 0));
			$("#sell_pr_lab_to").val($("#sell_price-slider").slider("values", 1));
		});
	</script>
</head>

<title>Коммерческое имущество</title>
<t:template>
	<div id="menu">
	<div id="menuTitle">Меню</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property">Упр. имуществом</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/r-e-market">Рынок</a>
		</div>
		
		<form:form id="searchForm" method="GET" commandName="cps">
			<div id="searchWrap">
			<div id="menuTitle">Поиск</div>
				<fieldset id = "searchBlock">
					<form:input class="textInp" type="text" path="name" placeholder="Наименование"></form:input>
				</fieldset>
				
				<fieldset id = "searchBlock"> 
				<legend>Тип</legend>
					<div id="searchEl">
						<form:checkboxes path="types" items="${types}"/>      
					</div>
				</fieldset>
				
				<fieldset id = "searchBlock">
				<legend>Цена продажи, &tridot;</legend>
					<div id="searchEl">
						<input type="text" class="value_lab" id="sell_pr_lab_fr" readonly>
						<input type="text" class="value_lab" id="sell_pr_lab_to" readonly style="float:right; text-align:right">
						<div class="slider" id="sell_price-slider"></div>
						
						<form:input id="sell_pr_from" class="textInp2" hidden="true" path="sellPriceFrom"></form:input>
						<form:input id="sell_pr_to" class="textInp2"  hidden="true" path="sellPriceTo"></form:input>
					</div>
				</fieldset>
				
				<fieldset id = "searchBlock">
				<legend>Износ, %</legend>
					<div id="searchEl">
						<input type="text" class="value_lab" id="depr_lab_fr" readonly>
						<input type="text" class="value_lab" id="depr_lab_to" readonly style="float:right; text-align:right">
						<div class="slider" id="depreciation-slider"></div>
						
						<form:input id="depr_from" hidden="true" path="depreciationFrom"></form:input>
						<form:input id="depr_to" hidden="true" path="depreciationTo"></form:input>
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
			<h1 align="center">Коммерческое имущество</h1>
			<c:if test="${empty comProps && !userHaveProps}">
				<div class = "noData">У вас нет имущества. Его можно купить на рынке. <a href = "${pageContext.request.contextPath}/property/r-e-market">РЫНОК</a></div>
			</c:if>
			<c:if test="${empty comProps && userHaveProps}">
				<div class = "noData">Поиск не дал результатов. Попробуйте задать другие параметры.</div>
			</c:if>
			
			<c:if test="${!empty comProps}">
				<div id="actionBlock">
					<a class="support-hover" href="${pageContext.request.contextPath}/property/get-cash/0">
											<p class="button small bRed"><span>Собрать всё</span></p> <span class="tip">Собрать доход со всего имущества</span>
					</a>
				</div>
				<table class="beaTable">
					<tr>
						<td>Тип</td>
						<td>Наимено- вание</td>
						<td>Уровень</td>
						<td>Район</td>
						<td>Цена продажи, &tridot;</td>
						<td>Износ, %</td>
						<td>Касса, &tridot;</td>
						<td>Собрать доход</td>
					</tr>
	
					<c:forEach items="${comProps}" var="prop">
						<tr>
							<c:choose>
								<c:when test="${prop.commBuildingType == 'STALL'}">
									<td style="text-align:left">Киоск</td>
								</c:when>
								<c:when test="${prop.commBuildingType == 'VILLAGE_SHOP'}">
									<td style="text-align:left">Сельский магазин</td>
								</c:when>
								<c:when test="${prop.commBuildingType == 'STATIONER_SHOP'}">
									<td style="text-align:left">Магазин канцтоваров</td>
								</c:when>
								<c:otherwise>
									<td>${prop.commBuildingType}</td>
								</c:otherwise>
							</c:choose>
	
							<td style="text-align:left"><a href="${pageContext.request.contextPath}/property/${prop.id}">${prop.name}</a></td>
							<td>${prop.level}</td>
													<!-- Район -->
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

							<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.sellingPrice}"/></td>
							<td>${prop.depreciationPercent}<progress max="100"
									value="${prop.depreciationPercent}"></td>
							<td>${prop.cash} / ${prop.cashCapacity}<progress
									max="${prop.cashCapacity}" value="${prop.cash}"></td>
	
							<td align="center">
								<c:if test="${prop.cash > 0}">
									<a class="support-hover" href="${pageContext.request.contextPath}/property/get-cash/${prop.id}">
											<p class="button small bRed"><span>&#10004;</span></p> <span class="tip">Собрать</span>
									</a>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
	
			<div class="pagination">
				<ul>${tagNav}</ul>
			</div>
		</div>
	</div>
	<div id="balChan">
		<c:choose>
			<c:when test="${changeBal.length() > 0}">
				${changeBal}&tridot;
				<script>
					popUp("<c:out value='${changeBal}'/>", "#balChan");
				</script>
			</c:when>
		</c:choose>
	</div>
</t:template>