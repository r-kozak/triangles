<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Подтверждение покупки</title>
</head>
<style>
* {
	margin: 0;
	padding: 0;
	-webkit-box-sizing: border-box;
}

body {
	width: 100%;
	display: -webkit-box;
	-webkit-box-pack: center;
	background: rgb(201, 255, 220);
}

.titl {
	border-bottom: 2px solid;
	margin-bottom: 5px;
	padding-bottom: 5px;
	font-size: 20px;
	color: firebrick;
	text-align: center;
	font-family: Comic Sans MS;
}

.buyBlock {
	min-height: 200px;
	width: 600px;
	margin-top: 180px;
	background: rgba(217, 253, 227, 0.71);
	border: 20px solid;
	border-color: rgb(194, 255, 215);
	font-size: 15px;
	padding: 10px 40px 10px 40px;
}

.buyBlock a {
	font-size: 14px;
	padding: 10px;
}

.buyBlock td {
	color: black;
	font-weight: bold;
	width: 50%;
}

.buyBlock td.info {
	font-size: 16px;
	color: #002b9f;
	font-weight: normal;
	width: auto;
	height: 30px;
}

.buyBlock td.credit {
	font-size: 16px;
	color: red;
	font-weight: bold;
	width: auto;
	height: 30px;
}

.ApplyButton {
	cursor: pointer;
	border-width: 0px;
	border-style: dotted;
	border-color: #f9f9f9;
	-webkit-border-radius: 21px;
	-moz-border-radius: 21px;
	border-radius: 21px;
	text-align: center;
	width: 100px;
	height: 40px;
	padding-top: undefinedpx;
	padding-bottom: undefinedpx;
	font-size: 18px;
	font-family: Arial;
	color: #ffffff;
	background: -moz-linear-gradient(top, #409a3b 30%, #1d4200 100%);
	background: -webkit-gradient(linear, left top, left bottom, color-stop(30%, #409a3b),
		color-stop(100%, #1d4200));
	background: -webkit-linear-gradient(top, #409a3b 30%, #1d4200 100%);
	background: -o-linear-gradient(top, #409a3b 30%, #1d4200 100%);
	background: -ms-linear-gradient(top, #409a3b 30%, #1d4200 100%);
	background: linear-gradient(to bottom, #409a3b 30%, #1d4200 100%);
	filter: progid: DXImageTransform.Microsoft.gradient( startColorstr='#409a3b',
		endColorstr='#1d4200', GradientType=0);
	display: inline-block;
	float: right;
}

.ApplyButton:hover {
	background: -moz-linear-gradient(top, #38cd51 30%, #037133 100%);
	background: -webkit-gradient(linear, left top, left bottom, color-stop(30%, #38cd51),
		color-stop(100%, #037133));
	background: -webkit-linear-gradient(top, #38cd51 30%, #037133 100%);
	background: -o-linear-gradient(top, #38cd51 30%, #037133 100%);
	background: -ms-linear-gradient(top, #38cd51 30%, #037133 100%);
	background: linear-gradient(to bottom, #38cd51 30%, #037133 100%);
	filter: progid: DXImageTransform.Microsoft.gradient( startColorstr='#38cd51',
		endColorstr='#037133', GradientType=0);
}

.CancelButton {
	cursor: pointer;
	border-width: 0px;
	border-style: dotted;
	border-color: #f9f9f9;
	-webkit-border-radius: 21px;
	-moz-border-radius: 21px;
	border-radius: 21px;
	text-align: center;
	width: 100px;
	height: 40px;
	padding-top: undefinedpx;
	padding-bottom: undefinedpx;
	font-size: 18px;
	font-family: Arial;
	color: #ffffff;
	background: -moz-linear-gradient(top, #e73737 30%, #b70202 100%);
	background: -webkit-gradient(linear, left top, left bottom, color-stop(30%, #e73737),
		color-stop(100%, #b70202));
	background: -webkit-linear-gradient(top, #e73737 30%, #b70202 100%);
	background: -o-linear-gradient(top, #e73737 30%, #b70202 100%);
	background: -ms-linear-gradient(top, #e73737 30%, #b70202 100%);
	background: linear-gradient(to bottom, #e73737 30%, #b70202 100%);
	filter: progid: DXImageTransform.Microsoft.gradient( startColorstr='#e73737',
		endColorstr='#b70202', GradientType=0);
	display: inline-block;
}

.CancelButton:hover {
	background: -moz-linear-gradient(top, #fa4d6f 30%, #dc0427 100%);
	background: -webkit-gradient(linear, left top, left bottom, color-stop(30%, #fa4d6f),
		color-stop(100%, #dc0427));
	background: -webkit-linear-gradient(top, #fa4d6f 30%, #dc0427 100%);
	background: -o-linear-gradient(top, #fa4d6f 30%, #dc0427 100%);
	background: -ms-linear-gradient(top, #fa4d6f 30%, #dc0427 100%);
	background: linear-gradient(to bottom, #fa4d6f 30%, #dc0427 100%);
	filter: progid: DXImageTransform.Microsoft.gradient( startColorstr='#fa4d6f',
		endColorstr='#dc0427', GradientType=0);
}

.inputs {
	height: 55px;
	border-top: 2px solid firebrick;
	padding-top: 15px;
}
</style>

<body>
	<div class="buyBlock">
		<form:form name="confirm" action="${prop.id}" modelAttribute="params" commandName="prop" method="post">
		<div class="titl">${title}</div>

		<table rules="rows" cellspacing=10>
			<tr>
				<td>Тип:</td>
				<c:choose>
					<c:when test="${prop.commBuildingType == 'STALL'}">
						<td class="info" colspan="4">Киоск</td>
					</c:when>
					<c:when test="${prop.commBuildingType == 'VILLAGE_SHOP'}">
						<td class="info" colspan="4">Сельский магазин</td>
					</c:when>
					<c:when test="${prop.commBuildingType == 'STATIONER_SHOP'}">
						<td class="info" colspan="4">Магазин канцтоваров</td>
					</c:when>
					<c:otherwise>
						<td class="info" colspan="4">${prop.commBuildingType}</td>
					</c:otherwise>
				</c:choose>
			</tr>
			<tr>
				<td>Район:</td>
				<c:choose>
					<c:when test="${prop.cityArea == 'GHETTO'}">
						<td class="info" colspan="4">Гетто</td>
					</c:when>
					<c:when test="${prop.cityArea == 'OUTSKIRTS'}">
						<td class="info" colspan="4">Окраина</td>
					</c:when>
					<c:when test="${prop.cityArea == 'CHINATOWN'}">
						<td class="info" colspan="4">Чайнатаун</td>
					</c:when>
					<c:when test="${prop.cityArea == 'CENTER'}">
						<td class="info" colspan="4">Центр</td>
					</c:when>
					<c:otherwise>
						<td class="info" colspan="4">${prop.cityArea}</td>
					</c:otherwise>
				</c:choose>
			</tr>
			<tr>
				<td>Цена:</td>
				<td class="info" colspan="4"><fmt:formatNumber type="number" maxFractionDigits="3"
						value="${prop.purchasePrice}"/>&tridot;</td>
			</tr>
			<tr>
				<td>Прибыль (день):</td>
				<td class="info">от ${data.profitMin}&tridot;</td>
				<td class="info"></td>
				<td class="info">до ${data.profitMax}&tridot;</td>
				<td class="info">(+${percent}% за район)</td>
			</tr>
			<tr>
				<td>Срок полезного использования (нед.):</td>
				<td class="info" colspan="4">${data.usefulLife}</td>
			</tr>
			<tr>
				<td>Баланс после покупки:</td>
				<c:choose>
					<c:when test="${bap >= 0}">
						<td class="info" colspan="4"><fmt:formatNumber type="number" maxFractionDigits="3"
						value="${bap}"/>&tridot;</td>
					</c:when>
					<c:otherwise>
						<td class="credit" colspan="4"><fmt:formatNumber type="number" maxFractionDigits="3"
						value="${bap}"/>&tridot;</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</table>

		<br />
			<div class="inputs">
			<input type="hidden" name="action" id="action">
			
				<input id="ButtonCodePreview" type="button" name="yesno" value="Отмена" class="CancelButton"
					onclick="document.confirm.action.value='cancel'; document.confirm.submit();" /> 
				<input id="ButtonCodePreview" type="button" name="yesno" value="Купить" class="ApplyButton" 
					onclick="document.confirm.action.value='confirm'; document.confirm.submit();"/>
			</div>
		</form:form>
	</div>
</body>

</html>