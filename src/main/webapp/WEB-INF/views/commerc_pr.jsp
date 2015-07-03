<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/main.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/buttons.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/beaTable.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/pagination.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/simpleTip.css" type="text/css" />

<script src="${pageContext.request.contextPath}/resources/jquery-2.1.4.js"></script>
<title>Коммерческое имущество</title>
</head>

<style>
.noProp {
  margin: 100;
  color: rgb(7, 115, 106);
  text-align: center;
  font-size: 50;
  border: 2px solid rgb(186, 16, 16);
  padding: 20 20 30 20;
}
.noProp a {
  margin: 50 200 0 200;
  color: rgb(146, 20, 160);
  border: 3px solid;
  padding: 5 20 5 20;
  text-decoration: none;
  font-size: 25;
  display: block;
}
</style>

<body>
	<div class="header">
		<a href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/resources/logo.png"
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
			<a href="${pageContext.request.contextPath}/syso">Balance: ${balance}&tridot;</a>
		</div>
	</div>
	
	<div class="content">
		<div class="tranBlock">
			<h1 align="center">Коммерческое имущество</h1>
			<c:if test="${empty comProps}">
				<div class = "noProp">У вас нет имущества. Его можно купить на рынке. <a href = "${pageContext.request.contextPath}/property/r-e-market">РЫНОК</a></div>
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


							<td align="center"><a
								href="${pageContext.request.contextPath}/property/get-cash/${prop.id}">
									<p class="button small bRed">
										<span>&#10004;</span>
									</p> <span class="tip">Собрать</span>
							</a></td>
						</tr>
					</c:forEach>
				</table>
			</c:if>

			<div class="pagination">
				<ul>${tagNav}</ul>
			</div>
		</div>
	</div>
</body>
</html>