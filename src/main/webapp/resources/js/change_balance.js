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
		"z-index" : "18"
	});
};

// функция для изменения баланса и состоятельности
// данные приходят при отправке запросов на сервер
// данные в формате JSON
function changeBal(data) {
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
					  '<a href="' + getContextPath() + '/transactions" target="_blank">транзакции.</a>' + 
					  'Или сразу идите <a href="' + getContextPath() + '/home">домой</a>, потому что сдесь уже делать нечего...');
			  $('#modalError').modal();
		  }
		}
  	});
}

function getContextPath() {
   return window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
}