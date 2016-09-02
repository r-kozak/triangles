<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Стройка</title>

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
					<c:choose>
						<c:when test="${availableForBuild > 0}">
							<button id="build_btn" class="btn btn-info btn-lg btn-license_up" title="Построить имущество" data-toggle="tooltip">
							<span class="glyphicon glyphicon-equalizer"> СТРОИТЬ 
							<span>(сегодня доступно: ${availableForBuild})</span></span></button>
						</c:when>
						<c:otherwise>
							<button id="build_btn" class="btn btn-info btn-lg btn-license_up" title="Построить имущество" data-toggle="tooltip" disabled=true>
							<span class="glyphicon glyphicon-equalizer"> СТРОИТЬ 
							<span>(сегодня недоступно)</span></span></button>
						</c:otherwise>
					</c:choose>
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
 				<c:when test="${empty constrProjects}">
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
							
							<c:if test="${countCompletedProj > 0}">
								<td><button id="0" class="btn btn-danger btn-sm from-constr-btn" 
										title="Принять всё из строительства" data-toggle="tooltip"> <span class="from_constr_btn_ico">
										 <span class="glyphicon glyphicon-ok"></span></span></button>
								</td>
							</c:if>
						</tr>
						
						<c:forEach items="${constrProjects}" var="constrProject">
							<tr>
								<td class="building_type_name">${constrProject.buildingType}</td> 
								<td class="city_area_name">${constrProject.cityArea}</td>

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
								
								<c:if test="${countCompletedProj > 0}">
									<td>
										<c:if test="${constrProject.completePerc == 100}">
											<button id="${constrProject.id}" class="btn btn-danger btn-lg from-constr-btn" 
												title="Принять объект из строительства" data-toggle="tooltip">  <span class="from_constr_btn_ico">
												<span class="glyphicon glyphicon-ok"></span></span></button>
										</c:if>
									</td>
								</c:if>
							</tr>
						</c:forEach>
					</table>
 				</c:otherwise>
 			</c:choose>
 		</div>
 	</div>
	<t:footer></t:footer>
</div> <!-- container -->

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
			<c:forEach items="${tradeBuildingsData}" var="buildingData">
				<tr>
					<td class="building_type_name" id="${buildingData.tradeBuildingType.ordinal()}">${buildingData.tradeBuildingType}</td>
					<td>${buildingData.buildTime}</td>
					<td>${buildingData.purchasePriceMin}</td>
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

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/enum_types/buildings_types.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/enum_types/city_areas_types.js"></script>

<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); // для отображения подсказок
});

// по клику на кнопку СТРОИТЬ
$('#build_btn').on('click', function() {
	$('#modalBuildTypes').modal(); // показать модальное окно с выбором типа постройки

	//по клику на кнопки выбора конкретного типа имущества для стройки (кнопки в модальном окне)
	$('.btn_build_info').on('click', function() {
		var bui_type = $(this).closest('tr').find('.building_type_name').attr('id'); // тип постройки
		$.ajax({
	  		  type: 'POST',
	  		  url: "${pageContext.request.contextPath}/building/pre-build",
	  		  data:  { buiType: bui_type },
	  		  dataType: "json",
	  		  async:false
	  		}).done(function(data) {
				if (data.error) {
					// показать сообщение с ошибкой
					$('#modalErrorBody').html(data.message);
					$('#modalError').modal();
				} else {
					// показать сообщение с вопросом
					$('#modalQuesTitle').html('<b>Вы хотите построить <span class="building_type_name">' + data.buiType + '</span>?</b>');
					replaceAllBuildingsTypeNames(); // заменить имя строения на нормальное ("STALL" -> "Киоск")
					
					// сформировать тело модального окна
					$('#modalQuesBody').html('<div class="text-danger">Выберите район: ' + data.cityAreaTag + '</div> <br/>' +
							'<div id="price">' + data.price + '</div> <br/>' +
							'<div id="balance_after">' + data.balanceAfter + '</div> <br/>' +
							'<div id="solvency_after">' + data.solvencyAfter + '</div> <br/>' +
							'<div>' + data.exploitation + '</div> <br/>' +
							'<div id="available_for_build_after">Доступно для постойки сегодня после операции: <b><c:out value='${availableForBuild - 1}'/> шт.</b></div><br/>' + 
							'<div>Количество: ' +
							'<a id="b_count_down" class="btn btn-default"><span class="glyphicon glyphicon-menu-down"></span></a>' + 
							'  <span>&nbsp;<b></span><span id="b_count">1</span><span></b>&nbsp;&nbsp;</span>' +
							'<a id="b_count_up" class="btn btn-default"><span class="glyphicon glyphicon-menu-up"></span></a></div>');
					replaceCityAreasNamesInSelectElement(); // заменить имя района города на нормальное ("GHETTO" -> "Гетто") в select элементе
							
					//назначить обработчик на клик кнопки Подтверждения стройки
					$('#modal_ques_confirm').unbind('click');
					
					$('#modal_ques_confirm').on('click', function() {
						var city_area = $('#city_area').val();
						city_area = transformCityAreaNameToServerValue(city_area); // получить значение, как на сервере в Enum классе
						confirmBuild(bui_type, city_area); // функция подтверждения покупки
					});
					$('#modal_ques_confirm').attr('disabled', false);
					
					$('#modalQues').modal();
					
					var price_str = $('#price').html();
					var price = parseInt(price_str.substring(19, price_str.length - 5));
					$('#b_count_down').on('click', function() {
						changeBcount("down", price);
					});

					$('#b_count_up').on('click', function() {
						changeBcount("up", price);
					});
				}
	   		}).fail(function(jqXHR, textStatus, errorThrown) {
	  			alert(jqXHR.status + " " + jqXHR.statusText);
	  		});
	});
});

