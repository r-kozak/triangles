<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Рыное недвижимости</title>
<t:template>
	<!-- Задний прозрачный фон-->
	<div id="wrap"></div>

	<div id="menu">
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property">Упр. имуществом</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/commerc-pr">Моё коммерческое</a>
		</div>
	</div>
	<div class="content">
		<div class="tranBlock">
			<h1 align="center">Рынок недвижимости</h1>

			<c:if test="${empty proposals}">
				<div class="noData">
					Предложений нет. Вернитесь домой и приходите через минуту. 
					<a href="${pageContext.request.contextPath}/home">ДОМОЙ</a>
				</div>
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