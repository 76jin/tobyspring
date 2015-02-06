package springbook.user.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.domain.User;

/**
 * 관계설정 책임이 추가된 UserDao 클라이언트인 main 메소드.
 *  -> 팩토리를 사용하도록 수정한 main 메소드.
 *  -> 애플리케이션 컨텍스트를 적용한 main 메소드.
 * @author kjlee
 *
 */
public class UserDaoTest {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		UserDao dao = context.getBean(UserDao.class);
		
		// test1
		DaoFactory factory = new DaoFactory();
		UserDao dao1 = factory.userDao();
		UserDao dao2 = factory.userDao();
		
		// 결과값이 다르다 --> 싱글톤이 아니라는 의미.
		System.out.println("dao1 by DaoFactory:" + dao1);
		System.out.println("dao2 by DaoFactory:" + dao2);
		
		// test2
		UserDao dao3 = context.getBean(UserDao.class);
		UserDao dao4 = context.getBean(UserDao.class);
		
		// 결과값이 동일하다 --> 싱글톤이라는 의미.
		System.out.println("dao3 by DaoFactory:" + dao3);
		System.out.println("dao4 by DaoFactory:" + dao4);
		// test end
		
		User user = new User();
		user.setId("whiteship");
		user.setName("백기선");
		user.setPassword("married");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공!");
		
		User user2 = dao.get(user.getId());
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		
		System.out.println(user2.getId() + " 조회 성공!");
	}
}