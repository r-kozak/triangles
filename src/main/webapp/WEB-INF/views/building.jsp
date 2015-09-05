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
</style>
<t:template>
	<!-- Задний прозрачный фон-->
	<div id="wrap"></div>

<div class="container">
	<div class="row">
		<t:menu/>
				
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
</t:template>