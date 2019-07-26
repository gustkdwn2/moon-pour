<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*, edu.bit.board.*"%>
<%
	request.setCharacterEncoding("UTF-8");
%>
<jsp:useBean id="vo" class="edu.bit.board.BoardVO">
	<jsp:setProperty name="vo" property="*" />
</jsp:useBean>
<%
	String pasgeNum = request.getParameter("pageNum");
	String password = request.getParameter("passwd");
	int num = vo.getNum();
	BoardDAO dao = BoardDAO.getInstance();
	boolean check = dao.delete(num, password);
	if(check) {
		response.sendRedirect("list.jsp");	
	} else {
		response.sendRedirect("deleteForm.jsp");
	}
	
	//response.sendRedirect("list.jsp");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>title</title>
</head>
<body>

</body>
</html>