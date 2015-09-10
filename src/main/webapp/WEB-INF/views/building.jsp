<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>${prop.name}</title>

<style>
.table tr td {
	text-align: center !important;
}
/*кнопка повышения уровня лицензии и кн. СТРОИТЬ */
.btn-license_up {
	width: 100%;	margin-top: 13px;
}
/*блок с информацией о лицензиях, таймером и кнопками*/
.license-block {
    border-top: #EEEEEE 1px solid;    border-bottom: #EEEEEE 1px solid;    margin-right: 0px !important;    margin-left: 0px !important;
}
/*блок с кнопками повышения уровня лицензии и кнопкой СТРОИТЬ*/
.license-btn-block{
	background:#dff0d8;		padding-bottom: 13px;
}
</style>
<t:template>
	<!-- Задний прозрачный фон-->
	<div id="wrap"></div>

<script>
	window.onload = function(){ 
		// по клику на кнопку Принять из строительства
		$('.from-constr-btn').on('click', function() {
			buildFromConstruct(this);
		});
		// по клику на кнопку Покупки лицензии
		$('.license-block').on('click', '.btn-license_up', function() {
			buyLicense(this);
		});
	}
</script>

<div class="container">
	<div class="row">
		<t:menu/>
			
		<div class="col-md-9">
			<h3 class="page-header" align=center>Стройка</h3>
			
			<div class="col-md-12 bg-info license-btn-block">
				<div>
					<button id="build_btn" class="btn btn-info btn-lg btn-license_up" title="Построить имущество" data-toggle="tooltip">
								<span class="glyphicon glyphicon-equalizer"> СТРОИТЬ</span></button>
				</div>
			</div>
			<div class="row"></div>
			
			<h4 class="page-header" align=center>Лицензии</h4>
			
			<div class="row license-block">
			
				<div class="col-md-2 text-center" style="background:#dff0d8; color:#4cae4c; padding:15; cursor:default">
					<div style="font-size:17">Лицензия</div>
					<div style="font-size:70">${licenseLevel}</div>
					<div style="font-size:13">уровень</div>
				</div>
				
				<div class="col-md-8 text-center text-danger" style="padding-top:20">
					<div style="font-size:20">Срок действия заканчивается через:</div>
					<div style="padding-top:35; padding-bottom:35;">
						<script>
							$(function() {
								var austDay = new Date(parseInt("<c:out value='${licenseExpire.time}'/>"));
								$('#licenseCountdown').countdown({
									until : austDay,
									expiryUrl : "${requestScope['javax.servlet.forward.request_uri']}"
								});
							});
						</script>
						<div id="licenseCountdown"></div>
					</div>
				</div>
				
				<div class="col-md-2 text-center license-btn-block">
					<div>
						<button id="buy_2" class="btn btn-success btn-lg btn-license_up" title="Купить лицензию уровня 2" data-toggle="tooltip">
										<span class="glyphicon glyphicon-arrow-up">2</span></button>
					</div>
					<div>
						<button id="buy_3" class="btn btn-success btn-lg btn-license_up" title="Купить лицензию уровня 3" data-toggle="tooltip">
										<span class="glyphicon glyphicon-arrow-up">3</span></button>
					</div>
					<div>
						<button id="buy_4" class="btn btn-success btn-lg btn-license_up" title="Купить лицензию уровня 4" data-toggle="tooltip">
										<span class="glyphicon glyphicon-arrow-up">4</span></button>
					</div>
				</div>
				
			</div> <!--  row license-block -->

 		</div> <!-- container.row.col-md-9 - лицензии-->
	</div> <!-- container.row - строка с кнопкой Строить и с лицензиями-->
	
	<div class="row"> 		
 		<div class="col-md-12">
 			<h4 class="page-header" align=center>В процессе постройки</h4>
 			
 			<c:choose>
 				<c:when test="">
					<table class="table">
						<tr class="text-danger" style="font-size:30">
							<td>Нажмите кнопку СТРОИТЬ, чтобы построить здание.</td>
						</tr>
					</table>
 				</c:when>
 				<c:otherwise>
 					<table class="table">
						<tr class="tableTitleTr">
							<td>Тип</td>
							<td>Район</td>
							<td>Кто строит</td>
							<td>Дата начала</td>
							<td class="hidden-sm">До эксплуатации</td>
							<td>Завершено, %</td>
							<td>Принять</td>
						</tr>
						<c:forEach items="${constrProjects}" var="constrProject">
							<tr>
								<c:choose>
									<c:when test="${constrProject.buildingType == 'STALL'}">
										<td>Киоск</td>
									</c:when>
									<c:when test="${constrProject.buildingType == 'VILLAGE_SHOP'}">
										<td>Сельский магазин</td>
									</c:when>
									<c:when test="${constrProject.buildingType == 'STATIONER_SHOP'}">
										<td>Магазин канцтоваров</td>
									</c:when>
								</c:choose>

								<td>${constrProject.cityArea}</td>

								<c:choose>
									<c:when test="${constrProject.buildersType == 'GASTARBEITER'}">
										<td>Гастарбайтеры</td>
									</c:when>
									<c:when test="${constrProject.buildersType == 'GERMANY_BUILDER'}">
										<td>Немцы</td>
									</c:when>
									<c:when test="${constrProject.buildersType == 'UKRAINIAN_BUILDER'}">
										<td>Украинцы</td>
									</c:when>
								</c:choose>
					
								<td><fmt:formatDate value="${constrProject.startDate}" pattern="dd-MM-yyyy HH:mm"/></td>
								<td class="hidden-sm" style="width:25%">
									<script>
										$(function() {
											var austDay = new Date(parseInt("<c:out value='${constrProject.finishDate.time}'/>"));
											$('#countdown_'+ "<c:out value='${constrProject.id}'/>").countdown({
												until : austDay,
												expiryUrl: "${requestScope['javax.servlet.forward.request_uri']}"
											});
										});
									</script>
									<div id='countdown_${constrProject.id}'></div>
								</td>
								<td>
									${constrProject.completePerc}
									<div class="progress">
										<div class="progress-bar progress-bar-striped" role="progressbar" aria-valuenow="${constrProject.completePerc}" 
											aria-valuemin="0" aria-valuemax="100" style="width: ${constrProject.completePerc}%;"></div>
									</div>
								</td>
								<td>
									<c:if test="${constrProject.completePerc == 100}">
										<button id="${constrProject.id}" class="btn btn-danger btn-lg from-constr-btn" 
											title="Принять объект из строительства" data-toggle="tooltip"> <span class="glyphicon glyphicon-ok"></span></button>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</table>
 				</c:otherwise>
 			</c:choose>
 		</div>
 	</div>
