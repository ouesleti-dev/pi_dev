package Controllers;

import Models.User;
import Services.AuthService;
import Utils.NavigationUtil;
import Utils.UserValidation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.net.URL;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private AuthService authService;

    public LoginController() {
        authService = new AuthService();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            if (!validateInputs(email, password)) {
                return;
            }

            User user = authService.login(email, password);
            showError("");

            // Redirection selon le rôle de l'utilisateur
            switch (user.getRole()) {
                case ROLE_ADMIN:
                    redirectToAdminInterface(event);
                    break;
                case ROLE_CLIENT:
                    redirectToClientInterface(event);
                    break;
                case ROLE_SUPER_ADMIN:
                    redirectToSuperAdminInterface(event);
                    break;
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void redirectToClientInterface(ActionEvent event) {
        try {
            // Assurez-vous que le chemin est correct et que le fichier existe
            String fxmlPath = "/Authentification/Panier.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);

            if (fxmlUrl == null) {
                throw new IllegalStateException("Impossible de trouver le fichier FXML: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Ajouter le CSS si nécessaire
            URL cssUrl = getClass().getResource("/styles/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("GoVibe - Panier");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showError("Erreur lors de la redirection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectToAdminInterface(ActionEvent event) {
        // Pour l'instant, rediriger vers l'interface client
        redirectToClientInterface(event);
    }

    private void redirectToSuperAdminInterface(ActionEvent event) {
        // Pour l'instant, rediriger vers l'interface client
        redirectToClientInterface(event);
    }

    // Méthode de redirection vers l'interface client (à implémenter plus tard)
    // private void redirectToClientInterface(ActionEvent event) {
    //     // Code de redirection à implémenter
    // }

    private boolean validateInputs(String email, String password) {
        try {
            UserValidation.isValidEmail(email);
            UserValidation.isValidPassword(password);
            return true;
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(!message.isEmpty());
    }

    @FXML
    private void initialize() {
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setVisible(false);
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setVisible(false);
        });
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            NavigationUtil.navigateTo(event, "/Authentification/register.fxml", "GoVibe - Inscription");
        } catch (Exception e) {
            showError("Erreur de redirection: " + e.getMessage());
            e.printStackTrace(); // Pour voir l'erreur complète dans la console
        }
    }
}