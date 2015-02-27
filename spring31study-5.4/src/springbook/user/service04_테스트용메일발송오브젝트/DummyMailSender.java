package springbook.user.service04_테스트용메일발송오브젝트;

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
