<%@ tag language="java" pageEncoding="UTF-8"%>

<div id="menu">
	<div id="menuTitle">Меню</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/transactions">Транзакции</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/r-e-market">Рынок</a>
		</div>
		<div id="elMenu">
			<a href="${pageContext.request.contextPath}/property/commerc-pr">Коммерческое имущество</a>
		</div>
		
		<jsp:doBody/>
</div>