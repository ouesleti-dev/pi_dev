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
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
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
        System.out.println("Début de la procédure de connexion");
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            System.out.println("Validation des entrées utilisateur");
            if (!validateInputs(email, password)) {
                System.out.println("Validation échouée, arrêt de la procédure");
                return;
            }

            System.out.println("Tentative de connexion avec email: " + email);
            User user = authService.login(email, password);
            System.out.println("Connexion réussie pour l'utilisateur: " + user.getEmail() + ", rôle: " + user.getRole());
            showError("");

            // Redirection selon le rôle de l'utilisateur
            System.out.println("Redirection selon le rôle: " + user.getRole());
            switch (user.getRole()) {
                case ROLE_ADMIN:
                    System.out.println("Redirection vers l'interface admin");
                    redirectToAdminInterface(event);
                    break;
                case ROLE_CLIENT:
                    System.out.println("Redirection vers l'interface client");
                    redirectToClientInterface(event);
                    break;
                case ROLE_SUPER_ADMIN:
                    System.out.println("Redirection vers l'interface super admin");
                    redirectToSuperAdminInterface(event);
                    break;
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de la connexion: " + e.getMessage());
        }
        System.out.println("Fin de la procédure de connexion");
    }

    private void redirectToClientInterface(ActionEvent event) {
        System.out.println("Début de la redirection vers l'interface client");
        try {
            // Approche simplifiée pour la redirection
            System.out.println("Chargement du fichier FXML: /Authentification/Panier.fxml");
            URL fxmlUrl = getClass().getResource("/Authentification/Panier.fxml");
            if (fxmlUrl == null) {
                throw new IllegalStateException("Impossible de trouver le fichier FXML: /Authentification/Panier.fxml");
            }
            System.out.println("URL du fichier FXML: " + fxmlUrl);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            System.out.println("Chargement du contenu FXML...");
            Parent root = loader.load();
            System.out.println("Contenu FXML chargé avec succès");

            Scene scene = new Scene(root);

            // Ajouter le CSS de base
            System.out.println("Chargement du CSS: /styles/style.css");
            URL cssUrl = getClass().getResource("/styles/style.css");
            if (cssUrl != null) {
                System.out.println("CSS trouvé: " + cssUrl);
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.out.println("ATTENTION: CSS de base non trouvé");
            }

            // Le CSS spécifique du panier est déjà inclus dans le fichier FXML
            System.out.println("Configuration de la scène et affichage");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("GoVibe - Panier");
            stage.setScene(scene);
            stage.show();
            System.out.println("Redirection terminée avec succès");
        } catch (IOException e) {
            showError("Erreur lors du chargement du fichier FXML: " + e.getMessage());
            System.err.println("Détails de l'erreur: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            showError("Erreur d'état: " + e.getMessage());
            System.err.println("Détails de l'erreur d'état: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showError("Erreur inattendue: " + e.getMessage());
            System.err.println("Détails de l'erreur inattendue: " + e.getMessage());
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
        if (message == null || message.isEmpty()) {
            errorLabel.setText("Une erreur inconnue s'est produite");
        } else {
            errorLabel.setText(message);
        }
        errorLabel.setVisible(true);
        System.err.println("Erreur affichée: " + errorLabel.getText());
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