<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Рынок недвижимости</title>

<head>
<style>
.buyBlock {
	padding: 10px 40px 10px 40px;
}

.buyBlock td.credit {
	font-size: 16px;
	color: red;
	font-weight: bold;
	width: auto;
	height: 30px;
}
</style>

	<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
	<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
	<script type="text/javascript">
		$(function () {
			$("#price-slider").slider({
				range: true,
				min: <c:out value='${reps.priceMin}'/>,
				max: <c:out value='${reps.priceMax}'/>,
				values: [<c:out value='${reps.priceFrom}'/>, <c:out value='${reps.priceTo}'/>],
				slide: function (event, ui) {
					$("#pr_from").val(ui.values[0]);
					$("#pr_to").val(ui.values[1]);
					$("#pr_lab_fr").val(ui.values[0]);
					$("#pr_lab_to").val(ui.values[1]);
				}
			});
			$("#pr_lab_fr").val($("#price-slider").slider("values", 0));
			$("#pr_lab_to").val($("#price-slider").slider("values", 1));
		});
		
		document.ready = function(){ 
			$(".buy_btn").on('click', function(event) {
				var clicked_btn = $(this);
				var propId = clicked_btn.attr('id');
				
	        	$.ajax({
	        		  type: 'POST',
	        		  url: "${pageContext.request.contextPath}/property/buy",
	        		  data:  { propId: propId, action: "info" },
	        		  dataType: "json",
	        		  async:true
	        		}).done(function(data) {
	        			$('#modal_confirm').unbind('click'); // удалим все обработчики события 'click' у элемента modal_confirm
	        			
	        			if(data.error) {
	        				$('#modalWindowTitle').html("Ошибка"); // задать заголовок модального окна
	        				$('#modalWindowBody').html('<div id="errorMessg">');
	           				$("#errorMessg").html('<h5>' + data.message + '</h5>');
	           				$('#modal_confirm').attr('disabled', true); // сделать кнопку "Купить" недоступной
	           				$('#modalWindow').modal(); // показать модальное окно
	           				
	           				hideBuyBtn(clicked_btn, "Ошибка"); // удалить кнопку Buy в таблице
	           			} else {
	           				var usedText = ""; // текст будет отображен, если имущество б/у
	           				if (data.condition == "б/у") {
	           					usedText = '<tr><td><b>Уровень:</b></td> <td>' + data.propLevel + '</td></tr>' +
								'<tr><td><b>Уровень кассы:</b></td> <td>' + data.cashLevel + '</td></tr>' +
								'<tr><td><b>Износ:</b></td> <td>' + data.depreciation + '%</td></tr>';
							}
							$('#modalWindowBody').html('<div class="buyBlock">' +
									'<table class="table">' + 
									'<tr><td><b>Номер заказа:</b></td> <td>' + data.propId + '</td></tr>' +
									'<tr><td><b>Состояние:</b></td> <td>' + data.condition + '</td></tr>' +
									'<tr><td><b>Тип:</b></td> <td>' + data.buildType + '</td></tr>' +
									'<tr><td><b>Район:</b></td> <td>' + data.cityArea + '</td></tr>' +
									usedText + 
									'<tr><td><b>Цена:</b></td><td>' + data.price + '&tridot;</td></tr>' +
									'<tr><td><b>Баланс после покупки:</b></td><td><span id="newBalLab">' + data.newBalance + '&tridot;</span></td></tr>' +
									'</table></div>');
							
							if (data.newBalance >= 0) {
								$('#newBalLab').addClass('text-success');
							} else {
								$('#newBalLab').addClass('text-danger');
							}
						
							$('#modalWindowTitle').html(data.title); // задать заголовок модального окна
							$('#modal_confirm').attr('disabled', false); // сделать кнопку "Купить" доступной
							$('#modalWindow').modal(); // показать модальное окно
							    
							// назначить обработчик кнопке modal_confirm (кнопка модального окна)
						    $('#modal_confirm').on('click', function() {
						    	sendPostConfirmBuy(propId, clicked_btn);
						  	});
	           			}
	        		});
	            return false;
	        });
		}; // document.ready()
		
		// фукнция отправки подтверждения покупки (id имущества, нажатая кнопка "Купить")
		function sendPostConfirmBuy(propId, clicked_btn) {
			$.ajax({
      		  type: 'POST',
      		  url: "${pageContext.request.contextPath}/property/buy",
      		  data:  { propId: propId, action: "confirm" },
      		  dataType: "json",
      		  async:true
      		}).done(function(data) {
      			$('#modal_confirm').unbind('click'); // удалим все обработчики события 'click' у элемента modal_confirm

      			if(data.error) {
      				$('#modalWindowTitle').html("Ошибка"); // задать заголовок модального окна
    				$('#modalWindowBody').html('<div id="errorMessg">');
       				$("#errorMessg").html('<h5>' + data.message + '</h5>');
       				$('#modal_confirm').attr('disabled', true); // сделать кнопку "Купить" недоступной

					hideBuyBtn(clicked_btn, "Ошибка"); // удалить кнопку Buy в таблице
       			} else {
				    $('#modalWindow').modal('hide'); // скрыть модальное окно
					changeBal(data); //изменить значение баланса
					hideBuyBtn(clicked_btn, "Куплено"); // удалить кнопку Buy
       			}
      		});
		};
		
		// функция скрывает нажатую кнопку BUY (в случае ошибки или удачной покупки)
		// clicked_btn - нажатая кнопка
		// text - текст в ячейке, будет вместо кнопки clicked_btn
		function hideBuyBtn(clicked_btn, text) {
		    //при удачной покупке или 
			$('#'+clicked_btn.attr('aria-describedby')).remove(); // удалить подсказку к кнопке покупки
			clicked_btn.closest("td").html('<p class="text-danger"><b>' + text +'</b></span>'); // в ячейку поместить текст "Куплено"
			clicked_btn.remove(); // удалить кнопку покупки
		}
	</script>
