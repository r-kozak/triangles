$('.land_lot_block #buy').on('click', getLandLotPrice);
$('.land_lot_block #info').on('click', landLotInfo);

function getLandLotPrice() {
	var cityArea = getCityAreaName(this);
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

}

/**
 * @param landLotElement - element (button, etc.) from land lot block
 * @returns city area enum name
 */
function getCityAreaName(landLotElement) {
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
	var info = '<b>Район</b>: <div id="city_area">' + getCityAreaName(data.cityArea) + '</div> </br>' + 
	'<b>Цена участка</b>: ' + data.price; 
	
	$('#modalForInfoBody').html(info);
	$('#modalForInfo').modal();
}

/**
 * Подтверждает покупку участка
 */
function confirmLandLotBuying(area) {
	
}