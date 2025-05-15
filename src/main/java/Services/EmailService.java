package Services;

import Models.ResetCode;
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

    /**
     * Envoie un email contenant un code de réinitialisation de mot de passe
     * @param recipientEmail L'adresse email du destinataire
     * @param recipientName Le nom du destinataire
     * @param resetCode Le code de réinitialisation
     * @throws MessagingException En cas d'erreur d'envoi
     */
    public void sendPasswordResetEmail(String recipientEmail, String recipientName, ResetCode resetCode) throws MessagingException {
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
        message.setSubject("Réinitialisation de votre mot de passe GoVibe");

        // Calculer le temps d'expiration en minutes
        long expirationMinutes = 10; // 10 minutes d'expiration

        // Corps du message HTML
        String htmlContent =
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                        "<div style='background-color: #4285F4; color: white; padding: 20px; text-align: center;'>" +
                        "<h1>Réinitialisation de mot de passe</h1>" +
                        "</div>" +
                        "<div style='padding: 20px; background-color: #f9f9f9;'>" +
                        "<p>Bonjour " + recipientName + ",</p>" +
                        "<p>Vous avez demandé la réinitialisation de votre mot de passe sur GoVibe.</p>" +
                        "<p>Voici votre code de vérification à 6 chiffres :</p>" +
                        "<div style='text-align: center; margin: 30px 0;'>" +
                        "<div style='font-size: 32px; font-weight: bold; letter-spacing: 5px; padding: 15px; background-color: #eee; border-radius: 5px; display: inline-block;'>" + resetCode.getCode() + "</div>" +
                        "</div>" +
                        "<p>Ce code est valable pendant " + expirationMinutes + " minutes.</p>" +
                        "<p>Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.</p>" +
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