<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu" />
</jsp:include>
<link rel="StyleSheet" href="css/dtree.css" type="text/css" />
<script type="text/javascript" src="js/dtree.js"></script>
<link rel="StyleSheet" href="css/modal.css" type="text/css" />

<script type="text/javascript">
	var trustCertificates = new Array();
	<c:forEach items="${trustCertificates}" var="certificate"> // 신뢰하는 인증서 리스트를 저장
		var info = new Object();
		info.trustStatus = "${certificate.hasConfidence}";
		info.serialNumber = "${certificate.serialNumber}";
		info.caCertificateString = "${certificate.caCertificateString}";
		info.method = "${certificate.method}";
		trustCertificates.push(info);
	</c:forEach>

	$(document).ready(function() {
		// 신뢰하는 인증서를 테이블 형태로 표시
		function refresh_trustCertificate() {
			$("#certificateList").empty();
			var tr = document.createElement("tr");
			$("#certificateList").append(tr);
			$(tr).css("background-color", "#297c77");
			$(tr).css("color", "#ffffff");
			$(tr).css("height", "25px");
			$(tr).css("text-align", "center");
			$(tr).css("font-size", "12px");
			$(tr).addClass("category");
			var td = document.createElement("td");
			$(tr).append(td);
			$(td).attr("width", "10%");
			$(td).text("인증서 신뢰");
			var td = document.createElement("td");
			$(tr).append(td);
			$(td).attr("width", "10%");
			$(td).text("일련번호");
			var td = document.createElement("td");
			$(tr).append(td);
			$(td).attr("width", "10%");
			$(td).text("입력");

			for (var i = 0; i < trustCertificates.length; i++) {
				if (i < 10) {
					var certificate_element = trustCertificates[i];
					var tr = document.createElement("tr");
					$(tr).css("text-align", "center");
					$(tr).css("font-size", "12px");
					$("#certificateList").append(tr);
					for (var j = 0; j < 3; j++) {
						var td2 = document.createElement("td");
						if (j == 0)
							$(td2).text(certificate_element.trustStatus);
						else if (j == 1)
							$(td2).text(certificate_element.serialNumber);
						else if (j == 2)
							$(td2).text(certificate_element.method);
						$(tr).append(td2);
					}
					$(tr).addClass(certificate_element.serialNumber);
					$(tr).click(function() {
						// 인증서 테이블 엔트리 클릭시 엔트리 색 변경 및 인증서 정보 표시
						$("#certificateList tr").removeClass("selected");
							$("#certificateList tr").each(function() {
								if ($(this).is(".category") == false) {
									$(this).css("color", "black");
									setCertificateColor(this);
								}
							});
							certificate_detail(this);
							$(this).addClass("selected");
							$(this).css("background-color", "blue");
							$(this).css("color", "white");
							console.log($(this).find(':nth-child(1)').text());
							$("#selectedCertificateSerialNumber").val($(this).find(':nth-child(2)').text());
						}
					);
					// 마우스 움직임에 따라, 또는 클릭에 따라 테이블 엔트리 색 조정
					$(tr).hover(
						function() {
							$(this).css("background-color", "#88bbaa");
							$(".selected").css("background-color", "blue");
						},
						function() {
							//$(this).css("background-color", "white");
							$(".selected").css("background-color", "blue");
							setCertificateColor(this);
						}
					);
					$(tr).addClass(certificate_element.trustStatus);
					setCertificateColor(tr);
				}
			}
		}
		// certificate 상세 정보 출력
		function certificate_detail(obj) {
			for (var i = 0; i < trustCertificates.length; i++) {
				var certificate_element = trustCertificates[i];
				var selectedCertificate = $(obj).attr('class').split(" ")[0];
				if (selectedCertificate == certificate_element.serialNumber) {
					console.log(certificate_element);
					$("#selectedIndex").val(certificate_element.serialNumber);
					$("#certificateViewer").empty();
					$("#certificateViewer").append(certificate_element.caCertificateString);
					break;
				}
			}
		}
		// certificate 리스트를 표현한 테이블의 에트리에 대한 클릭, hovering에 따라 색 변경
		function setCertificateColor(tr) {
			if ($(tr).is('.Trust')) {
				$(tr).css("background-color", "#ccffff");
			} else {
				$(tr).css("background-color", "#ffcccc");
			} 
			$(".selected").css("background-color", "blue");
			$(".selected").css("color", "white");
		}

		refresh_trustCertificate();
		
		// 
		var modal_certificate = document.getElementById('certificate_info_modal');
		var modal_certificate_btn = document.getElementById("btn_certificate_info_modal");
		var span_certificate = document.getElementsByClassName("close_certificate_modal")[0];
		modal_certificate_btn.onclick = function() {
			modal_certificate.style.display = "block";
		}
		span_certificate.onclick = function() {
			modal_certificate.style.display = "none";
		}
		window.onclick = function(event) {
			if (event.target == modal_certificate) {
				modal_certificate.style.display = "none";
			}
		}
		
		
		
	});		
	// 인증서의 신뢰여부 변경 루틴
	function changeConfidence(box) {
		console.log("test")
		  if(box.checked) {
		    $(box).val("Trust");
		  } else {
			  $(box).val("Not_Trust");
		  }
	}
	// 인증서를 직접 추가하는 경우 파일 경로에서 파일명을 추출
	function setFileName(location, path) {
		var filePath = path.split("\\");
		var fileName = filePath[filePath.length-1]; 
		document.getElementById(location).value = fileName;
	}
