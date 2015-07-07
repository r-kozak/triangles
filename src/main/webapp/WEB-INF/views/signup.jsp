<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>


<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css" type="text/css">
	<title>SignUp</title>
    
    <style>
        .signupForm {
            margin-top: 200px;
            width: 300px;
            height: 165px;
            padding: 2px;
        }
            
        input {
            padding: 12px;
            width: 294px;
            margin-bottom: 5px;
        }
        
        .subm {
            border: darkturquoise solid 2px;
            background: none;
            font-size: 20;
            color: darkturquoise;
            padding: 9px;
            cursor: pointer;
        }
    </style>
</head>

<body>
    <div class="header">
        <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/resources/img/logo.png" align="middle"></a>
    </div>
    
<form:form class="signupForm" action="signup" commandName="user" method="post">
    <form:input path="login" type="text" placeholder="Username" required="true" autofocus="true"/> 
    <form:input path="email" type="text" placeholder="E-mail" required="true" /> 
    <form:input path="password" type="password" placeholder="Password" required="true" /> 
    <form:input path="confirmPassword" type="password" placeholder="Confirm password" required="true" />
    <input class="subm" type="submit" value="Sign up"> 
    
    
   	<table class = "errors">
   		<tr><td><form:errors path="login"/></td></tr>
       	<tr><td><form:errors path="password"/></td></tr>
       	<tr><td><form:errors path="email"/></td></tr>
       	<tr><td><form:errors path="confirmPassword"/></td></tr>
   	</table>
</form:form>
</body>
</html>