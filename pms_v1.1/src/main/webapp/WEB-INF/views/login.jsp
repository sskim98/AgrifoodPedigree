<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
	<head>
		<link rel="stylesheet" href="css/login_normalize.css">
		<link rel="stylesheet" href="css/login_style.css">
	    <script type="text/javascript" src="js/jquery-1.11.2.min.js"></script>
    </head>
	<body>
		<section class="loginform">
			<!-- 사용자 로그인 처리 -->
			<form name="f" action="j_spring_security_check" method="post">
				<table style="width:100%; text-align:center;">
					<tr>
						<td>Username:</td>
						<td><input type='text' name='j_userid'/></td>
					</tr>
					<tr>
						<td>Password:</td>
						<td><input type='password' name='j_password'></td>
					</tr>
					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>
				</table>
				<!-- <input value="Send" name="submit" type="submit"> -->
				<!-- Sign Up으로 이동하여 회원가입하거나, 로그인 수행 -->
				<input type="submit" value="Sign Up" name="Sign Up" formaction="${homeUrl}/signup" style="position:relative; margin-left:30px; float:inherit; width:30%;"/>
				<input id="btn_login" name="submit" type="submit" value="Login" formaction="j_spring_security_check" style="position:relative; margin-left:30px; float:inherit; width:30%;">
				<input type="hidden" name="from" value="login_page" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</section>
	</body>
</html>