package springbook.user.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * UserDao의 생성 책임을 맡은 팩토리 클래스.
 *  - 팩토리의 메소드는 UserDao 타입의 오브젝트를 어떻게 만들고, 어떻게 준비시킬지를 결정한다.
 *  - AccountDao와 MessageDao 등이 추가되었다면 어떻게 될까?
 *   -> 중복되는 코드가 생기는 게 보인다!
 *  - DaoFactory를 사용하는 어플리케이션 컨텍스트.
 *  - DataSource 타입의 dataSource 빈 정의.
 * @author kjlee
 *
 */
// 어플리케이션 컨텍스트의 설정정보라는 표시.
@Configuration
public class DaoFactory {
	
	// 오브젝트 생성을 담당하는 IoC용 메소드라는 표시.
	@Bean
	public UserDao userDao() {
//		return new UserDao( connectionMaker() );
		UserDao userDao = new UserDao();
//		userDao.setConnectionMaker(connectionMaker());
		userDao.setDataSource(dataSource());
		return userDao;
	}

	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost/springbook");
		dataSource.setUsername("spring");
		dataSource.setPassword("book");
		return dataSource;
	}
}
