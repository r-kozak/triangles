<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/buttons.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/beaTable.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pagination.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/simpleTip.css" type="text/css" />

<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.4.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/change_balance.js"></script>
<title>Коммерческое имущество</title>
</head>

<body>
	<div class="header">
		<a href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/resources/img/logo.png"
			align="middle"></a>
		<div class="headerNav">
			<a href="${pageContext.request.contextPath}/exit"><p class="button small bGray">
					<span>exit</span>
				</p></a>
		</div>
	</div>
	</div>
	<div class="status">
		<div class="dominant">Dominant: 0&#9813;</div>
		<div class="balance">
			<a href="${pageContext.request.contextPath}/transactions">Balance: ${balance}&tridot;</a>
		</div>
	</div>
	
	<div class="content">
		<div class="tranBlock">
			<h1 align="center">Коммерческое имущество</h1>
			<c:if test="${empty comProps}">
				<div class = "noData">У вас нет имущества. Его можно купить на рынке. <a href = "${pageContext.request.contextPath}/property/r-e-market">РЫНОК</a></div>
			</c:if>
			
			<c:if test="${!empty comProps}">
				<table class="beaTable">
					<tr>
						<td>Тип</td>
						<td>Наименование</td>
						<td>Уровень</td>
						<td>Цена продажи, &tridot;</td>
						<td>Износ, %</td>
						<td>Касса, &tridot;</td>
						<td>Собрать доход</td>
					</tr>


					<c:forEach items="${comProps}" var="prop">
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

							<td><a href="${pageContext.request.contextPath}/property/${prop.id}">${prop.name}</a></td>
							<td>${prop.level}</td>
							<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.sellingPrice}"/></td>
							<td>${prop.depreciationPercent}<progress max="100"
									value="${prop.depreciationPercent}"></td>
							<td>${prop.cash} / ${prop.cashCapacity}<progress
									max="${prop.cashCapacity}" value="${prop.cash}"></td>

							<td align="center">
								<c:if test="${prop.cash > 0}">
									<a href="${pageContext.request.contextPath}/property/get-cash/${prop.id}">
											<p class="button small bRed">
												<span>&#10004;</span>
											</p> <span class="tip">Собрать</span>
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
</body>
</html>