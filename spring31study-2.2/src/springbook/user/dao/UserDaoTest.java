package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import springbook.user.domain.User;

/**
 * 관계설정 책임이 추가된 UserDao 클라이언트인 main 메소드.
 *  -> 팩토리를 사용하도록 수정한 main 메소드.
 *  -> 애플리케이션 컨텍스트를 적용한 main 메소드.
 *  -> XML 설정을 이용하도록 수정.
 * @author kjlee
 *
 */
public class UserDaoTest {
	
	// JUnit Test Method
	@Test
	public void addAndGet() throws ClassNotFoundException, SQLException {
		
		ApplicationContext context = new GenericXmlApplicationContext("/applicationContext.xml");
		UserDao dao = context.getBean(UserDao.class);
		
		User user = new User();
		user.setId("whiteship");
		user.setName("백기선");
		user.setPassword("married");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공!");
		
		User user2 = dao.get(user.getId());
	
		assertThat(user2.getName(), is(user.getName()));
		assertThat(user2.getPassword(), is(user.getPassword()));
	}
}