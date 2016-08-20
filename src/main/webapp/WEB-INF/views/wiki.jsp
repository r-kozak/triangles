<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<title>Wiki</title>
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
	bottom: 90px;
	right: 20px;
	border-radius: 30px;
	display: none;
	cursor: pointer;
}

#down {
	width: 55;
	height: 55;
	position: fixed;
	bottom: 90px;
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
			var scrollTime = curPos / 3.2;
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
			var scrollTime = (height - curPos) / 3.2;
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

<!-- 			Меню -->
				<div id="contents_title">Содержание</div>
				<ul>
					<li><a href="#pr">Имущество</a></li>
					<ul>
						<li><a href="#pr.ch">Характеристики</a></li>
						<ul>
							<li><a href="#pr.ch.st">Киоск</a></li>
							<li><a href="#pr.ch.vs">Сельський магазин</a></li>
							<li><a href="#pr.ch.ss">Магазин канцтоваров</a></li>
							<li><a href="#pr.ch.bs">Книжный магазин</a></li>
							<li><a href="#pr.ch.cs">Магазин сладостей</a></li>
							<li><a href="#pr.ch.ls">Маленький супермаркет</a></li>
							<li><a href="#pr.ch.ms">Средний супермаркет</a></li>
							<li><a href="#pr.ch.hs">Большой супермаркет</a></li>
							<li><a href="#pr.ch.re">Ресторан</a></li>
							<li><a href="#pr.ch.ci">Кинотеатр</a></li>
							<li><a href="#pr.ch.ma">Торговый центр</a></li>
						</ul>
						<li><a href="#pr.co">Порядок начислений</a></li>
						<ul>
							<li><a href="#pr.co.pr">Прибыль</a></li>
							<li><a href="#pr.co.de">Износ</a></li>
							<li><a href="#pr.co.re">Ремонт</a></li>
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
					</ul>
					<li><a href="#ba">Баланс</a></li>
					<ul>
						<li><a href="#ba.tr">Транзакции</a></li>
						<li><a href="#ba.cd">Кредит/депозит</a></li>
						<li><a href="#ba.so">Состоятельность</a></li>
						<li><a href="#ba.db">Дневной бонус</a></li>
					</ul>
					<li><a href="#do">Доминантность</a></li>
					<ul>
						<li><a href="#do.co">Начисление</a></li>
						<li><a href="#do.te">Обмен на деньги</a></li>
						<li><a href="#do.lo">Начисление лотерейных билетов</a></li>
					</ul>
					<li><a href="#lu">Повышение уровня</a></li>
					<ul>
						<li><a href="#lu.pr">Имущество</a></li>
						<li><a href="#lu.ca">Касса имущества</a></li>
					</ul>
					<li><a href="#lo">Лотерея</a></li>
					<ul>
						<li><a href="#lo.tb">Покупка билетов</a></li>
						<li><a href="#lo.pl">Игра в лотерею</a></li>
						<li><a href="#lo.ap">Статьи выигрыша (плюшки)</a></li>
							<ul>
								<li><a href="#lo.ap.lu">Повышение уровня</a></li>
								<li><a href="#lo.ap.li">Лицензии на строительство</a></li>
								<li><a href="#lo.ap.wi">Мудрость от всезнающего</a></li>
							</ul>
						<li><a href="#lo.ac">Статьи выигрыша (обычные)</a></li>
							<ul>
								<li><a href="#lo.aс.pr">Имущество</a></li>
								<li><a href="#lo.aс.tr">Деньги</a></li>
							</ul>
					</ul>
					<li><a href="#uc">Универсальные коэффициенты</a></li>
				</ol>
			</div>


<!-- 			Имущество -->
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

