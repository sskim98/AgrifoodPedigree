<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
<jsp:include page="common/navi.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
		<script type="text/javascript">
		// 인증서 등록
		$(document).ready(function() {			
			var privateKey="${privateKey}";
			var pmsCertificate = "${pmsCertificate}";
			var caCertificate = "${caCertificate}";
			// 개인키 및 인증서 정보를 화면에 표시
			$("#private_key").append(privateKey);
			console.log("test");
			$("#certificate").append(pmsCertificate);
		});
		</script>
			
	<div class="container" id="step1">
	    <div class="container">
	        <div class="row">
	            <div class="text-center col-sm-6">
	            <h3>인증서 선택</h3>
	                <div class="panel panel-default">
	                    <div class="panel-heading">PKI 인증서 및 private key를 등록</div>
	                    <div class="panel-body">
	                    	<form class="form-horizontal" action="${homeUrl}/uploadCertificate?${_csrf.parameterName}=${_csrf.token}" name="f" enctype="multipart/form-data" method="post" accept-charset="UTF-8" onsubmit="document.charset='UTF-8'">
								<div class="form-group">
									<input type="hidden" name="from" value="certificate_page" />
									<label for="uploadPrivateKey" class="col-sm-3 control-label">Private Key</label>
									<div class="col-sm-8">
										<!-- 개인키 파일 업로드 버튼 -->
										<input id="btn_upload_privateKey" name="uploadPrivateKey" required="required" type="file" value="upload privateKey" style="position: relative; margin-left: 5px; float: inherit; width: 100%;"/>
									</div>
								</div>
								<div class="form-group">
									<label for="uploadPMSCertificate" class="col-sm-3 control-label">PMS Certificate</label>
									<div class="col-sm-8">
										<!-- pms 인증서 파일 업로드 버튼 -->
										<input id="btn_upload_pms_certificate" name="uploadPMSCertificate" required="required" type="file"  value="upload PMS Certificate" style="position: relative; margin-left: 5px; float: inherit; width: 100%; border: 0px solid;"/>
									</div>									
								</div>
								<div class="form-group">
									<label for="uploadCACertificate" class="col-sm-3 control-label">CA Certificate</label>
									<div class="col-sm-8">
										<!-- ca 인증서 파일 업로드 버튼 -->
										<input id="btn_upload_ca_certificate" name="uploadCACertificate"  type="file"  value="upload CA Certificate" style="position: relative; margin-left: 5px; float: inherit; width: 100%; border: 0px solid;"/>
									</div>									
								</div>
								<br><br>
								<input id="btn_upload_privateKey" class="btn btn-primary"  name="uploadCertificateInfo" type="submit" value="upload"
										style="position: relative; margin-left: 5px; float: inherit; width: 30%;" />
						</form>
							
	                    </div>
	                </div>
		        </div>
	            <div class="text-center col-sm-6">
	                <h3>생성된 인증서 정보</h3>
	                <div class="row">
	                  <div class="panel panel-default">
	                    <div class="panel-heading"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span> Private Key - 개인키</div>
	                    <div id="private_key" class="panel-body wordwrap text-left"></div>
	                  </div>
	                </div>
	                <!--
	                <form class="form-horizontal" name="f" method="post" accept-charset="UTF-8" onsubmit="document.charset='UTF-8'">
						<div class="form-group">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="hidden" name="from" value="pedigreeinfo_page" />
							<input id="btn_submit_step1" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/generateCertificate" value="인증서 생성"
								style="position: relative; margin-left: 5px; float: inherit; width: 30%;">
							<input type="hidden" name="from" value="certificate_page" />
						</div>
					</form>
					-->
	                <div class="row">
	                  <div class="panel panel-default">
	                    <div class="panel-heading"><span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span> Certificate - 발급된 인증서</div>
	                    <div id="certificate" class="panel-body wordwrap text-left"></div>
	                  </div>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>

		<hr>
		<footer class="text-center">
		  <div class="container">
		    <div class="row">
		      <div class="col-xs-12">
		        <p>Copyright © KAIST ITC. All rights reserved.</p>
		      </div>
		    </div>
		  </div>
		</footer>

		
<jsp:include page="common/footer.jsp"/>