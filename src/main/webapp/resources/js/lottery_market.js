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
	
	$('#up_level_btn').on('click', function() {
		preMarketLevelUp();
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
		} else if (countToSell > licenseCount) {
			$('#modal_confirm').attr('disabled', true);
			$('#labelLicenseCount').addClass("text-danger");
		} else if (countToSell == 0) {
			$('#modal_confirm').attr('disabled', true);
		}
		
		var profit = licensePrices[licenseLevel] * countToSell;
		var premiumPercent = (parseInt($("#marketLevel").text()) - LEVELS_COUNT_WITHOUT_PREMIUM) * PREMIUM_PERCENT_FOR_ONE_LEVEL;
		var premiumSum = 0;
		if (premiumPercent > 0) {
			premiumSum = (profit * premiumPercent) / 100;
		}
		$("#profit").html(profit);
		$("#premiumSum").html(premiumSum);
		$("#totalProfit").html(profit + premiumSum);
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
					 showErrorMessage(data.message); // показать сообщение с ошибкой
				  } else {
					location = location;
				  }
			}).fail(function(jqXHR, textStatus, errorThrown) {
				alert(jqXHR.status + " " + jqXHR.statusText);
			});
	}
	
	/**
	 * Получить требования для повышения уровня Магазина лицензий
	 */
	function preMarketLevelUp() {
		$.ajax({
			  type: 'GET',
			  url: ctx + '/license-market/pre-level-up',
			  dataType: "json",
			  async:false
			}).done(function(data) {
				 if(data.error) {
					 showErrorMessage(data.message); // показать сообщение с ошибкой
				 } else {
					var targetLevel = parseInt($("#marketLevel").text()) + 1;
					$("#modalWindowTitle").html("Повысить до уровня: " + targetLevel);
					$("#text_modal_confirm").html("Повысить");
					
					// сформировать тело модального окна
					var modalBodyHtml = "";
					modalBodyHtml += '<p>Для повышения уровня выполните следующие требования:</p>'
						+ '<table class="table table-striped">';
					
					var isPossibleToUpLevel = true; // удовлетворены ли все требования 
					var requirements = data.requirements;
					for (var i = 0; i < requirements.length; i++) {
						var requirement = requirements[i];
						var sign = '';
						if (requirement.carriedOut) {
							sign = '<span class="text-success glyphicon glyphicon-ok"></span>';
						} else {
							sign = '<span class="text-danger glyphicon glyphicon-remove"></span>';
							isPossibleToUpLevel = false;
						}
						modalBodyHtml += '<tr><td>' + sign + '</td><td>' + requirement.description + '</td></tr>';
					}
					modalBodyHtml += '</table>';
					$("#modalWindowBody").html(modalBodyHtml);
					
					if (isPossibleToUpLevel) {
						$("#modal_confirm").unbind('click'); // удалим все обработчики события 'click' у элемента modal_confirm
						$("#modal_confirm").on("click", function() {
							confirmLevelUp();
						});
						$('#modal_confirm').attr('disabled', false);
					} else {
						$('#modal_confirm').attr('disabled', true);
					}
					
					$("#modalWindow").modal();
		  		 }
			}).fail(function(jqXHR, textStatus, errorThrown) {
				alert(jqXHR.status + " " + jqXHR.statusText);
			});
	}
	
	/**
	 * Подтвердить повышение уровня Магазина лицензий
	 */
	function confirmLevelUp() {
		$.ajax({
			  type: 'GET',
			  url: ctx + '/license-market/confirm-level-up',
			  dataType: "json",
			  async:false
			}).done(function(data) {
				 if(data.error) {
					 showErrorMessage(data.message); // показать сообщение с ошибкой
				 } else {
					location = location;
		  		 }
			}).fail(function(jqXHR, textStatus, errorThrown) {
				alert(jqXHR.status + " " + jqXHR.statusText);
			});
	}
}