<!-- 			Имущество.Характеристики -->
			<div id="pr.ch" class="sp_point fs2">Характеристики</div>
			<div class="text">
				<p>Ниже описаны характеристика каждого типа имущества.</p>
				<p>Срок полезного использования = [период_окупаемости_макс * 2]</p>
				<p>Прибыль минимальная = [цена_покупки_мин /
					(период_окупаемости_макс * 7)]</p>
				<p>Прибыль максимальная = [цена_покупки_мин /
					(период_окупаемости_мин * 7)]</p>
				<p>Вместимость кассы = прибыть_макс * коэф. уровня (k)
				<p>k - <a href="#uc">универсальный коэффициент</a> 
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
								<div id="pr.ch.ss" class="sp_point fs3">Магазин	канцтоваров</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'BOOK_SHOP'}">
								<div id="pr.ch.bs" class="sp_point fs3">Книжный магазин</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'CANDY_SHOP'}">
								<div id="pr.ch.cs" class="sp_point fs3">Магазин сладостей</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'LITTLE_SUPERMARKET'}">
								<div id="pr.ch.ls" class="sp_point fs3">Маленький супермаркет</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'MIDDLE_SUPERMARKET'}">
								<div id="pr.ch.ms" class="sp_point fs3">Средний супермаркет</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'BIG_SUPERMARKET'}">
								<div id="pr.ch.hs" class="sp_point fs3">Большой супермаркет</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'RESTAURANT'}">
								<div id="pr.ch.re" class="sp_point fs3">Ресторан</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'CINEMA'}">
								<div id="pr.ch.ci" class="sp_point fs3">Кинотеатр</div>
							</c:when>
							<c:when test="${cbdata.commBuildType == 'MALL'}">
								<div id="pr.ch.ma" class="sp_point fs3">Торговый центр</div>
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
								<table border=1px solid style="display: inline-table;">
									<tr>
										<c:forEach items="${cbdata.cashCapacity}" var="cashcap" begin="0" end="${max_cash_lev}">
											<td>${cashcap}</td>
										</c:forEach>
									</tr>
								</table>
							</li>
						</ul>
					</c:forEach>
				</c:if>
			</div>

<!-- 			Имущество.Порядок начислений -->
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
					забита, деньги пропадают. Если касса забита, при сборе, следующая дата 
					сбора прибыли = <b>дата этого сбора + 24 часа.</b></p>
				<p>Если для конкретного имущества нанят кассир (можно нанимать
					не для каждого вида имущества), тогда он сам собирает прибыль и за
					переполнение кассы беспокоиться не нужно.</p>

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
				
				<div id="pr.co.re" class="sp_point fs3">Ремонт</div>
				<p>Есть возможность ремонтировать имущество, чтобы сбросить процент износа. 
				<p>Существует 2 варианта ремонта:  
				<p>1. Полный, когда Состоятельность >= Суммы_ремонта. При этом процент износа сбрасывается до нуля.
				<p>2. Частичный, когда Состоятельность < Cуммы_ремонта. При этом Сумма_ремонта будет = Сумме_состоятельности и 
				  Процент_износа сбросится до определенной цифры, зависящей от возможной Суммы_ремонта.
				<p>Ремонт не возможен, если Cостоятельность = 0.
				<p>Коэф. уменьшения суммы ремонта = <b>${kdr}</b>.
				<p><b>Например:</b>
				<p><u>Полный ремонт</u>
				<p>Начальная_стоимость_имущества = 10 000.
				<p>Процент_износа = 50%.
				<p>Сумма_износа = Начальная_стоимость * Процент_износа = 10 000 * 50% = 5 000.
				<p>Сумма_ремонта = Сумма_износа / Коэф_уменьшения_суммы_ремонта= 5 000 / 2 = 2 500.
				<p>Состоятельность = 15000 (больше суммы ремонта).
				<p>Процент_износа_после_ремонта = 0%.
				<p>
				<p><u>Частичный ремонт</u>
				<p>Начальная_стоимость_имущества = 10 000.
				<p>Процент_износа = 70%.
				<p>Сумма_износа = Начальная_стоимость * Процент_износа = 10 000 * 50% = 7 000.
				<p>Сумма_ремонта = Состоятельность / Коэф_уменьшения_ суммы_ремонта = 7 000 / 2 = 3 500.
				<p>Состоятельность = 3 000 (меньше суммы ремонта). Значит сумма_ремонта тоже = 3 000.
				<p>Процент_износа_после_ремонта = Процент_износа - (Состоятельность * Процент_износа / Сумма_ремонта) = 70 - (3 000 * 70 / 3 500) = 10%.
			</div>

