<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
		<script type="text/javascript">
			/*
			 * 파트너 리스트를 보여주고 수정이나 추가/삭제 가능
			 */
			// view로 넘어온 파트너 정보 입력
			var partners = new Array();
			<c:forEach items="${partners}" var="partner">
				var info = new Object();
				info.name = "${partner.name}";
				info.street1 = "${partner.street1}";
				info.street2 = "${partner.street2}";
				info.city = "${partner.city}";
				info.state = "${partner.state}";
				info.postalCode = "${partner.postalCode}";
				info.country = "${partner.country}";
				info.addressId = "${partner.addressId}";
				info.pmsAddress = "${partner.pmsAddress}";
				info.importAddress = "${partner.importAddress}";
				info.userName = "${partner.userName}";
				info.userTitle = "${partner.userTitle}";
				info.userTelephone = "${partner.userTelephone}";
				info.userEmail = "${partner.userEmail}";
				info.userUrl = "${partner.userUrl}";
				info.privateCertificate = "${partner.privateCertificate}";
				partners.push(info);
			</c:forEach>
			
			$(document).ready(function() {
				var errorMsg="${errorMsg}";
	    		if( (errorMsg != undefined) || (errorMsg != "")) {
	    			$("#errorMsg").text(errorMsg);
	    		}
	    		//파트너 정보 표시
				refresh_partners(partners);
				
			});
			
			function refresh_partners(info) {
				// 파트너 정보 테이블에 추가
                $("#partnerlist").empty();
                var tr = document.createElement("tr");
                $("#partnerlist").append(tr);
                $(tr).css("background-color", "#296cf7");
                $(tr).css("color", "#ffffff");
                $(tr).css("height", "25px");
                $(tr).css("text-align", "center");
                $(tr).css("font-size", "12px");
				
                var td = document.createElement("td");
                $(tr).append(td);
                $(td).attr("width", "10%");
                $(td).text("name");
                var td = document.createElement("td");
                $(tr).append(td);
                $(td).attr("width", "10%");
                $(td).text("importAddress");
                var td = document.createElement("td");
                $(tr).append(td);
                $(td).attr("width", "10%");
                $(td).text("SGLN");
                
                for (var i = 0; i < info.length; i++) {
                    var partner = info[i];
                    var tr = document.createElement("tr");
                    $("#partnerlist").append(tr);
                    $(tr).css("text-align", "center");
                    $(tr).css("font-size", "12px");
                    var td2 = document.createElement("td");
                    $(td2).text(partner.name);
                    $(tr).append(td2);  
                    var td3 = document.createElement("td");
                    $(td3).text(partner.importAddress);
                    $(tr).append(td3);  
                    var td4 = document.createElement("td");
                    $(td4).text(partner.addressId);
                    $(tr).append(td4);    
                    $(tr).addClass(partner.importAddress);
                    $(tr).click(function () {
                    	$("#partnerlist tr").removeClass("selected");
                    	$("#partnerlist tr:gt(0)").css("background-color", "white");
                    	$("#partnerlist tr:gt(0)").css("color", "black");
                    	$(this).addClass("selected");
                    	$(this).css("background-color", "blue");
                    	$(this).css("color", "white");
                    	partner_detail(this);
                    });
                    $(tr).hover(function () {
                        $(this).css("background-color", "#aaffaa");
                        $(".selected").css("background-color", "blue");
                    },function () {
                        $(this).css("background-color", "white");
                        $(".selected").css("background-color", "blue");
                    });
                }
            }
			// 파트너 리스트 클릭시 세부 정보 표시
			function partner_detail(obj) {
                for (var i = 0; i < partners.length; i++) {
                	var partner_id = $(obj).attr('class');
                	partner_id = partner_id.replace(/\s*selected\s*/, '');
                	
                    if (partner_id == partners[i].importAddress) {
                    	$("#name").val(partners[i].name);
                    	$("#street1").val(partners[i].street1);
                    	$("#street2").val(partners[i].street2);
                    	$("#city").val(partners[i].city);
                    	$("#state").val(partners[i].state);
                    	$("#postalCode").val(partners[i].postalCode);
                    	$("#country").val(partners[i].country);
                    	$("#addressId").val(partners[i].addressId);
                    	$("#pmsAddress").val(partners[i].pmsAddress);
                    	$("#importAddress").val(partners[i].importAddress);
                    	$("#userName").val(partners[i].userName);
                    	$("#userTitle").val(partners[i].userTitle);
                    	$("#userTelephone").val(partners[i].userTelephone);
                    	$("#userEmail").val(partners[i].userEmail);
                    	$("#userUrl").val(partners[i].userUrl);
                    	$("#privateCertificate").text(partners[i].privateCertificate);
    					$("#index").val(partners[i].addressId);
                        break;
                    }
                }
            }
			
		</script>
