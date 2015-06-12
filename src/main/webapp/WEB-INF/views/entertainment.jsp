<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/main.css" type="text/css" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Entertainment</title>
</head>
<body>
	<div class="header">
		<a href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/resources/logo.png"
			align="middle"></a>
		<div class="headerNav"><a href="${pageContext.request.contextPath}/exit">exit</a></div>
	</div>
	<div class="status">
		<div class="dominant">Dominant: 0&#9813;</div>
		<div class="balance">Balance: ${balance}&tridot;</div>
	</div>
</body>
</html>