<!-- 			Имущество.Рынок имущества -->
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
				<p>Новым предложением считается то, которое появилось последние 24 часа.
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
				<p>Есть несколько вариантов покупки имущества. Это либо обычная покупка, либо покупка в кредит.
				<p> Обычная покупка осуществляется в том случае, если <b>количество
						денег >= цена имущества</b>. Баланс остается положительным (или нулевым).
				<p> Если денег не хватает, но <a href="#ba.so">состоятельность</a> пользователя позволяет совершить
				  покупку (даже, если баланс меньше нуля), можно купить имущество в кредит.
				<p> Если же денег не хватает и состоятельность низкая, в таком случае будет отказано в покупке. 
				
				<div id="pr.ma.se" class="sp_point fs3">Продажа</div>
				<p>Имущество можно продавать. После нажатия кнопки "Продать",
					имущество попадает на рынок, где его могут купить другие игроки.
					При продаже имуществу назначается конечная дата пребывания на
					рынке. Если до этой даты его никто не купит, оно продается
					автоматически.
				<p>Деньги за имущество поступают на баланс покупателя только
					тогда, когда имущество покупается другим игроком или автоматически
					продается при достижении конечной даты пребывания на рынке.
				<p>Пользователь не может видеть на рынке свое имущество, которое он выставил на продажу, только чужое.
				<p>На странице невдижимости имущество, которое выставлено на продажу выделяется желтым цветом. Точно так
				   же оно выделяется и на странице рынка недвижимости.
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


