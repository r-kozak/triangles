<%@ tag language="java" pageEncoding="UTF-8"%>

<html>
<head>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/img/ico.ico" type="image/x-icon">

<script>var CTX_PATH = "${pageContext.request.contextPath}"</script>

<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.4.js"></script>

<link rel='stylesheet' href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/css/bootstrap.min.css'>
<link rel='stylesheet' href='${pageContext.request.contextPath}/webjars/datatables/1.10.7/css/jquery.dataTables.min.css'>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/menu.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/specific_property.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery.countdown.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/search.css" type="text/css" />

<script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/enum_types/win_articles_types.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/change_balance.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/date_functions.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery.plugin.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery.countdown.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery.countdown-ru.js"></script>
<title>no title</title>
</head>
<body>
	<div id="header" class="header">
		<div class="logo">
			<a href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/resources/img/logo.png" align="middle"></a>
		</div>
 		<div class="headerNav">
 			<a title="Беседка" href="${pageContext.request.contextPath}/arbor"><span class="glyphicon glyphicon glyphicon-bullhorn"></span></a>
 			<a title="Рейтинг" href="${pageContext.request.contextPath}/rating"><span class="glyphicon glyphicon glyphicon-star"></span></a>
 			<a title="wiki" href="${pageContext.request.contextPath}/wiki"><span class="glyphicon glyphicon-question-sign"></span></a>
			<a title="Выход" href="${pageContext.request.contextPath}/exit"><span class="glyphicon glyphicon-log-out"></span></a>
		</div>
	</div>
	<div id="status" class="status">
		<table style="width:100%; text-align:center">
			<tbody>
				<tr>
				  <td>
					<div class="dominant">Доминантность: <span id="domiVal">${domi}</span>&#9813;</div>
				  </td>
				  <td>
					<div class="solvency">Состоятельность: <span id="solvencyVal">${solvency}</span>&tridot;</div>
				  </td>
				  <td>
					<div class="balance"><a href="${pageContext.request.contextPath}/transactions">Баланс: <span id="balanceVal">${balance}</span>&tridot;</a></div>
				  </td>  
				</tr>
			</tbody>
		</table>
	</div>
    <div id="bonus_btn">
      <img src="${pageContext.request.contextPath}/resources/img/bonus_btn.png" align="middle">
    </div>
    
    <!-- модальное окно для отображения бонусов -->
    <div class="modal fade" id="bonus_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title text-danger" id="bonus_modal_title">Ваш бонус</h4>
          </div>
          
        <div class="modal-body" id="bonus_modal_body">Тело</div>
        
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Ок :)</button>
          </div>
        </div>
      </div>
    </div>
	<jsp:doBody/>
</body>

</html>