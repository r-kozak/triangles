<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>${prop.name}</title>

<style>
#defaultCountdown {
  width: 200;
  margin-left:auto;
  margin-right: auto;
}
.table tr td {
	text-align: center !important;
}
#modalWindowBody {
	font-size: 16px;
}
</style>
<script>
window.onload = function(){ 
    //repair pop up
    $(function() {
        $("#repairBut").on('click', function(event) {
        	$.ajax({
        		  type: 'POST',
        		  url: "${pageContext.request.contextPath}/property/repair",
        		  data:  { propId: <c:out value='${prop.id}'/>, type: "info" },
        		  dataType: "json",
        		  async:true
        		}).done(function(data) {
  					var messageBlock = (!data.zeroSolvency) ? '#infoMessg' : '#errorMessg';
  					
					$('#modalWindowBody').html('<div id="infoMessg"></div>'+
					        		'<div id="errorMessg">');
					$(messageBlock).html(data.message);
					
					$('#modalWindowTitle').html('Ремонт имущества'); // задать заголовок модального окна
					$('#text_modal_confirm').html('Ремонт'); // текст для кнопки подтверждения
			    	if (data.zeroSolvency) { // не хватает денег ремонтировать
						$('#modal_confirm').attr('disabled', true);
					}
					$('#modalWindow').modal(); // показать модальное окно
					    
					$('#modal_confirm').unbind('click'); // удалим все обработчики события 'click' у элемента modal_confirm
					// назначить обработчик кнопке modal_confirm (кнопка модального окна)
					    $('#modal_confirm').on('click', function() {
					    	$('#modalWindow').modal('hide'); // скрыть модальное окно
					    	$('#modal_confirm').attr('disabled', false);
					    	if (!data.zeroSolvency) { // хватает денег
						     	sendPost("repair");
							}
					  });
        		});
            return false;
        });
    });
    
    //level-up for prop
    //получение суммы повышения уровня имущества и назначение обработчика на клик по кнопке propUpBut
    $.ajax({
      		  type: 'POST',
      		  url: "${pageContext.request.contextPath}/property/level-up",
      		  data:  { propId: <c:out value='${prop.id}'/>, action: "getSum", obj: "prop" },
      		  dataType: "json",
      		  async:true
      		}).done(function(data) {
      			if(data.error) {
      				$("#prop_up_td").html('<h5>' + data.message + '</h5>');
      			} else {
      				$("#propUpBut").attr("data-original-title", 'Улучшить. Сумма: ' + data.nextSum);
      				 
      				$('#propUpBut').on('click', function() {
      					var propName = "<c:out value='${prop.name}'/>"; // имя имущества
      					var currLevel = parseInt($('#prop_level_td').html()); // текущий уровень
      					var nextSum = $(this).attr('data-original-title').substring(17);
      					
      			    	$('#modalWindowTitle').html('Поднятие уровня имущества'); // задать заголовок модального окна
      			    	$('#text_modal_confirm').html('Улучшить'); // текст для кнопки подтверждения
      			    	$('#modalWindowBody').html('Вы точно хотите поднять уровень имущества <b>' + propName + '</b>? </br>' + 
      			    			'Сумма: <b>' + nextSum + '&tridot;</b> </br>' +
      			    			'Будет достигнут уровень: <b>' + (currLevel + 1) + '</b>'); // задать тело модального окна
      					$('#modalWindow').modal(); // показать модальное окно
      			    		
	    			$('#modal_confirm').unbind('click'); // удалим все обработчики события 'click' у элемента modal_confirm
   			  		// назначить обработчик кнопке modal_confirm (кнопка модального окна)
	    			$('#modal_confirm').on('click', function() {
		            	$('#modalWindow').modal('hide'); // скрыть модальное окно
		            	sendPostLevelUp("up", "prop") // повысить уровень
  			    	});
   		            return false;
   		        });
      			}
      		}); 
    
	//level-up for cash
	//получение суммы повышения уровня кассы имущества и назначение обработчика на клик по кнопке cashUpBut
	$.ajax({
	  		  type: 'POST',
	  		  url: "${pageContext.request.contextPath}/property/level-up",
	  		  data:  { propId: <c:out value='${prop.id}'/>, action: "getSum", obj: "cash" },
	  		  dataType: "json",
	  		  async:true
	  		}).done(function(data) {
	  			if(data.error) {
	  				$("#cash_up_td").html('<h5>' + data.message + '</h5>');
	  			} else {
	  				$("#cashUpBut").attr("data-original-title", 'Улучшить. Сумма: ' + data.nextSum);
	  				
	  				$('#cashUpBut').on('click', function() {
	  					var propName = "<c:out value='${prop.name}'/>"; // имя имущества
	  					var currLevel = parseInt($('#cash_level_td').html()); // текущий уровень
	  					var nextSum = $(this).attr('data-original-title').substring(17);
	  					
       			    	$('#modalWindowTitle').html('Поднятие уровня кассы'); // задать заголовок модального окна
       			    	$('#text_modal_confirm').html('Улучшить'); // текст для кнопки подтверждения
       			    	$('#modalWindowBody').html('Вы точно хотите поднять уровень кассы имущества <b>' + propName + '</b>? </br>' + 
       			    			'Сумма: <b>' + nextSum + '&tridot;</b> </br>' +
       			    			'Будет достигнут уровень: <b>' + (currLevel + 1) + '</b>'); // задать тело модального окна
       					$('#modalWindow').modal(); // показать модальное окно
       			    			
       			    	// удалим все обработчики события 'click' у элемента modal_confirm 
       			    	$('#modal_confirm').unbind('click'); 
       			  		// назначить обработчик кнопке modal_confirm (кнопка модального окна)
	       				$('#modal_confirm').on('click', function() {
			            	$('#modalWindow').modal('hide'); // скрыть модальное окно
			            	sendPostLevelUp("up", "cash"); // повысить уровень
	  			    	});
    		            return false;
		        	});
	  			}
	  		}); 
	
	//послать пост запрос на получении информации о имуществе (на продаже или нет)
	//также назначить обработчик при клике мышей по кнопке Продать
	$.post(
			  "${pageContext.request.contextPath}/property/sell",
			  { propId: <c:out value='${prop.id}'/>, action: "info" },
			  function(data) { 
				  sellProperty(data);
				  $('#propSellBut').on('click', function() {
					  sendSellPost(); // послать запрос чтобы продать или отменить продажу
					  $('#'+$('#propSellBut').attr('aria-describedby')).remove(); // удаление подсказки
				  });
			  	}
			);
	
	// при нажатии Переименовать
	$('#change_name_btn').on('click', function() {
		$('#new_prop_name').val($('#name').text());
		$('#modalChangeName').modal();
		
		$('#save_name').unbind('click'); // удалим все обработчики события 'click' у элемента save_name
		$('#save_name').on('click', function() {
			var URL = "${pageContext.request.contextPath}/property/operations/"+<c:out value='${prop.id}'/>
			var newName = $('#new_prop_name').val();
			$.post(
				URL,
				{ action: "change_name", newName: newName },
				function(data) { 
					$('#name').html(newName);
					$('#modalChangeName').modal('hide');
				}
			);
		});
	});
}; //window.onload()
	
	
//отправка запроса на повышение уровня кассы или имущества
function sendPostLevelUp(action0, obj0) {
 	$.ajax({
    		  type: 'POST',
    		  url: "${pageContext.request.contextPath}/property/level-up",
    		  data:  { propId: <c:out value='${prop.id}'/>, action: action0, obj: obj0 },
    		  dataType: "json",
    		  async:true
    		}).done(function(data) {
    			if(data.error) {
    				if(obj0 == "cash") {
    					$("#cash_up_td").html('<h5>' + data.message + '</h5>');
    				} else if (obj0 == "prop") {
    					$("#prop_up_td").html('<h5>' + data.message + '</h5>');
    				}
    			} 
    			//если было повышение
    			if (data.upped) {
    				changeBal(data);
    				
    				if(obj0 == "cash") {
     				$("#cashUpBut").attr("data-original-title", 'Улучшить. Сумма: ' + data.nextSum); // текст подсказки при наведении на кнопку
    					$('#cash_level_td').html(data.currLevel); // сама надпись уровня
    					$('#cashBlock').html($('#cashVal').attr("aria-valuenow") + ' / ' + data.cashCap); // 250 / 714
    					$('#cashVal').attr("aria-valuemax", data.cashCap); // установка максимального значения в кассе
    					$('#cashVal').attr("style", "width: " +  $('#cashVal').attr("aria-valuenow") / data.cashCap * 100 + "%"); // показать новую заполненность прогресс бара
    				} else if (obj0 == "prop") {
    					$("#propUpBut").attr("data-original-title", 'Улучшить. Сумма: ' + data.nextSum); // текст подсказки при наведении на кнопку
    					$('#prop_level_td').html(data.currLevel); // сама надпись уровня
    				}
    			} 
    	}); 
}
   
