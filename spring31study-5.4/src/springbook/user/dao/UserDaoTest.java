package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.Level;
import springbook.user.domain.User;

/**
 * 관계설정 책임이 추가된 UserDao 클라이언트인 main 메소드.
 *  -> 팩토리를 사용하도록 수정한 main 메소드.
 *  -> 애플리케이션 컨텍스트를 적용한 main 메소드.
 *  -> XML 설정을 이용하도록 수정.
 * 
 * 2. 테스트
 * 2.3 개발자를 위한 테스트 프레임워크 JUnit
 *  - 테스트 결과의 일관성
 *    - deleteAll(), getCount() 테스트 추가.
 *     --> 동일한 결과를 보장하는 테스트 완료.
 *  - 포괄적인 테스트
 *    - getCount() 테스트 추가.
 *    - addAndGet() 테스트 보완.
 *     --> 두 개의 User를 add()하고, 각 User id로 get() 하여 결과 비교.
 *    - get() 예외조건에 대한 테스트.
 *     --> get() 메소드에 전달된 id에 값이 없다면 어떻게 될까?
 *       1. null 같은 특별한 값을 리턴하는 방법.
 *       2. id에 해당하는 정보를 찾을 수 없다고 예외를 던지는 방법. (일단 이거 적용)
 *        - 예외 클래스 필요: EmptyResultDataAccessException by spring.
 *  - 테스트 코드의 개선
 *    - @Before : 반복되는 준비 작업을 테스트 실행 전에 먼저 실행시켜 주는 메소드.
 *    - setUp() 이름으로 보통 사용.
 *    
 * 2.4 스프링 테스트 적용
 *  - 스프링 테스트 컨텍스트 프레임워크 적용
 *    - @RunWith(SpringJUnitxxx.class) : 스프링의 테스트 컨텍스트 프레임워크 적용.
 *    - @ContextConfiguration(lcations="uri xml") : 애플리케이션 컨텍스트 위치 지정.
 *    - @Autowired : 자동 주입할 오브젝트.(애플리케이션 컨텍스트 오브젝트 저장 위해)
 *      -> 변수 타입과 일치하는 컨텍스트 내의 빈을 찾아 인스턴스 변수에 자동 주입해 준다. (자동 와이어링)
 *        
 * @author kjlee
 *
 */

// Spring의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정.
@RunWith(SpringJUnit4ClassRunner.class)
// 테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정.
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserDaoTest {
	
	// 테스트 오브젝트가 만들어지고 나면 스프링 테스트 컨텍스트에 의해 자동으로 값이 주입된다.
//	@Autowired
//	private ApplicationContext context;
	
	// fixture: 테스트를 수행하는 데 필요한 정보나 오브젝트.
	@Autowired
	private UserDao dao;
	
	@Autowired
	private DataSource dataSource;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		this.user1 = new User("gyumee", "박성철", "springno1", "user1@ksug.org", Level.BASIC, 1, 0);
		this.user2 = new User("leegw700", "이길원", "springno2", "user2@ksug.org", Level.SILVER, 55, 10);
		this.user3 = new User("bumjin", "박범진", "springno3", "user3@ksug.org", Level.GOLD, 100, 40);
	}
	
	// JUnit Test Method
	@Test
	public void addAndGet() throws SQLException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		User userget1 = dao.get(user1.getId());
		assertThat(userget1.getName(), is(user1.getName()));
		assertThat(userget1.getPassword(), is(user1.getPassword()));
		
		User userget2 = dao.get(user2.getId());
		assertThat(userget2.getName(), is(user2.getName()));
		assertThat(userget2.getPassword(), is(user2.getPassword()));
	}

	// 테스트 중에 발생할 것으로 기대하는 예외 클래스를 지정해 준다.
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");	// 예외가 발생해야 테스트가 성공이 된다.
	}
	
	@Test
	public void count() throws SQLException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
	
	// 현재 등록되어 있는 모든 사용자 정보를 가져오는 테스트.
	@Test
	public void getAll() throws SQLException {
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0)); // 데이터가 없으면 크기가 0인 리스트 리턴됨.
		
		dao.add(user1);		// Id: gyumee
		List<User> users1 = dao.getAll();
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);		// Id: leegw700
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3); 	// Id: bumjin
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user3, users3.get(0));
		checkSameUser(user1, users3.get(1));
		checkSameUser(user2, users3.get(2));
	}

	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getEmail(), is(user2.getEmail()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
	}
	
	@Test(expected=DuplicateKeyException.class)
	public void duplicateKey() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user1);		// 강제로 같은 사용자를 두 번 등록한다.
	}
	
	@Test
	public void sqlExceptionTranslate() {
		dao.deleteAll();
		
		try {
			dao.add(user1);
			dao.add(user1);
		} catch (DuplicateKeyException ex) {
			SQLException sqlEx = (SQLException)ex.getRootCause();
			SQLExceptionTranslator set = // 코드를 이용한 SQLExcepion의 전환
				new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			
			assertThat(set.translate(null, null, sqlEx), is(DuplicateKeyException.class));
		}
	}
	
	@Test
	public void update() {
		dao.deleteAll();
		
		dao.add(user1);		// 수정할 사용자
		dao.add(user2);		// 수정하지 않을 사용자
		
		// user object -> search by id -> modify -> update
		user1.setName("오민규");
		user1.setPassword("springno6");
		user1.setEmail("user99@ksug.org");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		dao.update(user1);
		
		User user1Update = dao.get(user1.getId());
		checkSameUser(user1, user1Update);
		
		User user2Update = dao.get(user2.getId());
		checkSameUser(user2, user2Update);
	}
	
}