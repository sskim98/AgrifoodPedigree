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
		// PMS에 저장된 EPCIS 이벤트 데이터들을 표시하기 위한 루틴
			var events = new Array();
			<c:forEach items="${events}" var="event">
			var event = new Object();
			event.id = "${event._id}";
			event.sgtin = "${event.epc}";
			event.eventTime = "${event.eventTime}";
			event.recordTime = "${event.recordTime}";
			event.bizStep = '${event.bizStep}';
			event.eventXml = '${event.eventXml}';
			events.push(event);
			</c:forEach>
			// 동일한 페이지에서 event가 pedigree로 변환되는 과정을 보이므로 저장된 pedigree도 보여줘야 한다.
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
				// pedigree들에 대한 테이블 삽입
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
							});
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
				//테이블 엔트리 클릭시 세부 내용 표시
				function pedigree_detail(obj) {
					for (var i = 0; i < pedigrees.length; i++) {
						var pedigree_element = pedigrees[i];
						var selectedGtin = $(obj).attr('class').split(" ")[0]; 
						if (selectedGtin == pedigree_element.sgtin) {
							console.log(pedigree_element);
							$("#selectedIndex").val(pedigree_element.sgtin);
							$("#detailed_xml").text(vkbeautify.xml(pedigree_element.xml));
							//drawPedigreeTree(pedigree_element);
							break;
						}
					}
				}
				// 클릭한 테이블 요소에 대한 색 변경
				function setPedigreeColor(tr) {
					if($(tr).is('.Initial')) {$(tr).css("background-color", "#ffcccc");}
					else if($(tr).is('.Shipped')) {$(tr).css("background-color", "#ffffcc");}
					else if($(tr).is('.Imported')) {$(tr).css("background-color", "aaaaaa");}
					else if($(tr).is('.Received')) {$(tr).css("background-color", "ccffff");}
					$(".selected").css("background-color", "blue");
	               	$(".selected").css("color", "white");
				}
				// 저장된 event의 추가/삭제가 이루어지면 이를 다시 표시하기 위한 루틴
				function refresh_events() {
					$("#eventList").empty();
					var tr = document.createElement("tr");
					$("#eventList").append(tr);
					$(tr).css("background-color", "#297c77");
					$(tr).css("color", "#ffffff");
					$(tr).css("height", "25px");
					$(tr).css("text-align", "center");
					$(tr).css("font-size", "12px");
					$(tr).addClass("category");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "10%");
					$(td).text("상품번호");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "10%");
					$(td).text("이벤트시간");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "10%");
					$(td).text("기록시간");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "10%");
					$(td).text("비즈니스단계");

					for (var i = 0; i < events.length; i++) {
						if(i<10) {
							var event = events[i];
							var tr = document.createElement("tr");
							$(tr).css("text-align", "center");
							$(tr).css("font-size", "10px");
							$("#eventList").append(tr);
							for (var j = 0; j < 4; j++) {
								var td2 = document.createElement("td");
								if (j == 0)
									$(td2).text(event.sgtin);
								else if (j == 1)
									$(td2).text(event.eventTime);
								else if (j == 2)
									$(td2).text(event.recordTime);
								else if (j == 3)
									$(td2).text(event.bizStep.split(":")[4]);
								
								$(tr).append(td2);
							}
							$(tr).addClass(event.id);
							$(tr).click(function() {
								$("#eventList tr").removeClass("selected");
								$("#eventList tr").each(function() {
									if($(this).is(".category") == false) {
										$(this).css("color","black");
										setEventColor(this);
									}
								});
								eventDetail(this);
								$(this).addClass("selected");
		                    	$(this).css("background-color", "blue");
		                    	$(this).css("color", "white");
		                    	console.log($(this).find(':nth-child(1)').text());
		                    	$("#selectedPedigreeSgtin").val($(this).find(':nth-child(1)').text());
		                    	$("#selectedPedigreeType").val($(this).find(':nth-child(2)').text());
		                    	
							});
		                    $(tr).hover(function () {
		                        $(this).css("background-color", "#88bbaa");
		                        $(".selected").css("background-color", "blue");
		                    },function () {
		                        $(this).css("background-color", "white");
		                        //$(".selected").css("background-color", "blue");
		                        setEventColor(this);
		                    });
		                    $(tr).addClass(event._id);
		                    setEventColor(tr);
						}
					}
				}
				//이벤트를 클릭했을때 상세정보 표시
				function eventDetail(obj) {
					for (var i = 0; i < events.length; i++) {
						var event = events[i];
						var selectedID = $(obj).attr('class').split(" ")[0]; 
						if (selectedID == event.id) {
							console.log(event.id);
							$("#selectedIndex").val(event.id);
							$("#detailed_xml").text(vkbeautify.xml(event.eventXml));
							break;
						}
					}
				}
				// 테이블 내 이벤트들 색 표현
				function setEventColor(tr) {
					$(tr).css("background-color", "white");
					$(".selected").css("background-color", "blue");
                	$(".selected").css("color", "white");
				}

				
				///////////////////////////////////////////////////////////
					var errorMsg="${errorMsg}";
		    		if( (errorMsg != undefined) || (errorMsg != "")) {
		    			$("#errorMsg").text(errorMsg);
		    		}
				
		    		refresh_events();
		    		refresh_pedigree();
			});
		</script>