//отправка запроса на ремонт имущества
function sendPost(type1) {
	$.post(
			  "${pageContext.request.contextPath}/property/repair",
			  { propId: <c:out value='${prop.id}'/>, type: type1 },
			  function(data) {
				  if (data.error) {
					 	$('#errorMessg').html(data.message);
				  } else {
					    $('#deprVal').attr("aria-valuenow", data.percAfterRepair); // прогресс-бар - текущее значение 
					    $('#deprVal').attr("style", "width: " + data.percAfterRepair + "%"); // показать заполненность прогрессбара
					    $('#deprBlock').html(Number(data.percAfterRepair) + "%");
					    $('#sellPriceVal').html(data.propSellingPrice);
					    changeBal(data);
					    $('#cancel').trigger('click');
					    if (data.percAfterRepair == 0) {
					    	$('#repair_td').html('<h5>Ремонт не нужен.</h5>');
					    }
			  		}
			  	}
			);
}

//отправка запроса на продажу / отмену продажи имущества
function sendSellPost() {
	$.post(
			  "${pageContext.request.contextPath}/property/sell",
			  { propId: <c:out value='${prop.id}'/>, action: "sell" },
			  function(data) {
				  if (!data.error) {
					sellProperty(data);		  
				  } else {
					  $('#modalErrorBody').html('Ошибка! Нельзя отменить. Возможно имущество уже купили. Проверьте ' + 
							  '<a href="${pageContext.request.contextPath}/transactions" target="_blank">транзакции.</a>' + 
							  'Или сразу идите <a href="${pageContext.request.contextPath}/home">домой</a>, потому что сдесь уже делать нечего...');
					  $('#modalError').modal();
				  }
			  	}
			);
}

