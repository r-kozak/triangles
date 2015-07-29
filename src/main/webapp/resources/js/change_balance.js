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

function setPage(obj) {
    document.getElementById('page').value = obj.getAttribute("value");
    document.getElementById('searchForm').submit();
    return false;
};