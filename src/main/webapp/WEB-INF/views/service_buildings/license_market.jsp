<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Магазин лицензий</title>

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

<t:template>

<script>var ctx = "${pageContext.request.contextPath}"</script>

<div class="container">
	<div class="row">
		<t:menu/>
		
		<div class="col-md-9">
			<c:choose>
				<c:when test="${marketBuilt}">
					<c:choose>
						<c:when test="${isMarketCanFunction}">
							<!-- магазин может функционировать -->
							<h3 class="page-header" align="center">Магазин лицензий (уровень <span id="marketLevel">${marketLevel}</span>/${marketLevelMax})
								<c:if test="${marketLevel < marketLevelMax}">
									<a id="up_level_btn" class="btn btn-success" data-toggle="tooltip" title="Повысить уровень">
										<span class="glyphicon glyphicon-menu-up"></span>
									</a>
								</c:if>
							</h3>
							<h4 class="page-header" align=center>Продать лицензии</h4>
							<div class="col-md-12 text-center sell_block">
								<div class="col-md-4">
									<div class="little_label">Уровень 2</div>
									<c:choose>
										<c:when test="${lic2Count > 0 && requirementToSellLic2.carriedOut}">
											<div class="sell_label notSelectable" id="2">×<span id="lic2CountVal">${lic2Count}</span></div>
										</c:when>
										<c:otherwise>
											<div class="sell_label notSelectable disabled_label" id="2" 
												data-requirement-message="${requirementToSellLic2.description}">×<span id="lic2CountVal">${lic2Count}</span>
											</div>
										</c:otherwise>
									</c:choose>
								</div>
								
								<div class="col-md-4">
									<div class="little_label">Уровень 3</div>
									<c:choose>
										<c:when test="${lic3Count > 0 && requirementToSellLic3.carriedOut}">
											<div class="sell_label notSelectable" id="3">×<span id="lic3CountVal">${lic3Count}</span></div>
										</c:when>
										<c:otherwise>
											<div class="sell_label notSelectable disabled_label" id="3" 
												data-requirement-message="${requirementToSellLic3.description}">×<span id="lic3CountVal">${lic3Count}</span>
											</div>
										</c:otherwise>
									</c:choose>
								</div>
								
								<div class="col-md-4">
									<div class="little_label">Уровень 4</div>
									<c:choose>
										<c:when test="${lic4Count > 0 && requirementToSellLic4.carriedOut}">
											<div class="sell_label notSelectable" id="4">×<span id="lic4CountVal">${lic4Count}</span></div>
										</c:when>
										<c:otherwise>
											<div class="sell_label notSelectable disabled_label" id="4" 
												data-requirement-message="${requirementToSellLic4.description}">×<span id="lic4CountVal">${lic4Count}</span>
											</div>
										</c:otherwise>
									</c:choose>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<!-- магазин НЕ может функционировать -->
							<h3 class="page-header" align="center">Магазин лицензий</h3>
					
							<div class="noData">
								<p>Магазин лицензий не функционирует. Для возобновления работы выполните следующие требования:</p>
								<table class="table table-striped">
									<c:forEach items="${requirementsToFunction}" var="requirement">
										<tr>
											<c:choose>
												<c:when test="${requirement.carriedOut}">
													<td><span class="text-success glyphicon glyphicon-ok"></span></td>
												</c:when>
												<c:otherwise>
													<td><span class="text-danger glyphicon glyphicon-remove"></span></td>
												</c:otherwise>
											</c:choose>
											<td>${requirement.description}</td>
										</tr>						
									</c:forEach>
								</table>				
							</div>
						</c:otherwise>
					</c:choose>
					
					<div class="row"> 		
				 		<div class="col-md-12">
				 			<h4 class="page-header" align=center>Партии лицензий на продаже</h4>
				 			
				 			<c:choose>
				 				<c:when test="${!empty licensesConsignments}">
									<table class="table">
										<tr class="tableTitleTr">
											<td>Уровень лицензий</td>
											<td>Количество</td>
											<td>До продажи</td>
											<td>Сумма прибыли</td>
										</tr>
										
										<c:forEach items="${licensesConsignments}" var="consignment">
											<tr>
												<td>${consignment.licenseLevel}</td> 
												<td>${consignment.countOnSell}</td>
												<td style="width:35%">
													<script>
														$(function() {
															var austDay = new Date(parseInt("<c:out value='${consignment.sellDate.time}'/>"));
															$('#countdown_'+ "<c:out value='${consignment.id}'/>").countdown({
																until : austDay,
																expiryUrl: "${requestScope['javax.servlet.forward.request_uri']}"
															});
														});
													</script>
													<div id='countdown_${consignment.id}'></div>
												</td>
												<td>${consignment.profit}</td>
											</tr>
										</c:forEach>
									</table>
				 				</c:when>
				 				<c:otherwise>
									<div class="text-danger text-center" style="font-size:30">
										Нет лицензий на продаже.
									</div>
				 				</c:otherwise>
				 			</c:choose>
				 		</div> <!-- <div class="col-md-12"> -->
				 	</div> <!-- <div class="row"> -->
				</c:when>
				<c:otherwise>
					<h3 class="page-header" align="center">Магазин лицензий</h3>
					
					<div class="noData">
						<p>Магазин лицензий не построен. Для строительства выполните следующие требования:</p>
						<table class="table table-striped">
							<c:forEach items="${requirementsToBuild}" var="requirement">
								<tr>
									<c:choose>
										<c:when test="${requirement.carriedOut}">
											<td><span class="text-success glyphicon glyphicon-ok"></span></td>
										</c:when>
										<c:otherwise>
											<td><span class="text-danger glyphicon glyphicon-remove"></span></td>
										</c:otherwise>
									</c:choose>
									<td>${requirement.description}</td>
								</tr>						
							</c:forEach>
						</table>
						<c:choose>
							<c:when test="${isMarketCanBeBuilt}">
								<a href="${pageContext.request.contextPath}/build-license-market">СТРОИТЬ</a>
							</c:when>
							<c:otherwise>
								<a class="disabledBtn" href="#">СТРОИТЬ</a>
							</c:otherwise>
						</c:choose>						
					</div>
				</c:otherwise>
			</c:choose>
		</div> <!-- <div class="col-md-9"> --> 
	</div> <!-- row -->
	<t:footer></t:footer>
</div>

	<div id="balChan">
		<c:if test="${changeBal.length() > 0}">
			${changeBal}&tridot;
			<script>
				popUp("<c:out value='${changeBal}'/>", "#balChan");
			</script>
		</c:if>
	</div>
	
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/license_market.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/lottery_market.js"></script>
	
<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); // для отображения подсказок
});
</script>

<!-- модальное окно для информации-->
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
