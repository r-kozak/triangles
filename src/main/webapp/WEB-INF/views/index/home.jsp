<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<title>Home</title>
<t:template>
	<div class="content">
		<div class="wrapperNav">
			<h1 align="center">Разделы</h1>
					
			<a href="${pageContext.request.contextPath}/property"><p class="button menu bGreen"><span>Управление имуществом</span></p></a>
			<a href="${pageContext.request.contextPath}/issues"><p class="button menu bRed"><span>Деньги и персонал</span></p></a>
			<a href="${pageContext.request.contextPath}/relations"><p class="button menu bYellow"><span>Взаимо- отношения</span></p></a>
			<a href="${pageContext.request.contextPath}/entertainment"><p class="button menu bPurple"><span>Прогулка по городу</span></p></a>
		</div>
	</div>
</t:template>
