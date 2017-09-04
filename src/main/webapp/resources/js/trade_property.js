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
		alert(data.price);
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText + " " + textStatus);
		console.log(errorThrown);
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
