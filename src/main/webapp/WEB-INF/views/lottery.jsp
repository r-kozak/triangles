<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Лотерея</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/lottery.css" type="text/css" />
<t:template>


<script>
	window.onload = function(){ 
		// при нажатии на одну из кнопок покупки билетов
		$('.buy_label').on('click', function() {
			buyTickets(this);
		});
		
		// при клике на одну из кнопок игры
		$('.play_label').on('click', function() {
			playLoto(this);
		});
		
		// получение мудрости
		$('#predictBlock').on('click', '.predictDiv', function() {
			getPredict();
		});
		
		// получить лицензию на строительство
		$('.plushki_label_lic').on('click', function() {
			useLicense(this.id);
		});
		
		// при клике на плюшку повышения уровня имущества
		$('#up_prop_btn').on('click', function() {
			getPropForLevelUp("prop");
		});
		
		// при клике на плюшку повышения уровня кассы имущества
		$('#up_cash_btn').on('click', function() {
			getPropForLevelUp("cash");
		});
		
		// при клике на подтверждение повышения уровня имущества или кассы
		$('body').on('click', '.confirm_up_btn', function() {
	 		confirmUpLevel(this);
	 	});
		
		if (${ls.page > 1}) {
			$('body, html').scrollTop($(document).height());
		} 
	}

	//функция для анимирования символа "Мудрость всезнающего"
	function animatePredictSign() {
		$( "#predictSign" ).animate({ color: "#FFCACA" }, 3000, function() {
			$( "#predictSign" ).animate({ color: "#F35A30" }, 3000, animatePredictSign())
		} );
	}
</script>

