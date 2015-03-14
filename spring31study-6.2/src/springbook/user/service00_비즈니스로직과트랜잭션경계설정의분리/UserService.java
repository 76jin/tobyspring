package springbook.user.service00_비즈니스로직과트랜잭션경계설정의분리;

import springbook.user.domain.User;

public interface UserService {
	void add(User user);
	void upgradeLevels();
}
