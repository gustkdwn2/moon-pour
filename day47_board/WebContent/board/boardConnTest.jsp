<%@page import="edu.bit.board.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html><head>
<meta charset="UTF-8">
<title>JSP</title>
</head>
<body>
<%
	BoardDAO dao = BoardDAO.getInstance();
	dao.getConnection();
	
	out.print("board connection success");

%>
</body>
</html>