<%--
  Created by IntelliJ IDEA.
  User: ш
  Date: 02.05.16
  Time: 20:52
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <form action="/login" method="post">
        User name:<br>
        <input type="text" name="username">
        <br>
        User password:<br>
        <input type="password" name="password">
        <br>
        <input type="submit" value="Submit">
        <br>
        <c:out value="${requestScope.errorMsg}"></c:out>
    </form>
</body>
</html>
