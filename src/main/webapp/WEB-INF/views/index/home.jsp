<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
#defaultCountdown {
  width: 100;
}
.countdown-row {
  padding: 0px 0px !important; 
}  
.countdown-section {
  font-size: 50% !important; 
}

	#control > #info > a {
		text-decoration: none;
		padding: 10px 70px;
		border: 2px solid;
		color: #B8860B;
		font-size: 22;
	}
	#control > #info > a:hover {
		color: #DD8400;
	}
	#info {
		margin-top: 8;
		text-align: center;
		font-size: 16;
	}

	#info > p {
		margin-bottom: 17;
	}
</style>

<title>Home</title>
<t:template>
	<div class="content">
		<div id="home_block">
			<div id="control_panel">
				<h3>Контрольная панель</h3>
				<div id="control">
					<div id="block_title">Рынок комм. имущества
						<a href="${pageContext.request.contextPath}/property/r-e-market"><img src="${pageContext.request.contextPath}/resources/img/arrow_right.png" height="25" align="right">
						</a>
					</div>
					<table>
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
				<div id="control">
					<div id="block_title">Коммерческое имущество
						<a href="${pageContext.request.contextPath}/property/commerc-pr"><img src="${pageContext.request.contextPath}/resources/img/arrow_right.png" height="25" align="right">
						</a>
					</div>
					<table>
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
										<td class="tableTd2">
											<script>
												$(function () {
													var austDay = new Date(parseInt("<c:out value='${nextProfit.time}'/>"));
													$('#defaultCountdown').countdown({
														until: austDay
													});
												});
											</script>
											<div id="defaultCountdown"></div>
										</td>
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
				<div id="control">
					<div id="block_title">Рынок труда</div>
				</div>
				<div id="control">

				</div>
			</div>
			<div id="statistic_wrap">
				<h3>Статистика</h3>
				<div class="statistic pr">
					<div id="block_title">Приход</div>
					<table>
						<tr>
							<td class="tableTd1">Всего заработано, &tridot;</td>
							<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitSum}"/></td>
						</tr>
						<tr>
							<td class="tableTd1">Прибыль с имущества, &tridot;</td>
							<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitFromProp}"/></td>
						</tr>
						<tr>
							<td class="tableTd1">Ежедневный бонус, &tridot;</td>
							<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitDB}"/></td>
						</tr>
						<tr>
							<td class="tableTd1">Депозиты, &tridot;</td>
							<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${profitDep}"/></td>
						</tr>
					</table>
				</div>
				<div class="statistic sp">
					<div id="block_title">Расход</div>
					<table>
						<tr>
							<td class="tableTd1">Всего потрачено, &tridot;</td>
							<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendSum}"/></td>
						</tr>
						<tr>
							<td class="tableTd1">На покупку имущества, &tridot;</td>
							<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendBuyPr}"/></td>
						</tr>
						<tr>
							<td class="tableTd1">Ремонт имущества, &tridot;</td>
							<td class="tableTd2">-</td>
						</tr>
						<tr>
							<td class="tableTd1">Выплата зарплат, &tridot;</td>
							<td class="tableTd2">-</td>
						</tr>
						<tr>
							<td class="tableTd1">Кредиты, &tridot;</td>
							<td class="tableTd2"><fmt:formatNumber type="number" maxFractionDigits="3" value="${spendCr}"/></td>
						</tr>
						<tr>
							<td class="tableTd1">Покупка лицензий на строительство, &tridot;</td>
							<td class="tableTd2">-</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
</t:template>
