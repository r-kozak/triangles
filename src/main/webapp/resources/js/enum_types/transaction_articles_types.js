/**
 * Позволяет заменять наименования статтей транзакций, что описаны в java перечислениях на нормальные наименования.
 */

var transactionArticles = {
	"DAILY_BONUS" : "Ежедневный бонус",
	"CREDIT" : "Кредит",
	"DEPOSIT" : "Депозит",
	"LEVY_ON_PROPERTY" : "Сбор с имущества",
	"BUY_PROPERTY" : "Покупка имущества",
	"PROPERTY_REPAIR" : "Ремонт имущества",
	"UP_CASH_LEVEL" : "Повышение уровня кассы",
	"UP_PROP_LEVEL" : "Повышение уровня имущества",
	"SELL_PROPERTY" : "Продажа имущества",
	"DOMINANT_TO_TRIAN" : "Обмен доминантности",
	"CONSTRUCTION_PROPERTY" : "Постойка имущества",
	"BUY_LICENSE" : "Покупка лицензий",
	"SELL_LICENSE" : "Продажа лицензий",
	"LOTTERY_TICKETS_BUY" : "Покупка лотерейных билетов",
	"LOTTERY_WINNINGS" : "Выигрыш в лотерею",
	"WITHDRAW" : "Вывод средств",
	"LAND_LOTS_BUY" : "Покупка участков",
	"BONUS" : "Бонус",
}

function getTransactionArticleName(articleName) {
	return transactionArticles[articleName];
} 

// функция заменяет 'DAILY_BONUS' на 'Ежедневный бонус' и др. статьи
function replaceAllTransactionArticlesNames() {
	$.each([ $('.transaction_article_name'), $('.transaction_articles label') ], function(index, array) {
		$.each(array, function(index, element) {
			var newArticleName = getTransactionArticleName($(element).text());
			$(element).html(newArticleName);
		});
	});
};

replaceAllTransactionArticlesNames();