<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

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

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/trade_property.css" type="text/css" />
<title>Торговое имущество</title>
<t:template>
<div class="container">
	<div class="row">
		<t:menu>
			<form:form id="searchForm" method="GET" commandName="cps">
				<div id="searchWrap">
						
				<div id="menuTitle">Поиск</div>
					<fieldset id = "searchBlock"> 
					<legend>Количество на странице</legend>
					    <form:select id="selectEl" path="rowsOnPage" onchange="$('#searchForm').submit()">
							<form:option value="12" label="12"/>
							<form:options items="${rowsOnPage}" />
						</form:select>
					</fieldset>
						
					<fieldset id = "searchBlock">
						<form:input class="textInp" type="text" path="name" placeholder="Наименование"></form:input>
					</fieldset>
					
					<fieldset id = "searchBlock"> 
					<legend>Тип</legend>
						<div id="searchEl" class="buildings_types">
							<form:checkboxes path="types" items="${types}"/>      
						</div>
					</fieldset>
					
					<fieldset id = "searchBlock"> 
					<legend>Район</legend>
						<div class="city_areas" id="searchEl">
							<form:checkboxes path="areas" items="${areas}"/>      
						</div>
					</fieldset>
					
					<fieldset id = "searchBlock"> 
					<legend>Состояние</legend>
						<div class="radio">
							<label><form:radiobutton path="state" value="all"></form:radiobutton>все</label>
						</div>
						<div class="radio">
							<label><form:radiobutton path="state" value="on_sale"></form:radiobutton>на продаже</label>
						</div>
						<div class="radio">
							<label><form:radiobutton path="state" value="not_on_sale"></form:radiobutton>не на продаже</label>
						</div>
					</fieldset>
					
					<fieldset id="searchBlock">
					<legend>Цена продажи, &tridot;</legend>
						<div id="searchEl">
							<input type="text" class="value_lab" id="sell_pr_lab_fr" readonly>
							<input type="text" class="value_lab" id="sell_pr_lab_to" readonly style="float:right; text-align:right">
							<div class="slider" id="sell_price-slider"></div>
							
							<form:input id="sell_pr_from" class="textInp2" hidden="true" path="sellPriceFrom"></form:input>
							<form:input id="sell_pr_to" class="textInp2"  hidden="true" path="sellPriceTo"></form:input>
						</div>
					</fieldset>
					
					<fieldset id="searchBlock">
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

		<div class="col-md-9">
			<h3 class="page-header" align=center>Торговое имущество</h3>
      
            <h4 class="page-header" align=center>Земельные участки</h4>
      
            <div class="col-md-12 text-center land_lots_block">
              <div class="col-md-3">
                <div id="land_lot_ghetto" class="land_lot_block">
                  <div class="notSelectable">Гетто: ${landLotGhettoBusy}/${landLotGhettoTotal}
                    <button id="info" class="btn btn-default btn-xs"><span class="glyphicon glyphicon-info-sign"></span></button> 
                  </div>
                  <a id="buy" class="notSelectable">КУПИТЬ</a>
                </div>
              </div>
              
              <div class="col-md-3">
                <div id="land_lot_outskirts" class="land_lot_block">
                  <div class="notSelectable">Окраина: ${landLotOutskirtsBusy}/${landLotOutskirtsTotal}
                     <button id="info" class="btn btn-default btn-xs"><span class="glyphicon glyphicon-info-sign"></span></button> 
                  </div>
                  <a id="buy" class="notSelectable">КУПИТЬ</a>
                </div>
              </div>
              
              <div class="col-md-3">
                <div id="land_lot_chinatown" class="land_lot_block">
                  <div class="notSelectable">Чайнатаун: ${landLotChinatownBusy}/${landLotChinatownTotal}
                    <button id="info" class="btn btn-default btn-xs"><span class="glyphicon glyphicon-info-sign"></span></button> 
                  </div>
                  <a id="buy" class="notSelectable">КУПИТЬ</a>
                </div>
              </div>
              
              <div class="col-md-3">
                <div id="land_lot_center" class="land_lot_block">
                  <div class="notSelectable">Центр: ${landLotCenterBusy}/${landLotCenterTotal}
                    <button id="info" class="btn btn-default btn-xs"><span class="glyphicon glyphicon-info-sign"></span></button>
                  </div>
                  <a id="buy" class="notSelectable">КУПИТЬ</a>
                </div>                
              </div>
            </div>
            <div class="row"></div> <!-- разделитель -->
      
			<c:if test="${empty tradeProps && !userHaveProps}">
				<div class = "noData">У вас нет имущества. Его можно купить на рынке или построить. 
					<a href = "${pageContext.request.contextPath}/property/r-e-market">РЫНОК</a>
					<a href = "${pageContext.request.contextPath}/building">СТРОИТЬ</a>
				</div>
			</c:if>
			<c:if test="${empty tradeProps && userHaveProps}">
				<div class = "noData">Поиск не дал результатов. Попробуйте задать другие параметры.</div>
			</c:if>
			
			<c:if test="${!empty tradeProps}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<button id="descr" class="btn btn-default btn-lg" data-toggle="tooltip" data-toggle="collapse" data-target="#pr_descr" 
					       title="Показать или скрыть подробное описание раздела Торговое имущество"><span class="glyphicon glyphicon-info-sign"></span></button>
					       
					     <button id="profit_from_all_btn" class="btn btn-default btn-lg" data-toggle="tooltip" title="Собрать прибыль со всего имущества" >
					       <span class="glyphicon glyphicon-piggy-bank" aria-hidden="true"></span></button>
					     	
					     <button id="sell_all_btn" class="btn btn-default btn-lg" data-toggle="tooltip" title="Продать или отменить продажу выбранного имущества" >
					       <span class="glyphicon glyphicon-briefcase" aria-hidden="true"></span></button>
					       
					     <button id="multiple_level_up_btn" class="btn btn-default btn-lg" data-toggle="tooltip" title="Повысить уровень до максимально возможного" >
					       <span class="glyphicon glyphicon-menu-up" aria-hidden="true">И</span></button>
					       
					     <button id="multiple_cash_up_btn" class="btn btn-default btn-lg" data-toggle="tooltip" title="Повысить уровень кассы до максимально возможного" >
					       <span class="glyphicon glyphicon-menu-up" aria-hidden="true">К</span></button>
					</div>
					<div class="panel-body collapse" id="pr_descr">
						<p><a href="${pageContext.request.contextPath}/wiki#pr">Торговое имущество</a> - это раздел, где можно посмотреть всё торговое
						имущество, которое принадлежит вам. Торговое имущество можно купить на <a href="${pageContext.request.contextPath}/r-e-market">рынке</a>
						или построить на  <a href="${pageContext.request.contextPath}/building">стройке.</a>
						Каждые сутки по каждому имуществу насчитывается <a href="${pageContext.request.contextPath}/wiki#pr.co.pr">прибыль</a>. 
						Каждую неделю насчитывается <a href="${pageContext.request.contextPath}/wiki#pr.co.de">износ</a>.
						У имущества или у его кассы можно <a href="${pageContext.request.contextPath}/wiki#lu">повышать уровень</a>, а также
						<a href="${pageContext.request.contextPath}/wiki#pr.co.re">ремонтировать</a></p>
					</div>
				</div>
				<table class="table table-striped" id="prop_table">
				<thead>
					<tr class="tableTitleTr">
						<td style="text-align:center"><input id="select_all" type="checkbox"></td>
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
					<c:forEach items="${tradeProps}" var="prop">
							<c:choose>
								<c:when test="${prop.onSale}">
									<tr class="warning text-danger">
								</c:when>
								<c:otherwise>
									<tr>
								</c:otherwise>
							</c:choose>
							
							<td><input id="${prop.id}" class="select_prop" type="checkbox"></td>
							<td style="text-align:left" class="building_type_name">${prop.tradeBuildingType}</td>
							<td style="text-align:left"><a class="bg-info" href="${pageContext.request.contextPath}/property/${prop.id}">${prop.name}</a></td>
							<td style="text-align:center">${prop.level}</td>
							<td style="text-align:center">${prop.cashLevel}</td>
							<td class="city_area_name">${prop.cityArea}</td>
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
										onclick="window.location.replace('${pageContext.request.contextPath}/property/get-cash/${prop.id}?redirectAddress=property/trade-property')">
										<span class="glyphicon glyphicon-piggy-bank"></span></button>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
				</table>
			</c:if>
			
			<ul class="pagination" style="float:right">
        		${paginationTag}
   			</ul>
		</div>
	</div>
	<t:footer></t:footer>
