package Controllers.Admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import Services.AuthService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminSidebarController implements Initializable {

    @FXML
    private HBox dashboardHBox;

    @FXML
    private Button dashboardButton;

    @FXML
    private HBox userHBox;

    @FXML
    private Button userButton;

    @FXML
    private HBox eventHBox;

    @FXML
    private Button eventButton;
    
    @FXML
    private HBox excursionHBox;

    @FXML
    private Button excursionButton;

    @FXML
    private HBox reservationHBox;

    @FXML
    private Button reservationButton;

    @FXML
    private Button logoutButton;

    private BorderPane mainBorderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Stocker le loader dans les propriétés du noeud pour pouvoir y accéder depuis d'autres contrôleurs
        Node thisNode = dashboardButton.getParent().getParent().getParent();
        thisNode.getProperties().put("loader", thisNode.getProperties().get("javafx.fxml.FXMLLoader"));
    }

    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    @FXML
    void handleDashboardClick(ActionEvent event) {
        loadPage("AdminDashboard.fxml");
        setActiveButton(dashboardButton);
    }

    @FXML
    private void handleUserListClick(ActionEvent event) {
        loadPage("UserList.fxml");
        setActiveButton(userButton);
    }

    @FXML
    private void handleEventListClick(ActionEvent event) {
        loadPage("EventList.fxml");
        setActiveButton(eventButton);
    }
    
    @FXML
    private void handleExcursionListClick(ActionEvent event) {
        loadPage("ExcursionList.fxml");
        setActiveButton(excursionButton);
    }

    @FXML
    private void handleReservationListClick(ActionEvent event) {
        loadPage("ReservationList.fxml");
        setActiveButton(reservationButton);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Déconnexion de l'utilisateur
            AuthService authService = AuthService.getInstance();
            authService.logout();

            // Redirection vers la page de login
            System.out.println("Déconnexion réussie");

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
            Stage stage = null;

            // Essayer d'obtenir la fenêtre à partir d'un élément visible de l'interface
            if (dashboardButton != null && dashboardButton.getScene() != null) {
                stage = (Stage) dashboardButton.getScene().getWindow();
            } else if (logoutButton != null && logoutButton.getScene() != null) {
                stage = (Stage) logoutButton.getScene().getWindow();
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
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadPage(String fxmlPath) {
        try {
            System.out.println("Chargement de la page: " + fxmlPath);

            // Construire le chemin complet
            String fullPath = "/fxml/admin/" + fxmlPath;
            System.out.println("Chemin complet: " + fullPath);

            // Utiliser getClass().getResource() pour charger le fichier FXML
            URL url = getClass().getResource(fullPath);

            if (url == null) {
                System.err.println("Impossible de trouver le fichier FXML: " + fullPath);

                // Essayer avec un chemin relatif simple
                url = getClass().getResource(fxmlPath);

                if (url == null) {
                    System.err.println("Impossible de trouver le fichier FXML avec le chemin relatif: " + fxmlPath);
                    return;
                }
            }

            System.out.println("URL trouvée: " + url);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Créer une nouvelle scène et la définir sur le stage actuel
            Scene scene = dashboardButton.getScene();
            if (scene != null) {
                Stage stage = (Stage) scene.getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Admin Panel - " + fxmlPath.replace(".fxml", ""));
                System.out.println("Page chargée avec succès dans une nouvelle scène");
            } else {
                System.err.println("Scene est null");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button button) {
        // Réinitialiser tous les boutons
        dashboardButton.getStyleClass().remove("active");
        userButton.getStyleClass().remove("active");
        eventButton.getStyleClass().remove("active");
        excursionButton.getStyleClass().remove("active");
        reservationButton.getStyleClass().remove("active");

        // Définir le bouton actif
        button.getStyleClass().add("active");
    }
}