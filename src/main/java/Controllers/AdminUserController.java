package Controllers;

import Models.User;
import Services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.List;

/**
 * Contrôleur pour la gestion des utilisateurs par l'administrateur
 */
public class AdminUserController {

    @FXML
    private ListView<User> userListView;

    @FXML
    private Label messageLabel;

    @FXML
    private Button refreshButton;

    private UserService userService;

    /**
     * Initialise le contrôleur
     */
    @FXML
    public void initialize() {
        userService = new UserService();
        
        // Configurer la cellule personnalisée pour afficher les utilisateurs
        userListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        
                        if (empty || user == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Créer un conteneur pour les informations de l'utilisateur et les boutons
                            HBox container = new HBox(10);
                            
                            // Afficher les informations de l'utilisateur
                            Label infoLabel = new Label(
                                String.format("%s %s (%s) - %s", 
                                    user.getNom(), 
                                    user.getPrenom(), 
                                    user.getEmail(),
                                    user.isBanned() ? "BANNI" : "ACTIF"
                                )
                            );
                            
                            // Bouton pour bannir/débannir l'utilisateur
                            Button banButton = new Button(user.isBanned() ? "Débannir" : "Bannir");
                            banButton.getStyleClass().add("action-button");
                            banButton.setOnAction(event -> {
                                try {
                                    if (user.isBanned()) {
                                        userService.unbanUser(user.getId());
                                        user.setBanned(false);
                                    } else {
                                        userService.banUser(user.getId());
                                        user.setBanned(true);
                                    }
                                    updateItem(user, false); // Mettre à jour l'affichage
                                } catch (Exception e) {
                                    showAlert("Erreur", "Erreur lors du bannissement/débannissement", e.getMessage());
                                }
                            });
                            
                            // Bouton pour supprimer l'utilisateur
                            Button deleteButton = new Button("Supprimer");
                            deleteButton.getStyleClass().add("delete-button");
                            deleteButton.setOnAction(event -> {
                                try {
                                    // Demander confirmation avant de supprimer
                                    boolean confirmed = showConfirmation(
                                        "Confirmation de suppression", 
                                        "Êtes-vous sûr de vouloir supprimer cet utilisateur ?",
                                        "Cette action est irréversible."
                                    );
                                    
                                    if (confirmed) {
                                        userService.deleteById(user.getId());
                                        loadUsers(); // Recharger la liste
                                    }
                                } catch (Exception e) {
                                    showAlert("Erreur", "Erreur lors de la suppression", e.getMessage());
                                }
                            });
                            
                            // Ajouter les éléments au conteneur
                            container.getChildren().addAll(infoLabel, banButton, deleteButton);
                            
                            // Définir le conteneur comme graphique de la cellule
                            setGraphic(container);
                        }
                    }
                };
            }
        });
        
        // Charger les utilisateurs
        loadUsers();
    }

    /**
     * Charge la liste des utilisateurs
     */
    private void loadUsers() {
        try {
            List<User> clients = userService.getClients();
            ObservableList<User> observableClients = FXCollections.observableArrayList(clients);
            userListView.setItems(observableClients);
            
            messageLabel.setText(clients.size() + " utilisateurs trouvés");
        } catch (Exception e) {
            messageLabel.setText("Erreur lors du chargement des utilisateurs: " + e.getMessage());
        }
    }

    /**
     * Gère le clic sur le bouton de rafraîchissement
     * @param event L'événement de clic
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadUsers();
    }

    /**
     * Gère le clic sur le bouton de retour
     * @param event L'événement de clic
     */
    @FXML
    private void handleBack(ActionEvent event) {
        // Naviguer vers le tableau de bord administrateur
        try {
            Utils.NavigationUtil.navigateTo(event, "/fxml/admin/AdminDashboard.fxml", "GoVibe - Tableau de bord administrateur");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur de navigation", e.getMessage());
        }
    }

    /**
     * Affiche une boîte de dialogue d'alerte
     * @param title Le titre de l'alerte
     * @param header L'en-tête de l'alerte
     * @param content Le contenu de l'alerte
     */
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue de confirmation
     * @param title Le titre de la confirmation
     * @param header L'en-tête de la confirmation
     * @param content Le contenu de la confirmation
     * @return true si l'utilisateur a confirmé, false sinon
     */
    private boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        ButtonType buttonTypeOk = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
        
        return alert.showAndWait().orElse(buttonTypeCancel) == buttonTypeOk;
    }
}