<div class="container">
	<div class="row">
		<t:menu/>
			
		<div class="col-md-9">
			<h3 class="page-header" align=center>Лотерея</h3>
			
			<div class="col-md-12 text-center buy_block">
				<div class="col-md-3">
					<div>Билеты</div>
					<div id="tickets_count" class="buy_label_no_handlers">${ticketsCount}</div>
				</div>
				<div class="col-md-3">
					<div>Купить</div>
					<div class="buy_label notSelectable" id="buy_1">1</div>
					<div class="little_label">${ticketsPrice[0]}&tridot; за 1 билет</div>
				</div>
				<div class="col-md-3">
					<div>Купить</div>
					<div class="buy_label notSelectable" id="buy_10">10</div>
					<div class="little_label">${ticketsPrice[1]}&tridot; за 1 билет</div>
				</div>
				<div class="col-md-3">
					<div>Купить</div>
					<div class="buy_label notSelectable" id="buy_50">50</div>
					<div class="little_label">${ticketsPrice[2]}&tridot; за 1 билет</div>
				</div>
			</div>
			<div class="row"></div> <!-- разделитель -->
			
			<h4 class="page-header" align=center>Играть</h4>
			
			<div class="col-md-12 text-center play_block">
				<div class="col-md-3">
					<div>Играть на</div>
					<div class="play_label notSelectable" id="play_1">1</div>
					<div class="little_label">билет</div>
				</div>
				<div class="col-md-3">
					<div>Играть на</div>
					<div class="play_label notSelectable" id="play_5">≤5</div>
					<div class="little_label">билетов</div>
				</div>
				<div class="col-md-3">
					<div>Играть на</div>
					<div class="play_label notSelectable" id="play_10">≤10</div>
					<div class="little_label">билетов</div>
				</div>
				<div class="col-md-3">
					<div>Играть на</div>
					<div class="play_label notSelectable" id="play_0">ВСЕ</div>
					<div class="little_label">билеты</div>
				</div>
			</div>
			<div class="row"></div> <!-- разделитель -->
			
			<h4 class="page-header" align=center>Выигранные плюшки</h4>
			
			<div class="col-md-12 text-center plushki_block">
				<div class="col-md-3">
					<div>Повышение уровня имущества</div>
					<div id="up_prop_btn" class="plushki_label notSelectable">×${upPropCount}</div>
				</div>
				<div class="col-md-3">
					<div>Повышение уровня кассы имущества</div>
					<div id="up_cash_btn" class="plushki_label notSelectable">×${upCashCount}</div>
				</div>
				<div class="col-md-3">
					<div>Лицензии на строительство</div>
					<div id="useLic2" class="plushki_label_lic notSelectable">ур. 2: ×<span id="lic2CountVal">${lic2Count}</span></div>
					<div id="useLic3" class="plushki_label_lic notSelectable">ур. 3: ×<span id="lic3CountVal">${lic3Count}</span></div>
					<div id="useLic4" class="plushki_label_lic notSelectable">ур. 4: ×<span id="lic4CountVal">${lic4Count}</span></div>
				</div>
				<div class="col-md-3">
					<div>Мудрость всезнающего<br/><br/></div>
					<div id="predictBlock">
						<c:choose>
							<c:when test="${!isPredictionAvailable}">
								<div class="plushki_label predictDiv notSelectable"><span class="glyphicon glyphicon-certificate" 
									style="font-size:98; color:#FFCACA"></span></div>
							</c:when>
							<c:otherwise>
								<div class="plushki_label predictDiv notSelectable"><span id="predictSign" class="glyphicon glyphicon-certificate" 
									style="font-size:98; color:#F35A30"></span></div>
								<script>
									animatePredictSign();
	 							</script>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
	</div> <!-- container.row - строка с лотерейными действиями -->
	
	<div class="row"> 		
			<h4 class="page-header" align=center>История выигрышей</h4>
		
		<div class="col-md-3">
			<form:form id="searchForm" name="searchForm" method="GET" commandName="ls" style="margin-top:0;">
				<div id="searchWrap">
				<div id="menuTitle">Поиск</div>
					<fieldset id = "searchBlock">
					<legend>Период</legend>
						<div id="searchEl">
							<div id="nadp">Начало:</div> <form:input class="dateEl" type="date" path="dateFrom"/>
							<button class="btn btn-default btn-xs" onclick="setDateValue('dateFrom', true)">←</button>
							<button class="btn btn-default btn-xs" onclick="setDateValue('dateFrom', false)">→</button>
						</div>
						<div id="searchEl">
							<div id="nadp">Конец:</div> <form:input class="dateEl" type="date" path="dateTo"/>
						<button class="btn btn-default btn-xs" onclick="setDateValue('dateTo', true)">←</button>
						<button class="btn btn-default btn-xs" onclick="setDateValue('dateTo', false)">→</button>
						</div>
					</fieldset>
		
					<fieldset id="searchBlock">
					<legend>Статья удачи</legend>
						<div id="searchEl">
							<form:checkboxes path="articles" items="${articles}"/>      
						</div>
					</fieldset>
		
					<form:checkbox id="needClear" path="needClear" hidden="true"/>
					<input id="page" path="page" name="page" value="1" hidden="true">
				</div>
				<div id="searchEl">
					<button id="searchSubmit" class="btn btn-primary btn-sm" type="submit" name="submit1">Искать</button>
					<input id="submClear" class="btn btn-danger btn-sm" data-toggle="tooltip" title="Очистить фильтр"  
					    type="button" value="&#10008;" onclick="document.getElementById('needClear').checked = true; document.getElementById('searchForm').submit();"/>
				</div>
			</form:form>
		</div>
		
		<div class="col-md-9">
			<table class="table">
			<tr class="tableTitleTr">
				<td>Дата</td>
				<td>Статья выигрыша</td>
				<td>Описание</td>
				<td>Кол-во выиграно</td>
				<td>Билетов потрачено</td>
			</tr>
			<c:forEach items="${lotteryStory}" var="lotterySt">
				<tr>
					<td><fmt:formatDate value="${lotterySt.date}" pattern="dd-MM-yyyy HH:mm"/></td>
					<td>${lotterySt.article}</td>
					<td style="text-align:left!important">${lotterySt.description}</td>
					<c:choose>
						<c:when test="${lotterySt.article == 'PREDICTION'}">
							<td>1</td>					
						</c:when>
						<c:otherwise>
							<td>${lotterySt.count}</td>
						</c:otherwise>
					</c:choose>
					<td>${lotterySt.ticketCount}</td>
				</tr>
			</c:forEach>
			</table>
		</div>
	</div> <!-- container.row -->
    
    <ul class="pagination" style="float:right">
        ${paginationTag}
    </ul>
	<t:footer></t:footer>
