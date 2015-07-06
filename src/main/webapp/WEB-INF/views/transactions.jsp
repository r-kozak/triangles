<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/buttons.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/beaTable.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pagination.css" type="text/css" />
<title>Транзакции</title>
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
		<div class="balance">Balance: ${balance}&tridot;</div>
	</div>

	<div class="content">
		<div class="tranBlock">
		<h1 align="center">Транзакции</h1>
			<table class="beaTable">
				<tr>
					<td>Дата</td>
					<td>Статья затрат</td>
					<td>Описание</td>
					<td>Прих./Расх.</td>
					<td>Сумма</td>
					<td>Баланс</td>
				</tr>

				<c:if test="${!empty transacs}">
					<c:forEach items="${transacs}" var="transac">
						<tr>
							<td><fmt:formatDate value="${transac.transactDate}" pattern="dd-MM-yyyy HH:mm:ss" /></td>
							
							<c:choose>
								<c:when test="${transac.articleCashFlow == 'DAILY_BONUS'}">
									<td>Ежедневный бонус</td>
								</c:when>
								<c:when test="${transac.articleCashFlow == 'CREDIT_DEPOSIT'}">
									<td>Кредит/депозит</td>
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
				</c:if>
			</table>

			<div class="pagination">
				<ul>${tagNav}</ul>
			</div>

		</div>
	</div>
</body>
</html>