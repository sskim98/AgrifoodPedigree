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
			 * pedigree 검색 결과를 보여주기 위한 페이지, 주로 mobile에서 표시
			 */
			// view로 전달된 pedigree 정보의 저장
			var pedigree = new Object();
			pedigree.sgtin = "${searchResult.sgtin}";
			pedigree.type = "${searchResult.type}";
			pedigree.modifiedTime = "${searchResult.modifiedTime}";
			pedigree.xml = '${searchResult.xml}';
			
			// pedigree의 trace 정보 저장
			var path = new Array();
			<c:forEach items="${traceInfo}" var="element">
				var element = new Object();
				element.company = "${element.company}";
				element.type = "${element.type}";
				element.validity = '${element.validity}';
				path.push(element);
			</c:forEach>
			
			$(document).ready(function() {	
				// pedigree trace 정보 설정
				function set_trace_info() {
					$("#traceInfo").empty();
					var tr = document.createElement("tr");
					$("#traceInfo").append(tr);
					$(tr).css("background-color", "#868686");
					$(tr).css("color", "#ffffff");
					$(tr).css("height", "25px");
					$(tr).css("text-align", "center");
					$(tr).css("font-size", "12px");
					$(tr).css("line-height", "1.5");
					$(tr).css("border-bottom", "2px solid #cc0000")
					$(tr).addClass("category");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "10%");
					$(td).text("회사(담당자)");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "10%");
					$(td).text("타입");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "10%");
					$(td).text("시간");
					var td = document.createElement("td");
					$(tr).append(td);
					$(td).attr("width", "20%");
					$(td).text("검증 결과");

					for (var i = 0; i < path.length; i++) {
						if(i<10) {
							var element = path[i];
							var tr = document.createElement("tr");
							$(tr).css("text-align", "center");
							$(tr).css("font-size", "12px");
							if(element.validity.indexOf("PKI") != -1) {
								$(tr).css("background-color", "#aaffff");
							}
							else if(element.validity.indexOf("Private") != -1) {
								$(tr).css("background-color", "#ffffaa");
							}
							$("#traceInfo").append(tr);
							for (var j = 0; j < 4; j++) {
								var td2 = document.createElement("td");
								if (j == 0)
									$(td2).text(element.company);
								else if (j == 1)
									$(td2).text(element.type);
								else if (j == 2)
									$(td2).text(element.eventTime);
								else if (j == 3)
									$(td2).text(element.validity);
								$(tr).append(td2);
							}
						}
					}
				}
				// 검색된 pedigree  상세 정보 표시
				function pedigree_detail(obj) {
					for (var i = 0; i < pedigrees.length; i++) {
						var pedigree_element = pedigrees[i];
						console.log(pedigree_element.sgtin + " : " + $(obj).attr('class'));
						if ($(obj).attr('class').indexOf(pedigree_element.sgtin) != -1) {
							$("#selectedIndex").val(pedigree_element.sgtin);
							$("#detailed_xml").text(vkbeautify.xml(pedigree_element.xml));
							drawPedigreeTree(pedigree_element);
							break;
						}
					}
				}
				// pedigree 타입에 따라 테이블 엔트리 색상 다르게 표시
				function setPedigreeColor(tr) {
					if($(tr).is('.Initial')) {$(tr).css("background-color", "#ffcccc");}
					else if($(tr).is('.Shipped')) {$(tr).css("background-color", "#ffffcc");}
					else if($(tr).is('.Unconfirmed')) {$(tr).css("background-color", "ffffff");}
					else if($(tr).is('.Received')) {$(tr).css("background-color", "ccffff");}
					$(".selected").css("background-color", "blue");
                	$(".selected").css("color", "white");
				}
				
				function pedigree_detail(obj) {
					$("#detailed_xml").text(vkbeautify.xml(pedigree.xml));
					drawPedigreeTree(pedigree);
				}
				// pedigree 개괄 정보를 트리로 표시
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
				set_trace_info();
				pedigree_detail(pedigree);
				
			});
		</script>
</head>
<body>
<div class="container" id="step0">
	<div class="row">
		<div class="text-center col-sm-6">
			<!-- pedigree 요약 정보 표시 -->
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
		<div class="text-center col-sm-6">
			<!-- pedigree 이동 정보 표시 -->
			<h3>Pedigree 이동 정보</h3>
			<div class="panel panel-default">
				<div class="panel-heading">이동 경로</div>
				<div class="panel-body" style="min-height:102px;">
					<table id="traceInfo" border="1"></table>
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
							<!-- pedigree 상세 정보 표시 -->
							<code id="detailed_xml" class="html wordwrap"></code>
						</pre>
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