<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
	    <script type="text/javascript">
	    // 회사정보 표시 및 수정 기능 제공
	    $(document).ready(function () {
	    	// 회사정보 업로드 과정에서의 오류를 표시
    		var errorMsg="${errorMsg}";
    		if( (errorMsg != undefined) || (errorMsg != "")) {
    			$("#errorMsg").text(errorMsg);
    		}
    		// view로 전달된 회상 정보 표시
    		var company = "${company}";
    		if( (company != undefined) && (company != null)){
	    		var country = "${company.country}";
	    		var province = "${company.province}";
	    		var locality = "${company.locality}";
	    		var name = "${company.name}";
	    		var department = "${company.department}";
	    		var domain = "${company.domain}";
	    		var email = "${company.email}";
	    		var epcisAddress = "${company.epcisAddress}";
	    		var street = "${company.street}";
	    		var postalCode = "${company.postalCode}";
	    		var addressId = "${company.addressId}";
	    		var pmsAddress = "${company.pmsAddress}";
	    		var importAddress = "${company.importAddress}";
	    		var userName = "${company.userName}";
	    		var userTitle = "${company.userTitle}";
	    		var userTelephone = "${company.userTelephone}";
	    		var userEmail = "${company.userEmail}";
	    		var userUrl = "${company.userUrl}";
	    		if(country != undefined) {$("#country").val(country);}
	    		if(province != undefined) {$("#province").val(province);}
	    		if(locality != undefined) {$("#locality").val(locality);}
	    		if(organization != undefined) {$("#organization").val(name);}
	    		if(organizationUnit != undefined) {$("#organizationUnit").val(department);}
	    		if(domain != undefined) {$("#domain").val(domain);}
	    		if(email != undefined) {$("#email").val(email);}
	    		if(epcisAddress != undefined) {$("#epcisAddress").val(epcisAddress);}
	    		if(street != undefined) {$("#street").val(street);}
	    		if(postalCode != undefined) {$("#postalCode").val(postalCode);}
	    		if(addressId != undefined) {$("#addressId").val(addressId);}
	    		if(pmsAddress != undefined) {$("#pmsAddress").val(pmsAddress);}
	    		if(importAddress != undefined) {$("#importAddress").val(importAddress);}
	    		if(userName != undefined) {$("#userName").val(userName);}
	    		if(userTitle != undefined) {$("#userTitle").val(userTitle);}
	    		if(userTelephone != undefined) {$("#userTelephone").val(userTelephone);}
	    		if(userEmail != undefined) {$("#userEmail").val(userEmail);}
	    		if(userUrl != undefined) {$("#userUrl").val(userUrl);}
    		}
    	});
	    </script>
