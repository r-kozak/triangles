<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Home</title>
<style>
#contents {
	border: 1px solid;
	width: auto;
	background: #FFFBF6;
	display: inline-block;
	padding: 10 20 0 0;
	margin: 0 20 20 0;
	line-height: 1.7;
	font-size: 75%;
	font-family: "Lucida Console", Monaco, monospace;
}

#contents a {
	color: blue;
	text-decoration: none;
}

#contents_title {
	font-size: 18;
	text-align: center;
	border-bottom: 1px dotted;
	margin-left: 20;
}

.sp_point {
	margin: 20 0 20 0;
	border-bottom: 1px solid gray;
	padding: 0 0 0 20;
	display: -webkit-box;
}

.fs1 {
	font-size: 26;
	font-weight: bold;
}

.fs2 {
	font-size: 22;
}

.fs3 {
	font-size: 17;
}

#up {
	width: 55;
	height: 55;
	position: fixed;
	bottom: 50px;
	right: 20px;
	border-radius: 30px;
	display: none;
	cursor: pointer;
}

#down {
	width: 55;
	height: 55;
	position: fixed;
	bottom: 50px;
	right: 20px;
	border-radius: 30px;
	display: none;
	cursor: pointer;
}

.pPageScroll {
	color: red;
	font: bold 12pt 'Comic Sans MS';
	text-align: center;
}

.text {
	padding: 0 10 10;
	color: #414141;
	background-color: #fafafa;
	border: 1px dashed #c6c6c6;
	text-align: left;
	letter-spacing: 1pt;
	word-spacing: 3pt;
	font-size: 15px;
	font-family: Georgia;
	line-height: 23px;
}

.wrapper {
	margin: 100 15 0 15
}

h1 {
	margin-bottom: 50;
	border-bottom: 2px solid lightgrey;
}

p {
	text-indent: 1.5em; /* Отступ первой строки */
	text-align: justify; /* Выравнивание по ширине */
	margin-top: 10px;
}

#header {
	display: none;
}

#status {
	display: none;
}
</style>

<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script type="text/javascript">
	//при скроле убирает верхнюю панель
	window.onscroll = function() {
		var scrolled = window.pageYOffset || document.documentElement.scrollTop;
		if (scrolled > 100) {
			$('#header').slideUp(700);
			$('#status').slideUp(700);
		} else if (scrolled <= 100) {
			$('#header').slideDown(700);
			$('#status').slideDown(700);
		}
		if (scrolled > screen.height && b) {
			$('#up').show();
			$('#down').hide();
		} else if (scrolled == 0) {
			$('#up').hide();
			b = true;
		}
	};

	//скролл для того, чтобы показать хедер при входе на страницу
	window.onload = function() {
		var scrolled = window.pageYOffset || document.documentElement.scrollTop;
		//если мы не перезагружаем, а входим, тогда показать хедер
		if (scrolled == 0) {
			$('#header').slideDown(700);
			$('#status').slideDown(700);
		}
	};

	//функции вверх-вниз
	var a = 0; //для запоминания позиции курсора
	var b = true;

	$(document).ready(function() {
		//Обработка нажатия на кнопку "Вверх"
		$('#up').click(function() {
			//Необходимо прокрутить в начало страницы
			a = $(document).scrollTop();
			var curPos = $(document).scrollTop();
			var scrollTime = curPos / 1.73;
			$('body,html').animate({
				'scrollTop' : 0
			}, scrollTime);
			$('#up').hide();
			$('#down').show();
			b = false;
		});

		//Обработка нажатия на кнопку "Вниз"
		$('#down').click(function() {
			//Необходимо прокрутить в конец страницы
			var curPos = $(document).scrollTop();
			var height = $('body').height();
			var scrollTime = (height - curPos) / 1.73;
			$('body,html').animate({
				'scrollTop' : a
			}, scrollTime);
			$('#down').hide();
			$('#up').show();
		});
	});
