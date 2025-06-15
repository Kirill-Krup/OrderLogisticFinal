package com.courcach.Server.Utils;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.Random;
public class MailUtil {
    private static final String SENDER_EMAIL = "krupeto302@gmail.com";
    private static final String SENDER_PASSWORD = "ovgf ytab ykrb fqjg";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    public MailUtil() {}

    public String sendVerificationCode(String recipientEmail) {
        String code = generateRandomCode(6);
        String subject = "Ваш код подтверждения для LogisticOrder";
        String body = "Здравствуйте!\n\n"
                + "Ваш 6-значный код подтверждения: " + code + "\n\n"
                + "Пожалуйста, введите этот код в приложении для завершения операции.\n"
                + "Если вы не запрашивали этот код, пожалуйста, проигнорируйте это письмо.\n\n"
                + "С уважением,\nКоманда LogisticOrder";

        // Настройки SMTP сервера
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Использовать STARTTLS
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));

        // Создание сессии с аутентификацией
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("Email sent successfully to " + recipientEmail + " with code: " + code);
            return code;
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + recipientEmail + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            code.append(rand.nextInt(10)); // Случайная цифра от 0 до 9
        }
        return code.toString();
    }
}