<jsp:include page="common/navi.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
	<div class="container" id="step1">
	    <div class="container">
	        <div class="row">
	            <div class="text-center col-sm-8">
	                <div class="panel panel-default">
	                    <div class="panel-heading">저장된 회사 정보</div>
	                    <div class="panel-body">
							<!-- 회사정보 변경을 change_company로 전송 -->
	                        <form class="form-horizontal" action="${homeUrl}/change_company" method="post">
	                        	<div class="form-group">
	                                <label for="street" class="col-sm-3 control-label">Street</label>
	                                <div class="col-sm-8">
	                                	<input id="street" class="form-control" type='text' name='street' placeholder="291 Daehak-ro"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="locality" class="col-sm-3 control-label">Locality Name</label>
	                                <div class="col-sm-8">
	                                	<input id="locality" class="form-control" type='text' name='locality' placeholder="Yusung-gu"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="organization" class="col-sm-3 control-label">Organization</label>
	                                <div class="col-sm-8">
	                                	<input id="organization" class="form-control" type='text' name='organization' placeholder="KAIST"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="organizationUnit" class="col-sm-3 control-label">Organization Unit</label>
	                                <div class="col-sm-8">
	                                	<input id="organizationUnit" class="form-control" type='text' name='organizationUnit' placeholder="ITC"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="province" class="col-sm-3 control-label">State or Province Name</label>
	                                <div class="col-sm-8">
	                                	<input id="province" class="form-control" type='text' name='province' placeholder="Daejeon"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="country" class="col-sm-3 control-label">Country Code</label>
	                                <div class="col-sm-8">
	                                	<input id="country" class="form-control" type="text" name='country' placeholder="KR"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="postalCode" class="col-sm-3 control-label">Postal Code</label>
	                                <div class="col-sm-8">
	                                	<input id="postalCode" class="form-control" type='text' name='postalCode' placeholder="34141"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="email" class="col-sm-3 control-label">Email Address</label>
	                                <div class="col-sm-8">
	                                	<input id="email" class="form-control" type='text' name='email' placeholder="sskim98@kaist.ac.kr"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="domain" class="col-sm-3 control-label">Common Name</label>
	                                <div class="col-sm-8">
	                                	<input id="domain" class="form-control" type='text' name='domain' placeholder="itc.kaist.ac.kr"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="addressId" class="col-sm-3 control-label">Address ID(SGLN)</label>
	                                <div class="col-sm-8">
	                                	<input id="addressId" class="form-control" type="text" name='addressId' placeholder="urn:epc:id:sgln:0614141.07346.1234"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="epcisAddress" class="col-sm-3 control-label">EPCIS Address</label>
	                                <div class="col-sm-8">
	                                	<input id="epcisAddress" class="form-control" type='text' name='epcisAddress' placeholder="http://localhost:8080/epcis"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="pmsAddress" class="col-sm-3 control-label">PMS Address</label>
	                                <div class="col-sm-8">
	                                	<input id="pmsAddress" class="form-control" type='text' name='pmsAddress' placeholder="http://localhost:8081/pms"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="importAddress" class="col-sm-3 control-label">Pedigree Import Address</label>
	                                <div class="col-sm-8">
	                                	<input id="importAddress" class="form-control" type='text' name='importAddress' placeholder="https://localhost:8444/pms/import"/>
	                                </div>
	                            </div>
	                            <hr width="100%" align="center" style="background:black">
	                            <h5 align="left" style="font-weight:bold">연락처 정보</h5>
		                        <div class="form-group">
		                            <label for="userName" class="col-sm-3 control-label">Name</label>
		                            <div class="col-sm-8">
		                            	<input id="userName" class="form-control" type='text' name='userName' placeholder="Gil Dong Hong" />
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label for="" class="col-sm-3 control-label">Title</label>
		                            <div class="col-sm-8">
		                            	<input id="userTitle" class="form-control" type='text' name='userTitle' placeholder="Sales Manager" />
		                            </div>
		                        </div>
	                            <div class="form-group">
		                            <label for="userTelephone" class="col-sm-3 control-label">Telephone</label>
		                            <div class="col-sm-8">
		                            	<input id="userTelephone" class="form-control" type='text' name='userTelephone' placeholder="+82-42-350-4294" />
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label for="userEmail" class="col-sm-3 control-label">Email</label>
		                            <div class="col-sm-8">
		                            	<input id="userEmail" class="form-control" type='text' name='userEmail' placeholder="gdhong@itc.kaist.ac.kr" />
		                            </div>
		                        </div>
		                        <div class="form-group">
		                            <label for="userUrl" class="col-sm-3 control-label">URL</label>
		                            <div class="col-sm-8">
		                             	<input id="userUrl" class="form-control" type='text' name='userUrl' placeholder="http://itc.kaist.ac.kr" />
		                            </div>
		                        </div>
	                            <h4 id="errorMsg"></h4>
	                            <!-- 회사정보에 대해 수정이 이루어지면 변경요청 -->
								<input name="submit" class="btn btn-primary" type="submit" value="change" style="position:relative; margin-left:30px; float:inherit; width:30%;">
								<input type="hidden" name="from" value="companyinfo_page" />
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	                        </form>
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