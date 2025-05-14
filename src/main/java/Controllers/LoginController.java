package Controllers;

import Models.User;
import Services.AuthService;
import Services.RoleService;
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
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private AuthService authService;
    private RoleService roleService;

    public LoginController() {
        authService = new AuthService();
        roleService = RoleService.getInstance();
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
            navigateToDashboard(user, event);

        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de la connexion: " + e.getMessage());
        }
        System.out.println("Fin de la procédure de connexion");
    }

    private void navigateToDashboard(User user, ActionEvent event) throws IOException {
        try {
            // Déterminer le type d'utilisateur
            String userType = roleService.getUserType(user);
            System.out.println("Type d'utilisateur détecté: " + userType);
            System.out.println("Rôle de l'utilisateur: " + user.getRole());
            System.out.println("Comparaison avec ROLE_ADMIN: " + RoleService.ROLE_ADMIN);
            System.out.println("Comparaison avec ROLE_SUPER_ADMIN: " + RoleService.ROLE_SUPER_ADMIN);
            System.out.println("Est-ce que userType == ROLE_ADMIN? " + (userType != null && userType.equals(RoleService.ROLE_ADMIN)));
            System.out.println("Est-ce que userType == ROLE_SUPER_ADMIN? " + (userType != null && userType.equals(RoleService.ROLE_SUPER_ADMIN)));

            String title;

            // Choisir le tableau de bord approprié
            String fxmlPath;

            // Vérification directe du rôle de l'utilisateur
            if (user.getRole() == User.Role.ROLE_ADMIN || user.getRole() == User.Role.ROLE_SUPER_ADMIN) {
                fxmlPath = "/fxml/admin/AdminDashboard.fxml";
                title = "Panneau d'administration";
                System.out.println("Redirection vers l'interface admin (vérification directe): " + fxmlPath);
            }
            // Vérification via userType (méthode originale)
            else if (userType != null && (userType.equals(RoleService.ROLE_ADMIN) || userType.equals(RoleService.ROLE_SUPER_ADMIN))) {
                fxmlPath = "/fxml/admin/AdminDashboard.fxml";
                title = "Panneau d'administration";
                System.out.println("Redirection vers l'interface admin (via userType): " + fxmlPath);
            } else {
                fxmlPath = "/fxml/ClientDashboard.fxml";
                title = "Tableau de bord client";
                System.out.println("Redirection vers l'interface client: " + fxmlPath);
            }

            // Obtenir l'URL du fichier FXML
            URL url = getClass().getResource(fxmlPath);
            System.out.println("URL du fichier FXML: " + url);

            if (url == null) {
                System.err.println("Impossible de trouver le fichier FXML: " + fxmlPath);

                // Essayer avec un chemin absolu
                File file = new File("src/main/resources" + fxmlPath);
                if (file.exists()) {
                    url = file.toURI().toURL();
                    System.out.println("URL créée à partir du fichier: " + url);
                } else {
                    System.err.println("Fichier FXML introuvable même avec chemin absolu: " + file.getAbsolutePath());

                    // Solution de secours pour les administrateurs
                    if (user.getRole() == User.Role.ROLE_ADMIN || user.getRole() == User.Role.ROLE_SUPER_ADMIN) {
                        // Essayer avec un autre chemin pour l'admin
                        String fallbackPath = "/Authentification/AdminDashboard.fxml";
                        System.out.println("Tentative avec chemin de secours admin: " + fallbackPath);
                        url = getClass().getResource(fallbackPath);

                        if (url == null) {
                            file = new File("src/main/resources" + fallbackPath);
                            if (file.exists()) {
                                url = file.toURI().toURL();
                                System.out.println("URL créée à partir du fichier de secours: " + url);
                            } else {
                                throw new IOException("Tous les fichiers FXML admin introuvables");
                            }
                        }
                    } else {
                        throw new IOException("Fichier FXML introuvable: " + fxmlPath);
                    }
                }
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Ajouter le CSS de base
            URL cssUrl = getClass().getResource("/styles/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("GoVibe - " + title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Erreur lors de la redirection: " + e.getMessage(), e);
        }
    }

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