// функция вызывается при загрузке страницы - для показа кнопки "Продать"
// и при нажатии кнопки "Продать" для перерисовки
function sellProperty(data) {
	if (data.onSale) {
	  $('#propSellBut').html('<span class="glyphicon glyphicon-remove-circle"></span>');
	  $('#propSellBut').attr("data-original-title", 'Отмена продажи');
	  $('#endSaleDateVal').html('<span class="text-danger">Дата продажи:<br/>' + data.endSaleDate + '</span>');
 	} else {
	  $('#propSellBut').html('<span class="glyphicon glyphicon-briefcase"></span>');
	  $('#propSellBut').attr("data-original-title", 'Продать');
	  $('#endSaleDateVal').html('');
  	}
}
</script>
<t:template>
	<!-- Задний прозрачный фон-->
	<div id="wrap"></div>

<div class="container">
	<div class="row">
		<t:menu/>
		
		<div class="col-md-9">
			<form:form name="property" action="operations/${prop.id}" commandName="prop" method="post">
				<input type="hidden" name="action" id="action">
							
				<h3 class="page-header" align="center"><span id="name">${prop.name}</span> 
					<a id="change_name_btn" class="btn btn-info" data-toggle="tooltip" title="Переименовать">
						<span class="glyphicon glyphicon-pencil"></span></a>
				</h3>
				
				<table class="table table-striped">
					<tr class="tableTitleTr">
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
								<c:when test="${prop.commBuildingType == 'BOOK_SHOP'}">
										<td>Книжный магазин</td>
								</c:when>
								<c:when test="${prop.commBuildingType == 'CANDY_SHOP'}">
									<td>Магазин сладостей</td>
								</c:when>
								<c:when test="${prop.commBuildingType == 'LITTLE_SUPERMARKET'}">
									<td>Маленький супермаркет</td>
								</c:when>
								<c:when test="${prop.commBuildingType == 'MIDDLE_SUPERMARKET'}">
									<td>Средний супермаркет</td>
								</c:when>
								<c:when test="${prop.commBuildingType == 'BIG_SUPERMARKET'}">
									<td>Большой супермаркет</td>
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
							<div id="deprBlock">
								${prop.depreciationPercent}%
							</div>
							<div class="progress">
								<div id="deprVal" class="progress-bar progress-bar-striped" role="progressbar" aria-valuenow="${prop.depreciationPercent}" aria-valuemin="0" aria-valuemax="100" 
									style="width: ${prop.depreciationPercent}%;"></div>
							</div>
						</td>
						<td>
							<div id="repair_td">
								<c:if test="${prop.depreciationPercent > 0}">
									<a id="repairBut" class="btn btn-danger" data-toggle="tooltip" title="Ремонт"><span class="glyphicon glyphicon-wrench"></span></a>
								</c:if>
								<c:if test="${prop.depreciationPercent == 0}">
									<h5>Ремонт не нужен.</h5>
								</c:if>
							</div>
						</td>
					</tr>
					<tr>
						<td>Уровень</td>
						<td id="prop_level_td">${prop.level}</td>
						<td>
							<div id="prop_up_td">
								<a id="propUpBut" class="btn btn-success" data-toggle="tooltip" title="Улучшить"><span class="glyphicon glyphicon-menu-up"></span></a>
							</div>
						</td>
					</tr>
					<tr>
						<td>Деньги в кассе</td>
						<td>
							<div id="cashBlock">
								<fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.cash}"/> / 
								<fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.cashCapacity}"/>
							</div>
							
							<div class="progress">
							  <div id="cashVal" class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${prop.cash}" aria-valuemin="0" aria-valuemax="${prop.cashCapacity}" 
							  		style="width: ${prop.cash / prop.cashCapacity * 100}%;"></div>
							</div>
						</td>
						<td>
							<c:choose>
	   							<c:when test="${prop.cash > 0}">
									<a id="repairBut" class="btn btn-danger" data-toggle="tooltip" title="Собрать прибыль"
									  onclick="document.property.action.value='get_cash'; document.property.submit();"><span class="glyphicon glyphicon-piggy-bank"></span></a>
								</c:when>    
	   							<c:otherwise>
	   							через...
	   								<script>
										$(function() {
											var austDay = new Date(parseInt("<c:out value='${prop.nextProfit.time}'/>"));
											$('#defaultCountdown').countdown({
												until : austDay,
												expiryUrl: "${requestScope['javax.servlet.forward.request_uri']}"
											});
										});
									</script>
									<div id="defaultCountdown"></div>
							    </c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td>Уровень кассы</td>
						<td id="cash_level_td">${prop.cashLevel}</td>
						<td>
							<div id="cash_up_td">
								<a id="cashUpBut" class="btn btn-success" data-toggle="tooltip" title="Улучшить"><span class="glyphicon glyphicon-menu-up"></span></a>
							</div>
						</td>
					</tr>
					<tr>
						<td>Стоимость продажи</td>
						<td id="sellPriceVal"><fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.sellingPrice}"/></td>
						<td>
							<a id="propSellBut" class="btn btn-danger" data-toggle="tooltip"></a>
							<div id="endSaleDateVal"></div>
						</td>
					</tr>
				</table>
			</form:form>
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
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/datatables/1.10.7/js/jquery.dataTables.min.js"></script>

