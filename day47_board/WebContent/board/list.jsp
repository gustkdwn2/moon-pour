<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page  import="edu.bit.board.*" %>
<%@ page import="java.util.*  ,  java.text.SimpleDateFormat"  %>

<%@ include file = "/view/color.jsp" %>

<%
	int pageSize = 7;
	int SU = pageSize - 1;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	String pageNum = request.getParameter("pageNum");
	
	if(pageNum == null) pageNum = "1";
	
	int currentPage = Integer.parseInt(pageNum);
	int startRow = (currentPage * pageSize) - SU;
	int endRow = (currentPage * pageSize);
	int count =0, number = 0;
	
	List list = null;
	BoardDAO dao = BoardDAO.getInstance();
	count = dao.getListAllCount();
	if( count > 0 ) {
		list = dao.getSelectAll(startRow, endRow);
		
	}
	
	number = count - (currentPage - 1) * pageSize;
	
%>

<html><head><title>게시판</title></head>
<link href="/view/mystyle.css" rel="stylesheet" type="text/css">
<body bgcolor="<%=bodyback_c %>">
	<center>
		<b>글 목록(전체 글 : <%= count %> 입니다)
		</b>
	
	<table width="700">
		<tr>
			<td bgcolor="<%=value_c%>" align="right"><a href="writeForm.jsp">글쓰기</a>
			</td>
		</tr>
	</table>
	<% if(count == 0 ) { %>
	<table width="700" border="1" cellpadding="0" cellspacing="0">
		<tr>
			<td align="center">게시판에 저장된 글이 없습니다.</td>
		</tr>
	</table>
	<% } else { %>
	<table width="700" border="1" cellpadding="0" cellspacing="0"		align="center">
		<tr height="30" bgcolor="<%=value_c%>">
			<td align="center" width="50">번 호</td>
			<td align="center" width="50">제 목</td>
			<td align="center" width="50">작성자</td>
			<td align="center" width="50">작성날짜</td>
			<td align="center" width="50">조회수</td>
			<td align="center" width="50">I P</td>
	<%
		for(int i=0; i < list.size(); i++ ) {
			BoardVO vo = (BoardVO)list.get(i);
	%>
		<tr height="30" >
			<td align="center" width="50"><%= number-- %></td>
			<td width="250">
			
	<%
	 		int wid = 0;
			if( vo.getRe_level() > 0 ) {  //답변글이라면,....
				wid = 5 * (vo.getRe_level());
	%><img src="../images/level.gif " width="<%=wid %>"  height="16" >	
			<img src="../images/re.gif" >
	<% }else  { %>		
		<img src="../images/re.gif" width="<%=wid %>"  height="16" >
	<% } // if end  %>	
	
		<a href="content.jsp?num=<%=vo.getNum() %>&pageNum=<%=currentPage %>">
				<%= vo.getSubject() %>
		</a>
		
	<%
			if ( vo.getReadcount() >= 3 ) {
	%> <img src="../images/hot.gif" width="<%=wid %>"  height="16" >
	<%    } // if end %>
			</td>
			<td align="center"  width="100">
					<a href="mailto:<%=vo.getEmail()%>" > <%=vo.getEmail()%> </a></td>
			<td align="center"  width="150"><%= sdf.format(vo.getReg_date()) %> </td>
			<td align="center"  width="150"><%= vo.getReadcount() %> </td>
			<td align="center"  width="150"><%= vo.getIp() %> </td>
		</tr>
	<% } //for end %>
	</table>
	<% } %>
	
	<%
			if( count > 0 ) {  //전체 페이지의 수를 연산
				int pageCount = count / pageSize + ( count % pageSize == 0 ? 0 : 1 );
				int startPage = (int)(currentPage / 5 ) * 5 + 1 ;
				int pageBlock = 5;
				int endPage = startPage + pageBlock - 1 ;
				
				if ( endPage > pageCount ) endPage = pageCount ;
				
				if ( startPage >5 ) {			
	%>
			<a href="list.jsp?pageNum=<%=startPage-5 %>">[이전]</a>		
	<%
				} //if end
				
				for( int i = startPage ; i <= endPage ;  i++ ) {
	%>
			<a href="list.jsp?pageNum=<%=i %>"><%=i %></a>	
	<%
				} // for end
				if ( endPage < pageCount ) {			
	%>
		<a href="list.jsp?pageNum=<%=startPage+5 %>">[다음]</a>	
	<%
				} //  if end
			} //out if end
	%>
</body>
</center>
</html>
