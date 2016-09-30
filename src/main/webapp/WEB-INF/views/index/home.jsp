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
		<h3 class="page-header">Панель управления (${userLogin})</h3>
		<div class="row">

			<div class="col-md-4">
				<div class="block_title text-center">
					<a href="${pageContext.request.contextPath}/property/r-e-market" class="btn"><span
						class="glyphicon glyphicon-shopping-cart text-danger"> Рынок торгового имущества</span></a>
				</div>
				<table class="table">
					<tr>
						<td class="tableTd1">Предложений всего</td>
						<td class="tableTd2"><span class="label label-default">${rePrCo}</span></td>
					</tr>
					<tr>
						<td class="tableTd1">Новых предложений</td>
						<td class="tableTd2">
							<span class="label label-<c:out value="${newRePrCo > 0 ? 'danger' : 'default'}"/>">${newRePrCo}</span>
						</td>
					</tr>
				</table>
			</div>

			<div class="col-md-4">
				<div class="block_title text-center">
					<a href="${pageContext.request.contextPath}/lottery" class="btn"><span
						class="glyphicon glyphicon-gift text-danger"> Лотерея</span></a>
				</div>
				<table class="table">
					<tr>
						<td class="tableTd1">Билетов</td>
						<td class="tableTd2"><span class="label label-<c:out value="${ticketsCount > 0 ? 'danger' : 'default'}"/>">${ticketsCount}</span></td>
					</tr>
					<tr>
						<td class="tableTd1">Игр сегодня</td>
						<td class="tableTd2"><span class="label label-default">${playsCountToday}/${lotteryPlayLimit}</span></td>
					</tr>
				</table>
			</div>

			<div class="col-md-4">
				<div class="block_title text-center">
					<a href="${pageContext.request.contextPath}/property/trade-property" class="btn"><span
						class="glyphicon glyphicon-piggy-bank text-danger"> Торговое имущество</span></a>
				</div>
				<table class="table">
					<c:choose>
						<c:when test="${comPrCount > 0}">
							<c:choose>
								<c:when test="${ready > 0}">
									<tr>
										<td class="tableTd1">Готовых к сбору дохода</td>
										<td class="tableTd2">
											<button class="btn btn-danger btn-xs" id="btn_get_cash_all_prop" type="button">
  												Собрать <span class="badge">${ready}</span>
											</button>
										</td>
									</tr>
								</c:when>
								<c:otherwise>
									<tr>
										<td class="tableTd1">До ближайшего сбора</td>
										<td class="tableTd2">
											<script>
												$(function() {
													var austDay = new Date(parseInt("<c:out value='${nextProfit.time}'/>"));
													$('#nextProfitCountdown').countdown({
														until : austDay,
														expiryUrl : "${requestScope['javax.servlet.forward.request_uri']}"
													});
												});
											</script>
											<div id="nextProfitCountdown"></div>
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
							<tr>
								<td class="tableTd1">Нужен ремонт для</td>
								<td class="tableTd2"><span class="label label-default">${needRepair}/${comPrCount}</span></td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td id="info">
									<p>Нет имущества</p>
									<a href="${pageContext.request.contextPath}/property/r-e-market">РЫНОК</a>
								</td>
							</tr>
							<tr><td></td></tr>
						</c:otherwise>
					</c:choose>
				</table>
			</div>

			<div class="col-md-4 col-md-offset-2">
				<div class="block_title text-center">
					<a href="${pageContext.request.contextPath}/building" class="btn"><span
						class="glyphicon glyphicon-equalizer text-danger"> Стройка</span></a>
				</div>
				<table class="table">
					<tr>
						<td class="tableTd1">Уровень лицензии</td>
						<td class="tableTd2">${licenseLevel}</td>
					</tr>
					<tr>
						<td class="tableTd1">До окончания лицензии</td>
						<td class="tableTd2">
							<script>
								$(function() {
									var austDay = new Date(parseInt("<c:out value='${licenseExpire.time}'/>"));
									$('#licenseExpireCountdown').countdown({
										until : austDay,
										expiryUrl : "${requestScope['javax.servlet.forward.request_uri']}"
									});
								});
							</script>
							<div id="licenseExpireCountdown"></div>
						</td>
					</tr>
					<tr>
						<c:choose>
							<c:when test="${countCompletedProj > 0}">
								<td class="tableTd1">Завершенных проектов</td>
								<td class="tableTd2"><span class="label label-danger">${countCompletedProj}/${totalProjCount}</span></td>
							</c:when>
							<c:otherwise>
								<td class="tableTd1">До принятия в эксплуатацию</td>
								<td class="tableTd2">
									<c:choose>
										<c:when test="${toExploitation != null}">
											<script>
												$(function() {
													var austDay = new Date(parseInt("<c:out value='${toExploitation.time}'/>"));
													$('#toExploitationCountdown').countdown({
														until : austDay,
														expiryUrl : "${requestScope['javax.servlet.forward.request_uri']}"
													});
												});
											</script>
											<div id="toExploitationCountdown"></div>
										</c:when>
										<c:otherwise>
											нет проектов
										</c:otherwise>
									</c:choose>
								</td>
							</c:otherwise>
						</c:choose>
					</tr>
					<tr>
						<td class="tableTd1">Начато сегодня</td>
						<td class="tableTd2">
							<span class="label label-<c:out value="${startedToConstructToday >= constructionLimitPerDay ? 'danger' : 'default'}"/>">${startedToConstructToday}/${constructionLimitPerDay}</span>
						</td>
					</tr>
				</table>
			</div>
			
			<div class="col-md-4">
				<div class="block_title text-center">
					<a href="${pageContext.request.contextPath}/license-market" class="btn"><span
						class="glyphicon glyphicon-briefcase text-danger"> Магазин лицензий</span></a>
				</div>
				<table class="table">
					<c:choose>
						<c:when test="${licenseMarket != null}">
							<tr>
								<td class="tableTd1">Уровень магазина</td>
								<td class="tableTd2"><span class="label label-default">${licenseMarket.level}</span></td>
							</tr>
							<tr>
								<td class="tableTd1">Статус магазина</td>
								<td class="tableTd2">
									<c:choose>
										<c:when test="${licenseMarketActive}">
											Активный
										</c:when>
										<c:otherwise>
											<span class="text-danger">Неактивный</span>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td class="tableTd1">До продажи</td>
								<td class="tableTd2">
									<c:choose>
										<c:when test="${sellLicensesCount > 0}">
											<script>
												$(function() {
													var austDay = new Date(parseInt("<c:out value='${toLicenseSell.time}'/>"));
													$('#toLicenseSellCountdown').countdown({
														until : austDay,
														expiryUrl : "${requestScope['javax.servlet.forward.request_uri']}"
													});
												});
											</script>
											<div id="toLicenseSellCountdown"></div>
										</c:when>
										<c:otherwise>
											нет партий
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<td id="info">
								<p>Построить</p>
								<a href="${pageContext.request.contextPath}/license-market">Магазин</a>
							</td>
							<tr><td></td></tr>
						</c:otherwise>
					</c:choose>
				</table>
			</div>
		</div>
		
		<div class="row">
		<h3 class="page-header">Статистика</h3>
		<div class="col-md-6">
			<table class="table">
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
				<tr>
					<td class="tableTd1">Выиграно в лотерею, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitLoto}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Продажа лицензий, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitFromLicensesSell}" /></td>
				</tr>
			</table>
		</div>
		<div class="col-md-6">
			<table class="table">
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
					<td class="tableTd1">Кредиты, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendCr}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Покупка лотерейных билетов, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendLoto}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Покупка лицензий на строительство, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendLicenseBuy}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">На строительство зданий, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendConstructProperty}" /></td>
				</tr>
				<tr>
					<td class="tableTd1">Выведено средств, &tridot;</td>
					<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendWithdraw}" /></td>
				</tr>
			</table>
		</div>
	</div>
	<t:footer></t:footer>
	</div>
</t:template>

<div id="balChan">
	<c:choose>
		<c:when test="${changeBal.length() > 0}">
			${changeBal}&tridot;
			<script>
				popUp("<c:out value='${changeBal}'/>", "#balChan");
			</script>
		</c:when>
	</c:choose>
</div>

<script>
	$(document).ready(function() {
		
		$('#btn_get_cash_all_prop').on('click', function() {
			window.location.replace("${pageContext.request.contextPath}/property/get-cash/0?redirectAddress=home");
		});
		
	});

</script>