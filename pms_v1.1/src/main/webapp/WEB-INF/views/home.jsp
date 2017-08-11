<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
<jsp:include page="common/navi.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
		<!-- PMS 접속 초기화면 -->
		<h1 id="banner">Welcome to GS1 Pedigree System Demo</h1>
		<div style="vertical-align:center; text-align:center">
			<img src="img/overview.png" style="margin-top: 30px; width:70%; height:70%; align:center">
		</div>
		

<jsp:include page="common/footer.jsp"/>