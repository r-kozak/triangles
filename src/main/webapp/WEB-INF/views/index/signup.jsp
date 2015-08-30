<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>


<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login-sign.css" type="text/css">
<link rel='stylesheet' href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/css/bootstrap.min.css'>
	<title>Регистрация</title>
</head>

<body>
    <div class="header">
        <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/resources/img/logo.png" align="middle"></a>
    </div>
    
    <div class="container">
    	<div class="row">
    		<div class="col-md-4 text-center"></div>
    		
    		<div class="col-md-4 text-center">
				<form:form class="lsForm" action="signup" commandName="user" method="post" role="form">
					<div class="form-group">
						<img id="i_logo" src="${pageContext.request.contextPath}/resources/img/i_logo.png">
					
					    <form:input path="login" class="form-control"  type="text" placeholder="Ваш логин" required="true" autofocus="true"/>
					    <p class="help-block"><form:errors path="login"/></p>
					     
					    <form:input path="email" class="form-control" type="text" placeholder="E-mail" required="true" />
					    <p class="help-block"><form:errors path="email"/></p>
					     
					    <form:input path="password" class="form-control" type="password" placeholder="Ваш пароль" required="true" />
					    <p class="help-block"><form:errors path="password"/></p>
					     
					    <form:input path="confirmPassword" class="form-control"  type="password" placeholder="Еще раз пароль" required="true" />
					    <p class="help-block"><form:errors path="confirmPassword"/></p>
					    
					    <button id="subm" type="submit" class="btn btn-success">Регистрация</button>
				    </div>
				</form:form>
			</div>
			
			<div class="col-md-4 text-center"></div>
		</div>
	</div>
</body>
</html>