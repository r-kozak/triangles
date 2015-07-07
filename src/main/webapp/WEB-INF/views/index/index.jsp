<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false" %>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css" type="text/css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login-sign.css" type="text/css">
	<title>Triangles</title>
</head>
<body>
    <div class="header">
        <a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/resources/img/logo.png" align="middle"></a>
    </div>
    
	<form:form class="lsForm" action="${pageContext.request.contextPath}/login" modelAttribute="user" method="post">
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
