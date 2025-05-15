package Controllers.Admin;

import Models.Event;
import Models.User;
import Services.EventService;
import Services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private Button userListButton;

    @FXML
    private Button eventListButton;

    @FXML
    private Button reservationListButton;

    @FXML
    private Label userCountLabel;

    @FXML
    private BarChart<String, Number> eventStatusChart;

    @FXML
    private CategoryAxis eventStatusAxis;

    @FXML
    private NumberAxis eventCountAxis;

    private UserService userService;
    private EventService eventService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation du dashboard
        System.out.println("Dashboard Admin initialisé");

        // Initialiser les services
        userService = new UserService();
        eventService = EventService.getInstance();

        // Charger les statistiques
        loadUserStatistics();
        loadEventStatusChart();
    }

    /**
     * Charge les statistiques des utilisateurs
     */
    private void loadUserStatistics() {
        try {
            // Récupérer le nombre d'utilisateurs avec le rôle ROLE_CLIENT
            List<User> clients = userService.getClients();
            userCountLabel.setText(String.valueOf(clients.size()));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques utilisateurs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge le graphique des événements par statut
     */
    private void loadEventStatusChart() {
        try {
            // Récupérer tous les événements
            List<Event> events = eventService.getAllEvents();

            // Compter les événements par statut
            Map<String, Integer> statusCounts = new HashMap<>();
            statusCounts.put(EventService.STATUS_APPROVED, 0);
            statusCounts.put(EventService.STATUS_PENDING, 0);
            statusCounts.put(EventService.STATUS_REJECTED, 0);

            for (Event event : events) {
                String status = event.getStatus();
                if (status != null) {
                    // Normaliser le statut pour correspondre aux constantes
                    if (status.equalsIgnoreCase("accepté")) {
                        status = EventService.STATUS_APPROVED;
                    } else if (status.equalsIgnoreCase("en attente")) {
                        status = EventService.STATUS_PENDING;
                    } else if (status.equalsIgnoreCase("rejeté")) {
                        status = EventService.STATUS_REJECTED;
                    }

                    // Incrémenter le compteur pour ce statut
                    statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
                }
            }

            // Créer la série de données pour le graphique
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Nombre d'événements");

            // Ajouter les données à la série
            series.getData().add(new XYChart.Data<>("Accepté", statusCounts.get(EventService.STATUS_APPROVED)));
            series.getData().add(new XYChart.Data<>("En attente", statusCounts.get(EventService.STATUS_PENDING)));
            series.getData().add(new XYChart.Data<>("Rejeté", statusCounts.get(EventService.STATUS_REJECTED)));

            // Effacer les données précédentes et ajouter la nouvelle série
            eventStatusChart.getData().clear();
            eventStatusChart.getData().add(series);

            // Personnaliser l'apparence du graphique
            eventStatusChart.setTitle("Événements par statut");
            eventStatusAxis.setLabel("Statut");
            eventCountAxis.setLabel("Nombre");

            // Appliquer des styles aux barres avec des couleurs plus claires
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getXValue().equals("Accepté")) {
                    data.getNode().setStyle("-fx-bar-fill: #7DCEA0;"); // Vert clair
                } else if (data.getXValue().equals("En attente")) {
                    data.getNode().setStyle("-fx-bar-fill: #85C1E9;"); // Bleu clair
                } else if (data.getXValue().equals("Rejeté")) {
                    data.getNode().setStyle("-fx-bar-fill: #F1948A;"); // Rouge clair
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du graphique des événements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUserListClick(ActionEvent event) {
        navigateToPage("/fxml/admin/UserList.fxml", "Liste des Utilisateurs");
    }

    @FXML
    private void handleEventListClick(ActionEvent event) {
        navigateToPage("/fxml/admin/EventList.fxml", "Liste des Événements");
    }

    @FXML
    private void handleReservationListClick(ActionEvent event) {
        navigateToPage("/fxml/admin/ReservationList.fxml", "Liste des Réservations");
    }

    private void navigateToPage(String fxmlPath, String title) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.err.println("Impossible de trouver le fichier FXML: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) userListButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}