</head>
<t:template>
<div class="container">
	<div class="row">
		<t:menu>
			<form:form id="searchForm" method="GET" commandName="reps">
				<div id="searchWrap">
				<div id="menuTitle">Поиск</div>
					<fieldset id = "searchBlock">
					<legend>Размещение начало</legend>
						<div id="searchEl">
							<div id="nadp">Начало:</div> <form:input class="dateEl" type="date" path="appearDateFrom"/>
						</div>
						<div id="searchEl">
							<div id="nadp">Конец:</div> <form:input class="dateEl" type="date" path="appearDateTo"/>
						</div>
					</fieldset>
					
					<fieldset id = "searchBlock">
					<legend>Размещение конец</legend>
						<div id="searchEl">
							<div id="nadp">Начало:</div> <form:input class="dateEl" type="date" path="lossDateFrom"/>
						</div>
						<div id="searchEl">
							<div id="nadp">Конец:</div> <form:input class="dateEl" type="date" path="lossDateTo"/>
						</div>
					</fieldset>
					
					<fieldset id = "searchBlock"> 
					<legend>Район</legend>
						<div id="searchEl">
							<form:checkboxes path="areas" items="${areas}"/>      
						</div>
					</fieldset>
					
					<fieldset id = "searchBlock"> 
					<legend>Тип</legend>
						<div id="searchEl">
							<form:checkboxes path="types" items="${types}"/>      
						</div>
					</fieldset>
					
					<fieldset id = "searchBlock">
					<legend>Цена, &tridot;</legend>
						<div id="searchEl">
							<input type="text" class="value_lab" id="pr_lab_fr" readonly>
							<input type="text" class="value_lab" id="pr_lab_to" readonly style="float:right; text-align:right">
							<div class="slider" id="price-slider"></div>
							
							<form:input id="pr_from" class="textInp2" hidden="true" path="priceFrom"></form:input>
							<form:input id="pr_to" class="textInp2"  hidden="true" path="priceTo"></form:input>
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
				<h3 class="page-header" align=center>Рынок недвижимости</h3>
	
				<c:if test="${empty proposals && marketEmpty}">
					<div class="noData">
						Предложений нет. Вернитесь домой и приходите через минуту. 
						<a href="${pageContext.request.contextPath}/home">ДОМОЙ</a>
					</div>
				</c:if>
				<c:if test="${empty proposals && !marketEmpty}">
					<div class = "noData">Поиск не дал результатов. Попробуйте задать другие параметры.</div>
				</c:if>
	
				<c:if test="${!empty proposals}">
				<div class="panel panel-default">
					<div class="panel-heading">
					    <button id="descr" class="btn btn-default btn-lg" data-toggle="tooltip"  data-toggle="collapse" data-target="#pr_descr" 
					     title="Показать или скрыть подробное описание раздела Рынок имущества"><span class="glyphicon glyphicon-info-sign"></span></button>
					</div>
					<div class="panel-body collapse" id="pr_descr">
						<p><a href="${pageContext.request.contextPath}/wiki#pr.ma">Рынок имущества</a> - это раздел, где можно купить коммерческое
						имущество (магазины, супермаркеты, заводы, фабрики и т.д.). Рынок является глобальным, если вы купили имущество, для других
						игроков оно станет недоступным.</p>	
					</div>
				</div>
					<table id="prop_table" class="table table-striped">
						<thead>
							<tr class="tableTitleTr" style="font-weight:bold">
								<td>Тип</td>
								<td>Район города</td>
								<td>Размещение</td>
								<td>Конец размещения</td>
								<td>Уровень</td>
								<td>Уровень кассы</td>
								<td>Износ, %</td>
								<td>Цена, &tridot;</td>
								<td>Купить</td>
							</tr>
						</thead>
						<tbody>
					<c:forEach items="${proposals}" var="prop">
							<c:choose>
								<c:when test="${prop.usedId != 0}">
									<tr class="warning text-danger">
								</c:when>
								<c:otherwise>
									<tr>
								</c:otherwise>
							</c:choose>
								
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
	
							<td><fmt:formatDate value="${prop.appearDate}" pattern="dd-MM-yyyy HH:mm" /></td>
							<td><fmt:formatDate value="${prop.lossDate}" pattern="dd-MM-yyyy HH:mm" /></td>
							<td>${prop.propLevel}</td>
							<td>${prop.cashLevel}</td>
							<td>${prop.depreciation}</td>
							<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.purchasePrice}" /></td>
							<td align="center">
								<button id=${prop.id} class="btn btn-danger btn-lg buy_btn" title="Купить имущество" data-toggle="tooltip">
								    <span class="glyphicon glyphicon-shopping-cart"></span></button>
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</c:if>
			</div>
	</div>	
</div> <!-- container -->
	
	<div id="balChan">
		<c:if test="${changeBal.length() > 0}">
			${changeBal}&tridot;
			<script>
				popUp("<c:out value='${changeBal}'/>", "#balChan");
			</script>
		</c:if>
	</div>
	
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/datatables/1.10.7/js/jquery.dataTables.min.js"></script>

<!-- Сортировка даты -->
<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.4/moment.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/datatables-plugins/1.10.7/sorting/datetime-moment.js"></script>

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
    
    //красивая табличка
    $.fn.dataTable.moment('DD-MM-YYYY HH:mm'); // сортировка даты в табличке
    $('#prop_table').dataTable( { // сделать сортировку, пагинацию, поиск
        "order": [[ 0, "asc" ]]
    } );
});
</script>

<!-- модальное окно для отображения информации о покупке -->
<div class="modal fade" id="modalWindow" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="modalWindowTitle">Заголовок</h4>
      </div>
      <div class="modal-body" id="modalWindowBody">
	        Тело
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
        <button id="modal_confirm" type="button" class="btn btn-success"><span id="text_modal_confirm">Купить</span></button>
      </div>
    </div>
  </div>
</div>
</t:template>