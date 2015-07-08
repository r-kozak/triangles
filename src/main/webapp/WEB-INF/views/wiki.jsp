<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Home</title>
<style>
#contents {
	border: 1px solid;
	width: auto;
	background: #FFFBF6;
	display: inline-block;
	padding: 10 20 0 0;
	margin: 0 20 20 0;
	line-height: 1.7;
	font-size: 75%;
	font-family: "Lucida Console", Monaco, monospace;
}

#contents a {
	color: blue;
	text-decoration: none;
}

#contents_title {
	font-size: 18;
	text-align: center;
	border-bottom: 1px dotted;
	margin-left: 20;
}

.sp_point {
	margin: 20 0 20 0;
	border-bottom: 1px solid gray;
	padding: 0 0 0 20;
	display: -webkit-box;
}
.fs1 {
	font-size: 26;
}
.fs2 {
	font-size: 22;
	text-indent: 1em;
}
.fs3 {
	font-size: 17;
	text-indent: 1.5em;
}

.fixed_button a {
	position: fixed;
	bottom: 50px;
	height: 8;
	right: 50px;
	z-index: 999;
	color: #FF9147;
	border: 2px dotted;
	padding: 10 18 30 18;
	text-decoration: none;
	font-size: 100%;
	background: transparent;
	border-radius: 50%;
}

.fixed_button:hover a {
	color: #FF6900;
	border: 4px dotted;
	font-weight: bold;
}

.text {
	padding: 0 10 10;
	color:#414141;
	background-color:#fafafa;
	border: 1px dashed #c6c6c6;
	text-align:left;
	letter-spacing:1pt;
	word-spacing:3pt;
	font-size:15px;
	font-family:Georgia;
	line-height:23px;
 
}

.wrapper {
	margin: 100 15 0 15
}
h1 {
	margin-bottom: 50;
	border-bottom: 2px solid lightgrey;
}
p { 
    text-indent: 1.5em; /* Отступ первой строки */
    text-align: justify; /* Выравнивание по ширине */
 	margin-top: 10px;   
   }

</style>

