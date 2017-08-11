<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
	<body>
		<!-- 사용자 인증이 실패했을 경우 표시되는 페이지 -->
		<h1 id="banner">Unauthorized Access !!</h1>
	
		<hr />
	
		<c:if test="${not empty error}">
			<div style="color:red"> 
				Caused : ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
			</div>
		</c:if>
	
		<p class="message">Access denied!</p>
		<a href="${homeUrl}/login">Go back to login page</a> 
		<h6><a href="javascript:window.history.back()">Back to last page</a></h6>
	</body>
</html>