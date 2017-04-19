<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
		<script type="text/javascript">
		//인증서 생성 페이지, 현재 사용하지 않음
        	$(document).ready(function () {
        		// pedigree 리스트를 보여주기 위한 루틴
				var pedigreeXml = '${pedigreeXml}'
				$("#generated_xml").text(vkbeautify.xml(pedigreeXml));
                function refresh_pedigree(json) {
                    $("#pedigreeList").empty();
                    var tr = document.createElement("tr");
                    $("#pedigreeList").append(tr);
                    $(tr).css("background-color", "#296cf7");
                    $(tr).css("color", "#ffffff");
                    $(tr).css("text-align", "center");
                    $(tr).css("font-size", "12px");
                    var td = document.createElement("td");
                    $(tr).append(td);
                    $(td).attr("width", "10%");
                    $(td).text("일련번호");
                    var td = document.createElement("td");
                    $(tr).append(td);
                    $(td).attr("width", "10%");
                    $(td).text("품명");
                    var td = document.createElement("td");
                    $(tr).append(td);
                    $(td).attr("width", "10%");
                    $(td).text("상품코드");
                    var td = document.createElement("td");
                    $(tr).append(td);
                    $(td).attr("width", "10%");
                    $(td).text("시리얼번호");
                    var td = document.createElement("td");
                    $(tr).append(td);
                    $(td).attr("width", "10%");
                    $(td).text("종류");

                    console.log(json);
                    pedigree_info = json;
                    for (var i = 0; i < json.num_of_pedigree; i++) {
                        var pedigree_element = json.element[i];
                        var tr = document.createElement("tr");
                        $("#pedigreeList").append(tr);
                        for (var j = 0; j < 5; j++) {
                            var td2 = document.createElement("td");
                            if (j == 0) $(td2).text(pedigree_element.serial_num);
                            else if (j == 1) $(td2).text(pedigree_element.pedigree.product_name);
                            else if (j == 2) $(td2).text(pedigree_element.pedigree.gtin);
                            else if (j == 3) $(td2).text(pedigree_element.pedigree.serial);
                            else if (j == 4) $(td2).text(pedigree_element.pedigree.pedigree_type);
                            $(tr).append(td2);
                        }
                        $(tr).addClass(pedigree_element.serial_num);
                        $(tr).click(function () {
                            pedigree_detail(this);
                        });
                        if (pedigree_element.pedigree.pedigree_type == 'initial') {
                            $(tr).css("background-color", "skyblue");
                        }
                        else if (pedigree_element.pedigree.pedigree_type == 'shipped') {
                            $(tr).css("background-color", "#ffaaaa");
                        }
                        else if (pedigree_element.pedigree.pedigree_type == 'disabled') {
                            $(tr).css("background-color", "#ffffaa");
                        }
                        else if (pedigree_element.pedigree.pedigree_type == 'received') {
                            $(tr).css("background-color", "#aaaaff");
                        }
                        console.log("test");
                    }
                }
                // pedigree 상세 내용 표시 루틴
                function pedigree_detail(obj) {
                    for (var i = 0; i < pedigree_info.num_of_pedigree; i++) {
                        var pedigree_element = pedigree_info.element[i];
                        if (pedigree_element.serial_num == $(obj).attr('class')) {
                            $("#detailed_xml").text(vkbeautify.xml(pedigree_element.pedigree.pedigree_xml));
                            break;
                        }
                    }
                }
        });
    </script>
<jsp:include page="common/navi.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
	<div class="container" id="step1">
	    <div class="container">
	        <div class="row">
	            <div class="text-center col-sm-6">
	            <h3>기관정보 등록</h3>
	                <div class="panel panel-default">
	                    <div class="panel-heading">기관의 필수 정보로 인증서를 생성을 DPMS에 요청</div>
	                    <div class="panel-body">
	                    	<!-- 인증서 생성을 위한 기관 정보 입력 -->
							<form class="form-horizontal" name="f" method="post" accept-charset="UTF-8" onsubmit="document.charset='UTF-8'">
								<div class="form-group">
									<label for="inputEmail" class="col-sm-3 control-label">국가코드</label>
	                                <div class="col-sm-8">
	                                <input type="email" class="form-control" id="CountryCode" placeholder="KR">
	                                </div>
								</div>
								<div class="form-group">
	                                <label for="inputRipple" class="col-sm-3 control-label">지역</label>
	                                <div class="col-sm-8">
	                                <input type="text" class="form-control" id="StateName" placeholder="Daejeon">
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputName" class="col-sm-3 control-label">시/군/구</label>
	                                <div class="col-sm-8">
	                                <input type="text" class="form-control" id="LocalityName" placeholder="Yuseong-gu">
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputName" class="col-sm-3 control-label">회사명</label>
	                                <div class="col-sm-8">
	                                <input type="text" class="form-control" id="CompanyName" placeholder="KAIST">
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputName" class="col-sm-3 control-label">부서명</label>
	                                <div class="col-sm-8">
	                                <input type="text" class="form-control" id="OrganizationalUnitName" placeholder="Future Device Team">
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputName" class="col-sm-3 control-label">도메인명</label>
	                                <div class="col-sm-8">
	                                <input type="text" class="form-control" id="DomainName" placeholder="itc.kaist.ac.kr">
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputName" class="col-sm-3 control-label">이메일</label>
	                                <div class="col-sm-8">
	                                <input type="text" class="form-control" id="EmailAddress" placeholder="gs1-all@itc.kaist.ac.kr">
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputName" class="col-sm-3 control-label">파트너</label>
	                                <div class="col-sm-8">
	                                    <textarea id="Partner" class="form-control" rows="5" placeholder="DPMS_A http://143.248.97.71"></textarea>
	                                </div>
	                            </div>
								<div class="form-group">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<input type="hidden" name="from" value="pedigreeinfo_page" />
									<input id="btn_submit_step1" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/generateCSR" value="CSR 생성"
										style="position: relative; margin-left: 5px; float: inherit; width: 30%;">
									<input type="hidden" name="from" value="certificate_page" />
								</div>
							</form>
	                    </div>
	                </div>
		        </div>
	            <div class="text-center col-sm-6">
	            	<!-- 생성된 인증서 정보 표시 -->
	                <h3>생성된 인증서 정보</h3>
	                <div class="row">
	                  <div class="panel panel-default">
	                    <div class="panel-heading"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span> Private Key - 개인키</div>
	                    <div id="private_key" class="panel-body wordwrap text-left"></div>
	                  </div>
	                </div>
	                 
	                <form class="form-horizontal" name="f" method="post" accept-charset="UTF-8" onsubmit="document.charset='UTF-8'">
						<div class="form-group">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="hidden" name="from" value="pedigreeinfo_page" />
							<input id="btn_submit_step1" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/generateCertificate" value="인증서 생성"
								style="position: relative; margin-left: 5px; float: inherit; width: 30%;">
							<input type="hidden" name="from" value="certificate_page" />
						</div>
					</form>
					
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