</div> <!-- container -->

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
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/enum_types/buildings_types.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/enum_types/city_areas_types.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/trade_property.js"></script>

<script>
$(document).ready(function(){
	var selected = new Array();
	
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
    	window.location.replace("${pageContext.request.contextPath}/property/get-cash/0?redirectAddress=property/trade-property");
    });
    
    //если нет имущества с НЕ собранным доходом - сделать кнопку "Собрать всё" не активной
    var noReady = ${ready <= 0};
    if (noReady == true) {
		$("#profit_from_all_btn").attr('disabled', true);
	}
    
    $("#sell_all_btn").attr('disabled', true); // неактивная кнопка 'Продать все'
    $("#multiple_level_up_btn").attr('disabled', true); // неактивная кнопка 'Повысить уровень имуществ' 
    $("#multiple_cash_up_btn").attr('disabled', true); // неактивная кнопка 'Повысить уровень касс' 
    
    // при нажатии на чекбокс "Выбрать все"
    $('#select_all').on('change', function() {
    	var checked = document.getElementById('select_all').checked;
    	
    	if (checked) {
			$('.select_prop').prop('checked', true);
			refreshSelected();
		} else {
			$('.select_prop').prop('checked', false);
			refreshSelected();
		}
    });
    
    // при нажатии на один из чекбоксов имущества
    $('.select_prop').on('change', function() {
	    refreshSelected();
    });
    
    // обновляет массив в выбранным имуществом
    function refreshSelected() {
    	selected = new Array();
    	var count = 0;
		$("input:checkbox[class=select_prop]:checked").each(function() {
	        selected.push($(this).attr('id'));
	        count++;
	    });
		if (count > 0) {
			$("#sell_all_btn").attr('disabled', false);	
			$("#multiple_level_up_btn").attr('disabled', false);
			$("#multiple_cash_up_btn").attr('disabled', false); 
		} else {
			$("#sell_all_btn").attr('disabled', true);
			$("#multiple_level_up_btn").attr('disabled', true);
			$("#multiple_cash_up_btn").attr('disabled', true); 
		}
    }
    
    $("#sell_all_btn").on('click', function() {
    	sendSellPost(selected, "${pageContext.request.contextPath}/property/sell", false);
    	location = location;
    });
    
    $("#multiple_level_up_btn").on('click', function() {
    	multipleLevelUp('prop');
    });
    
    $("#multiple_cash_up_btn").on('click', function() {
    	multipleLevelUp('cash');
    });
    
    function multipleLevelUp(obj) {
		$.ajax({
			  type: 'GET',
			  url: "${pageContext.request.contextPath}/property/multiple-level-up",
			  data:  { propIds: JSON.stringify(selected), obj: obj},
			  dataType: "json",
			  async:false
			}).done(function(data) {
				location = location;
			}).fail(function(jqXHR, textStatus, errorThrown) {
				alert(jqXHR.status + " " + jqXHR.statusText);
			});
    };
});
</script>

<!-- модальное окно для отображения ошибки -->
<div class="modal fade" id="modalError" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title text-danger" id="modalErrorTitle">Ошибка</h4>
      </div>
      
    <div class="modal-body" id="modalErrorBody">Тело</div>
    
    <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Ок</button>
      </div>
    </div>
  </div>
</div>

<!-- модальное окно для отображения информации о зданиях на участках -->
<div class="modal fade" id="modalForInfo" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="modalForInfoTitle">Имущество на участках</h4>
      </div>
      
    <div class="modal-body" id="modalForInfoBody">Тело</div>
    
    <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Ок</button>
      </div>
    </div>
  </div>
</div>

<!-- модальное окно для покупки участка -->
<div class="modal fade" id="modalForBuy" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="modalForInfoTitle">Купить участок</h4>
      </div>
      
    <div class="modal-body" id="modalForBuyBody">Тело</div>
    
    <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Отмена</button>
        <button id="buy_l_lot_btn" type="button" class="btn btn-success">Купить</button>
      </div>
    </div>
  </div>
</div>

</t:template>