<!-- deletePro.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="edu.bit.board.*" %>
<%@ page import="java.sql.Timestamp" %>

<%
	request.setCharacterEncoding("utf-8");
%>

<jsp:useBean id="vo"  class="edu.bit.board.BoardVO">
    <jsp:setProperty  name="vo"   property="*"  />
</jsp:useBean>

<%
	BoardDAO  dao = BoardDAO.getInstance();
	String num = request.getParameter("num");
	String pageNum = request.getParameter("pageNum");
	
	int check = dao.delete(Integer.parseInt(num), request.getParameter("passwd")); 
	
	if (check == 0) {
%>
		<script type="text/javascript">
			alert("비밀 번호가 맞지 않습니다.");
			history.go(-1);
		</script>
<%	}else {		%>
		<meta  http-equiv="Refresh"  content="0; url=list.jsp?pageNum=<%= pageNum %>" >
<%	}// if end %>







