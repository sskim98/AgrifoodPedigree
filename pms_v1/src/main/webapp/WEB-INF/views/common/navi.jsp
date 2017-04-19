<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
</head>
<body>
	<!-- menu 버튼들 정의 -->
	<nav class="navbar navbar-default navbar-inverse">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="collapse navbar-collapse">
				<!-- home에 대한 링크 -->
				<a class="navbar-brand" href="${homeUrl}/">GS1 - KAIST IT융합연구소 Pedigree 시스템</a>
				<ul class="nav navbar-nav navbar-right">
					<c:if test="${principal.username}">
						<li style="color:white;"><h6>User ID: &nbsp; <strong><sec:authentication property="principal.username" /></strong>&nbsp; &nbsp;</h6></li>
					</c:if>
					<c:if test="${!principal.username}">
						<li style="color:white;"><h6>User ID:  &nbsp; <strong><sec:authentication property="name" /></strong>&nbsp; &nbsp;</h6></li>
					</c:if>
					<sec:authorize access="isAuthenticated()">
						 <li style="color:white;"><h6><sec:authentication property="principal.authorities"/>&nbsp; &nbsp;</h6></li>
					</sec:authorize>
					<li>
						<c:url value="/j_spring_security_logout" var="logoutUrl"/>
						<form action="${logoutUrl}" method="post"> 
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="submit" value="LogOut" style="vertical-align:center; font-size:0.5em; height:15px; margin-top:8px; padding-top:0px; background-color:#444444; color:white;"/>
						</form>
					</li>
				</ul>
			</div>
		</div>
		<div class="container-fluid">
			<div class="collapse navbar-collapse"
				id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav">
					<!-- menu 버튼들 -->
					<li><a href="${homeUrl}/certificate">인증서</a></li>
					<li><a href="${homeUrl}/user_info">사용자 정보</a></li>
					<li><a href="${homeUrl}/userlist">사용자 리스트</a></li>
					<li><a href="${homeUrl}/company_info">회사 정보</a></li>
					<li><a href="${homeUrl}/partnerlist">파트너 정보</a></li>
					<li><a href="${homeUrl}/productlist">제품 정보</a></li>
					<li><a href="${homeUrl}/epcisEvent">EPCIS 이벤트</a></li>
					<li><a href="${homeUrl}/pedigreelist">페디그리</a></li>
				</ul>
			</div>
			</div>
	</nav>