<%@ tag language="java" pageEncoding="UTF-8"%>

<div class="col-md-3" id="menu">
	<div id="menuTitle">Меню</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/home">Домой</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/transactions">Транзакции</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/r-e-market">Рынок</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/commerc-pr">Коммерческое имущество</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/building">Стройка</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/loto">Лотерея</a>
		</div>
		
		<jsp:doBody/>
</div>