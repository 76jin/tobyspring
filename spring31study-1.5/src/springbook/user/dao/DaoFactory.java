package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * UserDao의 생성 책임을 맡은 팩토리 클래스.
 *  - 팩토리의 메소드는 UserDao 타입의 오브젝트를 어떻게 만들고, 어떻게 준비시킬지를 결정한다.
 *  - AccountDao와 MessageDao 등이 추가되었다면 어떻게 될까?
 *   -> 중복되는 코드가 생기는 게 보인다!
 *  - DaoFactory를 사용하는 어플리케이션 컨텍스트.
 * @author kjlee
 *
 */
// 어플리케이션 컨텍스트의 설정정보라는 표시.
@Configuration
public class DaoFactory {

	// 오브젝트 생성을 담당하는 IoC용 메소드라는 표시.
	@Bean
	public UserDao userDao() {
		return new UserDao( connectionMaker() );
	}

	@Bean
	// 분리해서 중복을 제거한 ConnectionMaker 타입 오브젝트 생성 코드.
	public ConnectionMaker connectionMaker() {
		return new NConnectionMaker();
	}
	
//	public AccountDao accountDao() {
//		return new AccountDao(connectionMaker());
//	}
//	public MessageDao messageDao() {
//		return new MessageDao(connectionMaker());
//	}
}