// изменение количества имуществ к постройке
function changeBcount(action, price) {
	var b_count_val = parseInt($("#b_count").html());
	var new_b_count_val = b_count_val;
	
	var curr_balance_str = $('#balance_after').html();
	var curr_balance_int = curr_balance_str.substring(27, curr_balance_str.length - 5);
	var curr_solvency_str = $('#solvency_after').html();
	var curr_solvency_int = curr_solvency_str.substring(36, curr_solvency_str.length - 5);
	var curr_available_for_build_int = "<c:out value='${availableForBuild}'/>" - b_count_val;
	var new_balance;
	var new_solvency;
	var new_available_for_build;
	
	if (action == "down") {
		if (b_count_val > 1) {
			new_b_count_val = b_count_val - 1;
			new_balance = parseInt(curr_balance_int) + price;
			new_solvency = parseInt(curr_solvency_int) + price;
			new_available_for_build = parseInt(curr_available_for_build_int) + 1;
		}
	} else if (action == "up") {
		new_b_count_val = b_count_val + 1;
		new_balance = parseInt(curr_balance_int) - price;
		new_solvency = parseInt(curr_solvency_int) - price;
		new_available_for_build = parseInt(curr_available_for_build_int) - 1;
	}
	if (new_b_count_val != b_count_val) {
		$("#b_count").html(new_b_count_val);
		
		$('#balance_after').html('Баланс после постройки: <b>' + new_balance + '&tridot;</b>');
		$('#solvency_after').html('Состоятельность после постройки: <b>' + new_solvency + '&tridot;</b>');
		$('#available_for_build_after').html('Доступно для постойки сегодня после операции: <b>' + new_available_for_build + ' шт.</b>');
		
		// если значения состоятельности и доступных для стройки объектов после операции положительные 
		if (new_solvency >= 0 && new_available_for_build >= 0) {
			$('#modal_ques_confirm').attr('disabled', false);
			$('#available_for_build_after').removeClass("text-danger");
		} else {
			// если значения состоятельности или доступных для стройки объектов после операции отрицательные
			$('#modal_ques_confirm').attr('disabled', true);
		}
		// если значение доступных для стройки объектов после операции отрицательное
		if (new_available_for_build < 0) {
			$('#available_for_build_after').addClass("text-danger");
		}
	}
}

// функция подтверждения постройки имущества (тип имущества, район города)
function confirmBuild(bui_type, city_area) {
	var count = parseInt($("#b_count").html());
	$.ajax({
		  type: 'POST',
		  url: "${pageContext.request.contextPath}/building/confirm-build",
		  data:  { buiType: bui_type, cityArea: city_area, count: count },
		  dataType: "json",
		  async:false
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
	$('.from_constr_btn_ico').html('<span class="glyphicon glyphicon-hourglass"></span>');
	
	$.ajax({
		  type: 'POST',
		  url: "${pageContext.request.contextPath}/building/from-construct",
		  data:  { constrId: btn_from_constr.id },
		  dataType: "json",
		  async:false
		}).done(function(data) {
			if (data.error) {
				// показать сообщение с ошибкой
				$('#modalErrorBody').html(data.message);
				$('#modalError').modal();
			} else {
				if (btn_from_constr.id == 0) {
					location = location;
				}
				btn_from_constr.closest('tr').remove(); // удалить строку с данными
				$('.from_constr_btn_ico').html('<span class="glyphicon glyphicon-ok"></span>');
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
		  async:false
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
		  async:false
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
</t:template>