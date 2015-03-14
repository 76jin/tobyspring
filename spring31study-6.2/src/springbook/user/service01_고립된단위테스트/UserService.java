package springbook.user.service01_고립된단위테스트;

import springbook.user.domain.User;

public interface UserService {
	void add(User user);
	void upgradeLevels();
}
