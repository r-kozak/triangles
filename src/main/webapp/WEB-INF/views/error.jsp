<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Ошибка</title>
</head>
<style>
	body {
		width: 100%;
		display: -webkit-box;
		-webkit-box-pack: center;
		background: #ffc5c5;
	}
	
	.errorBlock {
		height: 200px;
		width: 600px;
		margin-top: 250px;
		background: rgba(253, 217, 217, 0.71);
		border: 20px solid;
		border-color: rgb(255, 194, 194);
		color: rgb(27, 136, 52);
		font-size: 20px;
		font-family: Comic Sans MS;
		text-align: center;
		padding: 10px;
	}
	
	.errorBlock a {
		font-size: 14px;
		padding: 10px;
	}
</style>

<body>
	<div class="errorBlock">
		<p>${errorMsg}</p>
		<br>
		<a href="${pageContext.request.contextPath}/property/r-e-market">В течение 5 сек. вас перенаправит туда, откуда вы пришли, но можно и быстрее...</a>
		<script language="JavaScript" type="text/javascript">
			function toREmarket() {
				location = "${pageContext.request.contextPath}/${backLink}";
			}
			setTimeout('toREmarket()', 5000);
		</script>
	</div>
</body>

</html>