</div> <!-- container -->

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/datatables/1.10.7/js/jquery.dataTables.min.js"></script>

<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); // для отображения подсказок
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
        <button type="button" class="btn btn-default" data-dismiss="modal">Ок :(</button>
      </div>
    </div>
  </div>
</div>

<!-- модальное окно для отображения информации -->
<div class="modal fade" id="modalForInfo" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="modalForInfoTitle">Заголовок</h4>
      </div>
      
	  <div class="modal-body" id="modalForInfoBody">Тело</div>
	  
	  <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Ок :)</button>
      </div>
    </div>
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

<script>
// функция показа ошибки
function showErrorMsg(data) {
	$('#modalErrorBody').html(data.message);
	$('#modalError').modal();
}

// функция при нажатии на кнопки покупки билетов
function buyTickets(buy_btn) {
	var count = buy_btn.id.substring(4); 
	$.ajax({
		  type: 'GET',
		  url: "${pageContext.request.contextPath}/lottery/buy-tickets",
		  data:  { count: count },
		  dataType: "json",
		  async:false
		}).done(function(data) {
			if(data.error) {
				showErrorMsg(data); // показать сообщение с ошибкой
			} else {
				changeBal(data);
				$('#tickets_count').html(data.ticketsValue);
			}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText);
	});
}

// функция игры в лото
function playLoto(play_btn) {
	var count = play_btn.id.substring(5);
	$.ajax({
		  type: 'GET',
		  url: "${pageContext.request.contextPath}/lottery/play-loto",
		  data:  { count: count },
		  dataType: "json",
		  async:false
		}).done(function(data) {
			if(data.error) {
				showErrorMsg(data); // показать сообщение с ошибкой
			} else {
				location = location;
			}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText);
	});
}

//функция получения предсказания
function getPredict() {
	$.get( "${pageContext.request.contextPath}/lottery/get-predict", function(data) {
			if(data.error) {
				// показать сообщение с ошибкой
				var ref = "${pageContext.request.contextPath}/wiki#lo.ap.wi";
				$('#modalForInfoTitle').html('<a href="' + ref + '" target="_blank">Мудрость</a> еще не постигнута');
				$('#modalForInfoBody').html(data.message);
				$('#modalForInfo').modal();
			} else {
				$('#modalForInfoTitle').html("Мудрость №" + data.predictId);
				$('#modalForInfoBody').html(data.predictText);
				$('#modalForInfo').modal();

				$('#predictBlock').html('<div class="plushki_label predictDiv"><span class="glyphicon glyphicon-certificate"' + 
					'style="font-size:98; color:#FFCACA"></span></div>');
			}
		}).fail(function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR.status + " " + jqXHR.statusText);
		});
}

// применение лицензий разных уровней
function useLicense(id) {
	var level = id.substring(6);
	$.get( "${pageContext.request.contextPath}/lottery/use-license", { licLevel: level } )
	  .done(function( data ) {
		  if(data.error) {
			  showErrorMsg(data); // показать сообщение с ошибкой
		  } else {
			$('#modalForInfoTitle').html("Получена лицензия");
			$('#modalForInfoBody').html('Получена лицензия уровня: <b>' + level + "</b><br/>" +
					'Дата окончания лицензии: </b>' + data.licExpire + '</b>');
			$('#modalForInfo').modal();
			
			var currAm = parseInt($('#lic' + level + 'CountVal').text()); // текущее количество
			$('#lic' + level + 'CountVal').html(currAm - 1);
		  }
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText);
	});
}