<jsp:include page="common/navi.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
<div class="container" id="step0">
	<div class="row">
		<div class="text-center col-sm-6">
			<h3>EPCIS Event</h3>
			<div class="panel panel-default">
				<div class="panel-heading">이벤트 정보</div>
				<div class="panel-body" style="min-height:202px; max-height:202px;">
					<table id="eventList" border="1"></table>
				</div>
				<form class="form-horizontal" name="f" method="post">
						<div class="form-group">
							<div>
								<!-- EPCIS event 쿼리 및 처리를 위한 form -->
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<input type="hidden" name="from" value="pedigreeinfo_page" />
								<input id="selectedEvent" type="hidden" name="selectedEvent" value="" />
								<input id="getEPCISEvent" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/getEPCISEvent" value="EPCIS 이벤트 쿼리" style="position: relative; margin-left: 5px; float: inherit; width: 40%;">
								<input id="handleEvent" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/handleEvent" value="이벤트 처리" style="position: relative; margin-left: 5px; float: inherit; width: 40%;">
							</div>
							<br>
						</div>
					</form>			
			</div>
		</div>
		<div class="text-center col-sm-6">
			<!-- view로 전달된 pedigree 리스트를 표시 -->
			<h3>Pedigree</h3>
			<div class="panel panel-default">
				<div class="panel-heading">저장된 Pedigree List</div>
				<div class="panel-body" style="min-height:202px; max-height:202px;">
					<table id="pedigreeList" border="1"></table>
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
							<!-- 상세 pedigree 내용 표시 -->
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
	<!-- The Initial Pedigree Information Modal -->
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
													<input id="selectedPedigreeType" name="selectedPedigreeType" type='text' readonly style="align:center; width:60%; border:0px;"/>
												</div>
												<div class="form-group">
													<label for="selectedPedigreeSgtin" class="col-sm-3 control-label" style="align:left;">SGTIN</label>
													<input id="selectedPedigreeSgtin" name="selectedPedigreeSgtin" type='text' readonly style="align:center; width:60%; border:0px;"/>
												</div>
												<section class="loginform">
													<table id="partnerlist" border="1" style="font-size: 14px; text-align:center; width:100%">
													</table>
												</section>
												<br>
												<div class="form-group">
													<select id="partnerSelect" name="select" style="font-size: 14px; text-align:center; width:50%">
													</select>
												</div>
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												<input type="hidden" name="from" value="menu_page" />
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