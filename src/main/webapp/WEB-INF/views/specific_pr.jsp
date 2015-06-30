<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/main.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/buttons.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/beaTable.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/simpleTip.css" type="text/css" />

<script src="${pageContext.request.contextPath}/resources/jquery-2.1.4.js"></script>

<title>${prop.name}</title>
</head>


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

<style>
#NewNameInput{
	 margin: 6 310;
	 display:none;
	 border-width:3px;
	 border-style:dotted;
	 border-color:#cacaca;
	 -webkit-border-radius: 10px;
	 -moz-border-radius: 10px;
	 border-radius: 10px;
	 font-size:15px;
	 font-family:arial;
	 color:#7a3403;
	 padding-left:4px;
	 width:230px;
	 height:35px;
	 background:#ffffff;
	 z-index:201;
	 position: absolute;
}

#but1 {
	margin: 12 550;
	display: none;
	z-index:200;
	position: absolute;
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
	overflow: auto;
	z-index:100;
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
	<div id="wrap"></div>

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
						<c:choose>
							<c:when test="${prop.depreciationPercent > 75}">
								<td>${prop.depreciationPercent}</td>
							</c:when>
							<c:when test="${prop.depreciationPercent > 50}">
								<td>${prop.depreciationPercent}</td>
							</c:when>
							<c:otherwise>
								<td>${prop.depreciationPercent}</td>
							</c:otherwise>
						</c:choose>
						<td><a class="support-hover" href="${pageContext.request.contextPath}/property/repair/${prop.id}">
							<p class="button small bBlue"><span>Р</span></p><span class="tip">Ремонт</span></a></td>
					</tr>
					<tr>
						<td>Уровень</td>
						<td>${prop.level}</td>
						<td><a class="support-hover" href="${pageContext.request.contextPath}/property/level-up/${prop.id}">
							<p class="button small bBlue"><span>▲</span></p><span class="tip">Поднять</span></a></td>
					</tr>
					<tr>
						<td>Деньги в кассе</td>
						<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.cash}"/></td>
						<td><a class="support-hover" href="${pageContext.request.contextPath}/property/get-cash/${prop.id}">
							<p class="button small bBlue"><span>&#10004;</span></p><span class="tip">Собрать</span></a></td>
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
							<p class="button small bRed"><span>&tridot;</span></p><span class="tip">Продать</span></a></td>
					</tr>
				</table>
			</form:form>
		</div>
	</div>
</body>
</html>