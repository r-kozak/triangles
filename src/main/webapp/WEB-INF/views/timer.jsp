<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>                                                  
<html>                                                                                                           
<head>                                                                                                           
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">                                               
<title>jQuery Countdown</title>                                                                                  
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery.countdown.css" type="text/css" />
<style type="text/css">                                                                                          
#defaultCountdown {                                                                                              
	width: 240px;                                                                                                 
	height: 45px;                                                                                                 
}                                                                                                                
</style>                                                                                                         
<script	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>                      
<script src="${pageContext.request.contextPath}/resources/js/jquery.plugin.js"></script>                            
<script src="${pageContext.request.contextPath}/resources/js/jquery.countdown.js"></script>                         
<script src="${pageContext.request.contextPath}/resources/js/jquery.countdown-ru.js"></script>                      
<script>                                                                                                         
	$(function() {                                                                                                
		var austDay = new Date(parseInt("<c:out value='${to}'/>"));                                           
		//austDay = new Date(austDay.getFullYear() + 1, 0, 26);                                               
		$('#defaultCountdown').countdown({                                                                    
			until : austDay,
			expiryUrl: "${requestScope['javax.servlet.forward.request_uri']}"
		});                                                                                                   
	});                                                                                                           
</script>                                                                                                        
</head>                                                                                                          
<body>                                                                                                           
	<div id="defaultCountdown"></div>                                                                             
	<c:out value='${to}'/>                                                                                        
</body>                                                                                                          
</html>                                                                                                          