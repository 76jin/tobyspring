package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

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
 *  1.4 제어의 역전 (IoC)
 *   1.4.1 오브젝트 팩토리.
 *     - 팩토리: 객체의 생성방법을 결정하고, 그렇게 만들어진 오브젝트를 돌려 주는 것.
 *     - 오브젝트의 생성과 오브젝트를 사용하는 쪽의 역할과 책임을 분리하기 위해 사용.
 *   1.4.2 오브젝트 팩토리의 활용
 *     - 중복된 코드를 메소드로 분리해 낸다.
 *   1.4.3 제어권의 이전을 통한 제어관계 역전.
 *     - 오브젝트가 자신이 사용할 오브젝트를 스스로 선택하지 않는다. 생성하지도 않는다.
 *     - 또, 자신도 어떻게 만들어지고 어디서 사용되는지를 알 수 없다.
 *     - 모든 제어 권한을 자신이 아닌 다른 대상에게 위임하기 때문이다.
 *     - 제어권을 상위 템플릿 메소드에게 넘기고, 자신은 필요할 때에 호출되도록 사용되어 진다.
 *     - IoC를 적용하면? 설계가 깔끔해지고, 유연성이 증가, 확장성이 좋아짐.
 * 1.5 스프링의 IoC
 *  1.5.1 오브젝트 팩토리를 이용한 스프링 IoC
 *  	- 어플리케이션 컨텍스트와 설정정보.
 *  	  - 스프링 빈: 스프링 컨테이너가 생성, 관계설정, 사용 등을 제어해주는 오브젝트.
 *  	  - 빈 팩토리: 스프링에서 빈의 생성과 관계설정 같은 제어를 담당하는 IoC 오브젝트.
 *  	  - 어플리케이션 컨텍스트: 빈 팩토리를 좀더 확장한 것.
 *  	  - 설정정보: 어플리케이션 컨텍스트가 IoC를 적용하기 위해 사용하는 메타정보. XML 등.
 *  1.5.2 어플리케이션 컨텍스트의 동작방식.
 * 1.6 싱글톤 레지스트리와 오브젝트 스코프
 *  1.6.1 싱글톤 레지스트리로서의 애플리케이션 컨텍스트.
 *    - 왜 스프링은 싱글톤으로 빈을 만드는 것일까?
 *      -> 스프링이 주로 적용되는 대상이 자바 엔터프라이즈 기술을 이용하는 서버환경이기 때문에.
 *    - 서블릿은 대부분 멀티스레드 환경에서 싱글톤으로 동작한다.
 *    - 서블릿 클래스당 하나의 오브젝트만 만들어두고, 사용자의 요청을 담당하는 여러 스레드에서 
 *     하나의 오브젝트를 공유해소 동시에 사용한다.
 *    - 싱글톤 패턴의 한계
 *    	1. private 생성자를 갖고 있어서 상속할 수 없다.
 *    	2. 싱글톤은 테스트하기 힘들다.
 *    	3. 서버환경에서는 싱글톤이 하나만 만들어지는 것을 보장하지 못한다.
 *    	4. 싱글톤의 사용은 전역 상태를 만들 수 있기 때문에 바람직하지 못하다.
 *    - 대안: 스프링은 직접 싱글톤 형태의 오브젝트를 관리하는 기능을 제공.(싱글톤 레지스트리)
 *      -> 스프링에서는 싱글톤 방식으로 사용될 클래스라도 public 생성자 사용 가능.
 *      -> 스프링이 지지하는 객체지향적인 설계방법과 원칙, 디자인 패턴 등을 적용하는데 문제 없다.
 *  1.6.2 싱글톤과 오브젝트의 상태
 *    - 싱글톤은 멀티스레드 환경이라면 여러 스레드가 동시에 접근해서 사용할 수 있다 -> 상태 관리 주의.
 *    - 싱글톤이 멀티스레드 환경에서 서비스 형태의 오브젝트로 사용되는 경우에는 상태정보를 내부에 갖고
 *     있지 않은 무상태(stateless) 방식으로 만들어져야 한다.
 *      다중 사용자의 요청을 한꺼번에 처리하는 스레드들이 동시에 싱글톤 오브젝트의 인스턴스 변수를
 *     수정하는 것은 매우 위험한다.
 *    - 상태가 없는 방식으로 클래스를 만들 경우에 각 요청에 대한 정보나 DB나 서버의 리소스로부터 생성한 정보는 어떻게 다뤄야 할까?
 *      -> 파라미터와 로컬변수, 리턴 값 등을 이용하면 된다.
 *  1.6.3 스프링 빈의 스코프
 *    - 스프링 빈의 기본 스코프는 싱글톤이다.
 *    - 그 외에 프로토타입 스코프, 요청 스코프, 세션 스코프.
 *      - 요청 스코프: 웹을 통해 새로운 HTTP 요청이 생길 때마다 생성됨.
 * 1.7 의존관계 주입 (DI)
 *  1.7.1 제어의 역전(IoC)과 의존관계 주입.
 *    - Dependency Injection
 *     -> 오브젝트 레퍼런스를 외부로부터 제공(주입)받고 이를 통해 다른 오브젝트와 다이나믹하게 의존관계가 만들어지는 것이 핵심.
 *  1.7.2 런타임 의존관계 설정.
 *    - 의존관계: 방향성이 있다는 의미.
 *    - 의존하고 있다 -> 의존 대상의 기능이 추가, 변경되면 그 영향을 받아 수정할 필요가 있게 된다.
 *    - 인터페이스를 통해 의존관계를 제한해주면 그만큼 변경에서 자유로워질 수 있다.
 *    - 런타입 의존관계: 런타임 시에 오브젝트 사이에서 만들어지는 의존관계
 *    - 의존관계 주입 조건
 *    	1. 인터페이스의 파라미터를 통해 주입빋음.
 *    	2. 제 3의 존재가 의존관계를 결정한다. (컨테이너, 팩토리)
 *    	3. 레퍼런스를 외부에서 제공(주입)해 줌으로써 만들어짐.
 *      -> 설계 시점에는 알지 못했던 두 오브젝트의 관계를 맺도록 도와주는 제 3의 존재가 있다는 게 핵심.
 *  1.7.3 의존관계 검색과 주입.
 *    - 의존관계 검색 ( Dependency Loockup) DL
 *      -> 의존관계 맺는 방법이 스스로 검색을 이용함.(이름, 타입 등) getBean(xxx.class)
 *    - DI와 DL 중 어느 걸 쓸까?
 *      -> DI가 훨씬 깔끔하다. 그러나 최초 DL을 써야할 때가 있다.
 *      -> DI를 원하는 오브젝트는 먼저 자기 자신이 컨테이너가 관리하는 빈이 되어야 한다!!!
 *  1.7.4 의존관계 주입의 응용
 *    - DI의 설정 정보만 다르게 만들면 다양한 환경에서 이용 가능.
 *      -> 개발 환경에서, 운영 환경에서, 테스트 환경에서 등.
 *    - 부가기능 추가가 쉬움
 *      -> 관심사의 분리를 통해 얻어지는 높은 응집도 때문에 가능.
 *  1.7.5 메소드를 이용한 의존관계 주입.
 *    1. 수정자 메소드를 이용한 주입. -> 항상 set으로 시작. 가장 많이 사용!!
 *      -> 핵심기능: 파라미터로 전달된 값을 내부의 인스턴스 변수에 저장하는 것.
 *      -> 의미있고 단순한 이름을 사용하자! 애매하면 인터페이스 이름을 쓰면 됨.
 *    2. 일반 메소드를 이용한 주입. -> 여러 개의 파라미터를 받아야 한다면.
 * 1.8 XML을 이용한 설정.
 *  1.8.1 XML 설정
 *    - @Bean 메소드를 통해 얻을 수 있는 빈의 ID 정보
 *      - 빈의 이름, 빈의 클래스, 빈의 의존 오브젝트.
 *    - 클래스 설정과 XML 설정의 대응목록
 *      - 빈 설정파일 : @Configuration 		-> <beans>
 *      - 빈의 이름  : @Bean methodName() 		-> <bean id="methodName"
 *      - 빈의 클래스 : return new BeanClass();-> class="a.b.c...BeanClass">
 *  1.8.2 XML을 이용하는 애플리케이션 컨텍스트. 
 *    - applicationContext.xml
 *    - GenericXMLApplication("applicationContext.xml")
 *  1.8.3 DataSource 인터페이스로 변환
 *    - DataSource 인터페이스 적용
 *      -> org.springframework.jdbc-xxx.jar 추가.
 *    - 자바 코드 설정 방법.
 *    - XML 설정 방법.
 *  1.8.4 프로퍼티 값의 주입.
 *    - 값 주입 : <property name="username" value="spring" />
 *  
 * @author kjlee
 *
 */
public class UserDao {
	
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private ConnectionMaker connectionMaker;
	
	// 수정자 메소드 DI 방식을 사용함.
	public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}
	
	public UserDao() {}

	// 상태를 관리하는 것도 아니니 한 번만 만들어 인스턴스 변수에 저장해두고 메소드에서 사용하게 한다.
	public UserDao(ConnectionMaker connectionMaker) {
//		connectionMaker = new DConnectionMaker();	// 앗!! 그런데 여기에는 클래스 이름이 나오네!!!
		this.connectionMaker = connectionMaker; 
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException {
		// 인터페이스에 정의된 메소드를 사용하므로 클래스가 바뀐다고 해도 메소드 이름이 변경될 걱정이 없다.
//		Connection c = connectionMaker.makeConnection();
		Connection c = dataSource.getConnection();
		
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}

	
	public User get(String id) throws ClassNotFoundException, SQLException {
//		Connection c = connectionMaker.makeConnection();
		Connection c = dataSource.getConnection();
		
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
