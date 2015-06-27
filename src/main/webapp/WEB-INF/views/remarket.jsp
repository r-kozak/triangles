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

<script src="${pageContext.request.contextPath}/resources/jquery-2.1.4.js"></script>
<title>Home</title>
</head>

<style>
#popup {
	width: 600px;
	height: 155px;
	background: rgb(252, 241, 255);
	position: absolute;
	display: none;
	font-size: 52;
	text-align: center;
	color: #af2c6e;
	font-family: 'Arial Black', Gadget, sans-serif;
	border: 1px double #cbcbcb;
	z-index: 200;
}

#wrap {
	display: none;
	opacity: 0.8;
	position: fixed;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	padding: 16px;
	background-color: rgba(1, 1, 1, 0.725);
	z-index: 100;
	overflow: auto;
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
			<a href="${pageContext.request.contextPath}/transactions">Balance: ${balance}&tridot;</a>
		</div>
	</div>
	
	<!-- Задний прозрачный фон-->
	<div onclick="show('none')" id="wrap"></div>
	
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
									<td>${prop.cityArea}</td>
								</c:otherwise>
							</c:choose>

							<td><fmt:formatDate value="${prop.appearDate}" pattern="dd-MM-yyyy HH:mm:ss" /></td>
							<td><fmt:formatDate value="${prop.lossDate}" pattern="dd-MM-yyyy HH:mm:ss" /></td>

							<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.purchasePrice}" /></td>
							<td align="center"><a href="${pageContext.request.contextPath}/property/buy/${prop.id}"><p
										class="button small bRed">
										<span>BUY</span>
									</p></a></td>
						</tr>
					</c:forEach>
				</c:if>
			</table>

			<div class="pagination">
				<ul>${tagNav}</ul>
			</div>
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
</body>
</html>