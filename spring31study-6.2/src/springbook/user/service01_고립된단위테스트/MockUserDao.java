package springbook.user.service01_고립된단위테스트;

import java.util.ArrayList;
import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class MockUserDao implements UserDao {

	private List<User> users;	// 레벨 업그레이드 후보 User 오브젝트 목록
	private List<User> updated = new ArrayList<User>(); // 업그레이드 대상 오브젝트를 저장
	
	public MockUserDao(List<User> users) {
		this.users = users;
	}

	public List<User> getUpdated() {
		return this.updated;
	}
	
	public List<User> getAll() {
		return this.users;
	}
	
	public void update(User user) {
		this.updated.add(user);
	}
	
	// 실수로 사용될 위험을 방지하기 위해 "지원하지 않는 기능"이라는 예외를 발생시킴.
	public void add(User user) { throw new UnsupportedOperationException();	}
	public User get(String id) { throw new UnsupportedOperationException();	}
	public void deleteAll() { throw new UnsupportedOperationException(); }
	public int getCount() { throw new UnsupportedOperationException(); }
}
