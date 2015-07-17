<%@ tag language="java" pageEncoding="UTF-8"%>

<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/buttons.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/beaTable.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/simpleTip.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/menu.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pagination.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/specific_pr.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery.countdown.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/home.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/search.css" type="text/css" />

<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.4.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/change_balance.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery.plugin.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery.countdown.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery.countdown-ru.js"></script>

<title>no title</title>
</head>
<style>
</style>
<body>
	<div id="header" class="header">
		<div class="logo">
			<a href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/resources/img/logo.png" align="middle"></a>
		</div>
 		<div class="headerNav">
 			<a href="${pageContext.request.contextPath}/wiki">wiki</a>
			<a href="${pageContext.request.contextPath}/exit">exit</a>
		</div>
	</div>
	<div id="status" class="status">
		<div class="dominant">Dominant: 0&#9813;</div>
		<div class="balance"><a href="${pageContext.request.contextPath}/transactions">Balance: ${balance}&tridot;</a></div>
	</div>	
	
	<jsp:doBody/>
</body>
</html>