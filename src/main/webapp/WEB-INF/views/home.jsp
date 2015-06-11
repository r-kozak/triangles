<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/main.css" type="text/css" />
<title>Home</title>
</head>

<style>
.content {
	border: 2px solid;
	border-color: darkturquoise;
	border-bottom: none;
	width: 900px;
}

.wrapperNav {
	margin-top: 160px;
	margin-left: 170px;
	width: 550px;
	height: 550px;
}

.wrapperNav > div {
	border-top-left-radius: 5px;
	border-bottom-left-radius: 5px;
	border-left: 5px solid;
	border-left-color: darkturquoise;
	width: 220px;
	height: 220px;
	background: silver;
	display: inline-block;
	margin: 25px 25px 25px 25px;
}
</style>

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

	<div class="content">
		<div class="wrapperNav">
			<div class="d1">
				<a href="${pageContext.request.contextPath}/property"><img
					src="${pageContext.request.contextPath}/resources/property.png" align="middle"></a>
			</div>
			<div class="d2">
				<a href="${pageContext.request.contextPath}/bank"><img src="${pageContext.request.contextPath}/resources/bank.png"
					align="middle"></a>
			</div>
			<div class="d3">
				<a href="${pageContext.request.contextPath}/relations"><img
					src="${pageContext.request.contextPath}/resources/relations.png" align="middle"></a>
			</div>
			<div class="d4">
				<a href="${pageContext.request.contextPath}/entertainment"><img
					src="${pageContext.request.contextPath}/resources/entertainment.png" align="middle"></a>
			</div>
		</div>
		
		<div class="wrapperNav">
		</div>
	</div>
</body>
</html>