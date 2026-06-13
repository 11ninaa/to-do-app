package informaciska.com.ToDo;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String toEmail, String code) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("info@todoapp.mk");
        message.setTo(toEmail);
        message.setSubject("Вашиот верификациски код");
        message.setText("Код за верификација: " + code + "\nКодот важи 10 минути.");

        try {
            mailSender.send(message);
            System.out.println("Мејл успешно испратен до: " + toEmail + " (Проверете го MailHog на порта 8025)");

        } catch (MailException e) {
            System.err.println("Грешка при испраќање на мејл до MailHog/SMTP: " + e.getMessage());
        }
    }
}