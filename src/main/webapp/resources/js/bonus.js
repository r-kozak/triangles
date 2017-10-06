//////////////////// BONUS

$(document).ready(function(){
	$('#bonus_btn').on('click', takeBonus);
	flashBonusBtn();
	checkBonusAvailability();
});

function flashBonusBtn() {
	$('#bonus_btn img').animate({ width: "90%" }, 500, function() {
		$('#bonus_btn img').animate({ width: "100%" }, 500, flashBonusBtn())
	} );
}

function takeBonus() {
  $.ajax({
	  type: 'GET',
	  url: CTX_PATH + "/bonus/take",
	  async:false
	}).done(function(data) {
		// показать информационное окно для всех статтей, кроме выигрыша денег
		if (data.bonus != null && data.bonus.article == 'TRIANGLES') {
			changeBal(data); // показать изменения баланса, если он изменился 
		} else {
			// сформировать сообщение
			var msg = '';
			if (!data.error) {
				msg = 'Вы получили бонус: <b>' + getWinArticleName(data.bonus.article) + '</b>. </br>' +
				  		'Количество: <b>' + data.bonus.count + '</b>';
			} else {
				msg = data.message;
			}
			$('#bonus_modal_body').html(msg);
			$('#bonus_modal').modal();
		}
		_checkBonusAvailabilityRequest();
  	}).fail(function(jqXHR, textStatus, errorThrown) {
		console.log(jqXHR.status + " " + jqXHR.statusText);
	});
}

function checkBonusAvailability() {
	_checkBonusAvailabilityRequest();
	setTimeout(function() {
		checkBonusAvailability();
	}, 30000); // every 30 seconds check whether bonus is available or not
}

function _checkBonusAvailabilityRequest() {
	$.ajax({
		type : 'GET',
		url : CTX_PATH + "/bonus/available"
	}).done(function(data) {
		if (data.available) {
			$('#bonus_btn').show();
			tooglePageTitle(true);
		} else {
			$('#bonus_btn').hide();
			tooglePageTitle(false);
		}
	}).fail(function(jqXHR, textStatus, errorThrown) {
		console.log(jqXHR.status + " " + jqXHR.statusText);
	});
}

function tooglePageTitle(showAsterisk) {
	var title = $('title').html();
	var BONUS_LABEL = "[b] ";
	
	if (showAsterisk && !title.startsWith(BONUS_LABEL)) {
		$('title').html(BONUS_LABEL + title);
	} else if (!showAsterisk && title.startsWith(BONUS_LABEL)) {
		$('title').html(title.replace(BONUS_LABEL, ""));
	}
}
