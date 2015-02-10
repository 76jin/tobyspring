package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

/**
 * JDBC를 이용한 등록과 조회 기능이 있는 USerDao 클래스.
 * 
 * 1.2 DAO의 분리 (관심사의 분리)
 *  - UserDao의 관심사항
 * 	  1. DB와 연결을 위한 커넥션을 어떻게 가져올까.
 * 	  2. 사용자 등록을 위해 DB에 보낼 SQL 문장을 담을 Statement를 만들고 실행하는 것.
 * 	  3. 작업이 끝나면 사용한 리소스인 Statement와 Connection 리소스를 돌려주는 것.
 * 
 *  - 원칙과 패턴
 *    - 개방 폐쇄 원칙 (OCP)
 *    - 높은 응집도와 낮은 결합도.
 *    - 전략 패턴 Strategy pattern
 *      - UserDaoTest -> Userdao -> ConnectionMaker <- DConnectionMaker
 *      
 *  - 의존관계 주입 조건
 *    1. 인터페이스의 파라미터를 통해 주입빋음.
 *    2. 제 3의 존재가 의존관계를 결정한다. (컨테이너, 팩토리)
 *    3. 레퍼런스를 외부에서 제공(주입)해 줌으로써 만들어짐.
 *      -> 설계 시점에는 알지 못했던 두 오브젝트의 관계를 맺도록 도와주는 제 3의 존재가 있다는 게 핵심.
 *  
 *  2. 테스트
 *  2.3 개발자를 위한 테스팅 프레임워크 JUnit.
 *    - deleteAll(), getcount() 추가.
 *    
 *  3. 템플릿
 *    - 템플릿: 일정한 패턴으로 유지되는 특성을 가진 부분을 독립시켜서 활용하는 방법.
 *    - 3장 목적
 *    	1. 스프링에 적용된 템플릿 기법을 살펴보고,
 *    	2. 이를 적용해 완성도 있는 DAO 코드를 만드는 법을 알아본다.
 *  3.1 다시보는 초난감 DAO
 *    - UserDao의 문제점 --> 예외처리가 빠져 있음.
 *    - 예외처리 기능을 갖춘 DAO
 *    	- JDBC 수정 기능의 예외처리
 *    	- JDBC 조회 기능의 예외처리
 *  
 * @author kjlee
 *
 */
public class UserDao {
	
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public UserDao() {}
	
	public void add(User user) throws SQLException {
		Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}

	
	public User get(String id) throws SQLException {
		Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		User user = null;
		
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}
		
		rs.close();
		ps.close();
		c.close();
		
		// 결과가 없으면 지정한 예외를 던져주도록 한다.
		if (user == null)
			throw new EmptyResultDataAccessException(1);
		
		return user;
	}
	
	public void deleteAll() throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;

		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement("delete from users");
			
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (ps != null) {
				try { ps.close(); } catch (SQLException e) {}
			}
			if (c != null) {
				try { c.close(); } catch (SQLException e) {}
			}
		}
	}
	
	public int getCount() throws SQLException {
		
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement("select count(*) from users");
			
			rs = ps.executeQuery();
			rs.next();
			
			return rs.getInt(1);
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) {}
			}
			if (ps != null) {
				try { ps.close(); } catch (SQLException e) {}
			}
			if (c != null) {
				try { c.close(); } catch (SQLException e) {}
			}
		}
	}
	
}
