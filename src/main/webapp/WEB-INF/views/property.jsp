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
	<div class="status">
		<div class="dominant">Dominant: 0&#9813;</div>
		<div class="balance">Balance: ${balance}&tridot;</div>
	</div>

	<div class="content">
		<div class="wrapperNav">
			<h1 align="center">Управление имуществом</h1>
			
			<a href="${pageContext.request.contextPath}/r-e-market"><p class="button menu bRed"><span>Рынок недвижим.</span></p></a>
			<a href="${pageContext.request.contextPath}/m-p-market"><p class="button menu bPurple"><span>Рынок транспорта</span></p></a>
			<a href="${pageContext.request.contextPath}/private-pr"><p class="button menu bGreen"><span>Мое личное имущество</span></p></a>
			<a href="${pageContext.request.contextPath}/commerc-pr"><p class="button menu bYellow"><span>Мое коммерческое</span></p></a>
		</div>
	</div>
</body>
</html>