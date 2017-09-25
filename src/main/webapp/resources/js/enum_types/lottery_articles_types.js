/**
 * Позволяет заменять наименования статтей выигрыша в лотерею, что описаны в java перечислениях на нормальные наименования.
 */

var lotteryArticles = {
	"TRIANGLES" : "Деньги",
	"PROPERTY_UP" : "Повышение имущества",
	"CASH_UP" : "Повышение кассы имущества",
	"LICENSE_2" : "Лицензия на строительство ур. 2",
	"LICENSE_3" : "Лицензия на строительство ур. 3",
	"LICENSE_4" : "Лицензия на строительство ур. 4",
	"PREDICTION" : "Предсказание",
	"STALL" : "Киоск",
	"VILLAGE_SHOP" : "Сельский магазин",
	"STATIONER_SHOP" : "Магазин Канцтоваров",
	"LAND_LOT_GHETTO" : "Участок в гетто",
	"LAND_LOT_OUTSKIRTS" : "Участок на окраине",
	"LAND_LOT_CHINATOWN" : "Участок в чайнатауне",
	"LAND_LOT_CENTER" : "Участок в центре",
	"LOTTERY_TICKET" : "Лотерейные билеты"
}

function getLotteryArticleName(articleName) {
	return lotteryArticles[articleName];
} 

// функция заменяет 'TRIANGLES' на 'Деньги' и др. статьи
function replaceAllLotteryArticlesNames() {
	$.each([ $('.lottery_article_name'), $('.lottery_articles label') ], function(index, array) {
		$.each(array, function(index, element) {
			var newArticleName = getLotteryArticleName($(element).text());
			$(element).html(newArticleName);
		});
	});
};

replaceAllLotteryArticlesNames();