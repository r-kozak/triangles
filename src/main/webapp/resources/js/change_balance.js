// функция для движения окна с суммой, на которую изменился баланс после какой-то операции
function popUp(a, bloc) {
	$(bloc).css({
		"left" : (window.screen.availWidth) - a.length * 30,
		"top" : (window.screen.availHeight - ((window.screen.availHeight * 73) / 100)) / 2,
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