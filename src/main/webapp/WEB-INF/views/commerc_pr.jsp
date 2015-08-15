<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
	/*наименование имущества*/
	#name {
		width:185;
	}
</style>

<head>
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
	<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
	<script type="text/javascript">
		$(function () {
			$("#depreciation-slider").slider({
				range: true,
				min: <c:out value='${cps.depreciationMin}'/>,
				max: <c:out value='${cps.depreciationMax}'/>,
				values: [<c:out value='${cps.depreciationFrom}'/>, <c:out value='${cps.depreciationTo}'/>],
				slide: function (event, ui) {
					$("#depr_from").val(ui.values[0]);
					$("#depr_to").val(ui.values[1]); 
					$("#depr_lab_fr").val(ui.values[0]);
					$("#depr_lab_to").val(ui.values[1]);
				}
			});
			$("#depr_lab_fr").val($("#depreciation-slider").slider("values", 0));
			$("#depr_lab_to").val($("#depreciation-slider").slider("values", 1));
		});
		$(function () {
			$("#sell_price-slider").slider({
				range: true,
				min: <c:out value='${cps.sellPriceMin}'/>,
				max: <c:out value='${cps.sellPriceMax}'/>,
				values: [<c:out value='${cps.sellPriceFrom}'/>, <c:out value='${cps.sellPriceTo}'/>],
				slide: function (event, ui) {
					$("#sell_pr_from").val(ui.values[0]);
					$("#sell_pr_to").val(ui.values[1]);
					$("#sell_pr_lab_fr").val(ui.values[0]);
					$("#sell_pr_lab_to").val(ui.values[1]);
				}
			});
			$("#sell_pr_lab_fr").val($("#sell_price-slider").slider("values", 0));
			$("#sell_pr_lab_to").val($("#sell_price-slider").slider("values", 1));
		});
	</script>
</head>

<title>Коммерческое имущество</title>
<t:template>
<div class="content">
<t:menu>
	<form:form id="searchForm" method="GET" commandName="cps">
		<div id="searchWrap">
		<div id="menuTitle">Поиск</div>
			<fieldset id = "searchBlock">
				<form:input class="textInp" type="text" path="name" placeholder="Наименование"></form:input>
			</fieldset>
			
			<fieldset id = "searchBlock"> 
			<legend>Тип</legend>
				<div id="searchEl">
					<form:checkboxes path="types" items="${types}"/>      
				</div>
			</fieldset>
			
			<fieldset id = "searchBlock">
			<legend>Цена продажи, &tridot;</legend>
				<div id="searchEl">
					<input type="text" class="value_lab" id="sell_pr_lab_fr" readonly>
					<input type="text" class="value_lab" id="sell_pr_lab_to" readonly style="float:right; text-align:right">
					<div class="slider" id="sell_price-slider"></div>
					
					<form:input id="sell_pr_from" class="textInp2" hidden="true" path="sellPriceFrom"></form:input>
					<form:input id="sell_pr_to" class="textInp2"  hidden="true" path="sellPriceTo"></form:input>
				</div>
			</fieldset>
			
			<fieldset id = "searchBlock">
			<legend>Износ, %</legend>
				<div id="searchEl">
					<input type="text" class="value_lab" id="depr_lab_fr" readonly>
					<input type="text" class="value_lab" id="depr_lab_to" readonly style="float:right; text-align:right">
					<div class="slider" id="depreciation-slider"></div>
					
					<form:input id="depr_from" hidden="true" path="depreciationFrom"></form:input>
					<form:input id="depr_to" hidden="true" path="depreciationTo"></form:input>
				</div>
			</fieldset>

			<form:checkbox id="needClear" path="needClear" hidden="true"/>
			<input id="page" path="page" name="page" value="1" hidden="true" >
		</div>
		<div id="searchEl">
			<button id="searchSubmit" class="btn btn-primary btn-sm" type="submit" name="submit1">Искать</button>
			<input id="submClear" class="btn btn-danger btn-sm" data-toggle="tooltip" title="Очистить фильтр"  type="button" value="&#10008;" onclick="document.getElementById('needClear').checked = true; document.getElementById('searchForm').submit();"/>
		</div>
	</form:form>
</t:menu>

