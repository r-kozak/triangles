<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Служебное имущество</title>

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
			<h3 class="page-header" align="center">Служебное имущество</h3>
			
			<table class="table table-striped">
				<tr class="tableTitleTr">
					<td>Имущество</td>
					<td>Статус</td>
				</tr>
				<tr>
					<td><a class="bg-info" href="${pageContext.request.contextPath}/license-market">Магазин лицензий</a></td>
					<td>-</td>
				</tr>
				<tr>
					<td><a class="bg-info" href="${pageContext.request.contextPath}/resources-warehouse">Склад ресурсов</a></td>
					<td>-</td>
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