<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Транзакции</title>

<t:template>
<div class="container">
	<div class="row">
		<t:menu>
			<form:form id="searchForm" name="searchForm" method="GET" commandName="ts">
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
					<legend>Движение</legend>
						<form:select path="transfer" id="selectEl">
							<form:option value="NONE" label="--- Выбрать ---"/>
							<form:options items="${transfers}" />
						</form:select>
					</fieldset>
		
					<fieldset id="searchBlock">
					<legend>Статьи затрат</legend>
						<div id="searchEl">
							<form:checkboxes path="articles" items="${articles}"/>      
						</div>
					</fieldset>
		
					<form:checkbox id="needClear" path="needClear" hidden="true"/>
					<input id="page" path="page" name="page" value="1" hidden="true">
				</div>
				<div id="searchEl">
					<button id="searchSubmit" class="btn btn-primary btn-sm" type="submit" name="submit1">Искать</button>
					<input id="submClear" class="btn btn-danger btn-sm" data-toggle="tooltip" title="Очистить фильтр"  type="button" value="&#10008;" onclick="document.getElementById('needClear').checked = true; document.getElementById('searchForm').submit();"/>
				</div>
			</form:form>
		</t:menu>
	
		<div class="col-md-9">
			<c:if test="${!empty transacs}">
						
				<h3 class="page-header" align=center>Транзакции</h3>
				<div class="panel panel-default">
					<div class="panel-heading">
					  <div class="row">
					 	<div class="col-lg-6">
						    <button id="descr" class="btn btn-default btn-lg" data-toggle="tooltip"
						     	title="Показать или скрыть подробное описание раздела Транзакции"> <span class="glyphicon glyphicon-info-sign"></span>
						    </button>
							<button id="buy_100_tr" class="btn btn-default" data-toggle="tooltip"
				  				title="Купить 100&tridot; за 1 очко доминантности"> <span style="font-size:125%">+100&tridot;</span>
							</button>
							<button id="buy_1000_tr" class="btn btn-default" data-toggle="tooltip"
				  				title="Купить 1000&tridot; за 10 очков доминантности"> <span style="font-size:125%">+1000&tridot;</span>
							</button>
							<button id="withdraw_money_btn" class="btn btn-default" data-toggle="tooltip"
				  				title="Вывести средства со счета"> <span style="font-size:125%">Вывод средств</span>
							</button>
						</div>
						
						<div id="withdraw_block" class="col-lg-6" style="display: none">
  					        <div class="input-group">
						      <input id="withdraw_count" type="number" class="form-control" placeholder="Сумма вывода" style="font-size:125%; height:40px">
						      <span class="input-group-btn">
						        <button id="withdraw_money_ok_btn" class="btn btn-success" type="button"><span style="font-size:125%">Снять</span></button>
						      </span>
						      <span class="input-group-btn">
						        <button id="withdraw_money_cancel_btn" class="btn btn-danger" type="button"><span style="font-size:125%">Отмена</span></button>
						      </span>
						    </div><!-- /input-group -->
						</div>
					  </div>
					</div>
					<div class="panel-body collapse" id="tr_descr">
						<p><a href="${pageContext.request.contextPath}/wiki#ba.tr">Транзакции</a> - это раздел, где можно посмотреть все операции, которые 
					    повлияли на размер баланса, обменять очки доминантности на деньги. При просмотре транзакций, у вас есть возможность фильтровать 
					    их по разным статьям затрат, периоду, виду движения денежных средств.
					    </p>	
					</div>
				</div>
		
				<table id="transTable" class="table table-striped">
			      <thead>
					<tr class="tableTitleTr" style="font-weight:bold">
						<td>Дата</td>
						<td>Статья затрат</td>
						<td>Описание</td>
						<td>Прих./Расх.</td>
						<td>Сумма</td>
						<td>Баланс</td>
					</tr>
				  </thead>
				  <tbody>
						<c:forEach items="${transacs}" var="transac">
							<tr>
								<td><fmt:formatDate value="${transac.transactDate}" pattern="dd.MM.yyyy HH:mm:ss" /></td>
								
								<c:choose>
									<c:when test="${transac.articleCashFlow == 'DAILY_BONUS'}">
										<td style="text-align:left">Ежедневный бонус</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'CREDIT'}">
										<td style="text-align:left">Кредит</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'DEPOSIT'}">
										<td style="text-align:left">Депозит</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'LEVY_ON_PROPERTY'}">
										<td style="text-align:left">Сбор с имущества</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'BUY_PROPERTY'}">
										<td style="text-align:left">Покупка имущества</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'PROPERTY_REPAIR'}">
										<td style="text-align:left">Ремонт имущества</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'UP_CASH_LEVEL'}">
										<td style="text-align:left">Улучшение кассы</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'UP_PROP_LEVEL'}">
										<td style="text-align:left">Улучшение имущества</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'DOMINANT_TO_TRIAN'}">
										<td style="text-align:left">Обмен доминантности</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'SELL_PROPERTY'}">
										<td style="text-align:left">Продажа имущества</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'BUY_LICENSE'}">
										<td style="text-align:left">Покупка лицензий</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'CONSTRUCTION_PROPERTY'}">
										<td style="text-align:left">Постройка имущества</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'LOTTERY_WINNINGS'}">
										<td style="text-align:left">Выигрыш в лотерею</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'LOTTERY_TICKETS_BUY'}">
										<td style="text-align:left">Покупка лотерейных билетов</td>
									</c:when>
									<c:when test="${transac.articleCashFlow == 'WITHDRAW'}">
										<td style="text-align:left">Вывод средств</td>
									</c:when>
									<c:otherwise>
										<td>${transac.articleCashFlow}</td>
									</c:otherwise>
								</c:choose>
								
								<td style="text-align:left">${transac.description}</td>
								
								<c:choose>
									<c:when test="${transac.transferType == 'PROFIT'}">
										<td>Приход</td>
									</c:when>
									<c:when test="${transac.transferType == 'SPEND'}">
										<td>Расход</td>
									</c:when>
									<c:otherwise>
										<td>${transac.transferType}</td>
									</c:otherwise>
								</c:choose>
								<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${transac.sum}"/></td>
								<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${transac.balance}"/></td>
							</tr>
						</c:forEach>
					  </tbody>
				</table>
				
				<div class="panel panel-default" style="margin-bottom:0">
					<div class="panel-body">
					    <p class="text-right">Общая сумма операций по выбранным фильтрам: <b><fmt:formatNumber type="number" maxFractionDigits="3" 
					    value="${totalSum}"/>&tridot;</b></p>
					</div>
				</div>
				
				<ul class="pagination" style="float:right">
        			${paginationTag}
   				</ul>
			</c:if>
		</div> <!-- tranBlock (col-md-9)-->
	</div> <!-- row  -->
	<t:footer></t:footer>