<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); // для отображения подсказок
});
</script>

<!-- модальное окно повышения уровня имущества или кассы -->
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
        <button id="modal_confirm" type="button" class="btn btn-success"><span id="text_modal_confirm">Подтвердить</span></button> <!-- кнопка подтверждения улучшения имущ. или кассы -->
      </div>
    </div>
  </div>
</div>

<!-- модальное окно для отображения ошибки -->
<div class="modal fade" id="modalError" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="modalErrorTitle">Ошибка</h4>
      </div>
      <div class="modal-body" id="modalErrorBody">
        Тело
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Ок</button>
      </div>
    </div>
  </div>
</div>

<!-- модальное окно изменения наименования -->
<div class="modal fade" id="modalChangeName" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="modalChangeNameTitle">Во что переименуем?</h4>
      </div>
      <div class="modal-body" id="modalChangeNameBody">
		<!--  input with name  -->
		<div class="form-group"> 
			<label class="control-label" for="airport_edit_name">Новое имя (25 символов):</label>
			<input type="text" class="form-control" id="new_prop_name" placeholder="Введите новое имя" required maxlength="25">
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
        <button id="save_name" type="button" class="btn btn-success"><span class="glyphicon glyphicon-floppy-disk"></span></button>
      </div>
    </div>
  </div>
</div>
</t:template>