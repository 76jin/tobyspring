package springbook.user.service05_목오브젝트이용테스트;

import java.util.ArrayList;
import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MockMailSender implements MailSender {
	private List<String> requests = new ArrayList<String>();
	
	public List<String> getRequests() {
		return requests;
	}

	@Override
	public void send(SimpleMailMessage mailMessage) throws MailException {
		System.out.println("##### called send() in MockMailSender class");
		requests.add(mailMessage.getTo()[0]);
	}

	@Override
	public void send(SimpleMailMessage[] mailMessage) throws MailException {
	}

}
