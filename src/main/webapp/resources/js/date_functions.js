function setDateValue(elemId, yest) {
    //если дата пустая 
    if(document.getElementById(elemId).value == '') {
    	var date = new Date();
        document.getElementById(elemId).value = dateToStr(date);
    } else {
    	var dateStr = document.getElementById(elemId).value;
        var date = new Date(dateStr.replace(/\-/g,'/'));
        date.setDate((yest) ? date.getDate() - 1 : date.getDate() + 1); 
        document.getElementById(elemId).value = dateToStr(date);
    }
}

function dateToStr(date) {
    return date.getFullYear() + '-' + ('0' + (date.getMonth() + 1)).slice(-2) + '-' + ('0' + date.getDate()).slice(-2);
}