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
			var certificateType = "${certificateType}";  // PKI or Private
			var certificateValidity = "${certificateValidity}";  // 인증서 검증 결과
			var privateKey="${privateKey}";  // PMS 개인키
			var pmsCertificate = "${pmsCertificate}";  // PMS 인증서
			var caCertificate = "${caCertificate}";  // PMS CA 인증서
			var privateKeyFileName="${privateKeyFileName}";  // PMS 개인키 파일 이름
			var pmsCertificateFileName = "${pmsCertificateFileName}";  // PMS 인증서 파일 이름
			var caCertificateFileName = "${caCertificateFileName}";  // PMS CA 인증서 파일 이름
			var privateRootCertificateFileName = "${privateRootCertificateFileName}";  
			
			// 인증서 관련 정보를 표시
			$("#private_key").append(privateKey);
			if(certificateValidity.length >0) {
				$("#certificateValidity").append(certificateValidity);
			}
			$("#certificate").append(pmsCertificate);
			$("#caCertificate").append(caCertificate);
			if(privateKeyFileName.length > 0) {
				$("#storedPrivateKeyFileName").val("저장된 파일: " + privateKeyFileName);
			}
			if(pmsCertificateFileName.length > 0) {
				$("#storedPmsCertificateFileName").val("저장된 파일: " + pmsCertificateFileName);
			}
			if(caCertificateFileName.length > 0) {
				$("#storedCaCertificateFileName").val("저장된 파일: " + caCertificateFileName);
			}
			if(privateRootCertificateFileName.length > 0) {
				$("#storedPrivateRootCertificateFileName").val("저장된 파일: " + privateRootCertificateFileName);
			}

			if(certificateType != undefined) {
				changeCertificateType(certificateType);
			}
		});
		
		// 인증서 파일 경로에서 파일명만 추출 후 입력
		function setFileName(location, path) {
			var filePath = path.split("\\");
			var fileName = filePath[filePath.length-1]; 
			document.getElementById(location).value = fileName;
		}
		// 사용자가 인증서의 타입을 변경하는 루틴
		function changeCertificateType(type) {
			if(type == "PKI") {
				$("#certType_pki").prop("checked", true);
				$("#uploadPmsCertificateLabel").text("PKI Node Certificate");
				$("#uploadCaCertificateLabel").text("PKI CA Certificate");
			}
			else {
				$("#certType_private").prop("checked", true);
				$("#uploadPmsCertificateLabel").text("Private Node Certificate");
				$("#uploadCaCertificateLabel").text("Private Root Certificate");
			}
			
		}
		</script>
			
	<div class="container" id="step1">
	    <div class="container">
	        <div class="row">
	            <div class="text-center col-sm-6">
	                <div class="panel panel-default">
	                    <div class="panel-heading">PKI 인증서 및 private key를 등록</div>
	                    <div class="panel-body">
	                    	<form class="form-horizontal" action="${homeUrl}/uploadCertificate?${_csrf.parameterName}=${_csrf.token}" name="f" enctype="multipart/form-data" method="post" accept-charset="UTF-8" onsubmit="document.charset='UTF-8'">
								<div class="form-group">
									<label for="CertificateType" class="col-sm-3 control-label">Certificate Type</label>
									<div class="col-sm-8" style="text-align: left;">
										<input id="certType_pki" type="radio" name="certificateType" value="PKI" onclick="changeCertificateType(this.value)" style="margin-top: 10px; padding-bottom: 10px; "> PKI
  										<input id="certType_private" type="radio" name="certificateType" value="Private" onclick="changeCertificateType(this.value)" style="margin-left: 15px; margin-top: 10px; padding-bottom: 10px; "> Private
									</div>									
								</div>
								<div class="form-group">
									<label for="Validity" class="col-sm-3 control-label">Validity</label>
									<div class="col-sm-8">
										<p id="certificateValidity" style="position: relative; margin-left: 5px; margin-top: 7px; padding-bottom: 10px; float: inherit; border: 0px solid;"></p>
									</div>									
								</div>
								<div class="form-group">
									<input type="hidden" name="from" value="certificate_page" />
									<label for="uploadPrivateKey" class="col-sm-3 control-label">Private Key</label>
									<div class="col-sm-8">
										<!-- 개인키 파일 업로드 버튼 -->
										<input id="storedPrivateKeyFileName" name="storedPrivateKeyFileName" type="text" value="File was not found in database" style="position: relative; margin-left: 5px; margin-top: 5px; padding-bottom: 10px; float: inherit; width: 100%; border: 0px solid; color: red;" />
										<input id="btn_upload_privateKey" onchange="setFileName('privateKeyFileName', this.value)" name="privateKey" required="required" type="file" value="upload privateKey" style="position: relative; margin-left: 5px; float: inherit; width: 100%;"/>
									</div>
								</div>
								<div class="form-group">
									<label id="uploadPmsCertificateLabel" for="uploadPMSCertificate" class="col-sm-3 control-label">PKI Node Certificate</label>
									<div class="col-sm-8">
										<!-- pms 인증서 파일 업로드 버튼 -->
										<input id="storedPmsCertificateFileName" name="storedPrivateKeyFileName" type="text" value="File was not found in database" style="position: relative; margin-left: 5px; margin-top: 5px; padding-bottom: 10px; float: inherit; width: 100%; border: 0px solid; color: red;" />
										<input id="btn_upload_pms_certificate" onchange="setFileName('pmsCertificateFileName', this.value)" name="pmsCertificate" required="required" type="file"  value="upload PMS Certificate" style="position: relative; margin-left: 5px; float: inherit; width: 100%; border: 0px solid;"/>
									</div>									
								</div>
								<div id="caCertificateFormGroup" class="form-group">
									<label id="uploadCaCertificateLabel" for="uploadCACertificate" class="col-sm-3 control-label">PKI CA Certificate</label>
									<div class="col-sm-8">
										<!-- ca 인증서 파일 업로드 버튼 -->
										<input id="storedCaCertificateFileName" name="storedCaCertificateFileName" type="text" value="File was not found in database" style="position: relative; margin-left: 5px; margin-top: 5px; padding-bottom: 10px; float: inherit; width: 100%; border: 0px solid; color: red;" />
										<input id="btn_upload_ca_certificate" onchange="setFileName('caCertificateFileName', this.value)" name="caCertificate"  type="file"  value="upload CA Certificate" style="position: relative; margin-left: 5px; float: inherit; width: 100%; border: 0px solid;"/>
									</div>									
								</div>
								
								<div id="errorMsg" ></div>
								<br><br>
								<!-- 개인키, 인증서, CA 인증서를 업로드 하고 최종 업로드 버튼으로 서버에 전송, 각 파일명들은 hidden 옵션으로 전달 -->
								<input id="btn_upload_privateKey" class="btn btn-primary"  name="uploadCertificateInfo" type="submit" value="upload" style="position: relative; margin-left: 5px; float: inherit; width: 30%;" />
								<input id="privateKeyFileName" name="privateKeyFileName" type="hidden" style="position: relative; margin-left: 5px; float: inherit; width: 100%; border: 0px solid;" />
								<input id="pmsCertificateFileName" name="pmsCertificateFileName" type="hidden" style="position: relative; margin-left: 5px; float: inherit; width: 100%; border: 0px solid;" />
								<input id="caCertificateFileName" name="caCertificateFileName" type="hidden" style="position: relative; margin-left: 5px; float: inherit; width: 100%; border: 0px solid;" />
						</form>
	                    </div>
	                </div>
	                <div class="row">
	                	<div class="text-center col-sm-12">
	                  		<div class="panel panel-default">
	                    		<div class="panel-heading"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span> PMS Private Key </div>
	                    		<!-- PMS 개인키 정보를 표시 -->
	                    		<div id="private_key" class="panel-body wordwrap text-left"></div>
	                  		</div>
	                  	</div>
	                </div>
		        </div>
	            <div class="text-center col-sm-6">
					<div class="row">
	                  <div class="panel panel-default">
	                    <div class="panel-heading"><span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span> PMS Certificate </div>
	                    <!-- PMS 인증서 정보를 표시 -->
	                    <div id="certificate" class="panel-body wordwrap text-left"></div>
	                  </div>
	                </div>
	                <div class="row">
	                  <div class="panel panel-default">
	                    <div class="panel-heading"><span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span> CA Certificate </div>
	                    <!-- PMS CA 인증서 정보를 표시 -->
	                    <div id="caCertificate" class="panel-body wordwrap text-left"></div>
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