package edu.bit.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import dbclose.util.CloseUtil;

public class BoardDAO {

	private static BoardDAO instance = new BoardDAO();

	public static BoardDAO getInstance() {
		return instance;
	}

	private BoardDAO() {
	} // useBean 태그로 객체 생성하면 오류 생길 수 있음. 그거 방지하기 위해 만듦.

	public Connection getConnection() throws Exception {
		// 연결은 JNDI * Pool 형태로 연결 객체 생성해서 리턴할것
		Context initCtx = new InitialContext();

		DataSource ds = (DataSource) initCtx.lookup("java:comp/env/jdbc:BoardDB");
		System.out.println("connected...");

		return ds.getConnection();
	}// getConnection() end

	// insert(vo) method - 새 글 작성
	public int insert(BoardVO vo) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 답글인지 원본글(새 글)인지 구분해서 입력 시키는 로직. get 사용한 것들은 default = 0
		int num = vo.getNum(); // 내부적 부모 글번호
		int ref = vo.getRef(); // 부모의 ref(그룹 번호)
		int re_step = vo.getRe_step(); // 부모의 그룹 내 순서
		int re_level = vo.getRe_level(); // 부모의 re_level
		int number = 0; // board 테이블에 들어갈 번호(바깥으로 보인느 번호)
		StringBuffer sb = new StringBuffer();

		try {
			conn = getConnection();
			// 현재 board 테이블에 레코드 유무 판단과 글 번호 지정
			pstmt = conn.prepareStatement("select max(num) from board");
			rs = pstmt.executeQuery();

			if (rs.next()) {
				number = rs.getInt(1) + 1; // 1 : num, 다음 글 번호는 가장 큰 번호 + 1
			} else {
				number = 1; // 첫번째글이다
			}

			// 제목글과 답변글 간의 순서 결정
			if (num != 0) { // 답변글
				re_level = re_level + 1;
				String sql = "SELECT MAX(RE_STEP) FROM BOARD WHERE REF = ? AND RE_LEVEL = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, ref);
				pstmt.setInt(2, re_level);
				rs = pstmt.executeQuery();

				if (rs.next()) {
					if (re_step == 0) {
						re_step = rs.getInt(1) + 1;
					}
				} else {
					re_step = 0; // 첫번째 글
				} // in if end

			} else {
				ref = number;
				re_step = 0;
				re_level = 0;
			} // out if end

			// insert 처리 명령
			sb.append("insert into board(num, writer, subject, ");
			sb.append("email, content, passwd, reg_date, ref, re_step, "); // 여기 강사님 오타(괄호)
			sb.append("re_level, ip) values(board_num.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, vo.getWriter());
			pstmt.setString(2, vo.getSubject());
			pstmt.setString(3, vo.getEmail());
			pstmt.setString(4, vo.getContent());
			pstmt.setString(5, vo.getPasswd());
			pstmt.setTimestamp(6, vo.getReg_date());
			pstmt.setInt(7, ref); // 위에서 이미 구해서 변수에 담아놨음
			pstmt.setInt(8, re_step);
			pstmt.setInt(9, re_level);
			pstmt.setString(10, vo.getIp());

			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 필수는 아니지만 리소스들 닫아도 됨
		} // try end

		return 0; // 문제 없다는 신호

	}// insert end

	public int getListAllCount() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			conn = getConnection();

			// 현재 board 테이블의 레코드 수 구하기
			pstmt = conn.prepareStatement("select count(*) from board");
			rs = pstmt.executeQuery();

			if (rs.next())
				count = rs.getInt(1);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return count;
	}

	// getSelectAll
	public List<BoardVO> getSelectAll(int start, int end) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List list = null;
		try {
			conn = getConnection();
			StringBuffer sb = new StringBuffer();

			sb.append("select num, writer, email, subject, passwd, reg_date, ref, re_step, re_level,");
			sb.append("content, ip, readcount, r from(select num, writer, email, subject, passwd,");
			sb.append("reg_date, ref, re_step, re_level, content, ip, readcount, rownum r ");
			sb.append("from(select num, writer, email, subject, passwd, reg_date, ref, re_step,");
			sb.append("re_level, content, ip, readcount from board order by ref desc, re_step asc)");
			sb.append("order by ref desc, re_step asc, re_level asc, reg_date asc) where r>=? and r<=?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				list = new ArrayList(end);

				do {
					BoardVO vo = new BoardVO();
					vo.setNum(rs.getInt("num"));
					vo.setWriter(rs.getString("writer"));
					vo.setEmail(rs.getString("email"));
					vo.setSubject(rs.getString("subject"));
					vo.setPasswd(rs.getString("passwd"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setReadcount(rs.getInt("readcount"));
					vo.setRef(rs.getInt("ref"));
					vo.setRe_level(rs.getInt("re_level"));
					vo.setRe_step(rs.getInt("re_step"));
					vo.setIp(rs.getString("ip"));
					vo.setContent(rs.getString("content"));

					list.add(vo);
				} while (rs.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return list;
	}

	public BoardVO getDataDetail(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardVO vo = null;
		String sql = "";
		try {
			conn = getConnection();
			sql = "update board set readcount = readcount + 1 where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
			
			sql = "select * from board where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				vo = new BoardVO();
				vo.setNum(rs.getInt("num"));
				vo.setWriter(rs.getString("writer"));
				vo.setEmail(rs.getString("email"));
				vo.setSubject(rs.getString("subject"));
				vo.setPasswd(rs.getString("passwd"));
				vo.setReg_date(rs.getTimestamp("reg_date"));
				vo.setReadcount(rs.getInt("readcount"));
				vo.setRef(rs.getInt("ref"));
				vo.setRe_level(rs.getInt("re_level"));
				vo.setRe_step(rs.getInt("re_step"));
				vo.setIp(rs.getString("ip"));
				vo.setContent(rs.getString("content"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseUtil.close(rs); CloseUtil.close(pstmt);  CloseUtil.close(conn);
		}

		return vo;
	}

	public void delete() {

	}
}