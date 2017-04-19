<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
		<script type="text/javascript">
			// pedigree 생성, 검증 등에 대한 테스트 페이지, 현재 사용하지 않음
			var pedigrees = new Array();
			<c:forEach items="${pedigrees}" var="pedigree">
			var pedigree = new Object();
			pedigree.sgtin = "${pedigree.sgtin}";
			pedigree.type = "${pedigree.type}";
			pedigree.modifiedTime = "${pedigree.modifiedTime}";
			pedigree.xml = "${pedigree.xml}";
			pedigrees.push(pedigree);
			</c:forEach>

			$(document).ready(function() {
				var errorMsg = "${errorMsg}";
				if ((errorMsg != undefined) || (errorMsg != "")) {
					$("#errorMsg").text(errorMsg);
				}
				refresh_partners(partners);

			});
			$(document).ready(function() {
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
							if (j == 0)
								$(td2).text(pedigree_element.serial_num);
							else if (j == 1)
								$(td2).text(pedigree_element.pedigree.product_name);
							else if (j == 2)
								$(td2).text(pedigree_element.pedigree.gtin);
							else if (j == 3)
								$(td2).text(pedigree_element.pedigree.serial);
							else if (j == 4)
								$(td2).text(pedigree_element.pedigree.pedigree_type);
							
							$(tr).append(td2);
						}
						$(tr).addClass(pedigree_element.serial_num);
						$(tr).click(function() {
							pedigree_detail(this);
						});
						if (pedigree_element.pedigree.pedigree_type == 'initial') {
							$(tr).css("background-color", "skyblue");
						} else if (pedigree_element.pedigree.pedigree_type == 'shipped') {
							$(tr).css("background-color", "#ffaaaa");
						} else if (pedigree_element.pedigree.pedigree_type == 'disabled') {
							$(tr).css("background-color", "#ffffaa");
						} else if (pedigree_element.pedigree.pedigree_type == 'received') {
							$(tr).css("background-color", "#aaaaff");
						}
						console.log("test");
					}
				}
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
</jsp:include><div class="container" id="step1">
	<div class="container" id="step0">
		<div class="container">
			<div class="row">
				<div class="text-center col-sm-6">
					<h3>DPMS - pedigree 상태</h3>
					<div class="panel panel-default">
						<div class="panel-heading">DPMS의 pedigree 정보를 표현</div>
						<div class="panel-body">
							<table id="pedigreeList" border="1" style="font-size: 14px;">
							</table>
						</div>
					</div>
				</div>
				<div class="text-center col-sm-6">
					<h3>DPMS - pedigree 세부 정보</h3>
					<div class="panel panel-default">
						<div class="panel-heading">DPMS의 pedigree 세부 내용을 표현</div>
						<div class="panel-body">
							<div class="panel-body text-left">
								<pre>
									<code id="detailed_xml" class="html wordwrap"></code>
								</pre>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="container" id="step3">
	  <div class="row">
	    <div class="text-center col-sm-6">
	      <h3>CPMS - Pedigree 정보 수신</h3>
	      <div class="panel panel-default">
	        <div class="panel-heading">Pedigree 정보 수신</div>
	        <div class="panel-body">        
	        <div class="row"><textarea id="received_pedigree" class="form-control" rows="30"></textarea></div>
	        </div>
	      </div>
	    </div>
	    <div class="text-center col-sm-6">
	      <h3>수신한 Pedigree 검증 </h3>
	      <div class="row">
	        <div class="panel panel-default">
	          <div class="panel-heading"><span class="glyphicon glyphicon-user" aria-hidden="true"></span> 검증에 활용할 Public Key</div>
	          <div id="public_key" class="panel-body text-left wordwrap"></div>
	        </div>
	      </div>
	       <div class="row">
	        <div class="panel panel-default">
	          <div class="panel-heading"><span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span> 수신한 Pedigree 검증 결과</div>
	          <div id="verification_result" class="panel-body" style="font-size: 20px;">
	            <p id="validation_false" style="color:red"><span class="glyphicon glyphicon-remove-circle"></span> Validation Fail</p>
	            <p id="validation_true" style="color:green"><span class="glyphicon glyphicon-ok-circle"></span> Validation Success</p>
	          </div>
	        </div>
	      </div>
	      <div class="row">
	      <a id="btn_verify" class="btn btn-primary">Pedigree 정보 검증</a></div>
	    </div>
	  </div>
	</div>
	
	<div class="container" id="step2">
	  <div class="row">
	    <div class="text-center col-sm-4">
	      <h3>DPMS - 초기 Pedigree 정보 생성</h3>
	      <div class="panel panel-default">
	        <div class="panel-heading">제조/유통사에서 정보를 입력해 서명</div>
	        <div class="panel-body">
	          <form class="form-horizontal" name="f" method="post">
	            <div class="form-group">
	              <label for="inputEmail3" class="col-sm-3 control-label">일련번호</label>
	              <div class="col-sm-6">
	                <input type="text" class="form-control" id="serialNumber" placeholder="2938492849">
	              </div>
	            </div>
	            <div class="form-group">
	              <label for="inputRipple3" class="col-sm-3 control-label">품명</label>
	              <div class="col-sm-6">
	                <input type="text" class="form-control" id="drugName" placeholder="사과">
	              </div>
	            </div>
	            <div class="form-group">
	              <label for="inputName3" class="col-sm-3 control-label">생산자</label>
	              <div class="col-sm-6">
	                <input type="text" class="form-control" id="manufacturer" placeholder="KAIST">
	              </div>
	            </div>
	            <div class="form-group">
	              <label for="inputName3" class="col-sm-3 control-label">상품코드</label>
	              <div class="col-sm-6">
	                <input type="text" class="form-control" id="productCode" placeholder="10005900">
	              </div>
	            </div>
	            <div class="form-group">
	              <label for="inputName3" class="col-sm-3 control-label">containerSize</label>
	              <div class="col-sm-6">
	                <input type="text" class="form-control" id="containerSize" placeholder="3">
	              </div>
	            </div>
	            <div class="form-group">
	              <label for="inputName3" class="col-sm-3 control-label">Lot</label>
	              <div class="col-sm-6">
	                <input type="text" class="form-control" id="lot" placeholder="23">
	              </div>
	            </div>
	            <div class="form-group">
	              <label for="inputName3" class="col-sm-3 control-label">expirationDate</label>
	              <div class="col-sm-6">
	                <input type="text" class="form-control" id="expirationDate" placeholder="2015-09-03">
	              </div>
	            </div>
	            <div class="form-group">
	              <label for="inputName3" class="col-sm-3 control-label">수량</label>
	              <div class="col-sm-6">
	                <input type="text" class="form-control" id="quantity" placeholder="1">
	              </div>
	            </div>
	            <div class="form-group">
	              <label for="inputName3" class="col-sm-3 control-label">itemSerialNumber</label>
	              <div class="col-sm-6">
	                <input type="text" class="form-control" id="itemSerialNumber" placeholder="1">
	              </div>
	            </div>
	            <div class="form-group">
				<h4 id="errorMsg">${errorMsg}</h4>
				<input id="gen_initialPed" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/post_initialData" value="initialPedigree 생성" style="position:relative; margin-left:5px; float:inherit; width:30%;">
				<input type="hidden" name="from" value="companyinfo_page" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<input id="gen_shippedPed" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/post_shippedData" value="shippedPedigree 생성" style="position:relative; margin-left:5px; float:inherit; width:30%;">
				<input id="gen_receivedPed" class="btn btn-primary" name="submit" type="submit" formaction="${homeUrl}/post_receivedData" value="receivedPedigree 생성" style="position:relative; margin-left:5px; float:inherit; width:30%;">
				</div>
	          </form>
	        </div>
	      </div>
	    </div>
	    </div>
	    <div class="row">
		    <div class="text-center col-sm-7">
		      <h3>생성된 XML Pedigree</h3>
		       <div class="row">
		        <div class="panel panel-default">
		          <div class="panel-heading"><span class="glyphicon glyphicon-briefcase" aria-hidden="true"></span> 생성된 Pedigree</div>
		
		          <div class="panel-body text-left">
		            <pre><code id="generated_xml" class="html wordwrap"></code></pre>
		          </div>
		        </div>
		      </div>
			  <a id="btn_go_step3" class="btn btn-primary">다음 (서명한 Pedigree 전송)</a>
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