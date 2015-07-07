<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<title>Управление имуществом</title>
<t:template>
	<div class="content">
		<div class="wrapperNav">
			<h1 align="center">Управление имуществом</h1>
			
			<a href="${pageContext.request.contextPath}/property/r-e-market"><p class="button menu bRed"><span>Рынок недвижим.</span></p></a>
			<a href="${pageContext.request.contextPath}/property/m-p-market"><p class="button menu bPurple"><span>Рынок транспорта</span></p></a>
			<a href="${pageContext.request.contextPath}/property/private-pr"><p class="button menu bGreen"><span>Мое личное имущество</span></p></a>
			<a href="${pageContext.request.contextPath}/property/commerc-pr"><p class="button menu bYellow"><span>Мое коммерческое</span></p></a>
		</div>
	</div>
</t:template>