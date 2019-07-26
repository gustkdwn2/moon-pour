<!-- updatePro.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="edu.bit.board.*  , java.sql.Timestamp" %>

<% request.setCharacterEncoding("utf-8"); %>

<jsp:useBean id="vo" class="edu.bit.board.BoardVO">
	<jsp:setProperty name="vo" property="*" />
</jsp:useBean>

<%
	String pageNum = request.getParameter("pageNum");
	BoardDAO dao = BoardDAO.getInstance();
	int check = dao.update(vo); //실제 변경 내용 반영 함수
	
	if( check == 1 ){
%>	
	<meta http-equiv="Refresh" content="0; url=list.jsp?pageNum=<%=pageNum %>" >	

<%	}else { %>
	
	<script type="text/javascript">
	<!--
		alert("비밀 번호가 맞지 않습니다.");
		history.go(-1);
	//-->
	</script>
		
<% 	}//if end%>
















