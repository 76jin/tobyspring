package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

/**
 * JDBC를 이용한 등록과 조회 기능이 있는 USerDao 클래스.
 * 1.2 DAO의 분리 (관심사의 분리)
 *  - UserDao의 관심사항
 * 	1. DB와 연결을 위한 커넥션을 어떻게 가져올까.
 * 	  - getConnection() 메소드를 추출해서 중복을 제거한 UserDao
 * 	  - DB 연결 만들기의 독립.
 * 		- 상속을 통한 확장.
 * 	2. 사용자 등록을 위해 DB에 보낼 SQL 문장을 담을 Statement를 만들고 실행하는 것.
 * 	3. 작업이 끝나면 사용한 리소스인 Statement와 Connection 리소스를 돌려주는 것.
 * 1.3 DAO의 확장.
 * 	1.3.1 클래스의 분리.
 * 	  - 독립된 DB Connection을 사용하게 만든 UserDao
 *    - 문제점:
 *    	1. makeNewConnection() 메소드 이름을 변경하면, UserDao의 파일도 수정해야 한다.
 *    	2. DB 연결을 제공하는 클래스가 어떤 것인지를 UserDao가 알고 있어야 한다.
 *    	근본원인: UserDao가 바뀔 수 있는 정보에 대해 너무 많이 알고 있다.
 *  1.3.2 인터페이스의 도입.
 *    - 두 개의 클래스가 서로 긴밀하게 연결되어 있지 않도록 중간에 추상적인 느슨한 연결고리를 만듬.
 *    - 문제점:
 *    	1. 여전히 클래스 이름이 UserDao 에 나오고 있다.
 *  1.3.3 관계설정 책임의 분리.
 *    - 문제의 원인: UserDao 안에 분리되지 않은, 또 다른 관심사항이 존재하고 있다 -> DB 연결관련.
 *    - 해결방법: 다른 관심을 분리해서 클라이언트에게 넘긴다.
 *  1.3.4 원칙과 패턴.
 *    - 개방 폐쇄 원칙 (OCP)
 *    	: 클래스나 모듈은 확장에는 열려 있어야 하고 변경에는 닫혀 있어야 한다.
 *    - 높은 응집도와 낮은 결합도.
 *    	- 높은 응집도: 변화가 일어날 때 해당 모듈에서 변하는 부분이 크다는 것.
 *    	- 낮은 결합도: 하나의 변경이 발생할 때 최대한 다른 모듈이나 객체는 수정할 필요 없도록 만드는 것.
 *    - 전략 패턴 Strategy pattern
 *    	- 필요에 따라 변경이 필요한 알고리즘(기능)을 인터페이스를 통해 통째로 외부로 분리시키고,
 *       이를 구현한 구체적인 알고리즘 클래스를 필요에 따라 바꿔서 사용할 수 있게 하는 디자인 패턴.
 *      - UserDaoTest -> Userdao -> ConnectionMaker <- DConnectionMaker
 * 
 * @author kjlee
 *
 */
public class UserDao {

	private ConnectionMaker connectionMaker;

	// 상태를 관리하는 것도 아니니 한 번만 만들어 인스턴스 변수에 저장해두고 메소드에서 사용하게 한다.
	public UserDao(ConnectionMaker connectionMaker) {
//		connectionMaker = new DConnectionMaker();	// 앗!! 그런데 여기에는 클래스 이름이 나오네!!!
		this.connectionMaker = connectionMaker; 
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException {
		// 인터페이스에 정의된 메소드를 사용하므로 클래스가 바뀐다고 해도 메소드 이름이 변경될 걱정이 없다.
		Connection c = connectionMaker.makeConnection();
		
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}

	
	public User get(String id) throws ClassNotFoundException, SQLException {
		Connection c = connectionMaker.makeConnection();
		
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
	}
	
}