<!-- 			Имущество.Строительство -->
			<div id="pr.bu" class="sp_point fs2">Строительство</div>
			<div class="text">
				<p>Есть два способа для того, чтобы стать обладателем имущества.
					Первый - это <a href="#pr.ma.bu">покупка</a>, а второй -
					строительство. По-умолчанию для строительства доступен только один
					район города - гетто. Для выбора другого района города необходимо
					купить <a href="#pr.bu.li">лицензию</a> или выиграть в лотерею и применить.
				<p>Время постройки у каждого типа зданий разное (см. <a href="#pr.ch">характеристики</a>).
				
				<p>Также, на время постройки влияет тип <a href="#pr.bu.bt">строителей</a>, который
					попадется на конкретную постройку (выбирается рандомно).
				<p>Цена постройки не зависит от района города и всегда берется
					<u>минимальная</u> (из характеристик).
				<p>После окончания процесса постройки, объект нужно принять в эксплуатацию. При этом имущество принимается
				в эксплуатацию с <u>максимальной</u> ценой для даного типа.
				<p><b>Внимание!</b> Следует учитывать, что в день можно начинать строительство не более, чем ${constructionLimit} объектов.
					
				<div id="pr.bu.bt" class="sp_point fs3">Типы строителей</div>
				<p>Тип строителей влияет на скорость постройки. Один из 3-х типов строителей
				назначается на конкретный объект постройки рандомно, менять его нельзя. 
				<p><b>Типы строителей и скорость:</b>
					<ul>
						<li>Гастарбайтеры: <b>${builders[0]}</b></li>
						<li>Немцы: <b>${builders[1]}</b></li>
						<li>Украинцы: <b>${builders[2]}</b></li>
					</ul>
					
				<div id="pr.bu.li" class="sp_point fs3">Лицензии на строительство</div>
				<p>Изначально можно строить только в районе гетто. Лицензии позволяют строить в других 
				районах. Лицензии нужно покупать, они имеют ограниченное время действия. 
				Есть 4 уровня лицензий (1-4). При покупке высшего уровня, низшие открываются 
				тоже (уровень 1 открыт изначально и каждые ${licTerm[1]} дней срок такой лицензии продолжается 
				еще на ${licTerm[1]} дней). 
				<p><b>Например, при покупке лицензии уровня:</b>
					<ul>
						<li>4 - будут доступны лицензии уровней: 1, 2, 3, 4;</li>
						<li>3 - будут доступны лицензии уровней: 1, 2, 3.</li>
					</ul>
				<p><b>Уровни, цена покупки, сроки действия:</b>
				 <table border=1px solid style="text-align: center; margin: auto; border-collapse: collapse;">
					<tr><td><b>Уровень</b></td><td><b>Цена лицензии, &tridot;</b></td><td><b>Срок действия, дней</b></td></tr>
					<tr><td>1</td><td>${licPrice[1]}</td><td>${licTerm[1]}</td></tr>
					<tr><td>2</td><td>${licPrice[2]}</td><td>${licTerm[2]}</td></tr>
					<tr><td>3</td><td>${licPrice[3]}</td><td>${licTerm[3]}</td></tr>
					<tr><td>4</td><td>${licPrice[4]}</td><td>${licTerm[4]}</td></tr>
				</table>
				<p>При повышении уровня лицензии дата окончания лицензии сбрасывается и назначается новая, которая 
				соответствует уровню лицензии, к которому идет повышение.
				<p>Например, сегодня 01.09.2015. У вас есть лицензия уровня 2, вы ее купили 3 дня назад. Она 
				заканчивается 05.09.2015 (через 5 дней). Вы хотите купить лицензию уровня 4, чтобы строить в районе Центр.
				<p>Если вы купите лицензию уровня 4, срок действия ее станет 2 дня, закончится она 03.09.2015 и тогда
				уровень	лицензий сбросится к 1-му.
			</div>
			
<!-- 			Баланс -->
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
			
			<div id="ba.so" class="sp_point fs2">Состоятельность</div>
			<div class="text">
			<p>При покупке имущества, если не хватает денег на балансе, вам может помочь ваша состоятельность. Сумма состоятельности для
			  банка говорит, насколько вы кредитоспособны, чтобы в случае большой отрицательной суммы на балансе, вы могли продать некоторое 
			  имущество и выплатить кредит.
			<p><b>Формула расчета состоятельности:</b><br/>Состоятельность = [остаток на балансе + (остаточная стоимость всего имущества / 2)]
			<p><b>Например:</b><br/> 
			  у вас есть 5000&tridot; на балансе. Остаточная сумма вашего имущества составляет 12000&tridot;.
			  Максимальная сумма, на которую вы можете купить имущество = <br/>
			  = 5000 + (12000 / 2) = 11000&tridot;
			</div>
			
			<div id="ba.db" class="sp_point fs2">Дневной бонус</div>
			<div class="text">
			<p>Каждый день при первом посещении начисляется бонус за вход в игру. Заходя в игру <u>регулярно</u> в течении 10-ти дней, бонус
			  увеличивается. Если за первый день начисляется ${dailyBonus[1]}&tridot;, то за 10-й день уже ${dailyBonus[10]}&tridot;.
			<p><b>Размеры бонуса:</b>
			   <ul>
			   		<c:forEach items="${dailyBonus}" var="dailySum" varStatus="i" begin="1" end="10">
			   				<li>День ${i.count} = ${dailySum}&tridot;</li>
			   		</c:forEach>
			  	</ul>
			<p>После десятого дня цыкл обновляется и начисление начинается с дня 1. Если хотя бы один день в цикле пропущен, начисление 
			  тоже начинается с дня 1. 
			</div>
			
