var licensePrices = {
	"2" : "16000",
	"3" : "32000",
	"4" : "64000"
};

/**
 * Количество первых уровней, для которых нет надбавки к стоимости лицензий при их продаже. За каждый уровень после этого
 * числа начисляется <b>10%</b> к продажной стоимости лицензии.
 * Допустим, значение этой константы = 4. Если уровень магазина <= 4, значит к продажной стоимости нет никаких надбавок.
 * Если же, к примеру, уровень магазина = 6, тогда:
 * (6 - 4) * 10% = 20% - это процент надбавки к стоимости лицензии.
 */
var LEVELS_COUNT_WITHOUT_PREMIUM = 4;

var PREMIUM_PERCENT_FOR_ONE_LEVEL = 10;

window.onload = function(){
	/**
	 * По клику на кнопку продажи любого уровня лицензий.
	 */
	$('.sell_label').on('click', function() {
		var errorMsg = $(this).attr('data-requirement-message');
		var canBeSold = errorMsg == undefined; // лицензия может быть продана, т.к. нет никаких предупреждающих сообщений
		
		if (canBeSold) {
			sellLicense(this.id);
		} else {
			showErrorMessage(errorMsg);
		}
	});
	
	/**
	 * Показывает сообщение с ошибкой.
	 */
	function showErrorMessage(msg) {
		$("#modalWindowTitle").html("Недоступно");
		$("#modalWindowBody").html('<div style="color:red">' + msg + '</div>');
		$("#text_modal_confirm").html("Ok");
		$("#modalWindow").modal();
		
		$("#modal_confirm").unbind('click'); // удалим все обработчики события 'click' у элемента modal_confirm
		$("#modal_confirm").on("click", function() {
			$("#modalWindow").modal('toggle');	
		});
	};
	
	/**
	 * Позволяет продать лицензии определенного уровня.
	 */
	function sellLicense(licenseLevel) {
		// количество, доступное для продажи
		var licenseCount = parseInt($("#lic" + licenseLevel + "CountVal").text());
		
		$("#modalWindowTitle").html("Продать лицензии уровня " + licenseLevel);
		$("#modalWindowBody").html('Уровень лицензий: <b>' + licenseLevel + '</b> <br/>'
				+ '<span id="labelLicenseCount">Количество в наличии: <b>' + licenseCount + '</b></span> <br/>'
				+ 'Количество для продажи:&nbsp;&nbsp;<input type="number" min="0" max="' + licenseCount + '" id="countToSell"> <br/>'
				+ 'Прибыль от продажи: <b><span id="profit">0</span>&tridot;</b> <br/>'
				+ 'Надбавка за уровень магазина: <b><span id="premiumSum">0</span>&tridot;</b> <br/>'
				+ 'Всего прибыль от продажи: <b><span id="totalProfit">0</span>&tridot;</b> <br/>'
		);
		$("#text_modal_confirm").html("Продать");
		$("#modalWindow").modal();
		$('#modal_confirm').attr('disabled', true);
		
		$("#countToSell").bind("propertychange change click keyup input paste", function() {
			onChangeCountToSell(licenseCount, licenseLevel);
		});
		
		$("#modal_confirm").unbind('click'); // удалим все обработчики события 'click' у элемента modal_confirm
		$("#modal_confirm").on("click", function() {
			var countToSell = parseInt($("#countToSell").val());
			confirmLicenseSelling(countToSell, licenseLevel);	// подтвердить продажу лицензий
		});
	};
	
	/**
	 * Отрабатывает при изменении кначения input-a, куда вводится количество лицензий для продажи
	 * licenseCount - количество лицензий всего
	 */
	function onChangeCountToSell(licenseCount, licenseLevel) {
		var countToSell = parseInt($('#countToSell').val());
		if (countToSell > 0 && countToSell <= licenseCount) {
			$('#modal_confirm').attr('disabled', false);
			$('#labelLicenseCount').removeClass("text-danger");
			
			var profit = licensePrices[licenseLevel] * countToSell;
			var premiumPercent = (parseInt($("#marketLevel").text()) - LEVELS_COUNT_WITHOUT_PREMIUM) * PREMIUM_PERCENT_FOR_ONE_LEVEL;
			var premiumSum = 0;
			if (premiumPercent > 0) {
				premiumSum = (profit * premiumPercent) / 100;
			}
			$("#profit").html(profit);
			$("#premiumSum").html(premiumSum);
			$("#totalProfit").html(profit + premiumSum);
		} else if (countToSell > 0) {
			$('#modal_confirm').attr('disabled', true);
			$('#labelLicenseCount').addClass("text-danger");
		}
	}
	
	/**
	 * Подтвердить продажу лицензий
	 */
	function confirmLicenseSelling(countToSell, licenseLevel) {
		$.ajax({
			  type: 'POST',
			  url: ctx + '/confirm-license-selling',
			  data:  { licenseCount: countToSell, licenseLevel: licenseLevel },
			  dataType: "json",
			  async:false
			}).done(function(data) {
				 if(data.error) {
					showErrorMsg(data); // показать сообщение с ошибкой
				  } else {
					location = location;
				  }
			}).fail(function(jqXHR, textStatus, errorThrown) {
				alert(jqXHR.status + " " + jqXHR.statusText);
			});
	}
	
}