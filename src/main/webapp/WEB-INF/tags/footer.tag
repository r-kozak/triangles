<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:useBean id="now" class="java.util.Date" />

<div class="footer">
	<div style="display:inline-block;">
		<img src="${pageContext.request.contextPath}/resources/img/footer.png">
	</div>
	
	<div style="display:inline-block; float:right;">
		<div style="width:100%; height:10"></div>
		
		<div style="display:inline-block; width:100">
			<p><a href="${pageContext.request.contextPath}/transactions">Транзакции</a></p>
			<p><a href="${pageContext.request.contextPath}/property/r-e-market">Рынок</a></p>
		</div>
		
		<div style="display:inline-block; width:100">
			<p><a href="${pageContext.request.contextPath}/building">Стройка</a></p>
			<p><a href="${pageContext.request.contextPath}/lottery">Лотерея</a></p>
		</div>
		
		<div style="display:inline-block; width:110">
			<p><a href="${pageContext.request.contextPath}/arbor">Беседка</a></p>
			<p><a href="${pageContext.request.contextPath}/wiki">Вики</a></p>
		</div>
		
		<div style="display:inline-block; font-size:11">
			<span class="glyphicon glyphicon-time text-danger"></span> <fmt:formatDate value="${now}" pattern="dd.MM.yyyy - HH:mm:ss" /> <br>
			Roman Kozak <br>
			e-mail: roman.kozak2085@gmail.com <br>
			Triangles © 2015-2017 <br>
		</div>
	</div>
</div>