// показать доступное имущество для повышения уровня или уровня кассы
function getPropForLevelUp(obj) {
	$.ajax({
		  type: 'GET',
		  url: "${pageContext.request.contextPath}/lottery/level-up",
		  data:  { reqObj: obj },
		  dataType: "json",
		  async:false
		}).done(function(data) {
			 if(data.error) {
				  showErrorMsg(data); // показать сообщение с ошибкой
			  } else {
				var table = createTableContent(data, obj);
				
				var objName = "";
				if (obj == 'prop') {
					objName = "Имущество";
				} else if (obj == 'cash') {
					objName = "Кассы";
				}
				$('#modalForInfoTitle').html(objName + " для повышения уровня");
				$('#modalForInfoBody').html(table);			
				$('#modalForInfo').modal();
			  }
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText);
	});
}

// функция подтверждения повышения уровня имущества или кассы
// obj - объект ('property' || 'cash')
// prop_id - id имущества, чей уровень или уровень кассы повышать
function confirmUpLevel(clickedBtn) {
	var obj = clickedBtn.id.substring(0, 4);
	var prop_id = clickedBtn.id.substring(5);
	
			$.ajax({
        		  type: 'POST',
        		  url: "${pageContext.request.contextPath}/lottery/confirm-level-up",
        		  data:  { obj: obj, propId: prop_id },
        		  dataType: "json",
        		  async:false
        		}).done(function(data) {
			if(data.error) {
				showErrorMsg(data); // показать сообщение с ошибкой
				$('#modalForInfo').modal('hide');
			} else {
				// обновить значение уровня в той строке, где была нажата кнопка повышения уровня
				$(clickedBtn).closest('tr').find('.' + obj + 'LevelVal').html(data.currLevel);
				
				// если достигнут последний уровень - убрать кнопку и написать, что достигли последнего
				if(data.currLevel == data.maxLevel) {
					$(clickedBtn).closest('td').html('Посл. уровень');
				}
				
				// изменить значение плюшки
				var currPljushkaVal = parseInt($('#up_' + obj + '_btn').text().substring(1));
				
				console.log('currPljushkaVal=' + currPljushkaVal);
				console.log('obj=' + obj);
				console.log('$(#up_ + obj + _btn).html()=' + $('#up_' + obj + '_btn').html());
				$('#up_' + obj + '_btn').html('×' + (currPljushkaVal - 1));
			}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText);
	});
}

// функция формирования контента диалогового окна при повышении уровня имущества или кассы
function createTableContent(data, obj) {
	var objName = "";
	if (obj == 'prop') {
		objName = "имущества";
	} else if (obj == 'cash') {
		objName = "кассы";
	}
	var tableTitle = '<table class="table"><tr class="tableTitleTr">' +
	'<td>Наименование</td><td>Ур. имущества</td><td>Ур. кассы</td><td>Повысить ур.</td></tr>';
	
	var tableContent = "";
	var props = data.properties;
	for (var i = 0; i < props.length; i++) {
		tableContent += '<tr><td><a href="${pageContext.request.contextPath}/property/' + props[i].id + '" target="_blank">' + props[i].name + '<a/></td>' + 
		'<td class="propLevelVal">' + props[i].level + '</td>' +
		'<td class="cashLevelVal">' + props[i].cashLevel + '</td>' + 
		'<td><button id="' + obj + '_' + props[i].id +  '" class="btn btn-success confirm_up_btn" title="Повысить уровень ' + objName + '" data-toggle="tooltip">' +
			'<span class="glyphicon glyphicon-chevron-up"></span></button></td></tr>'
	}
	tableContent += '</table>';
	
	var table = tableTitle + tableContent;
	return table;
}
</script>
</t:template>