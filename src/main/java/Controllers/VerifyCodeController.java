package Controllers;

import Models.ResetCode;
import Models.User;
import Services.EmailService;
import Services.ResetCodeService;
import Services.UserService;
import Utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class VerifyCodeController {
    @FXML private TextField codeField;
    @FXML private Label messageLabel;
    @FXML private Button verifyButton;

    private final UserService userService = new UserService();
    private final ResetCodeService resetCodeService = new ResetCodeService();
    private final EmailService emailService = new EmailService();
    
    private int userId;
    
    /**
     * Initialise le contrôleur avec l'ID de l'utilisateur
     * @param userId L'ID de l'utilisateur
     */
    public void initData(int userId) {
        this.userId = userId;
    }
    
    /**
     * Gère la vérification du code
     * @param event L'événement de clic
     */
    @FXML
    public void handleVerifyCode(ActionEvent event) {
        // Réinitialiser le message
        hideMessage();
        
        // Récupérer le code
        String code = codeField.getText().trim();
        
        // Vérifier que le code n'est pas vide
        if (code.isEmpty()) {
            showMessage("Veuillez entrer le code reçu par email", true);
            return;
        }
        
        // Vérifier que le code est numérique et a 6 chiffres
        if (!code.matches("\\d{6}")) {
            showMessage("Le code doit être composé de 6 chiffres", true);
            return;
        }
        
        try {
            // Vérifier le code
            ResetCode resetCode = resetCodeService.verifyCode(userId, code);
            
            if (resetCode == null) {
                showMessage("Code invalide ou expiré", true);
                return;
            }
            
            // Afficher un message de succès
            showMessage("Code vérifié avec succès", false);
            
            // Rediriger vers la page de réinitialisation du mot de passe
            NavigationUtil.navigateTo(event, "/Authentification/reset_password.fxml", "GoVibe - Réinitialisation du mot de passe", userId, resetCode.getId());
            
        } catch (Exception e) {
            showMessage("Erreur: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    /**
     * Gère le renvoi du code
     * @param event L'événement de clic
     */
    @FXML
    public void handleResendCode(ActionEvent event) {
        // Réinitialiser le message
        hideMessage();
        
        try {
            // Récupérer l'utilisateur
            User user = userService.findUserById(userId);
            if (user == null) {
                showMessage("Erreur lors de la récupération de l'utilisateur", true);
                return;
            }
            
            // Générer un nouveau code de réinitialisation
            ResetCode resetCode = resetCodeService.generateResetCode(user);
            
            // Envoyer le code par email
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    user.getPrenom() + " " + user.getNom(),
                    resetCode
            );
            
            // Afficher un message de succès
            showMessage("Un nouveau code a été envoyé à votre adresse email", false);
            
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
