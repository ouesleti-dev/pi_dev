package Controllers;

import Models.User;
import Services.AuthService;
import Utils.NavigationUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Text welcomeText;

    private AuthService authService;

    public HomeController() {
        // Initialiser le service d'authentification
        authService = AuthService.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Récupérer l'utilisateur connecté
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            welcomeText.setText("Vous êtes connecté en tant que " + currentUser.getPrenom() + " " + currentUser.getNom());
        } else {
            welcomeText.setText("Aucun utilisateur connecté");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        // Déconnecter l'utilisateur
        authService.logout();

        try {
            // Charger le fichier FXML de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Authentification/login.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Appliquer les styles CSS
            URL cssUrl = getClass().getResource("/styles/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            // Obtenir la fenêtre actuelle
            // Comme l'événement peut venir d'un MenuItem qui n'est pas un Node,
            // nous utilisons une approche différente pour obtenir la fenêtre
            Stage stage = null;

            // Essayer d'obtenir la fenêtre à partir d'un élément visible de l'interface
            if (welcomeText != null && welcomeText.getScene() != null) {
                stage = (Stage) welcomeText.getScene().getWindow();
            }

            // Si nous avons trouvé la fenêtre, changer la scène
            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle("GoVibe - Connexion");
                System.out.println("Redirection vers la page de connexion effectuée");
            } else {
                throw new Exception("Impossible de trouver la fenêtre principale");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation", "Impossible de charger la page de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExit(ActionEvent event) {
        // Quitter l'application
        Platform.exit();
    }

    @FXML
    public void handleEvents(ActionEvent event) {
        // Naviguer vers la page des événements
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Fonctionnalité non implémentée");
    }

    @FXML
    public void handleProfile(ActionEvent event) {
        // Naviguer vers la page de profil
        showAlert(Alert.AlertType.INFORMATION, "Navigation", "Fonctionnalité non implémentée");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}