<t:template>
	<div class="content">
		<div class="wrapper">
		<h1 align="center">wiki</h1>
			<div class="fixed_button">
				<a href="">↑</a>
			</div>

			<div id="contents">
				<div id="contents_title">Содержание</div>
				<ol>
					<li><a href="#pr">Имущество</a></li>
					<ol type="a">
						<li><a href="#pr.ch">Характеристики</a></li>
							<ul>
								<li><a href="#pr.ch.st">Киоск</a></li>
								<li><a href="#pr.ch.vs">Сельський магазин</a></li>
								<li><a href="#pr.ch.ss">Магазин канцтоваров</a></li>
							</ul>
						<li><a href="#pr.co">Порядок начислений</a></li>
							<ul>
								<li><a href="#pr.co.pr">Прибыль</a></li>
								<li><a href="#pr.co.de">Износ</a></li>
							</ul>
						<li><a href="#pr.ma">Рынок имущества</a></li>
							<ul>
								<li><a href="#pr.ma.bu">Покупка</a></li>
								<li><a href="#pr.ma.se">Продажа</a></li>
							</ul>
						<li><a href="#pr.bu">Стройка</a></li>
							<ul>
								<li><a href="#pr.bu">Типы строителей</a></li>
							</ul>
					</ol>
					<li><a href="#ba">Баланс</a></li>
						<ul>
							<li><a href="#ba.tr">Транзакции</a></li>
							<li><a href="#ba.cd">Кредит/депозит</a></li>
							<li><a href="#ba.so">Состоятельность</a></li>
						</ul>
					<li><a href="#Ghj">Ghj</a></li>
				</ol>
			</div>

			<div id="pr" class="sp_point fs1">Имущество</div>
			<div class="text">
				<p>Имущество является одним из основных источников прибыли. Делится на:</p>
				<ul>
					<li>Коммерческое (приносит прибыль)</li>
						<ul>
							<li>Торговое (магазины, супермаркеты, торговые центры)</li>
							<li>Производственное (заводы, фабрики, комбинаты)</li>
							<li>Сфера услуг</li>
						</ul>
					<li>Личное (влияет на доминантность)</li>
				</ul>
				<p>У имущества есть общие характеристики (минимальная и максимальная прибыль, срок полезного использования...подробности ниже).</p>
				<p>Кроме общих характеристик, есть также характеристики конкретного экземпляра имущества:</p>
				<ul>
					<li>Наименование (можно и нужно менять)</li>
					<li>Уровень (влияет на прибыль)</li>
					<li>Район города (влияет на прибыль)</li>
					<li>Активность (при износе 100% - становится не активным)</li>
					<li>Износ (насчитывается каждую неделю)</li>
					<li>Деньги в кассе</li>
					<li>Уровень кассы (влияет на вместимость)</li>
					<li>Стоимость продажи (зависит от износа)</li>
				</ul>
			</div>
			
			<div id="pr.ch" class="sp_point fs2">Характеристики</div>
			<div class="text">
			<p>Ниже описаны характеристика каждого типа имущества.</p>
			<p>Срок полезного использования = [период_окупаемости_макс * 2]</p>
			<p>Прибыль минимальная = [цена_покупки_мин / (период_окупаемости_макс * 7)]</p>
			<p>Прибыль максимальная = [цена_покупки_мин / (период_окупаемости_мин * 7)]</p>
			<p>Вместимость кассы = прибыть_макс * коэф. уровня (k)</p>
			<table border=1px solid style="text-align:center; margin: auto; border-collapse: collapse;">
				<tr>
					<td><b>Уровень кассы</b></td>
					<td>0</td>
					<td>1</td>
					<td>2</td>
					<td>3</td>
					<td>4</td>
					<td>5</td>
				</tr>
				<tr>
					<td><b>k</b></td>
					<td>1</td>
					<td>${ccl1}</td>
					<td>${ccl2}</td>
					<td>${ccl3}</td>
					<td>${ccl4}</td>
					<td>${ccl5}</td>
				</tr>
			</table>
				<c:if test="${!empty commBuData}">
					<c:forEach items="${commBuData}" var="cbdata">
						<c:choose>
							<c:when test="${cbdata.commBuildType == 'STALL'}">
								<div id="pr.ch.st" class="sp_point fs3">Киоск</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'VILLAGE_SHOP'}">
								<div id="pr.ch.vs" class="sp_point fs3">Сельский магазин</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'STATIONER_SHOP'}">
								<div id="pr.ch.ss" class="sp_point fs3">Магазин канцтоваров</div>
							</c:when>
						</c:choose>
						<ul>
							<li>Сфера деятельности: <b> ${cbdata.buildType}</b></li>
							<li>Период окупаемости, недель: <b>${cbdata.paybackPeriodMin} - ${cbdata.paybackPeriodMax}</b></li>
							<li>Цена покупки: <b>${cbdata.purchasePriceMin} - ${cbdata.purchasePriceMax}</b></li>
							<li>Срок полезного использования, недель: <b>${cbdata.usefulLife}</b></li>
							<li>Прибыль, в день: <b>${cbdata.profitMin} - ${cbdata.profitMax}</b></li>
							<li>Срок пребывания на рынке, дней: <b>${cbdata.remTermMin} - ${cbdata.remTermMax}</b></li>
							<li>Время на постройку, дней: <b>${cbdata.buildTime}</b></li>
							<li>Вместимость кассы:
								<table border=1px solid style="display:inline;">
									<tr>
										<c:forEach items="${cbdata.cashCapacity}" var="cashcap">
											<td>${cashcap}</td>
										</c:forEach>
									</tr>
								</table>
							</li>
						</ul>
					</c:forEach>
				</c:if>
			</div>
		</div>
</t:template>
