<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
</head>
<body>
<div class="container" id="step0">
	<div class="row">
		<div class="text-center col-sm-6">
			<!-- pedigree 검색 실패시 보여줄 페이지 -->
			<h3>Pedigree 검색 실패</h3>
			<div class="panel panel-default">
				<div class="panel-heading">오류 내용</div>
				<div class="panel-body">
					<div class="panel-body text-left">
						<div id="xmlTree">입력하신 gtin으로 pedigree 정보를 찾을 수 없습니다.</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
	
	<footer class="text-center">
		<div class="container">
			<div class="row">
				<div class="col-xs-12">
					<p>Copyright © KAIST ITC. All rights reserved.</p>
				</div>
			</div>
		</div>
	</footer>
	<jsp:include page="common/footer.jsp" />