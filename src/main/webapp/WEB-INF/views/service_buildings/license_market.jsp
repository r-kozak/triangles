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

<div class="container">
	<div class="row">
		<t:menu/>
		
		<div class="col-md-9">
			<h3 class="page-header" align="center">Магазин лицензий</h3>
			
			<c:if test="${marketBuilt}">
				<div class="">
					<p>Магазин лицензий не построен. Для строительства выполните следующие требования:</p>
					<table class="table table-striped">
						<c:forEach items="${requirementsToBuild}" var="requirement">
							<tr>
								<c:choose>
									<c:when test="${requirement.carriedOut}">
										<td><span class="glyphicon glyphicon-ok"></span></td>
									</c:when>
									<c:otherwise>
										<td><span class="glyphicon glyphicon-remove"></span></td>
									</c:otherwise>
								</c:choose>
								<td>${requirement.description}</td>
							</tr>						
						</c:forEach>
					</table>
				</div>
			</c:if>
			
			<table class="table table-striped">
				<tr class="tableTitleTr">
					<td>Характеристика</td>
					<td>Значение</td>
					<td>Действие</td>
				</tr>
				<tr>
					<td>Уровень</td>
					<td>${marketLevel}/${marketLevelMax}</td>
					<td><a class="btn btn-success" data-toggle="tooltip" title="Повысить уровень"><span class="glyphicon glyphicon-menu-up"></span></a></td>
				</tr>
				<tr>
					<td>Лицензии на продаже</td>
					<td>${countLicenseOnSell}</td>
					<td><a class="btn btn-primary" data-toggle="tooltip" title="Детально"><span class="glyphicon glyphicon-eye-open"></span></a></td>
				</tr>
				<tr>
					<td>Лицензии уровня 2</td>
					<td class="license_count">${countLicense2}</td>
					<td id="2">
						<a class="btn btn-danger sell_license_btn" data-toggle="tooltip" title="Продать">
							<span class="glyphicon glyphicon-briefcase"></span>
						</a>
					</td>
				</tr>
				<tr>
					<td>Лицензии уровня 3</td>
					<td class="license_count">${countLicense3}</td>
					<td id="3">
						<a class="btn btn-danger sell_license_btn" data-toggle="tooltip" title="Продать">
							<span class="glyphicon glyphicon-briefcase"></span>
						</a>
					</td>
				</tr>
				<tr>
					<td>Лицензии уровня 4</td>
					<td class="license_count">${countLicense4}</td>
					<td id="4">
						<a class="btn btn-danger sell_license_btn" data-toggle="tooltip" title="Продать">
							<span class="glyphicon glyphicon-briefcase"></span>
						</a>
					</td>
				</tr>
			</table>
		</div>
	</div>
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
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	
<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); // для отображения подсказок
});
</script>

<!-- модальное окно -->
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

<script>

</script>