</div> <!-- container -->

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/datatables/1.10.7/js/jquery.dataTables.min.js"></script>

<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); // для отображения подсказок
});

// по клику на кнопку СТРОИТЬ
$('#build_btn').on('click', function() {
	$('#modalBuildTypes').modal(); // показать модальное окно с выбором типа постройки

	//по клику на кнопки выбора конкретного типа имущества для стройки (кнопки в модальном окне)
	$('.btn_build_info').on('click', function() {
		var bui_type = $(this).closest('tr').find('.bui_type').attr('id'); // тип постройки
		$.ajax({
	  		  type: 'POST',
	  		  url: "${pageContext.request.contextPath}/building/pre-build",
	  		  data:  { buiType: bui_type },
	  		  dataType: "json",
	  		  async:true
	  		}).done(function(data) {
				if (data.error) {
					// показать сообщение с ошибкой
					$('#modalErrorBody').html(data.message);
					$('#modalError').modal();
				} else {
					// показать сообщение с вопросом
					$('#modalQuesTitle').html('<b>Вы хотите построить ' + data.buiType + '?</b>');
					
					// сформировать тело модального окна
					$('#modalQuesBody').html('<div class="text-danger">Выберите район: ' + data.cityAreaTag + '</div> <br/>' +
							'<div>' + data.price + '</div> <br/>' +
							'<div>' + data.balanceAfter + '</div> <br/>' +
							'<div>' + data.exploitation + '</div>');
							
					//назначить обработчик на клик кнопки Подтверждения стройки
					$('#modal_ques_confirm').unbind('click');
					
					$('#modal_ques_confirm').on('click', function() {
						var city_area = $('#city_area').val();
						confirmBuild(bui_type, city_area); // функция подтверждения покупки
					});
					
					$('#modalQues').modal();
				}
	   		}).fail(function(jqXHR, textStatus, errorThrown) {
	  			alert(jqXHR.status + " " + jqXHR.statusText);
	  		});
	});
});

// функция подтверждения постройки имущества (тип имущества, район города)
function confirmBuild(bui_type, city_area) {
	$.ajax({
		  type: 'POST',
		  url: "${pageContext.request.contextPath}/building/confirm-build",
		  data:  { buiType: bui_type, cityArea: city_area },
		  dataType: "json",
		  async:true
		}).done(function(data) {
			if (data.error) {
				// показать сообщение с ошибкой
				$('#modalQues').modal('hide');
				$('#modalErrorBody').html(data.message);
				$('#modalError').modal();
			} else {
				// перезагрузить страницу
				window.location.replace('${pageContext.request.contextPath}/building');
			}
 		}).fail(function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR.status + " " + jqXHR.statusText);
		});
	
}

