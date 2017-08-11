<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
		<script type="text/javascript">
			/*
			 * pedigree 생성을 위해 product 정보를 연동해야 하는데 GS1 source와 연동하기 어려우므로 PMS에 저장하기 위한 페이지
			 */
			// 현재 view로 전달된 제품 정보를 저장
			var products = new Array();
			<c:forEach items="${products}" var="product">
				var info = new Object();
				info.name = "${product.name}";
				info.manufacturer = "${product.manufacturer}";
				info.productCode = "${product.productCode}";
				info.productCodeType = "${product.productCodeType}";
				info.dosageForm = "${product.dosageForm}";
				info.strength = "${product.strength}";
				info.containerSize = "${product.containerSize}";
				info.lot = "${product.lot}";
				info.expirationDate = "${product.expirationDate}";
				products.push(info);
			</c:forEach>
			
			$(document).ready(function() {
				var errorMsg="${errorMsg}";
	    		if( (errorMsg != undefined) || (errorMsg != "")) {
	    			$("#errorMsg").text(errorMsg);
	    		}
	    		// 제품 정보 표시
				refresh_products(products);
				
			});
			// 저장된 제품정보 리스트를 표로 표시
			function refresh_products(info) {
                $("#productlist").empty();
                var tr = document.createElement("tr");
                $("#productlist").append(tr);
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
                $(td).text("manufacturer");
                var td = document.createElement("td");
                $(tr).append(td);
                $(td).attr("width", "10%");
                $(td).text("productCode");
                
                for (var i = 0; i < info.length; i++) {
                    var product = info[i];
                    var tr = document.createElement("tr");
                    $("#productlist").append(tr);
                    $(tr).css("text-align", "center");
                    $(tr).css("font-size", "12px");
                    var td2 = document.createElement("td");
                    $(td2).text(product.name);
                    $(tr).append(td2);  
                    var td3 = document.createElement("td");
                    $(td3).text(product.manufacturer);
                    $(tr).append(td3);  
                    var td4 = document.createElement("td");
                    $(td4).text(product.productCode);
                    $(tr).append(td4);
                    $(tr).addClass(product.productCode);
                    $(tr).click(function () {
                    	//표의 엔트리를 클릭하면 상품정보를 input 필드에 표시
                    	$("#productlist tr").removeClass("selected");
                    	$("#productlist tr:gt(0)").css("background-color", "white");
                    	$("#productlist tr:gt(0)").css("color", "black");
                    	$(this).addClass("selected");
                    	$(this).css("background-color", "blue");
                    	$(this).css("color", "white");
                    	product_detail(this);
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
			// 제품 상세정보 표시
			function product_detail(obj) {
                for (var i = 0; i < products.length; i++) {
                	var product_id = $(obj).attr('class');
                	product_id = product_id.replace(/\s*selected\s*/, '');
                	
                    if (product_id == products[i].productCode) {
                    	$("#name").val(products[i].name);
                    	$("#manufacturer").val(products[i].manufacturer);
                    	$("#productCode").val(products[i].productCode);
                    	$("#productCodeType").val(products[i].productCodeType);
                    	$("#dosageForm").val(products[i].dosageForm);
                    	$("#strength").val(products[i].strength);
                    	$("#containerSize").val(products[i].containerSize);
                    	$("#lot").val(products[i].lot);
                    	$("#expirationDate").val(products[i].expirationDate);
    					$("#index").val(products[i].productCode);
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
	                <div class="panel panel-default">
	                    <div class="panel-heading">저장된 제품 정보</div>
	                    <div class="panel-body">
	
	                        <form class="form-horizontal" action="${homeUrl}/change_product" method="post">
	                            <div class="form-group">
	                                <label for="name" class="col-sm-3 control-label">Product Name</label>
	                                <div class="col-sm-8">
	                                	<input id="name" class="form-control" type="text" name='name' placeholder="carrot"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="manufacturer" class="col-sm-3 control-label">Manufacturer</label>
	                                <div class="col-sm-8">
	                                	<input id="manufacturer" class="form-control" type="text" name='manufacturer' placeholder="KAIST"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="productCode" class="col-sm-3 control-label">Product Code</label>
	                                <div class="col-sm-8">
	                                	<input id="productCode" class="form-control" type="text" name='productCode' placeholder="061414111111"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="productCodeType" class="col-sm-3 control-label">Product Code Type</label>
	                                <div class="col-sm-8">
	                                	<input id="productCodeType" class="form-control" type="text" name='productCodeType' placeholder="gtin"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="dosageForm" class="col-sm-3 control-label">Dosage Form</label>
	                                <div class="col-sm-8">
	                                	<input id="dosageForm" class="form-control" type="text" name='dosageForm' placeholder="Nullifiable"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="strength" class="col-sm-3 control-label">Strength</label>
	                                <div class="col-sm-8">
	                                	<input id="strength" class="form-control" type="text" name='strength' placeholder="Nullifiable"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="containerSize" class="col-sm-3 control-label">Container Size</label>
	                                <div class="col-sm-8">
	                                	<input id="containerSize" class="form-control" type="text" name='containerSize' placeholder="12"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="lot" class="col-sm-3 control-label">Lot</label>
	                                <div class="col-sm-8">
	                                	<input id="lot" class="form-control" type="text" name='lot' placeholder="urn:epc:id:sgln:0614141.07346.1234"/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="expirationDate" class="col-sm-3 control-label">Period of Circulation</label>
	                                <div class="col-sm-8">
	                                	<input id="expirationDate" class="form-control" type='text' name='expirationDate' placeholder="30" />
	                                </div>
	                            </div>
	                            <h4 id="errorMsg"></h4>
	                            <!-- 제품정보 추가 -->
	                            <input name="action" class="btn btn-primary" type="submit" value="Add" style="position:relative; margin-left:20px; float:inherit; width:20%;">
	                            <!-- 제품정보 업데이트 -->
								<input name="action" class="btn btn-primary" type="submit" value="Change" style="position:relative; margin-left:20px; float:inherit; width:20%;">
								<!-- 제품정보 삭제 -->
								<input name="action" class="btn btn-primary" type="submit" value="Delete" style="position:relative; margin-left:20px; float:inherit; width:20%;">
								<input id="index" type="hidden" name="index" value="" />
								<input type="hidden" name="from" value="productlist_page" />
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	                        </form>
	                    </div>
	                </div>
		        </div>
		        <div class="text-center col-sm-6">
				<div class="panel panel-default">
					<div class="panel-heading">제품 정보 리스트</div>
					<div class="panel-body">
						<section class="panel-body" style="min-height:202px; max-height:202px;">
							<!-- 제품 정보 리스트 표시 -->
							<table id="productlist" border="1" style="font-size: 14px; text-align:center">
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