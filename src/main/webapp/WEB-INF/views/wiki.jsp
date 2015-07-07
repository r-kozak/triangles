<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<title>Home</title>
<style>
#contents {
	border: 1px solid;
	width: auto;
	background: #FFFBF6;
	display: inline-block;
	padding: 10 20 0 0;
	float: left;
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

.sp_point1 {
	margin: 20 0 20 0;
	border-bottom: 1px solid gray;
	padding: 0 0 0 20;
	font-size: 25;
	display: -webkit-box;
}

.sp_point2 {
	padding: 0 0 10 20;
	font-size: 20;
	font-weigth: bold;
}

.fixed_button a {
	position: fixed;
	bottom: 50px;
	height: 8;
	right: 50px;
	z-index: 999;
	color: #FF9147;
	border: 2px dotted;
	padding: 10 18 30 18;
	text-decoration: none;
	font-size: 100%;
	background: transparent;
	border-radius: 50%;
}

.fixed_button:hover a {
	color: #FF6900;
	border: 4px dotted;
	font-weight: bold;
}

.text {
	font-size: 15;
	padding: 5 0 5 10;
	line-height: 1.4;
	letter-spacing: 1;
}

.wrapper {
	margin: 100 15 0 15
}
h1 {
	margin-bottom: 50;
	border-bottom: 2px solid lightgrey;
}

</style>

<t:template>
	<div class="content">
		<div class="wrapper">
		<h1 align="center">wiki</h1>
			<div class="fixed_button">
				<a href="">↑</a>
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
							</ul>
						<li><a href="#pr.bu">Стройка</a></li>
							<ul>
								<li><a href="#pr.bu">Типы строителей</a></li>
							</ul>
					</ol>
					<li><a href="#ba">Баланс</a></li>
						<ul>
							<li><a href="#ba.tr">Транзакции</a></li>
							<li><a href="#ba.cd">Кредит/депозит</a></li>
							<li><a href="#ba.so">Состоятельность</a></li>
						</ul>
					<li><a href="#Ghj">Ghj</a></li>
				</ol>
			</div>

			<div id="Def" class="sp_point1">fsdf</div>
			<div class="text">
				// Джерельний файл (.cpp)<br /> // Ініціалізація статичного члена.<br />
				Singleton Singleton::_instance;<br /> Singleton&
				Singleton::getInstance()<br /> {<br /> return _instance;<br /> }<br />
				Реалізації на Java[ред. • ред. код]<br /> Базовими засобами
				мови[ред. • ред. код]<br /> Enum singleton, починаючи з Java 1.5:<br />
			</div>
		</div>
</t:template>
