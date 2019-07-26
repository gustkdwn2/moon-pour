<!-- writePro.jsp -->
<%@page import="java.sql.Timestamp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*,  edu.bit.board.*" %>

<% request.setCharacterEncoding("utf-8"); %>
<jsp:useBean id="vo" class="edu.bit.board.BoardVO">
	<jsp:setProperty name="vo" property="*" />
</jsp:useBean>

<%
	vo.setReg_date(new Timestamp(System.currentTimeMillis()));
	vo.setIp(request.getRemoteAddr());
	
	/* BoardDAO dao = BoardDAO.getInstance();
	dao.insert(vo); */
	
	BoardDAO.getInstance().insert(vo);
	
	response.sendRedirect("list.jsp");
%>











