<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
	<link rel="StyleSheet" href="css/dtree.css" type="text/css" />
	<script type="text/javascript" src="js/dtree.js"></script>
	<link rel="StyleSheet" href="css/modal.css" type="text/css" />
	
		<script type="text/javascript">
			/*
			 * pedigree 리스트를 표시하고 export를 수행 
			 */
			// view로 넘어온 pedigree 정보를 저장
			var pedigrees = new Array();
			<c:forEach items="${pedigrees}" var="pedigree">
			var pedigree = new Object();
			pedigree.sgtin = "${pedigree.sgtin}";
			pedigree.type = "${pedigree.type}";
			pedigree.modifiedTime = "${pedigree.modifiedTime}";
			pedigree.xml = '${pedigree.xml}';
			pedigrees.push(pedigree);
			</c:forEach>

			$(document).ready(function() {
				// pedigree 리스트를 테이블에 추가
				var pedigreeXml = '${pedigreeXml}'
				$("#generated_xml").text(vkbeautify.xml(pedigreeXml));
				function refresh_pedigree() {
					$("#pedigreeList").empty();
					var tr = document.createElement("tr");
					$("#pedigreeList").append(tr);
					$(tr).css("background-color", "#297c77");
					$(tr).css("color", "#ffffff");
					$(tr).css("height", "25px");
					$(tr).css("text-align", "center");
					$(tr).css("font-size", "12px");
					$(tr).addClass("category");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "10%");
					$(td).text("일련번호");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "10%");
					$(td).text("타입");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "10%");
					$(td).text("변경일");

					for (var i = 0; i < pedigrees.length; i++) {
						if(i<10) {
							var pedigree_element = pedigrees[i];
							var tr = document.createElement("tr");
							$(tr).css("text-align", "center");
							$(tr).css("font-size", "12px");
							$("#pedigreeList").append(tr);
							for (var j = 0; j < 3; j++) {
								var td2 = document.createElement("td");
								if (j == 0)
									$(td2).text(pedigree_element.sgtin);
								else if (j == 1)
									$(td2).text(pedigree_element.type);
								else if (j == 2)
									$(td2).text(pedigree_element.modifiedTime);
								
								$(tr).append(td2);
							}
							$(tr).addClass(pedigree_element.sgtin);
							$(tr).click(function() {
								$("#pedigreeList tr").removeClass("selected");
								$("#pedigreeList tr").each(function() {
									if($(this).is(".category") == false) {
										$(this).css("color","black");
										setPedigreeColor(this);
									}
								});
								pedigree_detail(this);
								$(this).addClass("selected");
		                    	$(this).css("background-color", "blue");
		                    	$(this).css("color", "white");
		                    	console.log($(this).find(':nth-child(1)').text());
		                    	$("#selectedPreviousPedigreeSgtin").val($(this).find(':nth-child(1)').text());
		                    	$("#selectedPreviousPedigreeType").val($(this).find(':nth-child(2)').text());
		                    	$("#selectedShippedPedigreeSgtin").val($(this).find(':nth-child(1)').text());
		                    	$("#selectedShippedPedigreeType").val($(this).find(':nth-child(2)').text());
		                    	
							});
							// 마우스 움직임에 따라, 또는 클릭에 따라 테이블 엔트리 색 조정
		                    $(tr).hover(function () {
		                        $(this).css("background-color", "#88bbaa");
		                        $(".selected").css("background-color", "blue");
		                    },function () {
		                        //$(this).css("background-color", "white");
		                        $(".selected").css("background-color", "blue");
		                        setPedigreeColor(this);
		                    });
		                    $(tr).addClass(pedigree_element.type);
		                    setPedigreeColor(tr);
						}
					}
				}
				// pedigree 상세 정보 출력
				function pedigree_detail(obj) {
					for (var i = 0; i < pedigrees.length; i++) {
						var pedigree_element = pedigrees[i];
						var selectedGtin = $(obj).attr('class').split(" ")[0]; 
						if (selectedGtin == pedigree_element.sgtin) {
							console.log(pedigree_element);
							$("#selectedIndex").val(pedigree_element.sgtin);
							$("#detailed_xml").text(vkbeautify.xml(pedigree_element.xml));
							drawPedigreeTree(pedigree_element);
							break;
						}
					}
				}
				// pedigree 리스트를 표현한 테이블의 에트리에 대한 클릭, hovering에 따라 색 변경
				function setPedigreeColor(tr) {
					if($(tr).is('.Initial')) {$(tr).css("background-color", "#ffcccc");}
					else if($(tr).is('.Shipped')) {$(tr).css("background-color", "#ffffcc");}
					else if($(tr).is('.Imported')) {$(tr).css("background-color", "aaaaaa");}
					else if($(tr).is('.Received')) {$(tr).css("background-color", "ccffff");}
					$(".selected").css("background-color", "blue");
                	$(".selected").css("color", "white");
				}
				
				refresh_pedigree();
				// pedigree를 테이블에서 선택시 pedigree 개괄적 내용을 트리 형태로 분석 후 표시
				function drawPedigreeTree(element) {
					if (window.DOMParser)
					{
					    parser=new DOMParser();
					    xmlDoc=parser.parseFromString(element.xml,"text/xml");
					}
					else // 인터넷 익스플로러
					{
					    xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
					    xmlDoc.async=false;
					    xmlDoc.loadXML(element.xml); 
					} 
					
					d = new dTree('d');
					d.add(0,-1,"Product");
					var serialNumber = xmlDoc.getElementsByTagName("serialNumber")[0];
					d.add(1,0,'['+serialNumber.nodeName+'] ' + serialNumber.firstChild.nodeValue,'');
					var productInfo = xmlDoc.getElementsByTagName("productInfo")[0];
					d.add(2,0,'['+productInfo.nodeName+']','');
					if(productInfo.childNodes[0].firstChild != undefined)
					d.add(3,2,'['+productInfo.childNodes[0].nodeName+'] ' + productInfo.childNodes[0].firstChild.nodeValue,'');
					if(productInfo.childNodes[1].firstChild != undefined)
					d.add(4,2,'['+productInfo.childNodes[1].nodeName+'] ' + productInfo.childNodes[1].firstChild.nodeValue,'');
					if(productInfo.childNodes[2].firstChild != undefined)
					d.add(5,2,'['+productInfo.childNodes[2].nodeName+'] ','');
					if(productInfo.childNodes[2].childNodes[0].firstChild != undefined)
					d.add(6,5,'['+productInfo.childNodes[2].childNodes[0].nodeName+'] ' + productInfo.childNodes[2].childNodes[0].firstChild.nodeValue,'');
					if(productInfo.childNodes[2].getAttribute("type") != undefined)
					d.add(7,5,'['+productInfo.childNodes[2].getAttributeNode("type").name +'] ' + productInfo.childNodes[2].getAttributeNode("type").value,'');
					if(productInfo.childNodes[3].firstChild != undefined)
					d.add(8,2,'['+productInfo.childNodes[3].nodeName+'] ' + productInfo.childNodes[3].firstChild.nodeValue,'');
					if(productInfo.childNodes[4].firstChild != undefined)
					d.add(9,2,'['+productInfo.childNodes[4].nodeName+'] ' + productInfo.childNodes[4].firstChild.nodeValue,'');
					if(productInfo.childNodes[5].firstChild != undefined)
					d.add(10,2,'['+productInfo.childNodes[5].nodeName+'] ' + productInfo.childNodes[5].firstChild.nodeValue,'');
					var itemInfo = xmlDoc.getElementsByTagName("itemInfo")[0];
					d.add(11,0,'['+itemInfo.nodeName+']','');
					if(itemInfo.childNodes[0].firstChild != undefined)
					d.add(12,11,'['+itemInfo.childNodes[0].nodeName+'] ' + itemInfo.childNodes[0].firstChild.nodeValue,'');
					if(itemInfo.childNodes[1].firstChild != undefined)
					d.add(13,11,'['+itemInfo.childNodes[1].nodeName+'] ' + itemInfo.childNodes[1].firstChild.nodeValue,'');
					if(itemInfo.childNodes[2].firstChild != undefined)
					d.add(14,11,'['+itemInfo.childNodes[2].nodeName+'] ' + itemInfo.childNodes[2].firstChild.nodeValue,'');
					if(itemInfo.childNodes[3].firstChild != undefined)
					d.add(15,11,'['+itemInfo.childNodes[3].nodeName+'] ' + itemInfo.childNodes[3].firstChild.nodeValue,'');
					$("#xmlTree").html(d.toString());
					d.openAll();
				}
				
				// initial pedigree 의 직접 추가를 위한 modal
				var modal_initial = document.getElementById('initial_info_modal');
				var modal_initial_btn = document.getElementById("btn_initial_info_modal");
				var span_initial = document.getElementsByClassName("close_initial")[0];
				modal_initial_btn.onclick = function() {
					modal_initial.style.display = "block";
				}
				span_initial.onclick = function() {
					modal_initial.style.display = "none";
				}
				/*
				var modal_shipped = document.getElementById('shipped_info_modal');
				var modal_shipped_btn = document.getElementById("btn_shipped_info_modal");
				var span_shipped = document.getElementsByClassName("close_shipped")[0];
				modal_shipped_btn.onclick = function() {
					console.log($("#selectedPreviousPedigreeSgtin").val());
					console.log($("#selectedPreviousPedigreeType").val());
					if($("#selectedPreviousPedigreeSgtin").val() == "") {
						alert("please select a pedigree")
					}
					else if( ($("#selectedPreviousPedigreeType").val() != "Initial") && $("#selectedPreviousPedigreeType").val() != "Received") {
						alert("you can make a shipped pedigree from initial or received pedigree")
					}
					else {
						modal_shipped.style.display = "block";
					}
				}
				span_shipped.onclick = function() {
					modal_shipped.style.display = "none";
				}
				*/
				// pedigree export를 위해 pedigree를 수신할 파트너 설정
				// 파트너 선택을 위한 modal
				var modal_partner = document.getElementById('partner_info_modal');
				var modal_partner_btn = document.getElementById("btn_partner_info_modal");
				var span_partner = document.getElementsByClassName("close_partner")[0];
				modal_partner_btn.onclick = function() {
					console.log($("#selectedShippedPedigreeSgtin").val());
					console.log($("#selectedShippedPedigreeType").val());
					if($("#selectedShippedPedigreeSgtin").val() == "") {
						alert("please select a pedigree")
					}
					else if($("#selectedShippedPedigreeType").val() != "Shipped") {
						alert("you can send a shipped pedigree only")
					}
					else {
						modal_partner.style.display = "block";
					}
				}
				span_partner.onclick = function() {
					modal_partner.style.display = "none";
				}
				window.onclick = function(event) {
				    if (event.target == modal_initial) {
				    	modal_initial.style.display = "none";
				    }
				    else if (event.target == modal_partner) {
				    	modal_partner.style.display = "none";
				    }
				}
				
				///////////////////////////////////////////////////////////
				// view 로 넘어온 파트너 정보를 저장
				var partners = new Array();
				<c:forEach items="${partners}" var="partner">
					var info = new Object();
					info.importAddress = "${partner.importAddress}";
					info.name = "${partner.name}";
					info.addressId = "${partner.addressId}";
					partners.push(info);
				</c:forEach>
				
				$(document).ready(function() {
					var errorMsg="${errorMsg}";
		    		if( (errorMsg != undefined) || (errorMsg != "")) {
		    			$("#errorMsg").text(errorMsg);
		    		}
		    		// modal에 파트너 정보 삽입
					refresh_partners(partners);
					
				});
				
				
				function refresh_partners(info) {
					// pedigree export를 위해 modal에 파트너 정보를 추가
	                $(".partnerlist").empty();
	                $(".partnerlist").each( function() {
	                	var tr = document.createElement("tr");
		                $(this).append(tr);
		                $(tr).css("background-color", "#296cf7");
		                $(tr).css("color", "#ffffff");
		                $(tr).css("text-align", "center");
		                $(tr).css("font-size", "12px");
		                var td = document.createElement("td");
		                $(tr).append(td);
		                $(td).attr("width", "25%");
		                $(td).text("partner");
		                var td = document.createElement("td");
		                $(tr).append(td);
		                $(td).attr("width", "50%");
		                $(td).text("importAddress");
		                var td = document.createElement("td");
		                $(tr).append(td);
		                $(td).attr("width", "50%");
		                $(td).text("SGLN");
		                
		                for (var i = 0; i < info.length; i++) {
		                    var partner = info[i];
		                    var tr = document.createElement("tr");
		                    $(this).append(tr);
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
		                    $(tr).addClass(partner.addressId);
		                    $(tr).click(function () {
		                    	$(this).parent().find("tr").removeClass("selected");
		                    	$(this).parent().find("tr:gt(0)").css("background-color", "white");
		                    	$(this).parent().find("tr:gt(0)").css("color", "black");
		                    	partner_detail(this);
		                    	$(this).addClass("selected");
		                    	$(this).css("background-color", "blue");
		                    	$(this).css("color", "white");
		                    	$(this).parent().find(".partnerSelect").val($(this).attr('class').split(" ")[0]).attr("selected", "selected");
		                    	console.log($(this).attr('class').split(" ")[0]);
		                    	$("#selectedPartner").val($(this).attr('class').split(" ")[0]);
		                    });
		                    $(tr).hover(function () {
		                        $(this).css("background-color", "#aaffaa");
		                        $(".selected").css("background-color", "blue");
		                    },function () {
		                        $(this).css("background-color", "white");
		                        $(".selected").css("background-color", "blue");
		                    });
		                    var item = document.createElement("option");
		                    item.value = partner.importAddress;
		                    $(item).text(partner.addressId)
		                    $(this).parent().parent().find(".partnerSelect").append(item);
		                }
	                });
	                
	            }
				
				function partner_detail(obj) {
	                for (var i = 0; i < partners.length; i++) {
	                    if ($(obj).attr('class') == partners[i].importAddress) {
	                    	$("#name").val(partners[i].name);
	                    	$("#importAddress").val(partners[i].importAddress);
	    					$("#addressId").val(partners[i].addressId);
	    					$("#index").val(partners[i].importAddress);
	                        break;
	                    }
	                }
	            }
			});
		</script>