<!-- 			Доминантность -->
			<div id="do" class="sp_point fs1">Доминантность</div>
			<div class="text">
				<p>Доминантность - это еще один показатель успеха персонажа на равне с балансом и состоятельностью. 
				<p>Доминантность является внутриигровым рейтингом игроков. Ею можно меряться друг с другом :)
				<p>Кроме того, ее можно зарабатывать, менять на деньги, а еще она приносит лотерейные билеты.
			
			<div id="do.co" class="sp_point fs2">Начисление</div>
			<p>Очки доминантности можно заработать несколькими способами:
				<ol>
	    		<li>Покупая <b>НОВОЕ</b> имущество.</li>
	   			<p>При покупке нового имущества начисляется <b>10</b> очков доминантности.
	    		
	   			<li>Повышая уровень имущества.</li>
	    		<p>При повышении уровня имущества начисляется: <b>[уровень_к_которому_повышается * 1,5]</b> очков.
	    		<p>Например: пользователь повышает уровень имущества к 3. Значит: 3 * 1,5 = 4,5 ~ 5 (округлено до 5).
	    		   Пользователь получит 5 очков доминантности.
	   			
	    		<li>Повышая уровень кассы имущества.</li>
	    		<p>При повышении уровня кассы начисляется: <b>[уровень_к_которому_повышается]</b> очков.
	    		
	    		<li>Постройка имущества</li>
	    		<p>При постройке имущества тоже начисляются очки доминантности. За каждый тип имущества - разное количество:
	    		<ul>
					<li>Киоск - 0 оч.</li>
					<li>Сельский магазин - 1 оч.</li>
					<li>Магазин канцтоваров - 2 оч.</li>
					<li>Книжный магазин - 3 оч.</li>
					<li>Магазин сладостей - 4 оч.</li>
					<li>Маленький супермаркет - 5 оч.</li>
					<li>Средний супермаркет - 6 оч.</li>
					<li>Большой супермаркет - 7 оч.</li>
				</ul>
	  			</ol>
			
			<div id="do.te" class="sp_point fs2">Обмен на деньги</div>
			<p>Очки доминантности можно обменять на деньги (triangles).
   			<p>Сделать это можно на странице <a href="${pageContext.request.contextPath}/transactions">транзакций</a>.
    		<p>Есть 2 варианта обмена: 
    			<ol>
		    		<li>1 очко на 100&tridot;.</li>
		   			<li>10 очков на 1000&tridot;.</li>
	  			</ol>
    		<p>При этом у вас снимется 1 или 10 очков доминантности и на баланс тут же будет 
    		   начислено 100&tridot; или 1000&tridot;.
    		   
    		<div id="do.lo" class="sp_point fs2">Начисление лотерейных билетов</div>
			<p>Кроме всех вышеописанных функций, очки доминантности каждый день приносят бесплатные лотерейные билеты.
			   Механизм, как и у <a href="#ba.db">ежедневного бонуса</a>, то есть, сегодня зашел в игру - за сегодня 
			   начислились билеты. 
   			<p>За ${domiTicketPrice} очков доминантности начисляется 1 билет.
   			<p>Однако, следует учитывать, что есть ограничение на очки доминантности. Учитываются только ${domiLimit} очков 
   			доминантности. Это значит, что в день максимум может начисляться ${domiLimit} / ${domiTicketPrice} = 
   			${domiLimit / domiTicketPrice} билетов.
			</div> <!-- end of Доминантность -->

			
