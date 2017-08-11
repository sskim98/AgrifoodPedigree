<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
	    <script type="text/javascript">
	    	/*
	    	 * 사용자 정보 보기, 수정을 위한 페이지
	    	 */
	    	// view로 넘어온 사용자 정보를 저장
			var user = new Object();
			var error="${errorMsg}";
			console.log(error)
			user._id = "${user._id}";
			user.userID = "${user.userID}";
			user.password = "${user.password}";
			user.username = "${user.username}";
			user.department = "${user.department}";
			user.telephone = "${user.telephone}";
			user.email = "${user.email}";
			user.authorities = new Array();
			<c:forEach items="${user.roles}" var="role">
				var role = "${role}";					
				user.authorities.push(role);
			</c:forEach>
		
	    	$(document).ready(function () {
	    		// 사용자 정보 업데이트 실패시 오류 내용 표시
	    		var errorMsg="${errorMsg}";
	    		if( (errorMsg != undefined) || (errorMsg != "")) {
	    			$("#errorMsg").text(errorMsg);
	    		}
	    		// 사용자 정보를 필드에 자동 입력
	    		$("#userid").val(user.userID);
				$("#password").val(user.password);
				$("#username").val(user.username);
				$("#department").val(user.department);
				$("#telephone").val(user.telephone);
				$("#email").val(user.email);
				var authorities_text = "";
				for(var j=0; j<user.authorities.length; j++) {
					authorities_text += (user.authorities[j] + " ");
				}
				$("#authorities").val(authorities_text);
				$("#index").val(user.userID);
				
	    	});
	    </script>
<jsp:include page="common/navi.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
	<div class="container" id="step1">
	    <div class="container">
	        <div class="row">
	            <div class="text-center col-sm-6">
	                <div class="panel panel-default">
	                    <div class="panel-heading">저장된 사용자 정보</div>
	                    <div class="panel-body">
	
	                        <form class="form-horizontal" action="${homeUrl}/change_user" method="post">
	                            <div class="form-group">
	                                <label for="inputID" class="col-sm-3 control-label">ID</label>
	                                <div class="col-sm-8">
	                                	<!-- 수정할 수 없는 필드는 수정불가로 표시 -->
	                                	<input id="userid" class="form-control" type="text" name='userid' readonly/>
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputPassword" class="col-sm-3 control-label">Password</label>
	                                <div class="col-sm-8">
	                                	<input id="password" class="form-control" type='text' name='password' />
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputName" class="col-sm-3 control-label">Username</label>
	                                <div class="col-sm-8">
	                                	<input id="username" class="form-control" type='text' name='username' />
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputDept" class="col-sm-3 control-label">Department</label>
	                                <div class="col-sm-8">
	                                	<input id="department" class="form-control" type='text' name='department' />
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputTel" class="col-sm-3 control-label">Telephone Number</label>
	                                <div class="col-sm-8">
	                                	<input id="telephone" class="form-control" type='text' name='telephone' />
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputEmail" class="col-sm-3 control-label">Email</label>
	                                <div class="col-sm-8">
	                                	<input id="email" class="form-control" type='text' name='email' />
	                                </div>
	                            </div>
	                            <div class="form-group">
	                                <label for="inputAuthority" class="col-sm-3 control-label">Authorities</label>
	                                <div class="col-sm-8">
	                                	<input id="authorities" class="form-control" type='text' name='authorities' readonly/>
	                                </div>
	                            </div>
	                            <h4 id="errorMsg"></h4>
								<input name="submit" class="btn btn-primary" type="submit" value="change" style="position:relative; margin-left:30px; float:inherit; width:30%;">
								<input id="index" type="hidden" name="index" value="" />
								<input type="hidden" name="from" value="userinfo_page" />
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