</script>
<jsp:include page="common/navi.jsp">
	<jsp:param name="pageTitle" value="Menu" />
</jsp:include>
<div class="container" id="step0">
	<div class="row">
		<div class="text-center col-sm-6">
			<!-- pedigree 리스트 표현 -->
			<div class="panel panel-default">
				<div class="panel-heading">신뢰하는 사설 인증서 리스트</div>
				<div class="panel-body" style="min-height: 202px; max-height: 202px;">
					<table id="certificateList" border="1"></table>
				</div>
				<div class="row"
					style="vertical-align: bottom; padding-bottom: 15px;">
					<h4 id="errorMsg">${errorMsg}</h4>
					<form class="form-horizontal" name="f" method="post">
						<div class="form-group">
							<div>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="from" value="pedigreeinfo_page" />
								<input id="selectedIndex" type="hidden" name="selectedIndex" value="" />
								<!-- 인증서를 직접 추가하는 모달을 호출 -->
								<a id="btn_certificate_info_modal" class="btn btn-primary" style="position: relative; margin-left: 5px; float: inherit; width: 40%;">인증서 추가</a>
								<!-- <input id="gen_shippedPed" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/post_shippedData" value="shippedPedigree 생성" style="position: relative; margin-left: 5px; float: inherit; width: 40%;"> -->
								<!-- <a id="btn_shipped_info_modal" class="btn btn-primary" style="position: relative; margin-left: 5px; float: inherit; width: 40%;">shippedPedigree 생성</a> -->
								<input id="selectedCertificateSerialNumber" class="btn btn-primary" name="serialNumber" type="hidden" value="" style="position: relative; margin-left: 5px; float: inherit; width: 40%;">
								<!-- 인증서의 신뢰여부를 변경 -->
								<input id="btn_change_confidence" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/changeConfidence" value="인증서 신뢰 여부 변경" style="position: relative; margin-left: 5px; float: inherit; width: 40%;"></input>
								<!-- 인증서를 삭제 -->
								<input id="btn_delete_certificate" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/removeTrustCertificate" value="인증서 삭제" style="position: relative; margin-left: 5px; margin-top: 15px; float: inherit; width: 40%;"></input>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="text-center col-sm-6">
			<div class="row">
            	<div class="panel panel-default">
                	<div class="panel-heading">
                		<span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span> Certificate
                	</div>
                	<div id="certificateViewer" class="panel-body wordwrap text-left"></div>
                </div>
            </div>
		</div>
	</div>
	
	<!-- 인증서 추가를 위한 modal -->
	<div id=certificate_info_modal class="modal">
		<div class="modal-content-small">
			<div class="modal-header">
				<span class="close close_certificate_modal">×</span>
				<h3 style="text-align: center;">인증서 업로드</h3>
			</div>
			<div class="modal-body">
				<div class="center-block text-center">
					<div class="row">
						<div class="text-center col-sm-offset-0 col-sm-12">
							<div class="panel panel-default">
								<div class="text-center col-sm-12">
									<div class="panel panel-default">
										<div class="panel-heading">인증서 정보</div>
										<div class="panel-body">
											<form class="form-horizontal" action="${homeUrl}/uploadTrustCertificate?${_csrf.parameterName}=${_csrf.token}" name="f" enctype="multipart/form-data" method="post" accept-charset="UTF-8" onsubmit="document.charset='UTF-8'">
												<div class="form-group">
													<label for="CertificateType" class="col-sm-3 control-label">Trusting Certificate</label>
													<div class="col-sm-8">
														<p style="position: relative; margin-left: 5px; margin-top: 7px; padding-bottom: 10px; float: inherit; border: 0px solid;">yes</p>
														<!-- 인증서 업로드와 동시에 신뢰할 지 여부를 결정 -->
														<input id="hasConfidence" name="hasConfidence" onclick="changeConfidence(this);" value="Not_Trust" type="checkbox" style="position: relative; margin-left: 10px; margin-top: 11px; padding-bottom: 10px; float: inherit; border: 0px solid;"/>
													</div>									
												</div>
												<div class="form-group">
													<label for="uploadCACertificate" class="col-sm-3 control-label">Certificate</label>
													<div class="col-sm-8">
														<!-- ca 인증서 파일 업로드 버튼 -->
														<input id="storedCaCertificateFileName" name="storedPrivateKeyFileName" type="text" value="File was not found in database" style="position: relative; margin-left: 5px; margin-top: 5px; padding-bottom: 10px; float: inherit; width: 100%; border: 0px solid; color: red;" />
														<input id="btn_upload_ca_certificate" onchange="setFileName('storedCaCertificateFileName', this.value)" name="uploadCACertificate"  type="file" required="requeired" value="upload CA Certificate" style="position: relative; margin-left: 5px; float: inherit; width: 100%; border: 0px solid;"/>
													</div>									
												</div>
												<div id="errorMsg" ></div>
												<br><br>
												<input id="uploadTrustCertificate" class="btn btn-primary"  name="uploadTrustCertificate" type="submit" value="upload" style="position: relative; margin-left: 5px; float: inherit; width: 30%;" />
											</form>
										</div>
									</div>
								</div>
							</div>
						</div>
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