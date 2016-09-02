/**
 * Позволяет заменять наименования районов города, что описаны в java перечислениях на нормальные наименования.
 */

var cityAreas = {
	"GHETTO" : "Гетто",
	"OUTSKIRTS" : "Окраина",
	"CHINATOWN" : "Чайнатаун",
	"CENTER" : "Центр",
}

function getCityAreaName(articleName) {
	return cityAreas[articleName];
} 

// функция заменяет 'GHETTO' на 'Гетто' и др. статьи
function replaceAllCityAreasNames() {
	$.each([ $('.city_area_name'), $('.city_areas label') ], function(index, array) {
		$.each(array, function(index, element) {
			var newArticleName = getCityAreaName($(element).text());
			$(element).html(newArticleName);
		});
	});
}

function replaceCityAreasNamesInSelectElement() {
	$.each($('#city_area option'), function(index, element) {
		var newArticleName = getCityAreaName($(element).text());
		$(element).html(newArticleName);
	});
}

function transformCityAreaNameToServerValue(city_area) {
	var result;
	$.each(cityAreas, function(key, value) {
		if (value == city_area) {
			result = key;
			return false;
		}
	});
	return result;
}

replaceAllCityAreasNames();
replaceCityAreasNamesInSelectElement();