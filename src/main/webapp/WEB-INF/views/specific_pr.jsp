<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>${prop.name}</title>

<style>
.messagepop {
  background-color:#FFFFFF;
  border:1px solid #999999;
  cursor:default;
  display:none;
  margin-top: 15px;
  margin-left: -300;
  position:absolute;
  text-align:center;
  width:360;
  z-index:50;
  padding: 25px 25px 20px;
}

.variant{
	display: inline-block;
	border: 1px solid;
	margin: 0;
	width: 90;
	text-align: center;
	padding: 10;
	margin: 10 5 5 5;
	cursor: pointer;
}
#repair {
	background-color: rgb(168, 255, 168);
}

#cancel{
	background-color: rgb(255, 190, 190);
}

#repair:hover {
	background-color: rgb(0, 242, 0);
}

#cancel:hover{
	background-color: rgb(255, 113, 113);
}

#infoMessg {
	color:green;
	font-size: 12;
}
#errorMessg {
	color:red;
	font-size: 14;
}
#defaultCountdown {
  width: 200;
  margin-left:auto;
  margin-right: auto;
}
.table tr td {
	text-align: center !important;
}
</style>
<script>
	window.onload = function(){ 
	    document.getElementById('hider').onclick = function() {
	      	document.getElementById('but1').style.display = 'inline-block';
	      	document.getElementById('NewNameInput').style.display = 'inline-block';
	    	document.getElementById('wrap').style.display = 'block';
	    	document.getElementById('name').style.opacity = '0';
	    	document.getElementById('NewNameInput').focus();
	    	document.getElementById('NewNameInput').selectionStart = 
	    	 document.getElementById('NewNameInput').value.length;

	    }
	    
	    document.getElementById('wrap').onclick = function() {
	    	document.getElementById('wrap').style.display = 'none';
	      	document.getElementById('but1').style.display = 'none';
	      	document.getElementById('NewNameInput').style.display = 'none';
	    	document.getElementById('name').style.opacity = '1';
	    }
	    $("#NewNameInput").keydown(function(event){
	        if(event.keyCode == 13){
	            $(action).val('change_name');
	        }
	    });
	    
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
	        			  console.log(data);
	        			  
	        			  var zS = (!data.zeroSolvency) ? '<div id="repair" class="variant">Ремонт</div>' : "";
	        			  var messageBlock = (!data.zeroSolvency) ? '#infoMessg' : '#errorMessg';
	        			  $("#repairBut").addClass("selected").parent().append('<div class="messagepop pop">'+
	        	            		'<form method="post" id="repair_variants" action="/messages">' +
	        	            		'<h2>Ремонт</h2>' + 
	        	            		'<div id="infoMessg"></div>'+
	        	            		'<div id="errorMessg"></div>'+
	        	            		'<div id="all_variants">'+
	        	            		zS +
	        	            		'<div id="cancel" class="variant">Закрыть</div></div>'+
	        	            		'</form></div>');
	        			  $(messageBlock).html(data.message);
	        			  
	      	            $(".pop").slideFadeToggle();
	    	            $("#repairBut").hide();
	    	            
	    	            $('#cancel').on('click', function() {
	    		            $(".pop").slideFadeToggle();
	    		            $("#repairBut").removeClass("selected");
	    		            $("#repairBut").show();
	    		            $(".pop").remove();
	    		            return false;
	    		        });
	    	            
	    	            $('#repair').on('click', function() {
	    		            sendPost("repair");
	    		            return false;
	    		        });
	        		});
	        	
	            return false;
	        });
	        
	    });

	    $.fn.slideFadeToggle = function(easing, callback) {
	        return this.animate({ opacity: 'toggle', height: 'toggle' }, "fast", easing, callback);
	    };
	    
	    //level-up for cash
	    //получение суммы повышения уровня кассы имущества и назначение обработчика на клик по кнопке cashUpBut
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
    		            sendPostLevelUp("up", "prop");
    		            return false;
    		        });
       			}
       		}); 
	    
		//level-up for prop
		//получение суммы повышения уровня имущества и назначение обработчика на клик по кнопке propUpBut
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
			            sendPostLevelUp("up", "cash");
			            return false;
			        });
		  			}
		  		}); 
	}; //windows.onload()
	
	
    //отправка запроса на повышение уровня кассы или имущества
    function sendPostLevelUp(action0, obj0) {
    	var o = (obj0 == "cash") ? "кассы?" : "имущества?";
		var question = "Вы точно хотите повысить уровень " + o;
    	if(confirm(question)) {
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
						    changeBal(data);
						    $('#cancel').trigger('click');
						    if (data.percAfterRepair == 0) {
						    	$('#repair_td').html('<h5>Ремонт не нужен.</h5>');
						    }
				  		}
				  	}
				);
	}
	
	function changeBal(data) {
	    $('#balChan').html(data.changeBal + "&tridot;"); //блок с балансом для движения вверх
	    popUp(data.changeBal, "#balChan"); //движение вверх блока с балансом
	    $('#balanceVal').html(data.newBalance); //новое значение баланса
	    $('#solvencyVal').html(data.newSolvency); //новое значение состоятельности
	}
   
</script>
<t:template>
	<!-- Задний прозрачный фон-->
	<div id="wrap"></div>

<div class="content">
	<t:menu/>
	
		<div class="tranBlock">
			<form:form name="property" action="operations/${prop.id}" commandName="prop" method="post">
				<input type="hidden" name="action" id="action">
			
				<input id="NewNameInput" name="newName" type="text" value="${prop.name}" maxlength="25">
				<a class="btn btn-success" data-toggle="tooltip" title="Сохранить" 
					onclick="document.property.action.value='change_name'; document.property.submit();" id="but1">
					<span class="glyphicon glyphicon-floppy-disk"></span></a>

				
				<h1 id="name" align="center">${prop.name} <a id="hider" class="btn btn-info" data-toggle="tooltip" title="Переименовать">
					<span class="glyphicon glyphicon-pencil"></span></a></h1>
				
				<table class="table table-striped">
					<tr class="info">
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
						<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${prop.sellingPrice}"/></td>
						<td>
								<a id="cashUpBut" class="btn btn-danger" data-toggle="tooltip" title="Продать" href="#"><span class="glyphicon glyphicon-briefcase"></span></a>
						</td>
					</tr>
				</table>
			</form:form>
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
</t:template>