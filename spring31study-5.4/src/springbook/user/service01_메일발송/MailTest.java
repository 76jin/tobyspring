package springbook.user.service01_메일발송;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
import junit.framework.TestCase;
 
 
public class MailTest extends TestCase {
    public void testMailSend(){
        try {
            String[] emailList = { "ranian129@naver.com" };// 메일 보낼사람 리스트 
            String emailFromAddress = "ranian129@gmail.com";// 메일 보내는 사람
            String emailMsgTxt = "메일 테스트 내용 "; // 내용
            String emailSubjectTxt = "잘가는지 테스트 중~~~~~~~~~~";// 제목
             
            // 메일보내기 
            postMail(emailList, emailSubjectTxt, emailMsgTxt, emailFromAddress);
            System.out.println("모든 사용자에게 메일이 성공적으로 보내졌음~~");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
     
    private void postMail(String recipients[], String subject, String message, String from) throws MessagingException {
        boolean debug = false;
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
  
        String SMTP_HOST_NAME = "gmail-smtp.l.google.com";
         
        // Properties 설정
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
  
        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getDefaultInstance(props, auth);
  
        session.setDebug(debug);
  
        // create a message
        Message msg = new MimeMessage(session);
  
        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);
  
        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);
  
        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }
  
    /**
     * 구글 사용자 메일 계정 아이디/패스 정보
     */
    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username =  "id"; // gmail 사용자;
            String password = "pw"; // 패스워드;
            return new PasswordAuthentication(username, password);
        }
    }
}
