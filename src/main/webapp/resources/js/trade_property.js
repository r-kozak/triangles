$('.land_lot_block #buy').on('click', getLandLotPrice);
$('.land_lot_block #info').on('click', landLotInfo);
$('body').on('click', '.lot_sell_property_btn', sellPropertyOrCancel);
$('body').on('click', '.to_exploitation_btn', constrProjectToExploitation);

function getLandLotPrice() {
	var cityArea = extractCityAreaName(this);
	var url = CTX_PATH + "/land-lot/price?city_area=" + cityArea;
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

/**
 * Открывает окно с информацией, чем заняты участки в конкретном районе (каким имуществом или объектами строительства)
 */
function landLotInfo() {
	var cityArea = extractCityAreaName(this);
	$('#modalForInfoTitle').html("Имущество на участках. Район: " + getCityAreaName(cityArea));
	
	var url = CTX_PATH + "/land-lot/info?city_area=" + cityArea;
	$.ajax({
		  type: 'GET',
		  url: url,
		  dataType: "json",
		  async:true
	}).done(function(data) {
		var infoHtml = '<table class="table"><tr class="tableTitleTr"><td>Номер</td><td>Имя</td><td>Продажа</td></tr>';
		
		$(data.info).each(function(index, infoElement) {
			infoHtml += '<tr><td>'+ (index + 1) +'</td>';
			if (infoElement.isFunctioningProperty) {
				// если это уже функционирующее имущество
				infoHtml += '<td>'+ infoElement.propertyName +'</td>';
				infoHtml += '<td><a class="btn btn-danger btn-sm lot_sell_property_btn" id="property_' + infoElement.id + '"';
				if (infoElement.isOnSale) {
					infoHtml += ' title="Отмена продажи" data-toggle="tooltip"><span class="glyphicon glyphicon-remove-circle"></span>';
				} else {
					infoHtml += ' title="Продажа" data-toggle="tooltip"><span class="glyphicon glyphicon-briefcase"></span>';
				}
				infoHtml += '</a></td>';
			} else {
				// имущество находится на стадии строительства
				if (infoElement.completedPercent < 100) {
					infoHtml += '<td>Строится...</td>';
					infoHtml += '<td>'+ infoElement.completedPercent +'%</td>';
				} else {
					infoHtml += '<td>Готово!</td>';
					infoHtml += '<td><a title="Принять в эксплуатацию" class="btn btn-success btn-sm to_exploitation_btn" id="constr_project_' + 
						+ infoElement.id + '"><span class="glyphicon glyphicon-ok"></span></a></td>';
				}
			}
			infoHtml += '</tr>';
		});
		
		infoHtml += '</table>';
		$('#modalForInfoBody').html(infoHtml);
		$('#modalForInfo').modal();
		
		$('[data-toggle="tooltip"]').tooltip(); // для отображения подсказок
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

	var url = CTX_PATH + "/land-lot/buy?city_area=" + serverCityArea;
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

/**
 * Выставляет имущество на продажу или снимает с продажи
 */
function sellPropertyOrCancel() {
	var btn = this;
	var propId = $(btn).attr("id").substring("property_".length);

	$.ajax({
	  type: 'POST',
	  url: CTX_PATH + "/property/sell",
	  data:  { propIds: JSON.stringify(propId), action: "sell" },
	  dataType: "json",
	  async:false
	}).done(function(data) {
	  if (!data.error) {
		if (data.onSale) {
		  $(btn).html('<span class="glyphicon glyphicon-remove-circle"></span>');
		  $(btn).attr("data-original-title", 'Отмена продажи');
	 	} else {
		  $(btn).html('<span class="glyphicon glyphicon-briefcase"></span>');
		  $(btn).attr("data-original-title", 'Продать');
	  	}
		$('#'+$(btn).attr('aria-describedby')).remove(); // удаление подсказки
	  } else {
		  $('#modalErrorBody').html('Ошибка! Нельзя отменить. Возможно имущество уже купили. Проверьте ' + 
				  '<a href="' + CTX_PATH + '/transactions" target="_blank">транзакции.</a>' + 
				  'Или сразу идите <a href="' + CTX_PATH + '/home">домой</a>, потому что сдесь уже делать нечего...');
		  $('#modalError').modal();
	  }
  	});
}

/**
 * принимает объект строительства в эксплуатацию
 */
function constrProjectToExploitation() {
	var projectId = $(this).attr("id").substring("constr_project_".length); 
		
	$.ajax({
	  type: 'POST',
	  url: CTX_PATH + "/building/from-construct",
	  data:  { constrId: projectId },
	  dataType: "json",
	  async:false
	}).done(function(data) {
		if (data.error) {
			// показать сообщение с ошибкой
			$('#modalErrorBody').html(data.message);
			$('#modalError').modal();
		} else {
			location = location;
		}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		alert(jqXHR.status + " " + jqXHR.statusText);
	});
}