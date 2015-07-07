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

<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.4.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/change_balance.js"></script>

<title>no title</title>
</head>
<body>
	<div class="header">
		<a href="${pageContext.request.contextPath}/home"><img src="${pageContext.request.contextPath}/resources/img/logo.png"
			align="middle"></a>
 		<div class="headerNav">
			<a href="${pageContext.request.contextPath}/exit"><p class="button small bGray"><span>exit</span></p></a>
		</div>
	</div>
	<div class="status">
		<div class="dominant">Dominant: 0&#9813;</div>
		<div class="balance"><a href="${pageContext.request.contextPath}/transactions">Balance: ${balance}&tridot;</a></div>
	</div>	
	
	<jsp:doBody/>
</body>
</html>