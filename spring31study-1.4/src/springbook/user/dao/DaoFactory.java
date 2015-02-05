package springbook.user.dao;

/**
 * UserDao의 생성 책임을 맡은 팩토리 클래스.
 *  - 팩토리의 메소드는 UserDao 타입의 오브젝트를 어떻게 만들고, 어떻게 준비시킬지를 결정한다.
 *  - AccountDao와 MessageDao 등이 추가되었다면 어떻게 될까?
 *   -> 중복되는 코드가 생기는 게 보인다!
 * @author kjlee
 *
 */
public class DaoFactory {

	public UserDao userDao() {
		return new UserDao( connectionMaker() );
	}

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
