package springbook.user.service00_비즈니스로직과트랜잭션경계설정의분리;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailSender implements MailSender {

	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		System.out.println("##### called send() in DummyMailSender class");
	}

	@Override
	public void send(SimpleMailMessage[] simpleMessages) throws MailException {
		System.out.println("##### called sends() in DummyMailSender class");
	}
}