</div> <!-- container -->

<div id="balChan"></div>

<script type="text/javascript" src="webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript" src="webjars/datatables/1.10.7/js/jquery.dataTables.min.js"></script>

<!-- Сортировка даты -->
<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.4/moment.min.js"></script>
<script type="text/javascript" src="webjars/datatables-plugins/1.10.7/sorting/datetime-moment.js"></script>
	
<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); // для отображения подсказок
    $('[data-toggle="collapse"]').collapse(); // свернуть блок с описанием
  
    // по клику на купить 100 triangles - отправить POST запрос на покупку  
    $("#buy_100_tr").on("click", function() {
    		infoBuyTriangles(100);
        }
    );
    
 	// по клику на купить 1000 triangles - отправить POST запрос на покупку  
    $("#buy_1000_tr").on("click", function() {
    		infoBuyTriangles(1000);
        }
    );
 	
 	// по клику на "Вывод средств" - показать поле ввода числа и кнопку подтверждения и отмены  
    $('#withdraw_money_btn').on("click", function() {
    		$('#withdraw_block').show();
        }
    );
 	
    $('#withdraw_money_ok_btn').on("click", 
    	function() {
	    	var moneyCount = $('#withdraw_count').val();
	    	if (moneyCount == "") {
		    	return;
			}
	    	// снять деньги со счета
	    	$.ajax({
	  		  type: 'POST',
	  		  url: "${pageContext.request.contextPath}/withdraw-money",
	  		  data:  { count: moneyCount },
	  		  dataType: "json",
	  		  async:false
	  		}).done(function(data) {
	  			if (data.error) {
	  				// показать сообщение с ошибкой
	  				$('#modal_confirm').attr('disabled', true);
					$('#modalWindowTitle').html('Ошибка').addClass('text-danger'); // установить заголовок модального окна, добавить класс text-danger
					$('#modalWindowBody').html(data.message);
					$('#modalWindow').modal(); // показать модальное окно
	  			} else {
	  				$('#withdraw_count').val("");
	  				changeBal(data);
	  				// создать новую строку для таблицы транзакций
	  				var newTr = '<tr><td>'+ data.transactDate +'</td>' +
	  					'<td>Вывод средств</td>' +
	  					'<td>'+ data.description +'</td>' +
	  					'<td>Расход</td>' +
	  					'<td>'+ moneyCount +'</td>' +
	  					'<td>'+ data.newBalance +'</td></tr>';
	  				
	  				var transactionTable = $('.tableTitleTr').closest('table');
	  				transactionTable.find('tbody').prepend(newTr); // вставить новую строку на место первой
	  				transactionTable.find('tbody tr:last').remove(); // удалить последнюю строку с таблицы
	  			}
	   		}).fail(function(jqXHR, textStatus, errorThrown) {
	  			alert(jqXHR.status + " " + jqXHR.statusText);
	  		});
		}
	);
 	
    $('#withdraw_money_cancel_btn').on("click", function() {
    		$('#withdraw_block').hide();
    	}
    );
    
  	//по клику на кнопку "Описание" - показать или скрыть описание
   	$("#descr").on("click", function(){
   			$("#tr_descr").collapse('toggle');
   		}
   	);
    
    // запрос на получение информации от сервера при покупке triangles
    // count_ - количество для покупки
    function infoBuyTriangles(count_) {
    	$.ajax({
  		  type: 'POST',
  		  url: "${pageContext.request.contextPath}/buy-triangles",
  		  data:  { count: count_, action: "info" },
  		  dataType: "json",
  		  async:true
  		}).done(function(data) {
				var messageBlock = (data.domiEnough) ? '#infoMessg' : '#errorMessg';
				
				$('#modalWindowBody').html('<div id="infoMessg"></div>'+
				        		'<div id="errorMessg">');
				$(messageBlock).html(data.message);
				
				$('#modalWindowTitle').html('Покупка triangles').removeClass('text-danger'); // задать заголовок модального окна, удалить класс text-danger, если он есть 
				$('#text_modal_confirm').html('<span class="glyphicon glyphicon-shopping-cart"></span>'); // текст для кнопки подтверждения

				$('#modal_confirm').unbind('click'); // удалим все обработчики события 'click' у элемента modal_confirm
		    	if (!data.domiEnough) { // не хватает доминантности на покупку
					$('#modal_confirm').attr('disabled', true);
					$('#modalWindowTitle').html('Ошибка').addClass('text-danger'); // установить заголовок модального окна, добавить класс text-danger
					$('#modalWindowBody').html(data.message);
				} else {
					// назначить обработчик кнопке modal_confirm (кнопка модального окна)
			    	$('#modal_confirm').attr('disabled', false);
					$('#modal_confirm').on('click', function() {
					    	$('#modalWindow').modal('hide'); // скрыть модальное окно
						    confirmBuyTriangles(count_); //отправить запрос подтверждения
					  }
					);
				}
				$('#modalWindow').modal(); // показать модальное окно
  		});
      	return false;
    }
    
 	// запрос на подтверждение покупки triangles
    // count_ - количество для покупки
    function confirmBuyTriangles(count_) {
    	$.ajax({
  		  type: 'POST',
  		  url: "${pageContext.request.contextPath}/buy-triangles",
  		  data:  { count: count_, action: "confirm" },
  		  dataType: "json",
  		  async:false
  		}).done(function(data) {
  			window.location.replace('${pageContext.request.contextPath}/transactions');
  		});
      	return false;
    }
});
</script>

<!-- модальное окно покупки triangles за очки доминантности -->
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
</t:template>