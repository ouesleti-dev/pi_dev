package Controllers;

import Models.ResetCode;
import Models.User;
import Services.AuthService;
import Services.ResetCodeService;
import Services.UserService;
import Utils.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ResetPasswordController {
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private Button resetButton;

    private final UserService userService = new UserService();
    private final ResetCodeService resetCodeService = new ResetCodeService();
    private final AuthService authService = AuthService.getInstance();
    
    private int userId;
    private int resetCodeId;
    
    /**
     * Initialise le contrôleur avec l'ID de l'utilisateur et l'ID du code de réinitialisation
     * @param userId L'ID de l'utilisateur
     * @param resetCodeId L'ID du code de réinitialisation
     */
    public void initData(int userId, int resetCodeId) {
        this.userId = userId;
        this.resetCodeId = resetCodeId;
    }
    
    /**
     * Gère la réinitialisation du mot de passe
     * @param event L'événement de clic
     */
    @FXML
    public void handleResetPassword(ActionEvent event) {
        // Réinitialiser le message
        hideMessage();
        
        // Récupérer les mots de passe
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Vérifier que les mots de passe ne sont pas vides
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Veuillez remplir tous les champs", true);
            return;
        }
        
        // Vérifier que les mots de passe correspondent
        if (!password.equals(confirmPassword)) {
            showMessage("Les mots de passe ne correspondent pas", true);
            return;
        }
        
        // Vérifier que le mot de passe est assez fort (au moins 8 caractères)
        if (password.length() < 8) {
            showMessage("Le mot de passe doit contenir au moins 8 caractères", true);
            return;
        }
        
        try {
            // Récupérer l'utilisateur
            User user = userService.findUserById(userId);
            if (user == null) {
                showMessage("Erreur lors de la récupération de l'utilisateur", true);
                return;
            }
            
            // Récupérer le code de réinitialisation
            ResetCode resetCode = resetCodeService.getLastValidCodeForUser(userId);
            if (resetCode == null || resetCode.getId() != resetCodeId) {
                showMessage("Code de réinitialisation invalide ou expiré", true);
                return;
            }
            
            // Hacher le mot de passe avec bcrypt
            String hashedPassword = authService.hashPassword(password);
            
            // Mettre à jour le mot de passe de l'utilisateur
            user.setPassword(hashedPassword);
            boolean success = userService.updateUserPassword(user);
            
            if (!success) {
                showMessage("Erreur lors de la mise à jour du mot de passe", true);
                return;
            }
            
            // Marquer le code comme utilisé
            resetCodeService.markCodeAsUsed(resetCode);
            
            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Mot de passe réinitialisé");
            alert.setContentText("Votre mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter avec votre nouveau mot de passe.");
            alert.showAndWait();
            
            // Rediriger vers la page de connexion
            NavigationUtil.navigateTo(event, "/Authentification/login.fxml", "GoVibe - Connexion");
            
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
