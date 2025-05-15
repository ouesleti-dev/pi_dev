package Controllers;

import Models.ResetCode;
import Models.User;
import Services.AuthService;
import Services.EmailService;
import Services.ResetCodeService;
import Services.UserService;
import Utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class ForgotPasswordController {
    @FXML private TextField emailField;
    @FXML private Label messageLabel;
    @FXML private Button sendCodeButton;

    private final UserService userService = new UserService();
    private final ResetCodeService resetCodeService = new ResetCodeService();
    private final EmailService emailService = new EmailService();
    private final AuthService authService = new AuthService();

    /**
     * Gère l'envoi du code de réinitialisation
     * @param event L'événement de clic
     */
    @FXML
    public void handleSendCode(ActionEvent event) {
        // Réinitialiser le message
        hideMessage();
        
        // Récupérer l'email
        String email = emailField.getText().trim();
        
        // Vérifier que l'email n'est pas vide
        if (email.isEmpty()) {
            showMessage("Veuillez entrer votre adresse email", true);
            return;
        }
        
        try {
            // Vérifier que l'email existe dans la base de données
            if (!authService.emailExists(email)) {
                showMessage("Aucun compte n'est associé à cette adresse email", true);
                return;
            }
            
            // Récupérer l'utilisateur
            User user = userService.findUserByEmail(email);
            if (user == null) {
                showMessage("Erreur lors de la récupération de l'utilisateur", true);
                return;
            }
            
            // Générer un code de réinitialisation
            ResetCode resetCode = resetCodeService.generateResetCode(user);
            
            // Envoyer le code par email
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    user.getPrenom() + " " + user.getNom(),
                    resetCode
            );
            
            // Afficher un message de succès
            showMessage("Un code de réinitialisation a été envoyé à votre adresse email", false);
            
            // Désactiver le bouton d'envoi pour éviter les spams
            sendCodeButton.setDisable(true);
            
            // Rediriger vers la page de vérification du code
            NavigationUtil.navigateTo(event, "/Authentification/verify_code.fxml", "GoVibe - Vérification du code", user.getId());
            
        } catch (Exception e) {
            showMessage("Erreur: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    /**
     * Gère le retour à la page de connexion
     * @param event L'événement de clic
     */
    @FXML
    public void handleBackToLogin(ActionEvent event) {
        try {
            NavigationUtil.navigateTo(event, "/Authentification/login.fxml", "GoVibe - Connexion");
        } catch (Exception e) {
            showMessage("Erreur lors de la redirection: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    /**
     * Affiche un message à l'utilisateur
     * @param message Le message à afficher
     * @param isError true si c'est une erreur, false sinon
     */
    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        
        if (isError) {
            messageLabel.getStyleClass().remove("success-message");
            messageLabel.getStyleClass().add("error-message");
        } else {
            messageLabel.getStyleClass().remove("error-message");
            messageLabel.getStyleClass().add("success-message");
        }
    }
    
    /**
     * Cache le message
     */
    private void hideMessage() {
        messageLabel.setVisible(false);
    }
}
