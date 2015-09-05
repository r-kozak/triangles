<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
.countdown-row {
	padding: 0px 0px !important;
}
.countdown-section {
	font-size: 50% !important;
}
.block_title a {
	font-size: 17;
}
.tableTd2 {
	text-align: right;
}
.page-header {
	text-align: center;
}
#info>a {
	text-decoration: none;
	padding: 6px 55px;
	border: 2px solid;
	color: #B8860B;
	font-size: 16;
}
#info>a:hover {
	color: #DD8400;
}
#info {
	margin-top: 6;
	text-align: center;
	font-size: 13;
}
#info>p {
	margin-bottom: 12;
}
</style>

<title>Home</title>
<t:template>
	<div class="container">
		<h3 class="page-header">Контрольная панель</h3>
		<div class="row">

			<div class="col-md-4">
				<div class="block_title text-center">
					<a href="${pageContext.request.contextPath}/property/r-e-market" class="btn"><span
						class="glyphicon glyphicon-shopping-cart text-danger"> Рынок коммерческого имущества</span></a>
				</div>
				<table class="table table-compact">
					<tr>
						<td class="tableTd1">Предложений всего</td>
						<td class="tableTd2">${rePrCo}</td>
					</tr>
					<tr>
						<td class="tableTd1">Новых предложений</td>
						<td class="tableTd2">${newRePrCo}</td>
					</tr>
				</table>
			</div>

			<div class="col-md-4">
				<div class="block_title text-center">
					<a href="${pageContext.request.contextPath}/loto" class="btn"><span
						class="glyphicon glyphicon-gift text-danger"> Лотерея</span></a>
				</div>
				<table class="table table-compact">
					<tr>
						<td class="tableTd1">Билетов</td>
						<td class="tableTd2">-</td>
					</tr>
				</table>
			</div>

			<div class="col-md-4">
				<div class="block_title text-center">
					<a href="${pageContext.request.contextPath}/property/commerc-pr" class="btn"><span
						class="glyphicon glyphicon-piggy-bank text-danger"> Коммерческое имущество</span></a>
				</div>
				<table class="table table-compact">
					<c:choose>
						<c:when test="${comPrCount > 0}">
							<c:choose>
								<c:when test="${ready > 0}">
									<tr>
										<td class="tableTd1">Готовых к сбору дохода</td>
										<td class="tableTd2">${ready}</td>
									</tr>
								</c:when>
								<c:otherwise>
									<tr>
										<td class="tableTd1">До ближайшего сбора</td>
										<td class="tableTd2"><script>
											$(function() {
												var austDay = new Date(
														parseInt("<c:out value='${nextProfit.time}'/>"));
												$('#defaultCountdown')
														.countdown(
																{
																	until : austDay,
																	expiryUrl : "${requestScope['javax.servlet.forward.request_uri']}"
																});
											});
										</script>
											<div id="defaultCountdown"></div></td>
									</tr>
								</c:otherwise>
							</c:choose>
							<tr>
								<td class="tableTd1">Нужен ремонт для</td>
								<td class="tableTd2">${needRepair}/${comPrCount}</td>
							</tr>
						</c:when>
						<c:otherwise>
							<div id="info">
								<p>Нет имущества</p>
								<a href="${pageContext.request.contextPath}/property/r-e-market">РЫНОК</a>
							</div>
						</c:otherwise>
					</c:choose>
				</table>
			</div>

			<div class="col-md-4 col-md-offset-4">
				<div class="block_title text-center">
					<a href="${pageContext.request.contextPath}/building" class="btn"><span
						class="glyphicon glyphicon-equalizer text-danger"> Стройка</span></a>
				</div>
				<table class="table table-compact">
					<tr>
						<td class="tableTd1">Уровень лицензии</td>
						<td class="tableTd2">-</td>
					</tr>
					<tr>
						<td class="tableTd1">До окончания лицензии</td>
						<td class="tableTd2">-</td>
					</tr>
					<tr>
						<td class="tableTd1">До принятия в эксплуатацию</td>
						<td class="tableTd2">-</td>
					</tr>
				</table>
			</div>
			
		</div>
		<h3 class="page-header">Статистика</h3>
		<div class="col-md-6">
			<table class="table table-compact">
				<tr class="success text-success">
					<td colspan=2 align=center>Приход</td>
				</tr>
				<tr>
					<td class="tableTd1">Всего заработано, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitSum}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Прибыль с имущества, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitFromProp}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Продажа имущества, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitFromPropSell}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Ежедневный бонус, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitDB}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Депозиты, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitDep}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Обмен доминантности на &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitDomi}" /></td>
				</tr>
			</table>
		</div>
		<div class="col-md-6">
			<table class="table table-compact">
				<tr class="danger text-warning">
					<td colspan=2 align=center>Расход</td>
				</tr>
				<tr>
					<td class="tableTd1">Всего потрачено, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendSum}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">На покупку имущества, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendBuyPr}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">На повышение уровня имущества, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendUpLevel}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">На повышение уровня кассы, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendUpCash}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Ремонт имущества, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendRepair}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Выплата зарплат, &tridot;</td>
					<td class="tableTd2">-</td>
				</tr>
				<tr>
					<td class="tableTd1">Кредиты, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendCr}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Покупка лицензий на строительство, &tridot;</td>
					<td class="tableTd2">-</td>
				</tr>
			</table>
		</div>
	</div>
</t:template>
