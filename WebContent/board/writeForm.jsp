<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../view/color.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>방명록 글쓰기</title>
<link href="../view/mystyle.css" rel="stylesheet" type=text/css>
<script type="text/javascript" src="../view/script.js"></script>
</head>
<!-- 답변글의 경우 : 부모글의 글번호, 그룹화 번호, 그룹내의 순서, 들여쓰기 결정 -->
<!-- content.jsp (상세 보기)페이지에서 넘어옴 -->
<%
	int num = 0, ref = 1, re_step = 0, re_level = 0;

	try {
		if (request.getParameter("num") != null) { //답변글일 때, 이 떄의 num은 content.jsp에서 넘어온다.
			num = Integer.parseInt(request.getParameter("num"));
			ref = Integer.parseInt(request.getParameter("ref"));
			re_step = Integer.parseInt(request.getParameter("re_step"));
			re_level = Integer.parseInt(request.getParameter("re_level"));
		} //if end
%>
<body bgcolor="<%=bodyback_c%>">
	<center>
		<b>글쓰기</b>
	</center>
	<br>
	<form action="writePro.jsp" method="post" name="writeform"
		onsubmit="return writeSave()">
		<!-- 답변을 달기 위해서는 원본글의 번호륻 들고 가야 함 -->
		<input type="hidden" name="num" value="<%=num%>"> <input
			type="hidden" name="ref" value="<%=ref%>"> <input
			type="hidden" name="re_step" value="<%=re_step%>"> <input
			type="hidden" name="re_level" value="<%=re_level%>">

		<table width='400' border='1' cellspacing='0' cellpadding='0'
			bgcolor="<%=bodyback_c%>" align="center">

			<tr>
				<td align="right" colspan="2" bgcolor="<%=value_c%>"><a
					href="list.jsp"> 글목록 보기</a></td>
			</tr>
			<tr>
				<td width="70" bgcolor="<%=value_c%>" align="center">이 름</td>
				<td width="330"><input type="text" size="10" maxlength="10"
					name="writer"></td>
			</tr>
			<tr>
				<td width="70" bgcolor="<%=value_c%>" align="center">제 목</td>
				<td width="330">
					<%
						if (request.getParameter("num") == null) { //원본글의 번호가 없으면(즉 새 글이면?)
					%>
					<input type="text" size="40" maxlength="50" name="subject">
				</td>
				<%
					} else {
				%>
				<td><input type="text" size="40" maxlength="50" name="subject"
					value="[답변] "></td>
				<%
					} //if end
				%>
			</tr>

			<!--  --------------------------------------------------------------------------------------------- -->
			<tr>
				<td width="70" bgcolor="<%=value_c%>" align="center">EMAIL</td>
				<td width="330"><input type="text" size="40" maxlength="30"
					name="email"></td>
			</tr>
			<tr>
				<td width="70" bgcolor="<%=value_c%>" align="center">내 용</td>
				<td width="330"><textarea rows="13" cols="40" name="content"></textarea></td>
			</tr>
			<tr>
				<td width="70" bgcolor="<%=value_c%>" align="center">비밀번호</td>
				<td width="330"><input type="password" size="8" maxlength="10"
					name="passwd"></td>
			</tr>
			<tr>
				<td align="center" colspan="2" bgcolor="<%=value_c%>"><input
					type="submit" value="글쓰기"> <input type="reset" value="다시작성">
					<input type="button" value="목록보기"
					onclick="windows.location='list.jsp'">
			</tr>

		</table>

		<%
			} catch (Exception e) {
				e.printStackTrace();
			}
		%>
	</form>


</body>
</html>