<!-- 			Повышение уровня -->
			<div id="lu" class="sp_point fs1">Повышение уровня</div>
			<div class="text">
			<p>Можно повышать уровень имущества, кассы имущества. 
			
			<div id="lu.pr" class="sp_point fs2">Имущество</div>
			<p>Улучшая имущество, тем самым повышается прибыль, которую приносит данное имущество. Размер повышения прибыли зависит
			  от размера <a href="#uc">универсальных коэфициентов</a>
			<p>Максимальный уровень имущества до которого можно "дорасти" - <b>${max_prop_lev}</b>.
			<p>Сумма стоимости повышения уровня = Макс. стоим. имущества * k / kdp
			<p>k - универсальный коэф., = уровень к которому идет повышение
			<p>kdc - коэф. уменьшения стоимости повышения уровня = <b>${kdp}</b>.
			
			<div id="lu.ca" class="sp_point fs2">Касса имущества</div>
			<p>Улучшая кассу, тем самым увеличивается ее вместимость. Размер вместимости можно посмотреть в 
			  <a href="#pr.ch">характеристиках имущества.</a>
			<p>Максимальный уровень кассы имущества до которого можно "дорасти" - <b>${max_cash_lev}</b>.
			<p>Сумма стоимости повышения уровня = Нач. стоим. имущества * k / kdc
			<p>k - <a href="#uc">универсальный коэф.</a>, = уровень к которому идет повышение
			<p>kdc - коэф. уменьшения стоимости повышения уровня = <b>${kdc}</b>.
			</div>
			
<!-- 			Лотерея -->
			<div id="lo" class="sp_point fs1">Лотерея</div>
			<div class="text">
			<p>Лотерея - это еще один раздел игры. Основной механизм работы: вы покупаете билеты или вам <a href="#do.lo">
			начисляются	билеты</a> за очки доминантности и вы играете на них. Каждый билет несет в себе какой-то выигрыш, 
			который выбирается случайным образом. У одной статьи выигрыша Минимальный выигрыш - 5&tridot;, максимальный - 1 000 000&tridot;. 
			
			<div id="lo.tb" class="sp_point fs2">Покупка билетов</div>
			<p>Покупка билетов происходит обычным образом. Вы платите деньги, вам тут же начисляют билеты. Покупать можно
			разное количество билетов. И в зависимости от количества покупаемых билетов, на них разнится цена. Т.е., чем 
			больше билетов вы покупаете за один раз, тем меньшая цена на один билет.
			<p>Количество и цены, покупка:
				<ul>
					<li>1 билета - ${ticketsPrice[0]}&tridot; за 1 билет</li>
					<li>10 билетов - ${ticketsPrice[1]}&tridot; за 1 билет</li>
					<li>50 билетов - ${ticketsPrice[2]}&tridot; за 1 билет</li>
				</ul>
			
			<div id="lo.pl" class="sp_point fs2">Игра в лотерею</div>
			<p>При игре в лотерею, так же, как и при покупке, можно выбирать количество билетов. Варианты игры, на: 
				<ul>
					<li>1 билет</li>
					<li>≤5 билетов</li>
					<li>≤10 билетов</li>
					<li>ВСЕ билеты</li>
				</ul>
			<p>Например, у вас есть 8 билетов. 
			<p>Если вы нажмете на "1 билет" - у вас останется 7 билетов.
			<p>Если вы нажмете на "≤5 билетов" - у вас останется 3 билета.
			<p>Если вы нажмете на "≤10 билетов" - у вас не останется билетов.
			<p>Если вы нажмете на "ВСЕ" - у вас также не останется билетов.

			<div id="lo.ap" class="sp_point fs2">Статьи выигрыша (плюшки)</div>
				<div class="text">
					<div id="lo.ap.lu" class="sp_point fs3">Повышение уровня</div>
					<p>В лотерею вы можете выиграть бесплатное повышение уровня имущества или его кассы.
					<p>Количество выигранных плюшек повышения будут отображены на соответствующих кнопках. Кликнув на
					эту кнопку отобразится список имущества, которому можно повысить уровень или уровень его кассы 
					(смотря на какую кнопку вы нажали).
					
					<div id="lo.ap.li" class="sp_point fs3">Лицензии на строительство</div>
					<p>Есть шанс выиграть лицензии на строительство разных уровней (2, 3, 4). Чем больше уровень, тем 
					меньший шанс ее выиграть, т.е. проще выиграть лицензию уровня 2, чем уровня 4.
					<p>Воспользоваться лицензией очень просто. Нужно нажать на соответствующую кнопку на странице лотереи. 
					При нажатии вам будет установлена новая <a href="#pr.bu.li">лицензия</a> на соответствующий срок.
					
					<div id="lo.ap.wi" class="sp_point fs3">Мудрость от всезнающего</div>
					<p>Вам может посчастливиться и Всезнающий, который живет глубоко-глубоко в недрах программного кода 
					и никогда не дремлет, может поделиться с вами своей неиссякаемой мудростью. В мудрость желательно 
					хорошенько вникнуть, потому что Всезнающий просто так никогда ни с кем не делится своими глубокими
					знаниями об устройстве нашей Вселенной. 
					<p>Вы сами поймете, когда он поделился с вами своей мудростью. 
					<p>Если вы увидели, что Всезнающий поведал вам мудрость, которой уже делился с раньше (может уже и 
					не один раз), значит вы плохо вникли в нее прошлый раз. Мудрость нужно постигать, погружаться в нее, 
					добросовестно вникать. Иначе, он будет вам напоминать о ней снова и снова, пока вы не пережуете это все 
					и не расстворите в себе до маленьких комочков генных воспоминаний, называемых опытом души. Шутка. 
					Расслабьтесь и играйте :) Нет никакого Всезнающего, хотя... кто знает...
				</div>
				
				<div id="lo.ac" class="sp_point fs2">Статьи выигрыша (обычные)</div>
				<div class="text">
					<p>В отличии от <a href="#lo.ap">плюшек</a>, при выпадении обычной статьи, такие выигрышы зачисляются 
					сразу. Это значит, что имущество сразу будет введено в эксплуатацию, а деньги сразу поступят на счет, 
					их не нужно будет получать или применять.
					 
					<div id="lo.aс.pr" class="sp_point fs3">Имущество</div>
					<p>Можно выиграть все виды имущества (Киоск, Сельський магазин и т.д.). Вииграть можно только 
					имущество в районе <a href="#pr.ma.ca">Гетто</a> с минимальной ценой продажи для конкретного типа. 
					
					<div id="lo.aс.tr" class="sp_point fs3">Деньги</div>
					<p>Можно также выиграть разное количество денег. Начиная от 5&tridot;, заканчивая Джек-потом в 
					1 000 000&tridot;.
				</div>
			</div>
			
