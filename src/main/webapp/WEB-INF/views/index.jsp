<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false" %>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/main.css" type="text/css"/>
	<title>Triangles</title>
    
    <style>        
        .loginForm {
            margin-top: 200px;
            width: 300px;
            height: 165px;
            padding: 2px;
        }
        
        .loginForm a {
            text-decoration:none;
            color: dodgerblue;
            font-size: 15;
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
        
        .signUpRef {
            text-align: right;
            margin-top: -3px;
            margin-right: 3px;
        }
    </style>
</head>
<body>
    <div class="header">
        <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/resources/logo.png" align="middle"></a>
    </div>
    
	<form:form class="loginForm" action="${pageContext.request.contextPath}/login" modelAttribute="user" method="post">
	    <form:input path="login" id="username" type="text" placeholder="Username" required="true" autofocus="true"/> 
	    <form:input path="password" id="password" type="password" placeholder="Password" required="true" /> 
	    <input class="subm" type="submit" value="Log in" />
	    <div class="signUpRef"><a href="${pageContext.request.contextPath}/signup">Sign up!</a></div>
	    
	    <table class = "errors">
   		<tr><td><form:errors path="login"/></td></tr>
   	</table>
	</form:form>
</body>
</html>
