package Controllers;

import Models.User;
import Services.AuthService;
import Utils.NavigationUtil;
import Utils.UserValidation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private AuthService authService = new AuthService();

    @FXML
    protected void handleRegister(ActionEvent event) {
        try {
            // Réinitialiser le message d'erreur
            errorLabel.setVisible(false);

            // Valider les champs
            UserValidation.isValidName(nomField.getText());
            UserValidation.isValidPrenom(prenomField.getText());
            UserValidation.isValidEmail(emailField.getText());
            UserValidation.isValidPhone(telephoneField.getText());
            UserValidation.isValidPassword(passwordField.getText());

            // Vérifier que les mots de passe correspondent
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
            }

            // Créer l'utilisateur
            User user = new User(
                    nomField.getText(),
                    prenomField.getText(),
                    emailField.getText(),
                    telephoneField.getText()
            );
            user.setPassword(passwordField.getText());

            // Enregistrer l'utilisateur
            authService.register(user);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Inscription réussie");
            alert.setHeaderText("Bienvenue sur GoVibe!");
            alert.setContentText("Votre compte a été créé avec succès. Un email de bienvenue a été envoyé à " +
                    user.getEmail() + ". Vous pouvez maintenant vous connecter.");
            alert.showAndWait();

            // Rediriger vers la page de connexion
            redirectToLogin(event);

        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    @FXML
    protected void handleLogin(ActionEvent event) {
        redirectToLogin(event);
    }

    private void redirectToLogin(ActionEvent event) {
        try {
            NavigationUtil.navigateTo(event, "/Authentification/login.fxml", "GoVibe - Connexion");
        } catch (Exception e) {
            errorLabel.setText("Erreur lors de la redirection: " + e.getMessage());
            errorLabel.setVisible(true);
            e.printStackTrace(); // Pour voir l'erreur complète dans la console
        }
    }
}