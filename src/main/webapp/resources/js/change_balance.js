// функция для движения окна с суммой, на которую изменился баланс после какой-то операции
function popUp(a, bloc) {
	var X_BALANCE_POSITION = 87; // percent
	var winWidth = window.screen.availWidth;
	var balancePositionFromRight = winWidth - ((winWidth * X_BALANCE_POSITION) / 100);
	$(bloc).css({
		"left" : winWidth - balancePositionFromRight,
		"top" : 135,
		"opacity" : "1"
	});
	$(bloc).animate({
		"top" : "-=50px",
		"opacity" : "0.35"
	}, 700);
	$(bloc).animate({
		"top" : "-=20px",
		"opacity" : "0"
	}, 700);
	$(bloc).css({
		"z-index" : "1051"
	});
};

// функция для изменения баланса и состоятельности
// данные приходят при отправке запросов на сервер
// данные в формате JSON
function changeBal(data) {
	if (data.changeBal == null) {
		return;
	}
	
    $('#balChan').html(data.changeBal + "&tridot;"); //блок с балансом для движения вверх
    popUp(data.changeBal, "#balChan"); //движение вверх блока с балансом
    $('#balanceVal').html(data.newBalance); //новое значение баланса
    $('#solvencyVal').html(data.newSolvency); //новое значение состоятельности
    $('#domiVal').html(data.newDomi); //новое значение доминантности
};

function setPage(obj) {
    document.getElementById('page').value = obj.getAttribute("value");
    document.getElementById('searchForm').submit();
    return false;
};

//отправка запроса на продажу / отмену продажи имущества
function sendSellPost(ids, url, isNeedData) {
  $.ajax({
	  type: 'POST',
	  url: url,
	  data:  { propIds: JSON.stringify(ids), action: "sell" },
	  dataType: "json",
	  async:false
	}).done(function(data) {
		if (isNeedData) {
		  if (!data.error) {
			sellProperty(data);		  
		  } else {
			  $('#modalErrorBody').html('Ошибка! Нельзя отменить. Возможно имущество уже купили. Проверьте ' + 
					  '<a href="' + CTX_PATH + '/transactions" target="_blank">транзакции.</a>' + 
					  'Или сразу идите <a href="' + CTX_PATH + '/home">домой</a>, потому что сдесь уже делать нечего...');
			  $('#modalError').modal();
		  }
		}
  	});
}

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
          $('#bonus_btn').hide();
          
          var msg = '';
		  if (!data.error) {
			  msg = 'Вы получили бонус: <b>' + getWinArticleName(data.bonus.article) + '</b>. </br>' +
			  		'Количество: <b>' + data.bonus.count + '</b>';
		  } else {
			  msg = data.message;
		  }
		  $('#bonus_modal_body').html(msg);
		  $('#bonus_modal').modal();
		  
		  changeBal(data); // показать изменения баланса, если он изменился 
	  	}).fail(function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR.status + " " + jqXHR.statusText);
		});
	  
}

function checkBonusAvailability() {
	$.ajax({
		type : 'GET',
		url : CTX_PATH + "/bonus/available"
	}).done(function(data) {
		if (data.available) {
			$('#bonus_btn').show();
		} else {
			$('#bonus_btn').hide();
		}
		setTimeout(function() {
			checkBonusAvailability();
		}, 30000); // every 30 seconds check whether bonus is available or not
	}).fail(function(jqXHR, textStatus, errorThrown) {
		console.log(jqXHR.status + " " + jqXHR.statusText);
	});
}