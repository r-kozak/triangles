<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<style>
#searchWrap{
	padding: 2;
}
#menuTitle{
  font-size: 20;
  text-align: center;
  width: 100%;
  background: darkturquoise;
  color: aliceblue;
  height: 32;
  line-height: 1.6;
}
#searchBlock {
	
	padding: 5;
	margin-top: 10px; 
}
#searchTitle {
	
	text-align: center;
	background:rgb(0, 232, 236);
	color: rgb(110, 110, 110);
}
#forma {
	margin-top: 50;
}
#searchEl{
	margin-top:5;
}
#searchEl span{
	display: block;
	margin-top:5;
}
#searchEl span label{
	margin-left:5;
}
.date{
	width: 125;
}
#nadp {
	width:55;
	display: inline-block;
}
#searchSubmit {
  height: 28;
  width: 153;
  background: transparent;
  border: 3px solid darkturquoise;
  color: darkturquoise;
  font-weight: bold;
  font-size: 15;
  display: inline-block;
}
.submClear {
	 width: 40 !important;
	 vertical-align: bottom;
}
.submClear:hover {
	 color: red !important;
}
#searchSubmit:hover {
  background: transparent;
  border: 3px solid rgb(0, 171, 174);
  color: brown;
  cursor: pointer; 
}

</style>
<title>Развлечения</title>
<t:template>
<div id="menu">
<div id="menuTitle">Меню</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property">Упр. имуществом</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/r-e-market">Рынок</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/commerc-pr">Моё коммерческое</a>
		</div>

	<form:form id="forma" method="GET" commandName="tf">
		<div id="searchWrap">
		<div id="menuTitle">Поиск</div>
			<div id="searchBlock">
				<div id="searchTitle">Период</div>
				<div id="searchEl">
					<div id="nadp">Начало:</div> <form:input class="date" type="date" path="dateFrom"/>
				</div>
				<div id="searchEl">
					<div id="nadp">Конец:</div> <form:input class="date" type="date" path="dateTo"/>
				</div>
			</div>
			<div id="searchBlock">
				<div id="searchTitle">Движение</div>
				<div id="searchEl">
					<form:checkbox path="profit"/> PROFIT
				</div>
				<div id="searchEl">
					<form:checkbox path="spend"/> SPEND
				</div>
			</div>
			<div id="searchBlock">
				<div id="searchTitle">Статьи затрат</div>
				<div id="searchEl">
					<form:checkboxes path="articles" items="${articles}"/>      
				</div>

			</div>
			<form:checkbox id="needClear" path="needClear" hidden="true"/>
		</div>
		<div id="searchEl">
			<input id="searchSubmit" type="submit" name="submit1" value="Искать">
			<input id="searchSubmit" class="submClear" title="Очистить" type="button" value="&#10008;" onclick="document.getElementById('needClear').checked = true; document.getElementById('forma').submit();"/>
		</div>
	</form:form>
</div>
</t:template>