<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false" %>
<html>
<head>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/img/ico.ico" type="image/x-icon">

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css" type="text/css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login-sign.css" type="text/css">
<link rel='stylesheet' href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.5/css/bootstrap.min.css'>
	<title>Triangles</title>
</head>
<body>
    <div class="header">
        <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/resources/img/logo.png" align="middle"></a>
    </div>
    
    <div class="container">
    	<div class="row">
    		<div class="col-md-4 text-center"></div>
    		
    		<div class="col-md-4 text-center">
				<form:form class="lsForm" action="${pageContext.request.contextPath}/login" modelAttribute="user" method="post" role="form">
					<img id="i_logo" src="${pageContext.request.contextPath}/resources/img/i_logo.png">
					
					<div class="form-group">
					    <form:input path="login" class="form-control" id="username" type="text" placeholder="Ваш логин" required="true" autofocus="true"/>
					    <p></p>
					     
					    <form:input path="password" class="form-control" id="password" type="password" placeholder="Ваш пароль" required="true" />
					    <p class="help-block"><form:errors path="login"/></p>
			
					     
					    <button id="subm" type="submit" class="btn btn-success">Войти</button>
					    <div class="signUpRef"><a href="${pageContext.request.contextPath}/signup">Регистрация</a></div>
				   	</div>
				</form:form>
			</div>
			
			<div class="col-md-4 text-center"></div>
		</div>
    </div>
</body>
</html>
