<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Беседка</title>
<head>
	<script src='https://www.google.com/recaptcha/api.js'></script>
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
							<textarea class="form-control" rows="5" style="resize:none; font-size:13"></textarea>
							<span class="text-muted" style="font-size:11">Осталось: 500 симв.</span>
						</div>

					</div>
					
					<button class="btn btn-success btn" type="submit" style="width: 250; height: 76; float: right;">Сказать</button>
					<div style="display:inline-block;">
						<div class="g-recaptcha" data-sitekey="6LeKaQ8TAAAAAH3_1Vu7lf1bNk49UCUtHPRis9Tw"></div>
					</div>
				</form>
				
	 		</div> <!-- container.row.col-md-9 - лицензии-->
		</div> <!-- container.row - строка с кнопкой Строить и с лицензиями-->
	</div> 
</t:template>