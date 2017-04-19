<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%> 
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
		<script type="text/javascript">
			/*
			 * PMS 관리자에 의해 사용자 각각에 대한 설정을 할때 사용되는 페이지, 사용자 관리 페이지
			 */
			// view로 전달된 사용자 정보를 저장
			var users = new Array();
			<c:forEach items="${users}" var="user">
				var info = new Object();
				info._id = "${user._id}";
				info.userID = "${user.userID}";
				info.password = "${user.password}";
				info.username = "${user.username}";
				info.department = "${user.department}";
				info.telephone = "${user.telephone}";
				info.email = "${user.email}";
				info.authorities = new Array();
				<c:forEach items="${user.roles}" var="role">
					var role = "${role}";					
					info.authorities.push(role);
				</c:forEach>
				users.push(info);
			</c:forEach>
		
			$(document).ready(function() {
				var errorMsg="${errorMsg}";
	    		if( (errorMsg != undefined) || (errorMsg != "")) {
	    			console.log(errorMsg)
	    			$("#errorMsg").text(errorMsg);
	    		}
	    		// 사용자 정보 리스트 표시
				refresh_users(users);
				
			});
			
			function refresh_users(json) {
				//테이블의 엔트리에 각 사용자 추가
                $("#userlist").empty();
                var tr = document.createElement("tr");
                $("#userlist").append(tr);
                $(tr).css("background-color", "#296cf7");
                $(tr).css("color", "#ffffff");
                $(tr).css("height", "25px");
                $(tr).css("text-align", "center");
                $(tr).css("font-size", "12px");
                
                var td = document.createElement("td");
                $(tr).append(td);
                $(td).attr("width", "10%");
                $(td).text("_id");
                var td = document.createElement("td");
                $(tr).append(td);
                $(td).attr("width", "10%");
                $(td).text("userID");
                var td = document.createElement("td");
                $(tr).append(td);
                $(td).attr("width", "10%");
                $(td).text("username");
                
                for (var i = 0; i < json.length; i++) {
                    var user = json[i];
                    var tr = document.createElement("tr");
                    $("#userlist").append(tr);
                    $(tr).css("text-align", "center");
                    $(tr).css("font-size", "12px");
                    var td2 = document.createElement("td");
                    $(td2).text(user._id);
                    $(tr).append(td2);  
                    var td3 = document.createElement("td");
                    $(td3).text(user.userID);
                    $(tr).append(td3);  
                    var td4 = document.createElement("td");
                    $(td4).text(user.username);
                    $(tr).append(td4);    
                    console.log()
                    $(tr).addClass(user.userID);
                    $(tr).click(function () {
                    	$("#userlist tr").removeClass("selected");
                    	$("#userlist tr:gt(0)").css("background-color", "white");
                    	$("#userlist tr:gt(0)").css("color", "black");
                    	$(this).addClass("selected");
                    	$(this).css("background-color", "blue");
                    	$(this).css("color", "white");
                        user_detail(this);
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
			// 사용자 리스트에서 엔트리 클릭시 각 사용자 정보를 form 필드에 추가
			function user_detail(obj) {
                for (var i = 0; i < users.length; i++) {
                	var user_id = $(obj).attr('class');
                	user_id = user_id.replace(/\s*selected\s*/, '');
                	
                    if (user_id == users[i].userID) {
                    	console.log(user_id + "//" + users[i].userID)
                    	$("#userid").val(users[i].userID);
    					$("#password").val(users[i].password);
    					$("#username").val(users[i].username);
    					$("#department").val(users[i].department);
    					$("#telephone").val(users[i].telephone);
    					$("#email").val(users[i].email);
    					var authorities_text = "";
    					for(var j=0; j<users[i].authorities.length; j++) {
    						authorities_text += (users[i].authorities[j] + " ");
    					}
    					$("#authorities").val(authorities_text);
    					$("#index").val(users[i].userID);
                        //break;
                    }
                }
            }
		</script>
<jsp:include page="common/navi.jsp">
	<jsp:param name="pageTitle" value="Menu"/>
</jsp:include>
	<div class="container" id="step1">
	    <div class="container">
	        <div class="row">
	            <div class="text-center col-sm-6">
	            <h3>사용자 정보 수정</h3>
	                <div class="panel panel-default">
	                    <div class="panel-heading">저장된 사용자 정보</div>
	                    <div class="panel-body">
	
	                        <form class="form-horizontal" action="${homeUrl}/change_list" method="post">
	                            <div class="form-group">
	                                <label for="inputID" class="col-sm-3 control-label">ID</label>
	                                <div class="col-sm-8">
	                                	<input id="userid" class="form-control" type="text" name='userid'/>
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
	                                	<input id="authorities" class="form-control" type='text' name='authorities'/>
	                                </div>
	                            </div>
	                            <h4 id="errorMsg"></h4>
	                            <!-- 새로운 사용자 추가 -->
	                            <input name="action" class="btn btn-primary" type="submit" value="Add" style="position:relative; margin-left:20px; float:inherit; width:20%;">
	                            <!-- 기존 사용자 정보 수정 -->
								<input name="action" class="btn btn-primary" type="submit" value="Change" style="position:relative; margin-left:20px; float:inherit; width:20%;">
								<!-- 기존 사용자 삭제 -->
								<input name="action" class="btn btn-primary" type="submit" value="Delete" style="position:relative; margin-left:20px; float:inherit; width:20%;">
								<input id="index" type="hidden" name="index" value="" />
								<input type="hidden" name="from" value="userlist_page" />
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	                        </form>
	                    </div>
	                </div>
		        </div>
		        <div class="text-center col-sm-6">
	            <h3>사용자 리스트</h3>
				<div class="panel panel-default">
					<div class="panel-heading">저장된 사용자 정보</div>
					<div class="panel-body">
						<section class="loginform">
							<!-- 저장된 사용자 리스트 표시 -->
							<table id="userlist" border="1" style="font-size: 14px; text-align:center">
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