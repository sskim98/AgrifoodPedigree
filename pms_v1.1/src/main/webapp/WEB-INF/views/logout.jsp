<?xml version="1.0" encoding="UTF-8" ?>
<jsp:directive.page contentType="text/html" pageEncoding="UTF-8" />
 
              
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!-- 사용자 로그아웃 되었을 때 표시 -->
<% session.invalidate(); %>
You are now logged out!!

<a href="/pms/login">go back</a>