</script>
<t:template>
	<div class="content">
		<div class="wrapper">
			<h1 align="center">wiki</h1>
			<div id="up">
				<a class="pPageScroll"><img width=50
					src="${pageContext.request.contextPath}/resources/img/up_arrow.png"></a>
			</div>
			<div id="down">
				<a class="pPageScroll"><img width=50
					src="${pageContext.request.contextPath}/resources/img/down_arrow.png"></a>
			</div>

			<div id="contents">
				<div id="contents_title">Содержание</div>
				<ol>
					<li><a href="#pr">Имущество</a></li>
					<ol type="a">
						<li><a href="#pr.ch">Характеристики</a></li>
						<ul>
							<li><a href="#pr.ch.st">Киоск</a></li>
							<li><a href="#pr.ch.vs">Сельський магазин</a></li>
							<li><a href="#pr.ch.ss">Магазин канцтоваров</a></li>
						</ul>
						<li><a href="#pr.co">Порядок начислений</a></li>
						<ul>
							<li><a href="#pr.co.pr">Прибыль</a></li>
							<li><a href="#pr.co.de">Износ</a></li>
						</ul>
						<li><a href="#pr.ma">Рынок имущества</a></li>
						<ul>
							<li><a href="#pr.ma.bu">Покупка</a></li>
							<li><a href="#pr.ma.se">Продажа</a></li>
							<li><a href="#pr.ma.ca">Районы города</a></li>
						</ul>
						<li><a href="#pr.bu">Строительство</a></li>
						<ul>
							<li><a href="#pr.bu.bt">Типы строителей</a></li>
							<li><a href="#pr.bu.li">Лицензии на строительство</a></li>
						</ul>
					</ol>
					<li><a href="#ba">Баланс</a></li>
					<ul>
						<li><a href="#ba.tr">Транзакции</a></li>
						<li><a href="#ba.cd">Кредит/депозит</a></li>
						<li><a href="#ba.so">Состоятельность</a></li>
						<li><a href="#ba.db">Дневной бонус</a></li>
					</ul>
				</ol>
			</div>

			<div id="pr" class="sp_point fs1">Имущество</div>
			<div class="text">
				<p>Имущество является одним из основных источников прибыли.
					Делится на:</p>
				<ul>
					<li>Коммерческое (приносит прибыль)</li>
					<ul>
						<li>Торговое (магазины, супермаркеты, торговые центры)</li>
						<li>Производственное (заводы, фабрики, комбинаты)</li>
						<li>Сфера услуг</li>
					</ul>
					<li>Личное (влияет на доминантность)</li>
				</ul>
				<p>У имущества есть общие характеристики (минимальная и
					максимальная прибыль, срок полезного использования...подробности
					ниже).</p>
				<p>Кроме общих характеристик, есть также характеристики
					конкретного экземпляра имущества:</p>
				<ul>
					<li>Наименование (можно и нужно менять)</li>
					<li>Уровень (влияет на прибыль)</li>
					<li>Район города (влияет на прибыль)</li>
					<li>Активность (при износе 100% - становится не активным, не
						приносит прибыль)</li>
					<li>Износ (насчитывается каждую неделю)</li>
					<li>Деньги в кассе</li>
					<li>Уровень кассы (влияет на вместимость)</li>
					<li>Стоимость продажи (зависит от износа)</li>
				</ul>
			</div>

			<div id="pr.ch" class="sp_point fs2">Характеристики</div>
			<div class="text">
				<p>Ниже описаны характеристика каждого типа имущества.</p>
				<p>Срок полезного использования = [период_окупаемости_макс * 2]</p>
				<p>Прибыль минимальная = [цена_покупки_мин /
					(период_окупаемости_макс * 7)]</p>
				<p>Прибыль максимальная = [цена_покупки_мин /
					(период_окупаемости_мин * 7)]</p>
				<p>Вместимость кассы = прибыть_макс * коэф. уровня (k)</p>
				<table border=1px solid
					style="text-align: center; margin: auto; border-collapse: collapse;">
					<tr>
						<td><b>Уровень кассы</b></td>
						<td>0</td>
						<td>1</td>
						<td>2</td>
						<td>3</td>
						<td>4</td>
						<td>5</td>
					</tr>
					<tr>
						<td><b>k</b></td>
						<td>1</td>
						<td>${ccl1}</td>
						<td>${ccl2}</td>
						<td>${ccl3}</td>
						<td>${ccl4}</td>
						<td>${ccl5}</td>
					</tr>
				</table>
				<c:if test="${!empty commBuData}">
					<c:forEach items="${commBuData}" var="cbdata">
						<c:choose>
							<c:when test="${cbdata.commBuildType == 'STALL'}">
								<div id="pr.ch.st" class="sp_point fs3">Киоск</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'VILLAGE_SHOP'}">
								<div id="pr.ch.vs" class="sp_point fs3">Сельский магазин</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'STATIONER_SHOP'}">
								<div id="pr.ch.ss" class="sp_point fs3">Магазин
									канцтоваров</div>
							</c:when>
						</c:choose>
						<ul>
							<li>Сфера деятельности: <b> ${cbdata.buildType}</b></li>
							<li>Период окупаемости, недель: <b>${cbdata.paybackPeriodMin}
									- ${cbdata.paybackPeriodMax}</b></li>
							<li>Цена покупки: <b>${cbdata.purchasePriceMin} -
									${cbdata.purchasePriceMax}</b></li>
							<li>Срок полезного использования, недель: <b>${cbdata.usefulLife}</b></li>
							<li>Прибыль, в день: <b>${cbdata.profitMin} -
									${cbdata.profitMax}</b></li>
							<li>Срок пребывания на рынке, дней: <b>${cbdata.remTermMin}
									- ${cbdata.remTermMax}</b></li>
							<li>Время на постройку, дней: <b>${cbdata.buildTime}</b></li>
							<li>Вместимость кассы:
								<table border=1px solid style="display: inline;">
									<tr>
										<c:forEach items="${cbdata.cashCapacity}" var="cashcap">
											<td>${cashcap}</td>
										</c:forEach>
									</tr>
								</table>
							</li>
						</ul>
					</c:forEach>
				</c:if>
			</div>

			<div id="pr.co" class="sp_point fs2">Порядок начислений</div>
			<div class="text">
				<p>Ниже пойдет речь о начислениях, связанных с имуществом.</p>

				<div id="pr.co.pr" class="sp_point fs3">Прибыль</div>
				<p>Прибыль начисляется каждые 24 часа по любому активному
					коммерческому имуществу. Активным считается то, износ которого <
					100%.</p>
				<p>
					На прибыль влияет уровень здания. Коэфициенты по уровням и влиянию
					на прибыль такие же, как и по <a href="#pr.ch">вместимости
						кассы</a>.
				</p>
				<p>Прибыль начисляется в кассу имущества. Если касса полностью
					забита, деньги пропадают.</p>
				<p>Если для конкретного имущества нанят кассир (можно нанимать
					не для каждого вида имущества), тогда он сам собирает прибыль и за
					переполнение кассы беспокоится не нужно.</p>

				<div id="pr.co.de" class="sp_point fs3">Износ</div>
				<p>Износ начисляется каждую неделю. При полном износе конкретный
					экземпляр имущества становится не активным и не приносит прибыли.
				<p>Также, износ прямо влияет на цену продажи имущества.
				<p>
					Процент начисления износа зависит от <b>основного</b> и <b>дополнительного
						процента</b>. Основной процент = (100 / срок полезного использования).
				</p>
				<p>Дополнительный процент = (1-30% от основного). Расчитывается
					рандомно. Он может как добавляться, так и отниматься от основного.</p>
				<p>
					<b>Итого:</b>
				</p>
				<p>Общий процент = основной +/- дополнительный.</p>
			</div>

			<div id="pr.ma" class="sp_point fs2">Рынок имущества</div>
			<div class="text">
				<p>Рынок имущества является глобальным в игре. Это значит, что
					если один игрок купил какое-либо имущество, то становится его
					единственным владельцем и другим игрокам это имущество уже не будет
					доступно.
				<p>
					Имущество на рынке появляется каждые <b> ${frp_min} -
						${frp_max} </b> дней.
				<p>Предложения генерируются либо тогда, когда пришло время, либо
					тогда, когда рынок пустой.
				<p><b>Количество предложений зависит от:</b>
				<p>1) количества активных пользователей (тех, которые входили
					последний раз не больше двух недель назад);
				<p>
					2) коэфициента количества имуществ на одного пользователя <b>(1-4)
					</b>
				<p>Вид имущества, который появится, тоже зависит от рандома. Но
					одни виды, как правило, с худшими характеристиками, приоритетней за
					другие.
				<p>
					Если имущество никто не купил, через некоторое время оно пропадает.
					Срок пребывания на рынке для каждого вида имущества разный, указан
					в <a href="#pr.ch">характеристиках</a>.
				<p>
					Цена на имущество также указана в характеристиках, но может
					меняться в зависимости от <a href="#pr.ma.ca">района города</a>.
				<p>Также, есть два вида состояния имущества - новое и б/у (износ
					> 0%). На б/у имущество действует скидка от 5 до 10% от его
					продажной стоимости. Это значит, что б/у имущество покупать
					выгодней, чем новое. После покупки его можно отремонтировать и оно
					будет, как новое.
				<div id="pr.ma.bu" class="sp_point fs3">Покупка</div>
				<p>Есть несколько вариантов покупки имущества. Это либо покупка
					за наличные, либо покупка в кредит.
				<p>
					Купить за наличные можно лишь в том случае, если <b>количество
						денег >= цена имущества</b>. При покупке за наличные происходит
					обычное снятие денежных средств с баланса.
				<p>
					Покупка в кредит осуществляется только тогда, когда позволяет <a
						href="#ba.so">состоятельность</a> пользователя. При этом будет
					задан вопрос о том, согласен ли пользователь оформить покупку в
					кредит.
				<p>Если не хватает денег (но баланс > 0) или низкая
					состоятельность для покупки конкретного имущества - будет показана
					страница ошибок и максимальная сумма, которая доступна для покупки
					имущества.
				<p>Если же баланс < 0, в таком случае вообще запрещено покупать
					имущество.
				<div id="pr.ma.se" class="sp_point fs3">Продажа</div>
				<p>Имущество можно продавать. После нажатия кнопки "Продать",
					имущество попадает на рынок, где его могут купить другие игроки.
					При продаже имуществу назначается конечная дата пребывания на
					рынке. Если до этой даты его никто не купит, оно продается
					автоматически.
				<p>Деньги за имущество поступают на баланс покупателя только
					тогда, когда имущество покупается другим игроком или автоматически
					продается при достижении конечной даты пребывания на рынке.
				<p>При продаже б/у имущества на него действует скидка для
					покупателя в размере (5-10%). Продавцу при продаже поступит полная
					сумма, которая равняется остаточной стоимости.
				<p>Например: есть киоск. Его цена покупки = 5000&tridot;, износ
					на момент продажи = 30%, а значит остаточная стоимоть =
					3500&tridot;. На рынок имущество попадает со скидкой 10% (цена на
					рынке = 3150&tridot;). Покупатель приобретает имущество за
					3150&tridot;, а продавцу приходит 3500&tridot;.
				<p>В случае продажи имущества с износом 0%, оно считается новым.
					Скидка на него не предоставляется, цена продажи и покупки =
					первоначальной стоимости.
				<div id="pr.ma.ca" class="sp_point fs3">Районы города</div>
				<p>Имущество может располагаться в разных районах города. Район
					влияет на размер прибыли и цену покупки имущества.
				<ul>
					<li>Гетто: <b>+${gp}%</b></li>
					<li>Окраина: <b>+${op}%</b></li>
					<li>Чайнатаун: <b>+${chp}%</b></li>
					<li>Центр: <b>+${cep}%</b></li>
				</ul>
			</div>

			<div id="pr.bu" class="sp_point fs2">Строительство</div>
			<div class="text">
				<p>
					Есть два способа для того, чтобы стать обладателем имущества.
					Первый - это <a href="#pr.ma.bu">покупка</a>, а второй -
					строительство. По-умолчанию для строительства доступен только один
					район города - гетто. Для выбора другого района города необходимо
					купить <a href="#pr.bu.li">лицензию</a>.
				<p>
					Время постройки у каждого типа зданий разное (см. <a href="#pr.ch">характеристики</a>).
				
				<p>Также, на время постройки влияет тип <a href="#pr.bu.bt">строителей</a>, который
					попадется на конкретную постройку (выбирается рандомно).
				<p>Цена постройки не зависит от района города и всегда берется
					минимальная (из характеристик).
					
				<div id="pr.bu.bt" class="sp_point fs3">Типы строителей</div>
				<p>Тип строителей влияет на скорость постройки. Один из 3-х типов строителей
				назначается на конкретный объект постройки рандомно, менять его нельзя. 
				<p><b>Типы строителей и скорость:</b>
					<ul>
						<li>Гастарбайтеры: <b>${gabu}%</b></li>
						<li>Украинцы: <b>${uabu}%</b></li>
						<li>Немцы: <b>${gebu}%</b></li>
					</ul>
					
				<div id="pr.bu.li" class="sp_point fs3">Лицензии на строительство</div>
				<p>Изначально можно строить только в районе гетто. Лицензии позволяют строить в других 
				районах. Лицензии нужно покупать, они имеют ограниченное время действия. 
				Есть 4 уровня лицензий (1-4). При покупке высшего уровня, низшие открываются 
				тоже (уровень 1 открыт изначально и всегда). 
				<p><b>Например, при покупке лицензии уровня:</b>
					<ul>
						<li>4 - будут доступны лицензии уровней: 1, 2, 3, 4;</li>
						<li>3 - будут доступны лицензии уровней: 1, 2, 3.</li>
					</ul>
				<p><b>Уровни, цена покупки, сроки действия:</b>
				 <table border=1px solid
					style="text-align: center; margin: auto; border-collapse: collapse;">
					<tr>
						<td><b>Уровень</b></td><td><b>Цена лицензии, &tridot;</b></td><td><b>Срок действия, недель</b></td>
					</tr>
					<tr>
						<td>1</td><td>0</td><td>&infin;</td>
					</tr>
					<tr>
						<td>2</td><td>${prc2}</td><td>${literm2}</td>
					</tr>
					<tr>
						<td>3</td><td>${prc3}</td><td>${literm3}</td>
					</tr>
					<tr>
						<td>4</td><td>${prc4}</td><td>${literm4}</td>
					</tr>
				</table>
			</div>
			<div id="ba" class="sp_point fs1">Баланс</div>
			<div class="text">
				<p>Под балансом подразумевается количество денег пользователя. Баланс может увеличиваться или 
				  уменьшаться при определенных обстоятельствах. Например, баланс уменьшается при покупке 
				  недвижимости, а увеличивается при сборе денег с кассы имущества.
				<p>К балансу можно отнести понятия транзакций, кредита/депозита, состоятельности пользователя.
			</div>
			<div id="ba.tr" class="sp_point fs2">Транзакции</div>
			<div class="text">
			<p>Контроль над операциями уменьшения или увеличения баланса ведется с помощью транзакций.
			Это отдельный, информационный раздел, где можно посмотреть все операции, которые повлияли 
			на размер баланса.
			<p><b>Можно узнать:</b>
				<ul>
					<li>когда была операция</li>
					<li>статью затрат</li>
					<li>краткое описание</li>
					<li>вид движения (приход или расход)</li>
					<li>сумму</li>
					<li>баланс после осуществления операции</li>
				</ul>
			</div>
			<div id="ba.cd" class="sp_point fs2">Кредит/депозит</div>
			<div class="text">
			<p>Каждый месяц в зависимости от размера баланса, пользователю на сумму баланса
			  начисляются проценты по кредиту или депозиту. Если баланс > 0, начисляется депозит, иначе -
			  кредит.
			<p><b>Ставки:</b>
			 	<ul>
					<li>кредит = ${cr_rate * 100}%</li>
					<li>депозит = ${dep_rate * 100}%</li>
			  	</ul>
			<p><b>Например, у пользователя на балансе на момент начисления:</b>
				<ul>
					<li>100&tridot;. [100&tridot; + <fmt:formatNumber value="${100 * dep_rate}" maxFractionDigits="0"/>&tridot; = 
					<fmt:formatNumber value="${100+(100 * dep_rate)}" maxFractionDigits="0"/>&tridot;]</li>
					
					<li>-100&tridot;. [-100&tridot; - <fmt:formatNumber value="${100 * cr_rate}" maxFractionDigits="0"/>&tridot; = 
					<fmt:formatNumber value="${-100-(100 * cr_rate)}" maxFractionDigits="0"/>&tridot;]</li>
				</ul>
			</div>
			
		</div>
	</div>
</t:template>
