<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Беседка</title>
<head>
	<script src='https://www.google.com/recaptcha/api.js'></script>
	
	<script>
		window.onload = function(){ 
			$('#newMsgText').on('change keyup paste contextmenu click', function() {
				var textVal = $('#newMsgText').val();
				var textLen = textVal.length;
				
			    if (textVal.trim() != '') {
			    	$('#postNewMsgBtn').attr('disabled', false);
			    } else {
			    	$('#postNewMsgBtn').attr('disabled', true);
			    }
			    
			    if (textLen > 500) {
			    	$('#newMsgText').val(textVal.substring(0,500));
			    	textLen = $('#newMsgText').val().length;
				}
			    $('#leftSymInfo').html('Осталось ' + (500 - textLen) + ' симв.'); 
			});
			
			$('#postNewMsgBtn').on('click', function() {
				var captchaVal = $('#g-recaptcha-response').val();
				if (captchaVal == '') {
					errorMsg();
					return false;
				}
			});
			
			function errorMsg() {
				$('#error_alert').css("opacity", 1);
				$('#error_alert').html(
						'<div class="alert alert-danger" style="height:76; padding-top:25">' + 'Подтвердите, что вы не робот' + '</div'
					);
				$('#error_alert').animate({"opacity" : "0"}, 1200);
			}
			
			$('.removeMsg').on('click', function() {
				var btnId = this.id;
				var msgId = btnId.substring(4);
				$.ajax({
					    url: '${pageContext.request.contextPath}/arbor?msgId=' + msgId,
					    type: 'DELETE',
			    		async:true
		    		}).done(function() {
		    			$('#' + btnId).remove();
		    			$('#msgDate_' + msgId).html('сообщение удалено');
		      		}).fail(function(jqXHR, textStatus, errorThrown) {
					alert(jqXHR.status + " " + jqXHR.statusText);
				});
			});
		} // window.onload
	</script>
</head>

<t:template>
	<div class="container">
		<div class="row">		
		<t:menu />
			<div class="col-md-9">
				<h3 class="page-header" align=center>Беседка</h3>
				
				<form action="arbor" method="post">
					<div class="panel panel-default">
						<div class="panel-heading">
							<span class="text-success">Сказать пару слов:</span>
							<textarea id="newMsgText" name="message" class="form-control" rows="5" style="resize:none; 
								font-size:13" maxlength="500"></textarea>
							<span id="leftSymInfo" class="text-muted" style="font-size:11">Осталось: 500 симв.</span>
						</div>
					</div>
					
					<button id="postNewMsgBtn" class="btn btn-success btn" style="width: 250; height: 76; float: right;" 
						disabled="disabled">Сказать</button>
						
					<div id="error_alert" style="height:76; display:inline-block; float:right; margin-right:30"></div>
					
					<div style="display:inline-block;">
						<div class="g-recaptcha" data-sitekey="6Ldw3yUTAAAAALvCg4EcqXzNwYHev3rlFuIvLUOW"></div>
					</div>
				</form>
				
				<h3 class="page-header"></h3>
				
				<c:forEach items="${messages}" var="msg">
					<div class="panel panel-${msg.level}">
						<div class="panel-heading">
							<span>#${msg.id}.</span>
							<span>${msg.author}</span>

							<span id="msgDate_${msg.id}" style="float:right"><fmt:formatDate value="${msg.date}" pattern="dd-MM-yyyy HH:mm"/></span>
							
							<c:if test="${currUserLogin.equals(msg.author) || currUserAdmin}">
								<a id="rem_${msg.id}" class="btn btn-default btn-sm removeMsg" title="Удалить" 
									style="float:right; margin:-2 10"><span class="glyphicon glyphicon-remove"></span></a>
							</c:if>
						</div>
						<div class="panel-body">
							 ${msg.message}
						</div>
					</div>
				</c:forEach>
				
	 		</div> <!-- container.row.col-md-9-->
		</div> <!-- container.row -->
		
		<t:footer></t:footer>
	</div> 
</t:template>