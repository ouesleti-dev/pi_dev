package Controllers.Admin;

import Models.User;
import Services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserListController implements Initializable {

    @FXML
    private ListView<User> userListView;

    @FXML
    private Button backButton;

    @FXML
    private TextField searchField;

    @FXML
    private Label messageLabel;

    private UserService userService;
    private ObservableList<User> clientList;
    private FilteredList<User> filteredClientList;

    public UserListController() {
        userService = new UserService();
        clientList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation de la liste des utilisateurs
        System.out.println("Liste des utilisateurs initialisée");

        // Configurer la ListView avec une cellule personnalisée
        userListView.setCellFactory(param -> new ClientListCell());

        // Charger les clients
        loadClients();

        // Configurer la recherche en temps réel
        setupSearchField();

        // Mettre à jour le message
        updateMessageLabel();
    }

    /**
     * Charge la liste des clients depuis la base de données
     */
    private void loadClients() {
        try {
            // Récupérer les clients (utilisateurs avec rôle ROLE_CLIENT)
            List<User> clients = userService.getClients();

            // Mettre à jour la liste observable
            clientList.clear();
            clientList.addAll(clients);

            // Initialiser la liste filtrée si elle n'existe pas encore
            if (filteredClientList == null) {
                filteredClientList = new FilteredList<>(clientList, p -> true);
                userListView.setItems(filteredClientList);
            } else {
                // Réinitialiser le prédicat pour afficher tous les clients
                filteredClientList.setPredicate(p -> true);
            }

            // Mettre à jour le message
            updateMessageLabel();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des clients", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Met à jour le message affiché en bas de la liste
     */
    private void updateMessageLabel() {
        int count = filteredClientList != null ? (int) filteredClientList.stream().count() : clientList.size();
        messageLabel.setText(count + " client(s) trouvé(s)");
    }

    /**
     * Configure la recherche en temps réel
     */
    private void setupSearchField() {
        if (searchField == null) {
            System.err.println("Champ de recherche non disponible");
            return;
        }

        // Initialiser la liste filtrée si ce n'est pas déjà fait
        if (filteredClientList == null) {
            filteredClientList = new FilteredList<>(clientList, p -> true);
            userListView.setItems(filteredClientList);
        }

        // Ajouter un listener au champ de recherche pour mettre à jour le filtre en temps réel
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredClientList.setPredicate(client -> {
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

            // Mettre à jour le message avec le nombre de résultats filtrés
            updateMessageLabel();
        });
    }

    /**
     * Supprime un client de la base de données
     * @param client Le client à supprimer
     */
    private void deleteClient(User client) {
        try {
            // Demander confirmation avant de supprimer
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation de suppression");
            confirmAlert.setHeaderText("Supprimer le client");
            confirmAlert.setContentText("Voulez-vous vraiment supprimer le client " +
                    client.getPrenom() + " " + client.getNom() + " ?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Supprimer le client de la base de données
                userService.deleteById(client.getId());

                // Supprimer le client de la liste
                clientList.remove(client);

                // Mettre à jour le message
                updateMessageLabel();

                // Afficher un message de succès
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Client supprimé",
                        "Le client a été supprimé avec succès.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du client", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Bascule le statut de bannissement d'un client
     * @param client Le client à bannir ou débannir
     */
    private void toggleBanStatus(User client) {
        try {
            String action = client.isBanned() ? "débanner" : "banner";
            String title = client.isBanned() ? "Confirmation de débannissement" : "Confirmation de bannissement";
            String header = client.isBanned() ? "Débanner le client" : "Banner le client";

            // Demander confirmation avant de bannir/débannir
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle(title);
            confirmAlert.setHeaderText(header);
            confirmAlert.setContentText("Voulez-vous vraiment " + action + " le client " +
                    client.getPrenom() + " " + client.getNom() + " ?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (client.isBanned()) {
                    // Débanner le client
                    userService.unbanUser(client.getId());
                    client.setBanned(false);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Client débanné",
                            "Le client a été débanné avec succès.");
                } else {
                    // Banner le client
                    userService.banUser(client.getId());
                    client.setBanned(true);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Client banné",
                            "Le client a été banné avec succès.");
                }

                // Rafraîchir la liste pour mettre à jour l'affichage
                userListView.refresh();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du changement de statut", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Affiche une alerte
     */
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        // Rafraîchir la liste des utilisateurs
        System.out.println("Rafraîchissement de la liste des utilisateurs");
        loadClients();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        navigateToDashboard();
    }

    private void navigateToDashboard() {
        try {
            URL url = getClass().getResource("/fxml/admin/AdminDashboard.fxml");
            if (url == null) {
                System.err.println("Impossible de trouver le fichier FXML: /fxml/admin/AdminDashboard.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) userListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Panneau d'administration");
        } catch (IOException e) {
            System.err.println("Erreur lors de la navigation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Classe interne pour personnaliser l'affichage des cellules de la ListView
     */
    private class ClientListCell extends ListCell<User> {
        private HBox content;
        private VBox infoContainer;
        private HBox userInfoHeader;
        private Label nameLabel;
        private Label emailLabel;
        private Label phoneLabel;
        private Label statusLabel;
        private Button deleteButton;
        private Button banButton;
        private HBox buttonsBox;

        public ClientListCell() {
            super();

            // Créer les labels pour les informations utilisateur
            nameLabel = new Label();
            nameLabel.getStyleClass().add("event-title");
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            statusLabel = new Label();
            statusLabel.getStyleClass().add("item-status");
            statusLabel.setStyle("-fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-font-size: 12px;");

            // Créer l'en-tête avec le nom et le statut
            userInfoHeader = new HBox(10, nameLabel, statusLabel);
            userInfoHeader.setAlignment(Pos.CENTER_LEFT);

            // Créer les labels pour les détails
            emailLabel = new Label();
            emailLabel.getStyleClass().add("event-info");
            emailLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 13px;");

            phoneLabel = new Label();
            phoneLabel.getStyleClass().add("event-info");
            phoneLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 13px;");

            // Créer un bouton avec un symbole de corbeille en gris
            deleteButton = new Button("🗑"); // Symbole Unicode de corbeille
            deleteButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 40px; -fx-min-height: 40px; -fx-background-radius: 20px;");
            // Ajouter un effet de survol avec des nuances de gris
            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 40px; -fx-min-height: 40px; -fx-background-radius: 20px;"));
            deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 40px; -fx-min-height: 40px; -fx-background-radius: 20px;"));
            // Ajouter un tooltip pour indiquer la fonction du bouton
            Tooltip tooltip = new Tooltip("Supprimer");
            tooltip.setStyle("-fx-font-size: 12px;");
            deleteButton.setTooltip(tooltip);
            deleteButton.setOnAction(event -> {
                User client = getItem();
                if (client != null) {
                    deleteClient(client);
                }
            });

            // Créer un bouton avec un symbole de bannissement (sera mis à jour dans updateItem)
            banButton = new Button("⛔"); // Symbole Unicode d'interdiction par défaut
            banButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 40px; -fx-min-height: 40px; -fx-background-radius: 20px;");
            // Ajouter un effet de survol
            banButton.setOnMouseEntered(e -> {
                if (getItem() != null && getItem().isBanned()) {
                    banButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 40px; -fx-min-height: 40px; -fx-background-radius: 20px;");
                } else {
                    banButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 40px; -fx-min-height: 40px; -fx-background-radius: 20px;");
                }
            });
            banButton.setOnMouseExited(e -> {
                if (getItem() != null && getItem().isBanned()) {
                    banButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 40px; -fx-min-height: 40px; -fx-background-radius: 20px;");
                } else {
                    banButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 40px; -fx-min-height: 40px; -fx-background-radius: 20px;");
                }
            });
            // Le tooltip sera mis à jour dans updateItem en fonction de l'état de bannissement
            banButton.setOnAction(event -> {
                User client = getItem();
                if (client != null) {
                    toggleBanStatus(client);
                }
            });

            // Créer une boîte pour les boutons avec un espacement approprié
            buttonsBox = new HBox(10, banButton, deleteButton);
            buttonsBox.setAlignment(Pos.CENTER_RIGHT);
            buttonsBox.setPadding(new Insets(0, 0, 0, 10)); // Ajouter un peu d'espace à gauche

            // Créer le conteneur d'informations
            infoContainer = new VBox(5);
            infoContainer.getChildren().addAll(userInfoHeader, emailLabel, phoneLabel);
            HBox.setHgrow(infoContainer, Priority.ALWAYS);

            // Créer le conteneur principal
            content = new HBox(15);
            content.getChildren().addAll(infoContainer, buttonsBox);
            content.setAlignment(Pos.CENTER_LEFT);
            content.setPadding(new Insets(10, 15, 10, 15));
            content.getStyleClass().add("user-card");
            content.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        }

        @Override
        protected void updateItem(User client, boolean empty) {
            super.updateItem(client, empty);

            if (empty || client == null) {
                setText(null);
                setGraphic(null);
            } else {
                // Mettre à jour le nom complet
                nameLabel.setText(client.getPrenom() + " " + client.getNom());

                // Mettre à jour l'email avec une icône
                emailLabel.setText("✉️ " + client.getEmail());

                // Mettre à jour le téléphone avec une icône
                phoneLabel.setText("📞 " + (client.getTelephone() != null ? client.getTelephone() : "Non renseigné"));

                // Afficher le statut de bannissement
                if (client.isBanned()) {
                    statusLabel.setText("BANNI");
                    statusLabel.getStyleClass().removeAll("status-valide", "status-abondonne");
                    statusLabel.getStyleClass().add("status-abondonne");
                    statusLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3px 8px; -fx-background-radius: 4px;");

                    banButton.setText("🔓"); // Symbole de cadenas ouvert pour débannir
                    banButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 40px; -fx-min-height: 40px; -fx-background-radius: 20px;");
                    banButton.setTooltip(new Tooltip("Débanner"));

                    // Ajouter une bordure rouge pour indiquer visuellement que l'utilisateur est banni
                    content.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #e74c3c; -fx-border-width: 1px; -fx-border-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
                } else {
                    statusLabel.setText("ACTIF");
                    statusLabel.getStyleClass().removeAll("status-valide", "status-abondonne");
                    statusLabel.getStyleClass().add("status-valide");
                    statusLabel.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3px 8px; -fx-background-radius: 4px;");

                    banButton.setText("⛔"); // Symbole d'interdiction pour bannir
                    banButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-min-width: 40px; -fx-min-height: 40px; -fx-background-radius: 20px;");
                    banButton.setTooltip(new Tooltip("Banner"));

                    // Style normal pour les utilisateurs actifs
                    content.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
                }

                setGraphic(content);
            }
        }
    }
}