//по нажатию на кнопку Принять из строительства
function buildFromConstruct(btn_from_constr) {
	$.ajax({
		  type: 'POST',
		  url: "${pageContext.request.contextPath}/building/from-construct",
		  data:  { id: btn_from_constr.id },
		  dataType: "json",
		  async:true
		}).done(function(data) {
			if (data.error) {
				// показать сообщение с ошибкой
				$('#modalErrorBody').html(data.message);
				$('#modalError').modal();
			} else {
				btn_from_constr.closest('tr').remove(); // удалить строку с данными
			}
 		}).fail(function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR.status + " " + jqXHR.statusText);
		});
}

// по нажатию кнопки Купить лицензию
function buyLicense(btnBuy) {
	var buyLevel = btnBuy.id.substring(4);
	
	$.ajax({
		  type: 'POST',
		  url: "${pageContext.request.contextPath}/building/license-buy-info",
		  data:  { level: buyLevel },
		  dataType: "json",
		  async:true
		}).done(function(data) {
			if (data.error) {
				// показать сообщение с ошибкой
				$('#modalErrorBody').html(data.message);
				$('#modalError').modal();
			} else {
				// показать сообщение с вопросом
				$('#modalQuesTitle').html('<b>Вы хотите купить лицензию?</b>');
				
				// сформировать тело модального окна
				$('#modalQuesBody').html('<div>' + data.licenseLevel + '</div> <br/>' +
						'<div>' + data.licensePrice + '</div> <br/>' +
						'<div>' + data.balAfter + '</div> <br/>' +
						'<div>' + data.licenseTerm + '</div>');
						
				//назначить обработчик на клик кнопки Подтверждения покупки лицензии
				$('#modal_ques_confirm').unbind('click');
				
				$('#modal_ques_confirm').on('click', function() {
					confirmBuyLicense(buyLevel); // функция подтверждения покупки лицензии
				});
				
				$('#modalQues').modal();
			}
		}).fail(function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR.status + " " + jqXHR.statusText);
		});
}

// функция подтверждения покупки лицензии
function confirmBuyLicense(buyLevel) {
	$.ajax({
		  type: 'POST',
		  url: "${pageContext.request.contextPath}/building/license-buy",
		  data:  { level: buyLevel },
		  dataType: "json",
		  async:true
		}).done(function(data) {
			if (data.error) {
				// показать сообщение с ошибкой
				$('#modalErrorBody').html(data.message);
				$('#modalError').modal();
			} else {
				// перезагрузить страницу
				window.location.replace('${pageContext.request.contextPath}/building');
			}
		}).fail(function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR.status + " " + jqXHR.statusText);
		});
}
</script>

<!-- модальное окно с выбором типов зданий -->
<div class="modal fade" id="modalBuildTypes" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="modalBuildTypesTitle">Выберите тип для постройки</h4>
      </div>
      <div class="modal-body" id="modalBuildTypesBody">
        <table class="table">
			<tr class="tableTitleTr">
				<td>Тип</td>
				<td>Время постройки, дней</td>
				<td>Цена, &tridot;</td>
				<td>Строить</td>
			</tr>
			<c:forEach items="${commBuData}" var="cbdata">
				<tr>
					<c:choose>
						<c:when test="${cbdata.commBuildType == 'STALL'}">
							<td class="bui_type" id="${cbdata.commBuildType}">Киоск</td>
						</c:when>
						<c:when test="${cbdata.commBuildType == 'VILLAGE_SHOP'}">
							<td class="bui_type" id="${cbdata.commBuildType}">Сельский магазин</td>
						</c:when>
						<c:when test="${cbdata.commBuildType == 'STATIONER_SHOP'}">
							<td class="bui_type" id="${cbdata.commBuildType}">Магазин канцтоваров</td>
						</c:when>
					</c:choose>
					<td>${cbdata.buildTime}</td>
					<td>${cbdata.purchasePriceMin}</td>
					<td>
						<button class="btn btn-success btn_build_info" title="Строить" data-toggle="tooltip">
										<span class="glyphicon glyphicon-equalizer"></span></button>
					</td>
				</tr>
			</c:forEach>
		</table>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
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
</t:template>