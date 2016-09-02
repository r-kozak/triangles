var buildingsTypes = {
	"STALL" : "Киоск",
	"VILLAGE_SHOP" : "Сельский магазин",
	"STATIONER_SHOP" : "Магазин	канцтоваров",
	"BOOK_SHOP" : "Книжный магазин",
	"CANDY_SHOP" : "Магазин сладостей",
	"LITTLE_SUPERMARKET" : "Маленький супермаркет",
	"MIDDLE_SUPERMARKET" : "Средний супермаркет",
	"BIG_SUPERMARKET" : "Большой супермаркет",
	"RESTAURANT" : "Ресторан",
	"CINEMA" : "Кинотеатр",
	"MALL" : "Торговый центр",
}

function getBuildingTypeName(buildingType) {
	return buildingsTypes[buildingType];
}

// array - массив элементов, например td, которая в себе содержит 'STALL'
// функция заменяет внутри td 'STALL' на 'Киоск'
function replaceBuildingTypeNames(array) {
	$.each(array, function(index, element) {
		var newTypeName = getBuildingTypeName($(element).text());
		$(element).html(newTypeName);
	});
}

function replaceAllBuildingsTypeNames() {
	$.each([ $('.building_type_name'), $('.buildings_types label') ], function(index, array) {
		replaceBuildingTypeNames(array);
	});
};

replaceAllBuildingsTypeNames();