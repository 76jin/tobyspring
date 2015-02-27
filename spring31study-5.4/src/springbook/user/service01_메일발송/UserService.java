package springbook.user.service01_메일발송;

import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserService {
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;

	private PlatformTransactionManager transactionManager;
	UserDao userDao;

	public void setTransactionManager(
			PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void upgradeLevels() {
		TransactionStatus status = 
		this.transactionManager.getTransaction(new DefaultTransactionDefinition());
				
		try {
			List<User> users = userDao.getAll();
			for (User user : users) {
				if (canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			this.transactionManager.commit(status);
		} catch (Exception e) {
			this.transactionManager.rollback(status);
			throw e;
		}
	}

	protected void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
		sendUpgradeEmail(user);
//		sendUpgradeEmail_gmail(user);
	}

	private void sendUpgradeEmail(User user) {
		final String FROM_EMAIL = "test@test.com";		// 보내는 사람
		final String TO_EMAIL = "ranian129@naver.com";	// 받는 사람
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "mail.ksug.org");
		Session s = Session.getInstance(props, null);
		
		MimeMessage message = new MimeMessage(s);
		try {
			message.setFrom(new InternetAddress(FROM_EMAIL));
//			message.addRecipient(Message.RecipientType.TO, 
//					new InternetAddress(user.getEmail()));
			message.addRecipient(Message.RecipientType.TO, 
					new InternetAddress(TO_EMAIL));
			message.setSubject("Upgrade 안내");
			message.setText("사용자님의 등급이 " + user.getLevel().name() +
					"로 업그레이드되었습니다.");
			Transport.send(message);
		} catch (AddressException e) {
			throw new RuntimeException(e);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException(e);
		}
	}
	private void sendUpgradeEmail_gmail(User user) {
		final String USER_ID = "xxx@gmail.com";	// gmail 계정 ID
		final String USER_PW = "xxx";				// gmail 계정 비밀번호
		final String FROM_EMAIL = "test@test.com";		// 보내는 사람
		final String TO_EMAIL = "ranian129@naver.com";	// 받는 사람
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USER_ID, USER_PW);
			}};
			Session s = Session.getInstance(props, auth);
			
			MimeMessage message = new MimeMessage(s);
			try {
				message.setFrom(new InternetAddress(FROM_EMAIL));
//			message.addRecipient(Message.RecipientType.TO, 
//					new InternetAddress(user.getEmail()));
				message.addRecipient(Message.RecipientType.TO, 
						new InternetAddress(TO_EMAIL));
				message.setSubject("Upgrade 안내");
				message.setText("사용자님의 등급이 " + user.getLevel().name() +
						"로 업그레이드되었습니다.");
				Transport.send(message);
			} catch (AddressException e) {
				throw new RuntimeException(e);
			} catch (MessagingException e) {
				throw new RuntimeException(e);
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException(e);
			}
	}

	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch(currentLevel) {
			case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
			case GOLD: return false;
			default: throw new IllegalArgumentException("Unknown Level:"+currentLevel);
		}
	}

	public void add(User user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}
	
	
	
}