<!-- 			Универсальные коэффициенты -->
			<div id="uc" class="sp_point fs1">Универсальные коэффициенты</div>
			<div class="text">
			<p>Универсальные коэффициенты используются при:
				<ul>
					<li>повышении уровня имущества (расчет суммы);</li>
					<li>повышении уровня кассы имущества (расчет суммы);</li>
					<li>расчете вместимости кассы (в зависимости от уровня);</li>
			  	</ul>
				<table border=1px solid
					style="text-align: center; margin: auto; border-collapse: collapse;">
					<tr>
						<td><b>Уровень</b></td>
						<td>0</td>
						<td>1</td><td>2</td>			
						<td>3</td><td>4</td>
						<td>5</td><td>6</td>
						<td>7</td><td>8</td>
						<td>9</td><td>10</td>
					</tr>
					<tr>
						<td><b>k</b></td>
						<td>1</td>
						<td>${univCoef[1]}</td> <td>${univCoef[2]}</td>
						<td>${univCoef[3]}</td> <td>${univCoef[4]}</td>
						<td>${univCoef[5]}</td> <td>${univCoef[6]}</td>
						<td>${univCoef[7]}</td> <td>${univCoef[8]}</td>
						<td>${univCoef[9]}</td> <td>${univCoef[10]}</td>
					</tr>
				</table>
			</div>
				
<!-- 			             THE END                      -->
			<h1>
		</div>
	<t:footer></t:footer>
	</div>
</t:template>
