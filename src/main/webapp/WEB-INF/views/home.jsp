<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/main.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/buttons.css" type="text/css" />
<title>Home</title>
</head>
<body>
	<div class="header">
		<a href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/resources/logo.png"
			align="middle"></a>
 		<div class="headerNav">
			<a href="${pageContext.request.contextPath}/exit"><p class="button small bGray"><span>exit</span></p></a></div>
		</div>
	</div>
	<div class="status">
		<div class="dominant">Dominant: 0&#9813;</div>
		<div class="balance"><a href="${pageContext.request.contextPath}/transactions">Balance: ${balance}&tridot;</a></div>
	</div>

	<div class="content">
		<div class="wrapperNav">
			<h1 align="center">Разделы</h1>
					
			<a href="${pageContext.request.contextPath}/property"><p class="button menu bGreen"><span>Управление имуществом</span></p></a>
			<a href="${pageContext.request.contextPath}/issues"><p class="button menu bRed"><span>Деньги и персонал</span></p></a>
			<a href="${pageContext.request.contextPath}/relations"><p class="button menu bYellow"><span>Взаимо- отношения</span></p></a>
			<a href="${pageContext.request.contextPath}/entertainment"><p class="button menu bPurple"><span>Прогулка по городу</span></p></a>
		</div>
	</div>
</body>
</html>