package springbook.user.domain;

/**
 * 사용자 정보 저장용 자바빈 User 클래스.
 * @author kjlee
 *
 */
public class User {
	String id;
	String name;
	String password;
	String email;
	
	Level level;
	int login;
	int recommend;
	
	// 자바빈 규약을 따르는 클래스에 생성자를 명시적으로 추가했을 때는 디폴트 생성자도 함께 정의해줘야 함!
	public User() {}
	
	public User(String id, String name, String password, String email,
			Level level, int login, int recommend) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		this.email = email;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void upgradeLevel() {
		Level nextLevel = this.level.nextLevel();
		if (nextLevel == null) {
			throw new IllegalArgumentException(this.level + "은 업그레이드가 불가능합니다!");
		} else {
			this.level = nextLevel;
		}
	}

}