<jsp:include page="common/navi.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
<div class="container" id="step0">
	<div class="row">
		<div class="text-center col-sm-6">
			<!-- pedigree 리스트 표현 -->
			<h3>Pedigree</h3>
			<div class="panel panel-default">
				<div class="panel-heading">저장된 Pedigree List</div>
				<div class="panel-body" style="min-height:202px; max-height:202px;">
					<table id="pedigreeList" border="1"></table>
				</div>
				<div class="row" style="vertical-align:bottom; padding-bottom:15px;">
					<h4 id="errorMsg">${errorMsg}</h4>
					<form class="form-horizontal" name="f" method="post">
						<div class="form-group">
							<div>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<input type="hidden" name="from" value="pedigreeinfo_page" />
								<input id="selectedIndex" type="hidden" name="selectedIndex" value="" />
								<!-- initial pedigree 생성을 위한 modal을 호출 -->
								<a id="btn_initial_info_modal" class="btn btn-primary" style="position: relative; margin-left: 5px; float: inherit; width: 40%;">Initial Pedigree 생성</a>
								<!-- <input id="gen_shippedPed" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/post_shippedData" value="shippedPedigree 생성" style="position: relative; margin-left: 5px; float: inherit; width: 40%;"> -->
								<!-- <a id="btn_shipped_info_modal" class="btn btn-primary" style="position: relative; margin-left: 5px; float: inherit; width: 40%;">shippedPedigree 생성</a> -->
								<!-- <input id="gen_receivedPed" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/post_receivedData" value="receivedPedigree 생성" style="position: relative; margin-left: 5px; float: inherit; width: 40%;">  -->
								<!-- pedigree export를 위한 modal을 호출 -->
								<a id="btn_partner_info_modal" class="btn btn-primary" style="position: relative; margin-left: 5px; float: inherit; width: 40%;">Pedigree 전달</a>
							</div>
						</div>
					</form>						
				</div>
			</div>
		</div>
		<div class="text-center col-sm-6">
			<!-- 선택한 pedigree의 요약정보를 tree로 표시 -->
			<h3>Pedigree 요약 정보</h3>
			<div class="panel panel-default">
				<div class="panel-heading">제품 정보</div>
				<div class="panel-body">
					<div class="panel-body text-left">
						<div id="xmlTree"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="text-center col-sm-12">
			<div class="row">
				<div class="panel panel-default">
					<div class="panel-heading">
						<span class="glyphicon glyphicon-briefcase" aria-hidden="true"></span>
						Pedigree XML
					</div>
					<div class="panel-body text-left">
						<pre>
							<!-- pedigree 선택시 pedigree의 xml 데이터를 표시 -->
							<code id="detailed_xml" class="html wordwrap"></code>
						</pre>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- The Initial Pedigree Information Modal -->
	<div id=initial_info_modal class="modal">
		<div class="modal-content-small">
			<div class="modal-header">
				<span class="close close_initial">×</span>
				<h3 style="text-align:center;">Initial Pedigree 정보 생성</h3>
			</div>
			<div class="modal-body">
				<div class="center-block text-center">
					<div class="row">
						<div class="text-center col-sm-offset-0 col-sm-12">
							<div class="panel panel-default">
								<div class="panel-heading">제조/유통 정보 입력</div>
								<h5></h5>
								<div class="panel-body">
									<form class="form-horizontal" name="f" method="post" accept-charset="UTF-8" onsubmit="document.charset='UTF-8'">
										<div class="form-group">
											<label for="inputSerialNumber" class="col-sm-3 control-label">일련번호</label>
											<div class="col-sm-6">
												<input type="text" class="form-control" id="gtin" name="serialNumber"
													placeholder="2938492849">
											</div>
										</div>
										<div class="form-group">
											<label for="inputProductName" class="col-sm-3 control-label">품명</label>
											<div class="col-sm-6">
												<input type="text" class="form-control" id="productName" name="productName"
													placeholder="사과">
											</div>
										</div>
										<div class="form-group">
											<label for="inputManufacturer" class="col-sm-3 control-label">생산자</label>
											<div class="col-sm-6">
												<input type="text" class="form-control" id=manufacturer name="manufacturer" placeholder="KAIST">
											</div>
										</div>
										<div class="form-group">
											<label for="inputProductCode" class="col-sm-3 control-label">상품코드</label>
											<div class="col-sm-6">
												<input type="text" class="form-control" id="productCode" name="productCode" placeholder="10005900">
											</div>
										</div>
										<div class="form-group">
											<label for="inputContainerSize" class="col-sm-3 control-label">containerSize</label>
											<div class="col-sm-6">
												<input type="text" class="form-control" id="containerSize" name="containerSize" placeholder="3">
											</div>
										</div>
										<div class="form-group">
											<label for="inputLot" class="col-sm-3 control-label">Lot</label>
											<div class="col-sm-6">
												<input type="text" class="form-control" id="lot" name="lot" placeholder="23">
											</div>
										</div>
										<div class="form-group">
											<label for="inputExpirationDate" class="col-sm-3 control-label">expirationDate</label>
											<div class="col-sm-6">
												<input type="text" class="form-control" id="expirationDate" name="expirationDate" placeholder="2015-09-03">
											</div>
										</div>
										<div class="form-group">
											<label for="inputQuantity" class="col-sm-3 control-label">수량</label>
											<div class="col-sm-6">
												<input type="text" class="form-control" id="quantity" name="quantity" placeholder="1">
											</div>
										</div>
										<div class="form-group">
											<label for="inputItemSerialNumber" class="col-sm-3 control-label">itemSerialNumber</label>
											<div class="col-sm-6">
												<input type="text" class="form-control" id="itemSerialNumber" name="itemSerialNumber" placeholder="1">
											</div>
										</div>
										<div class="form-group">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<input type="hidden" name="from" value="pedigreeinfo_page" />
											<input id="gen_initialPed" class="btn btn-primary"
												name="submit" type="submit"
												formaction="${homeUrl}/post_initialData"
												value="Pedigree 생성"
												style="position: relative; margin-left: 5px; float: inherit; width: 30%;">
											<input type="hidden" name="from" value="pedigreeinfo_page" />
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- The Shipped Pedigree Information Modal -->
	<!-- 
	<div id=shipped_info_modal class="modal">
		<div class="modal-content-small">
			<div class="modal-header">
				<span class="close close_shipped">×</span>
				<h3 style="text-align:center;">Shipped Pedigree 생성</h3>
			</div>
			<div class="modal-body">
				<div class="center-block text-center">
					<div class="row">
						<div class="text-center col-sm-offset-0 col-sm-12">
							<div class="panel panel-default">
								<div class="text-center col-sm-12">
						            <h3>저장된 파트너 리스트</h3>
									<div class="panel panel-default">
										<div class="panel-heading">저장된 파트너 정보</div>
										<div class="panel-body">
											<form action="${homeUrl}/post_shippedData" method="post">
												<div class="form-group">
													<label for="selectedPreviousPedigreeType" class="col-sm-3 control-label" style="align:left;" >Pedigree Type</label>
													<input id="selectedPreviousPedigreeType" name="selectedPedigreeType" type='text' readonly style="align:center; width:60%; border:0px;"/>
												</div>
												<div class="form-group">
													<label for="selectedPreviousPedigreeSgtin" class="col-sm-3 control-label" style="align:left;">SGTIN</label>
													<input id="selectedPreviousPedigreeSgtin" name="selectedPreviousPedigreeSgtin" type='text' readonly style="align:center; width:60%; border:0px;"/>
												</div>
												<section class="loginform">
													<table class="partnerlist" border="1" style="font-size: 14px; text-align:center; width:100%">
													</table>
												</section>
												<br>
												<div class="form-group">
													<select class="partnerSelect" name="select" style="font-size: 14px; text-align:center; width:50%">
													</select>
												</div>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												<input type="hidden" name="from" value="menu_page" />
												<button type="submit" class="btn btn-primary" value="shipping" style="position: relative; margin-left: 5px; float: inherit; width: 30%;">Shipped Pedigree 생성</button>
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
	</div> -->
	<!-- pedigree export를 위한 modal -->
	<div id=partner_info_modal class="modal">
		<div class="modal-content-small">
			<div class="modal-header">
				<span class="close close_partner">×</span>
				<h3 style="text-align:center;">Pedigree 전송</h3>
			</div>
			<div class="modal-body">
				<div class="center-block text-center">
					<div class="row">
						<div class="text-center col-sm-offset-0 col-sm-12">
							<div class="panel panel-default">
								<div class="text-center col-sm-12">
						            <h3>저장된 파트너 리스트</h3>
									<div class="panel panel-default">
										<div class="panel-heading">저장된 파트너 정보</div>
										<div class="panel-body">
											<form action="${homeUrl}/export" method="post">
												<div class="form-group">
													<label for="selectedPedigreeType" class="col-sm-3 control-label" style="align:left;" >Pedigree Type</label>
													<input id="selectedShippedPedigreeType" name="selectedShippedPedigreeType" type='text' readonly style="align:center; width:60%; border:0px;"/>
												</div>
												<div class="form-group">
													<label for="selectedShippedPedigreeSgtin" class="col-sm-3 control-label" style="align:left;">SGTIN</label>
													<input id="selectedShippedPedigreeSgtin" name="selectedShippedPedigreeSgtin" type='text' readonly style="align:center; width:60%; border:0px;"/>
												</div>
												<section class="loginform">
													<!-- 파트너 정보를 이곳에 삽입하여 선택하게 한 후 export 버튼을 눌러서 전송 -->
													<table class="partnerlist" border="1" style="font-size: 14px; text-align:center; width:100%">
													</table>
												</section>
												<input id="selectedPartner" type="hidden" name="selectedPartner" value="" />
												<br>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												<input type="hidden" name="from" value="menu_page" />
												<!-- pedigree export 버튼 -->
												<button type="submit" class="btn btn-primary" value="export" style="position: relative; margin-left: 5px; float: inherit; width: 30%;">Pedigree 전송</button>
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