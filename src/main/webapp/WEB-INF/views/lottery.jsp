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
		
		//
		$('.play_label').on('click', function() {
			playLoto(this);
		});
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
					<div class="buy_label" id="buy_1">1</div>
					<div class="little_label">500&tridot; за 1 билет</div>
				</div>
				<div class="col-md-3">
					<div>Купить</div>
					<div class="buy_label" id="buy_10">10</div>
					<div class="little_label">475&tridot; за 1 билет</div>
				</div>
				<div class="col-md-3">
					<div>Купить</div>
					<div class="buy_label" id="buy_50">50</div>
					<div class="little_label">450&tridot; за 1 билет</div>
				</div>
			</div>
			<div class="row"></div> <!-- разделитель -->
			
			<h4 class="page-header" align=center>Играть</h4>
			
			<div class="col-md-12 text-center play_block">
				<div class="col-md-3">
					<div>Играть на</div>
					<div class="play_label" id="play_1">1</div>
					<div class="little_label">билет</div>
				</div>
				<div class="col-md-3">
					<div>Играть на</div>
					<div class="play_label" id="play_5">≤5</div>
					<div class="little_label">билетов</div>
				</div>
				<div class="col-md-3">
					<div>Играть на</div>
					<div class="play_label" id="play_10">≤10</div>
					<div class="little_label">билетов</div>
				</div>
				<div class="col-md-3">
					<div>Играть на</div>
					<div class="play_label" id="play_0">ВСЕ</div>
					<div class="little_label">билеты</div>
				</div>
			</div>
			<div class="row"></div> <!-- разделитель -->
			
			<h4 class="page-header" align=center>Выигранные плюшки</h4>
			
			<div class="col-md-12 text-center plushki_block">
				<div class="col-md-3">
					<div>Повышение уровня имущества</div>
					<div class="plushki_label">×</div>
				</div>
				<div class="col-md-3">
					<div>Повышение уровня кассы имущества</div>
					<div class="plushki_label">×</div>
				</div>
				<div class="col-md-3">
					<div>Лицензии на строительство</div>
					<div class="plushki_label_lic">уровень 2: ×</div>
					<div class="plushki_label_lic">уровень 3: ×</div>
					<div class="plushki_label_lic">уровень 4: ×</div>
				</div>
				<div class="col-md-3">
					<div>Мудрость всезнающего<br/><br/></div>
					<div class="plushki_label"><span class="glyphicon glyphicon-certificate" style="font-size:98; color:#FFCACA"></span></div>
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
					<td>${lotterySt.count}</td>
					<td>${lotterySt.ticketCount}</td>
				</tr>
			</c:forEach>
			</table>
		</div>
	</div> <!-- container.row -->
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

<!-- модальное окно для отображения вопросов -->
<div class="modal fade" id="modalQues" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="modalQuesTitle">Заголовок</h4>
      </div>
      
	  <div class="modal-body" id="modalQuesBody">Тело</div>
	  
	  <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Нет</button>
		<button id="modal_ques_confirm" type="button" class="btn btn-success"><span id="text_modal_confirm">Да</span></button>
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
// функция при нажатии на кнопки покупки билетов
function buyTickets(buy_btn) {
	var count = buy_btn.id.substring(4); 
	$.ajax({
		  type: 'GET',
		  url: "${pageContext.request.contextPath}/lottery/buy-tickets",
		  data:  { count: count },
		  dataType: "json",
		  async:true
		}).done(function(data) {
			if(data.error) {
				// показать сообщение с ошибкой
				$('#modalErrorBody').html(data.message);
				$('#modalError').modal();
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
		  async:true
		}).done(function(data) {
			if(data.error) {
				// показать сообщение с ошибкой
				$('#modalErrorBody').html(data.message);
				$('#modalError').modal();
			} else {
				//changeBal(data);
				//$('#tickets_count').html(data.ticketsValue);
				location = location;
			}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText);
	});
}
</script>
</t:template>