<div class="tranBlock">
			<h1 align="center">Коммерческое имущество</h1>
			<c:if test="${empty comProps && !userHaveProps}">
				<div class = "noData">У вас нет имущества. Его можно купить на рынке. <a href = "${pageContext.request.contextPath}/property/r-e-market">РЫНОК</a></div>
			</c:if>
			<c:if test="${empty comProps && userHaveProps}">
				<div class = "noData">Поиск не дал результатов. Попробуйте задать другие параметры.</div>
			</c:if>
			
			<c:if test="${!empty comProps}">
				<div class="panel panel-default">
					<div class="panel-heading">
					     <button id="profit_from_all_btn" class="btn btn-default btn-lg" data-toggle="tooltip" title="Собрать прибыль со всего имущества" >
					     <span class="glyphicon glyphicon-piggy-bank" aria-hidden="true"></span></button>
					     	
					    <button id="descr" class="btn btn-default btn-lg" data-toggle="tooltip" data-toggle="collapse" data-target="#pr_descr" 
					     title="Показать или скрыть подробное описание раздела Коммерческое имущество"><span class="glyphicon glyphicon-info-sign"></span></button>
					</div>
					<div class="panel-body collapse" id="pr_descr">
						<p><a href="${pageContext.request.contextPath}/wiki#pr">Коммерческое имущество</a> - это раздел, где можно посмотреть всё коммерческое
						имущество, которое принадлежит вам. Коммерческое имущество можно купить на <a href="${pageContext.request.contextPath}/r-e-market">рынке.</a>
						Каждые сутки по каждому имуществу насчитывается <a href="${pageContext.request.contextPath}/wiki#pr.co.pr">прибыль</a>. 
						Каждую неделю насчитывается <a href="${pageContext.request.contextPath}/wiki#pr.co.de">износ</a>.</p>	
					</div>
				</div>
				<table class="table table-striped" id="prop_table">
				<thead>
					<tr class="info">
						<td style="text-align:center">Тип</td>
						<td style="text-align:center">Наименование</td>
						<td style="text-align:center">Уровень</td>
						<td style="text-align:center">Уровень кассы</td>
						<td style="text-align:center">Район</td>
						<td style="text-align:center">Цена продажи, &tridot;</td>
						<td style="text-align:center">Износ, %</td>
						<td style="text-align:center">Касса, &tridot;</td>
						<td style="text-align:center">Собрать прибыль</td>
					</tr>
				<thead>
				<tbody>
					<c:forEach items="${comProps}" var="prop">
						<tr>
							<c:choose>
								<c:when test="${prop.commBuildingType == 'STALL'}">
									<td style="text-align:left">Киоск</td>
								</c:when>
								<c:when test="${prop.commBuildingType == 'VILLAGE_SHOP'}">
									<td style="text-align:left">Сельский магазин</td>
								</c:when>
								<c:when test="${prop.commBuildingType == 'STATIONER_SHOP'}">
									<td style="text-align:left">Магазин канцтоваров</td>
								</c:when>
								<c:otherwise>
									<td>${prop.commBuildingType}</td>
								</c:otherwise>
							</c:choose>
	
							<td style="text-align:left"><a class="bg-info" href="${pageContext.request.contextPath}/property/${prop.id}">${prop.name}</a></td>
							<td style="text-align:center">${prop.level}</td>
							<td style="text-align:center">${prop.cashLevel}</td>
													<!-- Район -->
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

							<td style="text-align:center"><fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.sellingPrice}"/></td>
							<td>
								<div style="text-align:center">${prop.depreciationPercent}</div>
								<div class="progress">
  									<div class="progress-bar progress-bar-striped" role="progressbar" aria-valuenow="${prop.depreciationPercent}" aria-valuemin="0" aria-valuemax="100" 
  										style="width: ${prop.depreciationPercent}%;"></div>
								</div>
							</td>
							<td>
								<div style="text-align:center">
									<div>
										<fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.cash}"/> / 
										<fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.cashCapacity}"/></div>
									</div>
								<div class="progress">
								  <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${prop.cash}" aria-valuemin="0" aria-valuemax="${prop.cashCapacity}" 
								  		style="width: ${prop.cash / prop.cashCapacity * 100}%;"></div> 
								</div>
							</td>
							<td style="text-align:center">
								<c:if test="${prop.cash > 0}">
										<button class="btn btn-danger btn-lg" title="Собрать прибыль" data-toggle="tooltip" 
										onclick="window.location.replace('${pageContext.request.contextPath}/property/get-cash/${prop.id}')">
										<span class="glyphicon glyphicon-piggy-bank"></span></button>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
				</table>
			</c:if>
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

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/datatables/1.10.7/js/jquery.dataTables.min.js"></script>

<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); // для отображения подсказок
    $('[data-toggle="collapse"]').collapse(); // свернуть блок с описанием
    
    //по клику на кнопку "Описание" - показать или скрыть описание
    $("#descr").on("click",
    		function(){
    			$("#pr_descr").collapse('toggle');
    		}
    	);
    
    // по клику на кнопку Собрать всё - пройти по ссылке
    $("#profit_from_all_btn").on('click', function() {
    	window.location.replace("${pageContext.request.contextPath}/property/get-cash/0");
    });
    
    //если нет имущества с НЕ собранным доходом - сделать кнопку "Собрать всё" не активной
    var noReady = ${ready <= 0};
    if (noReady == true) {
		$("#profit_from_all_btn").attr('disabled', true);
	}
    
    //красивая табличка
    $('#prop_table').dataTable( { // сделать сортировку, пагинацию, поиск
        "order": [[ 7, "desc" ]] // сортировка по деньгам в кассе
    } );
});
</script>

</t:template>