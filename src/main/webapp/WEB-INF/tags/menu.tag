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
			<a href="${pageContext.request.contextPath}/property/trade-property">Торговое имущество</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/service-buildings">Служебное имущество</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/building">Стройка</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/lottery">Лотерея</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/arbor">Беседка</a>
		</div>
		
		<jsp:doBody/>
</div>