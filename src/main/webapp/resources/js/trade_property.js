$('.land_lot_block #buy').on('click', getLandLotPrice);
$('.land_lot_block #info').on('click', landLotInfo);

function getLandLotPrice() {
	var cityArea = extractCityAreaName(this);
	var url = getContextPath() + "/land-lot/price?city_area=" + cityArea;
	$.ajax({
		  type: 'GET',
		  url: url,
		  dataType: "json",
		  async:true
	}).done(function(data) {
		if (data.error) {
			showErrorModal(data);
		} else {
			showBuyModal(data);
		}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText + " " + textStatus);
	});
}

function landLotInfo() {
	var cityArea = extractCityAreaName(this);
	var url = getContextPath() + "/land-lot/price?city_area=" + cityArea;
	$('#modalForInfoTitle').html("Имущество на участках. Район: " + getCityAreaName(cityArea));
	
	var url = getContextPath() + "/land-lot/info?city_area=" + cityArea;
	$.ajax({
		  type: 'GET',
		  url: url,
		  dataType: "json",
		  async:true
	}).done(function(data) {
		var infoHtml = data.info;
		console.log(infoHtml);
		
		$('#modalForInfoBody').html(infoHtml);
		$('#modalForInfo').modal();
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText + " " + textStatus);
	});
	
}

/**
 * @param landLotElement - element (button, etc.) from land lot block
 * @returns city area enum name
 */
function extractCityAreaName(landLotElement) {
	var areaName = $(landLotElement).closest('.land_lot_block').attr('id').substring('land_lot_'.length);
	return areaName.toUpperCase();
}

function getContextPath() {
   return window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
}

/**
 * Открывает окно для отображения ошибок
 */
function showErrorModal(data) {
	$('#modalErrorBody').html(data.message);
	$('#modalError').modal();
}

/**
 * Открывает окно для подтверждения покупки участка
 */
function showBuyModal(data) {
	var info = '<b>Район</b>: <span id="city_area">' + getCityAreaName(data.cityArea) + '</span> </br>' + 
	'<b>Цена участка</b>: ' + data.price + ' ◬'; 
	
	$("#buy_l_lot_btn").unbind('click'); // удалим все обработчики события 'click'
	$("#buy_l_lot_btn").on('click', confirmLandLotBuying);
	
	$('#modalForBuyBody').html(info);
	$('#modalForBuy').modal();
}

/**
 * Подтверждает покупку участка
 */
function confirmLandLotBuying(area) {
	var cityArea = $('#buy_l_lot_btn').closest('.modal-content').find('#city_area').html();
	var serverCityArea = transformCityAreaNameToServerValue(cityArea);

	var url = getContextPath() + "/land-lot/buy?city_area=" + serverCityArea;
	$.ajax({
		  type: 'POST',
		  url: url,
		  dataType: "json",
		  async:true
	}).done(function(data) {
		if (data.error) {
			showErrorModal(data);
		} else {
			location = location; // refresh page
		}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText + " " + textStatus);
	});
}