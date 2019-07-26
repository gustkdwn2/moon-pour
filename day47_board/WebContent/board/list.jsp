<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page  import="edu.bit.board.*" %>
<%@ page import="java.util.*  ,  java.text.SimpleDateFormat"  %>

<%@ include file = "/view/color.jsp" %>

<%
	int pageSize = 7;  //화면에 출력 레코드 수
	int SU = pageSize - 1; 
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	String pageNum = request.getParameter("pageNum");
	
	if( pageNum == null ) pageNum = "1";
	
	int currentPage = Integer.parseInt(pageNum);  // ex) 1
	int startRow = (currentPage * pageSize) - SU; // (1 * 7) - 6 = 1 : start
	int endRow = (currentPage * pageSize);		  // (1 * 7) = 7 : end
	int count = 0, number = 0;  //전체글수, 화면에 보여줄 글번호
	
	List list = null;
	BoardDAO dao = BoardDAO.getInstance();
	count = dao.getListAllCount(); //전체 페이지 수 리턴
	
	if( count > 0 ) {
		list = dao.getSelectAll(startRow, endRow);
	}
	
	number = count - (currentPage - 1) * pageSize; 
	// ex)     16 - (1 - 1) * 7  = 16
	// ex)     16 - (2 - 1) * 7  = 9		
			
%>

<html><head><title>게시판</title></head>
<link href="/view/mystyle.css" rel="stylesheet" type="text/css">
<body bgcolor="<%=bodyback_c %>">
	<center>
		<b>글 목록(전체 글 : <%= count %>)
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
		for(int i=0; i < list.size(); i++ ) { //ArrayList 에 저장된 list size 만큼 반복
			BoardVO vo = (BoardVO)list.get(i);
	%>
		<tr height="30" >
			<td align="center" width="50"><%= number-- %></td>
			<td width="250">
			
	<%
	 		int wid = 0;
			if( vo.getRe_step() > 0 ) {  //답변글이라면 답변 이미지를 Subject앞에 출력 
				wid = 5 * (vo.getRe_step());
	%><img src="../images/level.gif " width="<%=wid %>"  height="16" >	
			<img src="../images/re.gif" >
	<% }//if end %>		
	
		<a href="content.jsp?num=<%=vo.getNum() %>&pageNum=<%=currentPage %>">
				<%= vo.getSubject() %>
		</a>
		
	<%
			if ( vo.getReadcount() >= 3 ) { // 조회수가 3이상이면 hot이미지 subject뒤에 삽입			
	%> <img src="../images/hot.gif"  height="16" >
		
	<%      } // if end %>
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
	
	<%      // 페이지 번호 //////////
			if( count > 0 ) {  //전체 페이지의 수를 연산  ex) 16 % 7 = 2
				int pageCount = count / pageSize + ( count % pageSize == 0 ? 0 : 1 );
				int startPage = (int)(currentPage / 5 ) * 5 + 1 ; //(1/5)*5+1 = 1
				int pageBlock = 5;
				int endPage = startPage + pageBlock - 1 ;  // 1 + 5 - 1 = 5
				
				if ( endPage > pageCount ) endPage = pageCount ;
				
				if ( startPage > 5 ) {			
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
