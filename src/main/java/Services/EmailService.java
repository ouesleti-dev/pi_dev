package Services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private final String username = "mohamedoueslati788@gmail.com";
    private final String password = "dfeu ivtv jiyr hieq";

    public void sendWelcomeEmail(String recipientEmail, String recipientName) throws MessagingException {
        // Configurer les propriétés
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Créer une session avec authentification
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Créer le message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Bienvenue sur GoVibe!");

        // Corps du message HTML
        String htmlContent =
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                        "<div style='background-color: #4285F4; color: white; padding: 20px; text-align: center;'>" +
                        "<h1>Bienvenue sur GoVibe!</h1>" +
                        "</div>" +
                        "<div style='padding: 20px; background-color: #f9f9f9;'>" +
                        "<p>Bonjour " + recipientName + ",</p>" +
                        "<p>Nous sommes ravis de vous accueillir sur GoVibe. Votre compte a été créé avec succès!</p>" +
                        "<p>Vous pouvez maintenant vous connecter et profiter de tous nos services.</p>" +
                        "<div style='text-align: center; margin: 30px 0;'>" +
                        "<a href='http://www.govibe.com/login' style='background-color: #4285F4; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Se connecter</a>" +
                        "</div>" +
                        "<p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>" +
                        "<p>Cordialement,<br>L'équipe GoVibe</p>" +
                        "</div>" +
                        "<div style='background-color: #333; color: white; padding: 15px; text-align: center; font-size: 12px;'>" +
                        "&copy; 2023 GoVibe. Tous droits réservés." +
                        "</div>" +
                        "</div>";

        message.setContent(htmlContent, "text/html; charset=utf-8");

        // Envoyer le message
        Transport.send(message);
    }
}