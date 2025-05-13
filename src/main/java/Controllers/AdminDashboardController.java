package Controllers;

import Models.User;
import Services.UserService;
import Utils.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private ListView<User> clientListView;

    @FXML
    private TextField searchField;

    private UserService userService;
    private ObservableList<User> clientList;

    public AdminDashboardController() {
        userService = new UserService();
        clientList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurer la ListView avec une cellule personnalisée
        clientListView.setCellFactory(param -> new ClientListCell());

        // Charger les clients
        loadClients();

        // Configurer la recherche en temps réel
        setupSearchField();
    }

    private void loadClients() {
        try {
            List<User> clients = userService.getClients();
            clientList.clear();
            clientList.addAll(clients);
            clientListView.setItems(clientList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des clients", e.getMessage());
        }
    }

    private void setupSearchField() {
        // Créer une FilteredList wrapée autour de l'ObservableList
        FilteredList<User> filteredData = new FilteredList<>(clientList, p -> true);

        // Ajouter un listener au champ de recherche pour mettre à jour le filtre en temps réel
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(client -> {
                // Si le champ de recherche est vide, afficher tous les clients
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Convertir le texte de recherche en minuscules pour une recherche insensible à la casse
                String lowerCaseFilter = newValue.toLowerCase();

                // Comparer les champs du client avec le texte de recherche
                if (client.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Correspondance sur le nom
                } else if (client.getPrenom().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Correspondance sur le prénom
                } else if (client.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Correspondance sur l'email
                } else if (client.getTelephone() != null && client.getTelephone().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Correspondance sur le téléphone
                }
                return false; // Pas de correspondance
            });
        });

        // Ajouter les données filtrées à la ListView
        clientListView.setItems(filteredData);
    }

    @FXML
    void refreshClients(ActionEvent event) {
        loadClients();
    }

    @FXML
    void viewClientDetails(ActionEvent event) {
        User selectedClient = clientListView.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Aucun client sélectionné",
                    "Veuillez sélectionner un client pour voir ses détails.");
            return;
        }

        // Afficher les détails du client dans une alerte pour l'instant
        // Plus tard, vous pourriez créer une interface dédiée pour les détails
        showAlert(Alert.AlertType.INFORMATION, "Détails du client",
                selectedClient.getPrenom() + " " + selectedClient.getNom(),
                "ID: " + selectedClient.getId() + "\n" +
                "Email: " + selectedClient.getEmail() + "\n" +
                "Téléphone: " + (selectedClient.getTelephone() != null ? selectedClient.getTelephone() : "Non renseigné") + "\n" +
                "Vérifié: " + (selectedClient.isVerified() ? "Oui" : "Non") + "\n" +
                "Date d'inscription: " + (selectedClient.getCreatedAt() != null ? selectedClient.getCreatedAt().toString() : "Non disponible"));
    }

    @FXML
    void deleteClient(ActionEvent event) {
        User selectedClient = clientListView.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Aucun client sélectionné",
                    "Veuillez sélectionner un client à supprimer.");
            return;
        }

        // Demander confirmation avant de supprimer
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Supprimer le client");
        confirmAlert.setContentText("Voulez-vous vraiment supprimer le client " +
                selectedClient.getPrenom() + " " + selectedClient.getNom() + " ?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Supprimer le client de la base de données
                    userService.deleteById(selectedClient.getId());

                    // Supprimer le client de la liste observable
                    clientList.remove(selectedClient);

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Client supprimé",
                            "Le client a été supprimé avec succès.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression",
                            "Une erreur est survenue lors de la suppression du client: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    void backToLogin(ActionEvent event) {
        try {
            NavigationUtil.navigateTo(event, "/Authentification/login.fxml", "GoVibe - Connexion");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la redirection", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
