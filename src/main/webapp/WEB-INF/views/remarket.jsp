<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/main.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/buttons.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/beaTable.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/pagination.css" type="text/css" />
<title>Home</title>
</head>
<body>
	<div class="header">
		<a href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/resources/logo.png"
			align="middle"></a>
 		<div class="headerNav">
			<a href="${pageContext.request.contextPath}/exit"><p class="button small bGray"><span>exit</span></p></a></div>
		</div>
	</div>
	<div class="status">
		<div class="dominant">Dominant: 0&#9813;</div>
		<div class="balance"><a href="${pageContext.request.contextPath}/transactions">Balance: ${balance}&tridot;</a></div>
	</div>

	<div class="content">
		<div class="tranBlock">
		<h1 align="center">Рынок недвижимости</h1>
			<table class="beaTable">
				<tr>
					<td>Тип</td>
					<td>Район города</td>
					<td>Размещение</td>
					<td>Конец размещения</td>
					<td>Цена</td>
					<td>Купить</td>
				</tr>

				<c:if test="${!empty proposals}">
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
									<td>${transac.articleCashFlow}</td>
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
									<td>${transac.cityArea}</td>
								</c:otherwise>
							</c:choose>
							
							<td><fmt:formatDate value="${prop.appearDate}" pattern="dd-MM-yyyy HH:mm:ss" /></td>
							<td><fmt:formatDate value="${prop.lossDate}" pattern="dd-MM-yyyy HH:mm:ss" /></td>							
							
							<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.purchasePrice}"/></td>
							<td align="center"><a href="${pageContext.request.contextPath}/property/buy/${prop.id}"><p class="button small bRed"><span>BUY</span></p></a></td>
						</tr>
					</c:forEach>
				</c:if>
			</table>

			<div class="pagination">
				<ul>${tagNav}</ul>
			</div>

		</div>
	</div>
</body>
</html>