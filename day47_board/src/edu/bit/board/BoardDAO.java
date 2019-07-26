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
	
	private BoardDAO() { }  // useBean 태그로 객체 생성하면 오류 생길 수 있음. 그거 방지하기 위해 만듦.
	
	public Connection getConnection() throws Exception {
		// 연결은 JNDI * Pool 형태로 연결 객체 생성해서 리턴할것
		Context initCtx = new InitialContext();
	
		DataSource  ds = 
			(DataSource)initCtx.lookup("java:comp/env/jdbc:BoardDB");
		System.out.println("connected...");
		
		return ds.getConnection();
	}//getConnection() end
	
	//insert(vo) method - 새 글 작성
	public int insert(BoardVO vo) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		//답글인지 원본글(새 글)인지 구분해서 입력 시키는 로직. get 사용한 것들은 default = 0
		int num = vo.getNum();	//내부적 부모 글번호
		int ref = vo.getRef();	//부모의 ref(그룹 번호)
		int re_step = vo.getRe_step();	//부모의 그룹 내 순서
		int re_level = vo.getRe_level();	//부모의 re_level
		int number = 0; // board 테이블에 들어갈 번호(바깥으로 보인느 번호)
		StringBuffer sb = new StringBuffer();
		
		try {
			conn = getConnection();
			//현재 board 테이블에 레코드 유무 판단과 글 번호 지정
			pstmt = conn.prepareStatement("select max(num) from board");
			rs = pstmt.executeQuery();
			
			if( rs.next()) { 
				number = rs.getInt(1) + 1;	//1 : num, 다음 글 번호는 가장 큰 번호 + 1
			} else {
				number = 1; //첫번째글이다	
			}
			
			// 제목글과 답변글 간의 순서 결정
			if( num!= 0) {	//답변글
				re_level = re_level + 1;
				String sql = "SELECT MAX(RE_STEP) FROM BOARD WHERE REF = ? AND RE_LEVEL = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, ref);
				pstmt.setInt(2, re_level);
				rs = pstmt.executeQuery();
				
				if( rs.next()) {
					if(re_step == 0) {
						re_step = rs.getInt(1) + 1;
					}
				} else {
					re_step = 0;	//첫번째 글
				}	// in if end
				
			} else {
				ref = number;
				re_step = 0;
				re_level = 0;
			}	//out if end
			
			//insert 처리 명령
			sb.append("insert into board(num, writer, subject, ");
			sb.append("email, content, passwd, reg_date, ref, re_step, "); //여기 강사님 오타(괄호)
			sb.append("re_level, ip) values(board_num.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, vo.getWriter());
			pstmt.setString(2, vo.getSubject());
			pstmt.setString(3, vo.getEmail());
			pstmt.setString(4, vo.getContent());
			pstmt.setString(5, vo.getPasswd());
			pstmt.setTimestamp(6, vo.getReg_date());
			pstmt.setInt(7, ref);	//위에서 이미 구해서 변수에 담아놨음
			pstmt.setInt(8, re_step);
			pstmt.setInt(9, re_level);
			pstmt.setString(10, vo.getIp());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseUtil.close(rs); CloseUtil.close(pstmt);  CloseUtil.close(conn);
		}	//try end
		
		return 0; 	//문제 없다는 신호

	}//insert end
	
	//페이징처리 시작
	
	//getListAllCount()list.jsp 페이지에서 사용할 레코드 갯수 얻어오는 함수
	public int getListAllCount() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0; 
		
		try {
			conn = getConnection();
			//현재 board 테이블의 레코드 수 구하기
			pstmt = conn.prepareStatement("SELECT COUNT (*) FROM BOARD");
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt(1);//쿼리결과(갯수)를 count변수에 넣음
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}//end try
		return count;
	}//getListAllCount end
	
	//getSelectAll(start, end) : list.jsp에서 사용
	public List<BoardVO> getSelectAll(int start, int end){
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
//			System.out.println(sb.toString());
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1,  start);
			pstmt.setInt(2,  end);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				list = new ArrayList(end);	//생성자에 end값을 넣음.... 가변길이 그릇의 크기 지정
				do {
					BoardVO vo = new BoardVO();
					vo.setNum(rs.getInt("num"));
					vo.setWriter(rs.getString("writer"));
					vo.setEmail(rs.getString("email"));
					vo.setSubject(rs.getString("subject"));
					vo.setPasswd(rs.getString("passwd"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setRef(rs.getInt("ref"));
					vo.setRe_step(rs.getInt("re_step"));
					vo.setRe_level(rs.getInt("re_level"));
					vo.setContent(rs.getString("content"));
					vo.setIp(rs.getString("ip"));
					vo.setReadcount(rs.getInt("readcount"));
					
					//list 객체에 저장빈인 BoardVO 객체에 저장
					list.add(vo);
					
				}while(rs.next());
			}	//if end
			
		} catch (Exception e) {
			e.printStackTrace();
		}	finally {
			CloseUtil.close(rs); CloseUtil.close(pstmt);  CloseUtil.close(conn);
		}
		return list;
	}	//getSelectAll end
	
	//상세보기 :content.jsp - getDataDetail() - 글 번호(num)를 가지고 상세보기 구현 가능
	@SuppressWarnings("resource")
	public BoardVO getDataDetail(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		BoardVO vo = null;
		String sql = "";
		
		try {
			conn = getConnection();
			//클릭하는 순간 조회수(readcount) 증가
			sql = "UPDATE BOARD SET READCOUNT = (READCOUNT + 1) WHERE NUM = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement("select * from Board where num = ?");
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				//객체를 꼭 블럭 안에서 채워줘야함
				vo = new BoardVO();
				vo.setNum(rs.getInt("num"));
				vo.setWriter(rs.getString("writer"));
				vo.setEmail(rs.getString("email"));
				vo.setSubject(rs.getString("subject"));
				vo.setPasswd(rs.getString("passwd"));
				vo.setReg_date(rs.getTimestamp("reg_date"));
				vo.setRef(rs.getInt("ref"));
				vo.setRe_step(rs.getInt("re_step"));
				vo.setRe_level(rs.getInt("re_level"));
				vo.setContent(rs.getString("content"));
				vo.setIp(rs.getString("ip"));
				vo.setReadcount(rs.getInt("readcount"));
			}	// if end
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			CloseUtil.close(rs); CloseUtil.close(pstmt);  CloseUtil.close(conn);
		}
		return vo;
	}	// getDataDetail(num) end
	
	//delete( num, passwd ) - deletePro.jsp
		public int delete(int num, String passwd) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String dbpasswd = "";
			int result = 0;

			try {
				conn = getConnection();
				pstmt = conn.prepareStatement("SELECT PASSWD FROM BOARD WHERE NUM = ?");
				pstmt.setInt(1, num);
				rs = pstmt.executeQuery();

				if (rs.next()) {
					dbpasswd = rs.getString("passwd");

					if (dbpasswd.equals(passwd)) {
						pstmt = conn.prepareStatement("DELETE FROM BOARD WHERE NUM=?");
						pstmt.setInt(1, num);
						result = pstmt.executeUpdate();
						result = 1; // 글삭제 성공
					} else
						result = 0; // 비밀번호 틀림

				} // out if end

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				CloseUtil.close(rs);
				CloseUtil.close(pstmt);
				CloseUtil.close(conn);
			} // end try

			return result;
		} // delete( num, passwd ) end
		
		//update(num) - 업데이트시 사용하는 함수(디비에서 다 가져다가 화면 출력)
		public BoardVO update(int num) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			BoardVO vo = null;
			
			try {
				conn = getConnection();
				pstmt = conn.prepareStatement("select * from board where num = ?");
				pstmt.setInt(1,  num);
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					vo = new BoardVO();
					vo.setNum(rs.getInt("num"));
					vo.setWriter(rs.getString("writer"));
					vo.setEmail(rs.getString("email"));
					vo.setSubject(rs.getString("subject"));
					vo.setPasswd(rs.getString("passwd"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setRef(rs.getInt("ref"));
					vo.setRe_step(rs.getInt("re_step"));
					vo.setRe_level(rs.getInt("re_level"));
					vo.setContent(rs.getString("content"));
					vo.setIp(rs.getString("ip"));
					vo.setReadcount(rs.getInt("readcount"));
				}	//if end
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				CloseUtil.close(rs);
				CloseUtil.close(pstmt);
				CloseUtil.close(conn);
			}
			return vo;
		} //update(int num) end
		
		//override (실제로 수정) <= updatePro.jsp에서 사용
	public int update(BoardVO vo) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String dbpasswd = "";
		String sql="";
		int result = -1;

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("select passwd from board where num=?");
			pstmt.setInt(1, vo.getNum());
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dbpasswd = rs.getString("passwd");
				if(dbpasswd.equals(vo.getPasswd())) {
					sql = "update board set writer=?, email=?, subject=?, ";
					sql += "passwd=?, content=? where num=?";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, vo.getWriter());
					pstmt.setString(2,  vo.getEmail());
					pstmt.setString(3,  vo.getSubject());
					pstmt.setString(4, vo.getPasswd());
					pstmt.setString(5,  vo.getContent());
					pstmt.setInt(6, vo.getNum());
					pstmt.executeUpdate(); //더이상 보낼 것이 없으면 [-1]을 리턴
					result = 1;
				} else {
					result = 0;
				}
			}//if end
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseUtil.close(rs);
			CloseUtil.close(pstmt);
			CloseUtil.close(conn);
		}
		return result;
	}// update(vo) end

}
