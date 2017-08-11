<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
	<head>
		<link rel="stylesheet" href="css/login_normalize.css">
		<link rel="stylesheet" href="css/login_style.css">
	    <script type="text/javascript" src="js/jquery-1.11.2.min.js"></script>
	    <script type="text/javascript">
	    	$(document).ready(function () {
	    		/*
	    		 * 회원 가입 페이지
	    		 */
	    		// 회원가입 실패시 실패 이유와 이전에 입력했던 값을 표시
	    		var errorMsg="${errorMsg}";
	    		if( (errorMsg != undefined) || (errorMsg != "")) {
	    			$("#errorMsg").text(errorMsg);
	    		}
	    		var userid = "${userid}";
	    		var password = "${password}";
	    		var username = "${username}";
	    		var department = "${department}";
	    		var telephone = "${telephone}";
	    		var email = "${email}";
	    		if(userid != undefined) {$("#userid").val(userid);}
	    		if(password != undefined) {$("#password").val(password);}
	    		if(username != undefined) {$("#username").val(username);}
	    		if(department != undefined) {$("#department").val(department);}
	    		if(telephone != undefined) {$("#telephone").val(telephone);}
	    		if(email != undefined) {$("#email").val(email);}
	    		
	    	});
	    </script>
    </head>
	<body>
		<section class="loginform">
			<form name="f" action="${homeUrl}/signup" method="post">
				<table style="width:100%; text-align:left;">
					<tr>
						<td>ID:</td>
						<td><input id="userid" type='text' name='userid'/></td>
					</tr>
					<tr>
						<td>Password:</td>
						<td><input id="password" type='password' name='password'></td>
					</tr>
					<tr>
						<td>Username:</td>
						<td><input id="username" type='text' name='username'></td>
					</tr>
					<tr>
						<td>Department:</td>
						<td><input id="department" type='text' name='department'></td>
					</tr>
					<tr>
						<td>Telephone Number:</td>
						<td><input id="telephone" type='text' name='telephone'></td>
					</tr>
					<tr>
						<td>Email:</td>
						<td><input id="email" type='text' name='email'></td>
					</tr>
					<tr>
						<td colspan="1">&nbsp;</td>
					</tr>
				</table>
				<!-- <input value="Send" name="submit" type="submit"> -->
				<h4 id="errorMsg">${errorMsg}</h4>
				<input name="submit" type="submit" value="Sign up" style="position:relative; margin-left:30px; float:inherit; width:30%;">
				<input type="hidden" name="from" value="signup_page" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</section>
	</body>
</html>