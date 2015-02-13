package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
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
 *  3.2 변하는 것과 변하지 않는 것
 *    - JDBC try/catch/finnaly 코드의 문제점
 *      - 복잡한 try/catch/finnaly 코드의 중복
 *      - 수정 중에 c.close() 같은 코드가 빠지면 큰 문제가 발생할 수도 있음.
 *      - 문제의 핵심: 변하지 않는, 그러나 많은 곳에서 중복되는 코드와 로직을 분리하지 않은 것.
 *    - 분리와 재사용을 위한 디자인 패턴 적용
 *    	- 성격이 다른 것을 찾아내자!
 *    	- 템플릿 메소드 패턴의 적용
 *    	  1. 상속을 통해 기능을 확장해서 사용한다.
 *    	  2. 변하지 않는 부분은 슈퍼클래스에 둔다.
 *    	  3. 변하는 부분은 추상 메소드로 정의해둬서 서브클래스에서 오버라이드해서 새롭게 정의한다.
 *    	  - 문제점
 *    		-> DAO 로직마다 상속을 통해 새로운 클래스를 만들어야 한다.
 *    		-> 확장구조가 이미 클래스를 설계하는 시점에서 고정되어 버린다.
 *    	- 전략 패턴의 적용
 *    	  1. 오브젝트를 아예 둘로 분리한다.
 *    	  2. 클래스 레벨에서는 인터페이스를 통해서만 의존하도록 만든다.
 *    	  - 특정 확장 기능은 전략 인터페이스를 통해 외부의 독립된 전략 클래스에 위임한다.
 *    		-> PreparedStatement를 만드는 부분을 외부 기능을 호출하여 완성시킴.
 *    		-> 인터페이스의 메소드를 통해 PreparedStatement 생성 전략을 변경하도록 함.
 *    	  - 문제점 -> 아직 구현 클래스가 UserDao에 고정되어 있다!!!
 *    	- DI 적용을 위한 클라이언트/컨텍스트 분리
 *    	  - 전략 패턴의 실제적인 사용 방법
 *    		- Context가 어떤 전략을 사용하게 할 것인가는 Client가 결정하는 게 일반적이다!!
 *    		- Client가 구체적인 전략의 하나를 선택하고 오브젝트로 만들어서 Context에 전달한다.
 *    		- Context는 전달받은 그 Strategy 구현 클래스의 오브젝트를 사용한다.
 *    	  - DI : 이런 전략 패턴의 장점을 일반적으로 활용할 수 있도록 만든 구조.
 *
 *  3.3 JDBC 전략 패턴의 최적화
 *    - 전략 클래스의 추가 정보
 *    	- add() 메소드에도 적용.
 *    	  --> 부가정보인 user를 제공해 줄 수 없는 상황이 발생함!
 *    		--> User 정보를 생성자로부터 제공 받도록 만듬.
 *    - 전략과 클라이언트의 동거
 *      - 현재 만들어진 구조에 두 가지 불만.
 *        1. DAO 메소드마다 새로운 StatementStrategy 구현 클래스를 만들어야 한다.
 *        2. User와 같은 부가정보가 있는 경우, 생성자와 인스턴스 변수를 번거롭게 만들어야 한다.
 *      - 어떻게 해결할 수 있을까?
 *        1. 로컬 클래스를 사용
 *        	- 둘 다 UserDao에서만 사용되고, UserDao 메소드 로직에 강하게 결합되어 있음.
 *          -> 로컬 클래스로 만들어도 좋은 상태임.
 *          --> 클래스 파일이 하나 줄어듬.
 *          --> User 같은 부가정보가 있어서 쉽게 접근 가능.
 *        2. 익명 내부 클래스를 사용
 *          - 좀 더 간결하게 클래스 이름도 제거 가능!
 *          
 * 3.4 컨텍스트와 DI
 *   - JdbcContext의 분리
 *     - 클라이언트: UserDao의 메소드
 *     - 컨텍스트: jdbcContextWithStatementStrategy() 메소드
 *     	 --> 다른 DAO에서도 사용이 가능!!
 *     		--> 독립 클래스로 만들자!!
 *     - 전략: 익명 내부 클래스로 만들어지는 것
 *   - 클래스 분리
 *     - jdbcContext Class
 *       - workWithStatementStrategy() : 컨텍스트가 됨.
 *     - 스프링 빈으로 등록하여 사용
 *     	 장점: 오브젝트 사이의 의존관계가 설정파일에 명확하게 드러남.
 *       단점: 구체적인 클래스와의 관계가 설정에 직접 노출됨.
 *     - 코드로 수동 DI하여 사용
 *       장점: 그 관계가 외부에는 드러나지 않음.
 *       단점: 여러 오브젝트가 사용하도라도 싱글톤으로 만들 수 없고, DI 위한 부가적인 코드 필요.
 *     - 상황에 따라 적절한 방법을 선택한다.
 *       단, 왜 그렇게 선택했는지에 대한 분명한 이유와 근거가 있어야 한다.
 *       자신없다면, 차라리 인터페이스를 만들어 평범한 DI구조로 만들어라.
 *       
 * 3.5 템플릿과 콜백
 *    - 템플릿/콜백 패턴
 *    	- 전략 패턴의 기본 구조 + 익명 내부 클래스 활용
 *      - 복잡하지만 바뀌지 않는 일정한 패턴 작업 + 일부만 자주 바꿔 사용
 *    - 편리한 콜백의 분리와 재활용 -> executeSql(query)
 *    - 콜백과 템플릿의 결합
 *  
 *  3.6 스프링의 JdbcTemplate
 *    - JdbcTemplate: 스프링이 제공하는 JDBC 코드용 기본 템플릿.
 * 
 * 4. 예외
 *   - 예외를 처리하는 베스트 프렉티스를 살펴본다.
 * 4.1 사라진 SQLException
 *   - 예외처리와 관련된 나쁜 습관
 *     - 초난감 예외 블랙홀
 *       1. 예외를 잡고 아무것도 하지 않는다.
 *       2. 예외가 발생하면 화면에 출력하는 코드만 추가한다.
 *     - 무의미하고 무책임한 throws
 *   - 예외는 처리돼야 한다.
 *     - 화면 출력은 예외 처리가 아니다!
 *     - 방법이 없다면 throws SQLException을 선언해서 메소드 밖으로 던져라.
 *   - 모든 예외는 적절하게 복구되든지 아니면 작업을 중단시키고 운영자 또는 개발자에게 통보돼야 한다.
 *   - 예외의 종류
 *     1. Error : 시스템 에러. 어플리케이션에서는 대응 방법이 없다.
 *     2. Exception, 체크 예외 : 어플리케이션에서 처리 가능하고, 반드시 예외 처리 코드 추가 필요.
 *     3. RuntimeException, 언체크 예외: 주로 개발자 부주의해서 발생하는 경우에 발생하도록 한 예외.
 *   - 예외 처리 방법
 *     1. 예외 복구
 *     	- 문제를 해결해서 정상 상태로 돌려 놓는 것.
 *      - 체크 예외는 이렇게 예외를 복구할 가능성이 있는 경우 사용한다.
 *     2. 예외 처리 회피
 *     	- 예외 처리를 자신이 담당하지 않고, 자신을 호출한 쪽으로 던져버리는 것.
 *      - throw e;
 *      - 예외를 회피하는 것은, 의도가 분명해야 한다.
 *     3. 예외 전환 
 *     	- 적절한 예외로 전환해서 밖으로 던진다.
 *      - 예외 전환 방법
 *     	  1. 의미를 분명하게 해줄 수 있는 예외로 바꿔주기 위해 사용.
 *     	   - 보통 전환하는 예외는, 원래 발생한 예외를 담아 중첩 예외로 만드는 것이 좋다. initCase(e)
 *        2. 예외를 처리하기 쉽고 단순하게 만들기 위해 포장(Wrapper)하는 것.
 *         - 주로 체크 예외를 언체크 예외인 런타임 예외로 바꾸는 경우에 사용.
 *         - 어차피 복구가 불가능한 예외라면 가능한 한 빨리 런타임 예외로 포장해 던지게 한다.
 *   - 예외 처리 전략
 *     - 런타임 예외의 보편화
 *     - 어플리케이션 예외 처리 : 개발자가 조치를 취해야 하는 경우.
 *       1. 정상적인 처리 경우와 비정상적인 처리 경우의 리턴값을 다르게 한다.
 *       2. 정상적인 처리 경우는 그대로 두고, 비정상적 처리는 비즈니스적인 의미를 가진 예외를 던지게 한다.
 *   - SQLException은 어떻게 됐나?
 *     -> 대부분의 SQLException은 복구가 불가능하다 -> 가능한 빨리 런타임 예외로 전환해 줘야 한다.
 *     -> 스프링의 API 메소드에 정의된 예외는 대부분 런타임 예외이다 --> 생략 가능!!
 *       - DataAccessException
 * 
 * 4.2 예외 전환
 *   - 목적
 *   	1. 런타임 예외로 포장해서 굳이 불필요한 catch/throws 줄여주는 것.
 *   	2. 좀 더 의미있고 추상화된 예외로 바꿔서 던져주는 것.
 *   - JDBC의 한계
 *     - 비표준 SQL : DB마다 최적화 기법이 달라서 표준이 어려움.
 *       -> 해결방법: DAO를 DB별로 따로 만든다.
 *     - 호환성 없는 SQLException DB 에러 정보와 잘 따르지 않는 상태 코드.
 *       - DB마다 에러의 종류와 원인도 다르다.
 *       - JDBC는 데이터 처리 중 발생 에러는 그냥 SQLException 하나에 모두 담는다.
 *       -> 해결방법: DB 에러코드 매핑을 통한 전환.
 *         - DB별 에러코드를 참고해서 발생한 예외의 원인이 무엇인지 해석해 주는 기능을 만든다.
 *         -> 스프링에서 이 기능을 제공함 -> SQLErrorCodes class
 *   - 기술에 독립적인 UserDao 만들기
 *     - UserDao를 인터페이스로 만들고, 구현과 분리한다.
 *       - UserDao: 인터페이스
 *       - UserDaoJdbc: JDBC를 이용해 구현한 클래스
 *       - UserDaoJpa: JPA를 이용해 구현한 클래스
 *     
 * @author kjlee
 *
 */
public class UserDaoJdbc implements UserDao {

	private JdbcTemplate jdbcTemplate;
	private RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			return user;
		}
	};
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public UserDaoJdbc() {}
	
	public void add(final User user) {
		this.jdbcTemplate.update(
			"insert into users(id, name, password, level, login, recommend) " +
				"values(?,?,?,?,?,?)", 
				user.getId(), user.getName(), user.getPassword(), 
				user.getLevel().intValue(), user.getLogin(), user.getRecommend());
	}
	
	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?",
			new Object[] {id}, userMapper);
	}
	
	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}

	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}

	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", userMapper);
	}

	@Override
	public void update(User user) {
		this.jdbcTemplate.update(
				"update users set name=?, password=?, level=?, login=?, "
				+ "recommend=? where id=?",
					user.getName(), user.getPassword(), 
					user.getLevel().intValue(), user.getLogin(),
					user.getRecommend(), user.getId());
	}
	
}
