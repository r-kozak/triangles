function popUp(a, bloc) {
	var b = 3;
	if (a.length >= 7)
		b = 4;

	$(bloc).css({
		"left" : (window.screen.availWidth) - (16 - (a.length - b)) * a.length,
		"top" : (window.screen.availHeight - 727) / 2,
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

function setPage(obj) {
    document.getElementById('page').value = obj.getAttribute("value");
    document.getElementById('searchForm').submit();
    return false;
};

function searchFormSubmit(f) {
	if(document.getElementById('sell_pr_from').value == "") {
		document.getElementById('sell_pr_from').value = 0;
	}
	if(document.getElementById('sell_pr_to').value == "") { 
		document.getElementById('sell_pr_to').value = 0;
	}
	return f.submit();
};