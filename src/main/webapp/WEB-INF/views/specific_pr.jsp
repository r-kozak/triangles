<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>${prop.name}</title>
<script>
	window.onload = function(){ 
	    document.getElementById('hider').onclick = function() {
	      	document.getElementById('but1').style.display = 'inline-block';
	      	document.getElementById('NewNameInput').style.display = 'inline-block';
	    	document.getElementById('wrap').style.display = 'block';
	    	document.getElementById('name').style.opacity = '0';
	    }
	    
	    document.getElementById('wrap').onclick = function() {
	    	document.getElementById('wrap').style.display = 'none';
	      	document.getElementById('but1').style.display = 'none';
	      	document.getElementById('NewNameInput').style.display = 'none';
	    	document.getElementById('name').style.opacity = '1';
	    }
	};
</script>

<t:template>
	<!-- Задний прозрачный фон-->
	<div id="wrap"></div>

	<div id="menu">
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property">Упр. имуществом</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/r-e-market">Рынок</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/commerc-pr">Моё коммерческое</a>
		</div>
	</div>
	
	<div class="content">
		<div class="tranBlock">
			<form:form name="property" action="operations/${prop.id}" commandName="prop" method="post">
				<input type="hidden" name="action" id="action">
			
				<input id="NewNameInput" name="newName" type="text" value="${prop.name}" maxlength="25">
				<p onclick="document.property.action.value='change_name'; document.property.submit();" 
							id="but1" class="button small bGreen"><span>&#10004;</span></p>
				
				
				<h1 id="name" align="center">${prop.name} <a id="hider" class="support-hover">
				<p class="button small bBlue"><span>&#9997;</span></p><span class="tip"><font size="2">Изменить</font></span></a></h1>
				
		
				<table class="beaTable">
					<tr>
						<td>Характеристика</td>
						<td>Значение</td>
						<td>Действие</td>
					</tr>
					<tr>
						<td>Тип</td>
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
						<td></td>
					</tr>
					<tr>
						<td>Район</td>
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
						<td></td>
					</tr>
					<tr>
						<td>Вид деятельности</td>
						<td>${type}</td>
						<td></td>
					</tr>
					<tr>
						<td>Активность</td>
						<c:choose>
							<c:when test="${prop.valid}">
								<td>активное</td>
							</c:when>
							<c:otherwise>
								<td>не активное</td>
							</c:otherwise>
						</c:choose>
						<td></td>
					</tr>
					<tr>
						<td>Процент износа</td>
						<td>
							<div>
								<c:choose>
									<c:when test="${prop.depreciationPercent > 75}">
										${prop.depreciationPercent} / 100
									</c:when>
									<c:when test="${prop.depreciationPercent > 50}">
										${prop.depreciationPercent} / 100
									</c:when>
									<c:otherwise>
										${prop.depreciationPercent} / 100
									</c:otherwise>
								</c:choose>
							</div>
							<progress max="100" value="${prop.depreciationPercent}">
						</td>
						<td><a class="support-hover" href="${pageContext.request.contextPath}/property/repair/${prop.id}">
							<p class="button small bGreen"><span>Р</span></p><span class="tip">Ремонт</span></a>
						</td>
					</tr>
					<tr>
						<td>Уровень</td>
						<td>${prop.level}</td>
						<td><a class="support-hover" href="${pageContext.request.contextPath}/property/level-up/${prop.id}">
							<p class="button small bPurple"><span>▲</span></p><span class="tip">Поднять</span></a></td>
					</tr>
					<tr>
						<td>Деньги в кассе</td>
						<td>
							<div>
								<fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.cash}"/> / 
								<fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.cashCapacity}"/>
							</div>
							<progress max="${prop.cashCapacity}" value="${prop.cash}">
						</td>
						<td>
							<c:if test="${prop.cash > 0}">
								<a class="support-hover"><p onclick="document.property.action.value='get_cash'; document.property.submit();" 
								class="button small bRed"><span>&#10004;</span></p><span class="tip">Собрать</span></a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td>Уровень кассы</td>
						<td>${prop.cashLevel}</td>
						<td><a class="support-hover" href="${pageContext.request.contextPath}/property/cash-up/${prop.id}">
							<p class="button small bBlue"><span>▲</span></p><span class="tip">Поднять</span></a></td>
					</tr>
					<tr>
						<td>Стоимость продажи</td>
						<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.sellingPrice}"/></td>
						<td><a class="support-hover" href="${pageContext.request.contextPath}/property/sell/${prop.id}">
							<p class="button small bGray"><span>&tridot;</span></p><span class="tip">Продать</span></a></td>
					</tr>
				</table>
			</form:form>
		</div>
	</div>
	<div id="balChan">
		<c:if test="${changeBal.length() > 0}">
			${changeBal}&tridot;
			<script>
				popUp("<c:out value='${changeBal}'/>", "#balChan");
			</script>
		</c:if>
	</div>
</t:template>