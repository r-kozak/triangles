<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Рейтинг</title>

<style>
.table tr td {
	text-align: center !important;
}
</style>

<t:template>
	<div class="container">
		<div class="row">		
			<div class="col-md-12">
				<h3 class="page-header" align=center>Рейтинг игроков</h3>
				
				<table class="table">
					<tr class="tableTitleTr">
						<td>Место</td>
						<td>Имя</td>
						<td>Доминантность</td>
					</tr>
					<c:forEach items="${users}" var="user" varStatus="i">
						<tr>
							<td>${i.count}</td>
				   			<td>${user[0]}</td>
							<td>${user[1]}</td>
						</tr>
			   		</c:forEach>
				</table>			
	
	 		</div> <!-- container.row.col-md-9 - лицензии-->
		</div> <!-- container.row -->
		
		<t:footer></t:footer>
	</div> 
</t:template>