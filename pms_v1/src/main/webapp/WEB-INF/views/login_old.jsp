<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<jsp:include page="common/header.jsp">
	<jsp:param name="pageTitle" value="Login"/>
</jsp:include>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!-- 사용자 로그인 테스트를 위해 사용했던 view , 현재 사용하지 않음 -->
<h1>Please Log In to Your Account</h1>
<p>
	Please use the form below to log in to your account.
</p>
<spring:url value="/j_spring_security_check" var="loggin" />

<%-- <form action="j_spring_security_check" method="post"> --%>
<form action="${loggin}" method="post"> 
	<label for="j_username">Login</label>:
	<input id="j_username" name="j_username" size="20" maxlength="50" type="text"/>
	<br />

<%-- For experimentation with an alternate checkbox name
	<input id="_remember_me" name="_remember_me" type="checkbox" value="true"/>
	<label for="_remember_me">Remember Me?</label>
	<br />
--%>

	<input id="_spring_security_remember_me" name="_spring_security_remember_me" type="checkbox" value="true"/>
	<label for="_spring_security_remember_me">Remember Me?</label>
	<br />
	<label for="j_password">Password</label>:
	<input id="j_password" name="j_password" size="20" maxlength="50" type="password"/>
	<br />
	
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	<button type="submit" value="Login">login</button>
	
</form>

<jsp:include page="common/footer.jsp"/>