<jsp:include page="common/navi.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include><div class="container" id="step1">
	    <div class="container">
	        <div class="row">
	            <div class="text-center col-sm-6">
	            <h3>파트너 정보 수정</h3>
	                <div class="panel panel-default">
	                    <div class="panel-heading">저장된 파트너 정보</div>
	                    <div class="panel-body">
	
	                        <form class="form-horizontal" action="${homeUrl}/change_partner?${_csrf.parameterName}=${_csrf.token}" method="post" name="f" enctype="multipart/form-data" accept-charset="UTF-8" onsubmit="document.charset='UTF-8'">
	                            <div class="form-group">
	                                <label for="name" class="col-sm-3 control-label">Company Name</label>
	                                <div class="col-sm-8">
	                                	<input id="name" class="form-control" type="text" name='name' placeholder="KAIST"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="street1" class="col-sm-3 control-label">Street1</label>
	                                <div class="col-sm-8">
	                                	<input id="street1" class="form-control" type="text" name='street1' placeholder="KI-building(E4) 291 Daehak-ro"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="street2" class="col-sm-3 control-label">Street2</label>
	                                <div class="col-sm-8">
	                                	<input id="street2" class="form-control" type="text" name='street2' placeholder="Yusung-gu"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="city" class="col-sm-3 control-label">City</label>
	                                <div class="col-sm-8">
	                                	<input id="city" class="form-control" type="text" name='city' placeholder="Daejeon"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="state" class="col-sm-3 control-label">State or Region</label>
	                                <div class="col-sm-8">
	                                	<input id="state" class="form-control" type="text" name='state' placeholder="Daejeon"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="postalCode" class="col-sm-3 control-label">Postal Code</label>
	                                <div class="col-sm-8">
	                                	<input id="postalCode" class="form-control" type="text" name='postalCode' placeholder="34141"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="country" class="col-sm-3 control-label">Country</label>
	                                <div class="col-sm-8">
	                                	<input id="country" class="form-control" type="text" name='country' placeholder="KR"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="addressId" class="col-sm-3 control-label">Address ID(SGLN)</label>
	                                <div class="col-sm-8">
	                                	<input id="addressId" class="form-control" type="text" name='addressId' placeholder="urn:epc:id:sgln:0614141.07346.1234"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="pmsAddress" class="col-sm-3 control-label">PMS Address</label>
	                                <div class="col-sm-8">
	                                	<input id="pmsAddress" class="form-control" type='text' name='pmsAddress' placeholder="http://localhost:8081/pms" />
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="importAddress" class="col-sm-3 control-label">Pedigree Import Address</label>
	                                <div class="col-sm-8">
	                                	<input id="importAddress" class="form-control" type='text' name='importAddress' placeholder="https://localhost:8445/pms/import" />
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="privateCertificate" class="col-sm-3 control-label">Private Certificate</label>
	                                <div class="col-sm-8">
	                            		<input id="btn_private_certificate" name="privateCertificate" type="file"  value="privateCertificate" style="position: relative; margin-left: 5px; float: inherit; width: 100%; border: 0px solid;"/>
	                            		<h6 id="privateCertificate" class="panel-body wordwrap text-left"></h7>
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
	                            <!-- 파트너 정보는 EPCIS 쿼리를 통해서도 얻어올 수 있음 -->
	                            <input name="action" class="btn btn-primary" type="submit" value="EPCIS Query" style="position:relative; margin-left:10px; float:inherit; width:30%;">
	                            <!-- 파트너 정보 직접 추가 -->
	                            <input name="action" class="btn btn-primary" type="submit" value="Add" style="position:relative; margin-left:10px; float:inherit; width:30%;">
	                            <!-- 파트너 정보 수정 -->
								<input name="action" class="btn btn-primary" type="submit" value="Change" style="position:relative; margin-left:10px; float:inherit; width:30%;">
								<input id="index" type="hidden" name="index" value="" />
								<input type="hidden" name="from" value="partnerlist_page" />
								<!--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />-->
	                        </form>
	                    </div>
	                </div>
		        </div>
		        <div class="text-center col-sm-6">
	            <h3>저장된 파트너 리스트</h3>
				<div class="panel panel-default">
					<!-- 파트너 리스트 표시 -->
					<div class="panel-heading">저장된 파트너 정보</div>
					<div class="panel-body">
						<section class="panel-body" style="min-height:202px; max-height:202px;">
							<table id="partnerlist" border="1" style="font-size: 14px; text-align:center">
							</table>
						</section>
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
		

<jsp:include page="common/footer.jsp"/>