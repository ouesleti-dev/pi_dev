package Controllers.Admin;

import Models.Excursion;
import Models.User;
import Services.AuthService;
import Services.ExcursionService;
import Services.RoleService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ExcursionListController implements Initializable {

    @FXML
    private ListView<Excursion> excursionListView;

    @FXML
    private TextField searchField;

    @FXML
    private Label totalExcursionsText;

    @FXML
    private VBox noExcursionsBox;

    @FXML
    private Button addButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button backButton;

    @FXML
    private Pagination excursionPagination;

    private ExcursionService excursionService;
    private AuthService authService;
    private RoleService roleService;
    private ObservableList<Excursion> allExcursions;
    private FilteredList<Excursion> filteredExcursions;

    // Nombre d'excursions par page
    private static final int ITEMS_PER_PAGE = 3;

    public ExcursionListController() {
        excursionService = ExcursionService.getInstance();
        authService = AuthService.getInstance();
        roleService = RoleService.getInstance();
        allExcursions = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurer la ListView
        excursionListView.setCellFactory(param -> new ExcursionListCell());

        // Configurer la pagination
        excursionPagination.setPageFactory(this::createPage);

        // Configurer la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterExcursions(newValue);
        });

        // Charger les excursions
        loadExcursions();
    }

    private void loadExcursions() {
        try {
            List<Excursion> excursions = excursionService.getAllExcursions();
            allExcursions.clear();
            allExcursions.addAll(excursions);

            // Initialiser la liste filtrée
            filteredExcursions = new FilteredList<>(allExcursions, p -> true);

            // Configurer la pagination
            int pageCount = (filteredExcursions.size() / ITEMS_PER_PAGE) + (filteredExcursions.size() % ITEMS_PER_PAGE > 0 ? 1 : 0);
            excursionPagination.setPageCount(Math.max(1, pageCount));
            excursionPagination.setCurrentPageIndex(0);

            // Mettre à jour la ListView avec la première page
            updateListView(0);

            // Mettre à jour le compteur
            totalExcursionsText.setText("Total: " + excursions.size() + " excursion(s)");

            // Afficher ou masquer le message "Aucune excursion"
            noExcursionsBox.setVisible(excursions.isEmpty());
            excursionListView.setVisible(!excursions.isEmpty());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des excursions", e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterExcursions(String searchText) {
        if (filteredExcursions == null) return;

        filteredExcursions.setPredicate(excursion -> {
            // Si le texte de recherche est vide, afficher toutes les excursions
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Convertir le texte de recherche en minuscules pour une recherche insensible à la casse
            String lowerCaseSearch = searchText.toLowerCase();

            // Vérifier si l'excursion correspond aux critères de recherche
            return excursion.getTitre().toLowerCase().contains(lowerCaseSearch) ||
                   excursion.getDestination().toLowerCase().contains(lowerCaseSearch) ||
                   excursion.getTransport().toLowerCase().contains(lowerCaseSearch) ||
                   String.valueOf(excursion.getPrix()).contains(lowerCaseSearch) ||
                   String.valueOf(excursion.getDuree()).contains(lowerCaseSearch);
        });

        // Mettre à jour la pagination
        int pageCount = (filteredExcursions.size() / ITEMS_PER_PAGE) + (filteredExcursions.size() % ITEMS_PER_PAGE > 0 ? 1 : 0);
        excursionPagination.setPageCount(Math.max(1, pageCount));
        excursionPagination.setCurrentPageIndex(0);

        // Mettre à jour la ListView
        updateListView(0);

        // Mettre à jour le compteur
        totalExcursionsText.setText("Total: " + filteredExcursions.size() + " excursion(s)");

        // Afficher ou masquer le message "Aucune excursion"
        noExcursionsBox.setVisible(filteredExcursions.isEmpty());
        excursionListView.setVisible(!filteredExcursions.isEmpty());
    }

    @FXML
    private void handleAddExcursion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/ExcursionAdd.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter une excursion");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recharger les excursions après l'ajout et mettre à jour la pagination
            loadExcursions();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire d'ajout", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadExcursions();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/AdminDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("GoVibe - Admin Dashboard");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour au dashboard", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Crée une page pour la pagination
     * @param pageIndex L'index de la page à créer
     * @return Le nœud à afficher pour cette page
     */
    private Node createPage(int pageIndex) {
        // Cette méthode est appelée par la pagination pour créer chaque page
        updateListView(pageIndex);
        return excursionListView;
    }

    /**
     * Met à jour la ListView avec les éléments de la page spécifiée
     * @param pageIndex L'index de la page à afficher
     */
    private void updateListView(int pageIndex) {
        if (filteredExcursions == null) return;

        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredExcursions.size());

        if (fromIndex > toIndex) {
            excursionListView.setItems(FXCollections.observableArrayList());
            return;
        }

        // Créer une sous-liste pour la page actuelle
        ObservableList<Excursion> pageItems = FXCollections.observableArrayList(
                filteredExcursions.subList(fromIndex, toIndex)
        );

        excursionListView.setItems(pageItems);
    }

    // Classe interne pour afficher les excursions dans la ListView
    private class ExcursionListCell extends ListCell<Excursion> {
        @Override
        protected void updateItem(Excursion excursion, boolean empty) {
            super.updateItem(excursion, empty);

            if (empty || excursion == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            try {
                // Créer un conteneur principal pour l'élément
                HBox mainContainer = new HBox(15);
                mainContainer.getStyleClass().add("excursion-item");
                mainContainer.setPadding(new Insets(10));

                // Conteneur pour les informations
                VBox infoContainer = new VBox(8);
                infoContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                infoContainer.setPrefWidth(500);
                infoContainer.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(infoContainer, javafx.scene.layout.Priority.ALWAYS);

                // Titre de l'excursion avec style amélioré
                Label titleLabel = new Label(excursion.getTitre());
                titleLabel.getStyleClass().addAll("excursion-title", "bold-text");
                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                // Conteneur pour les détails en 2 colonnes
                GridPane detailsGrid = new GridPane();
                detailsGrid.setHgap(15);
                detailsGrid.setVgap(5);

                // Destination
                Label destinationLabel = new Label(excursion.getDestination());
                Label destinationTitle = new Label("Destination:");
                destinationTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
                detailsGrid.add(destinationTitle, 0, 0);
                detailsGrid.add(destinationLabel, 1, 0);



                // Date
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Label dateLabel = new Label(dateFormat.format(excursion.getDate()));
                Label dateTitle = new Label("Date:");
                dateTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
                detailsGrid.add(dateTitle, 0, 2);
                detailsGrid.add(dateLabel, 1, 2);

                // Durée
                Label durationLabel = new Label(excursion.getDuree() + " jour(s)");
                Label durationTitle = new Label("Durée:");
                durationTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
                detailsGrid.add(durationTitle, 2, 0);
                detailsGrid.add(durationLabel, 3, 0);

                // Transport
                Label transportLabel = new Label(excursion.getTransport());
                Label transportTitle = new Label("Transport:");
                transportTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
                detailsGrid.add(transportTitle, 2, 1);
                detailsGrid.add(transportLabel, 3, 1);

                // Événement associé
                Label eventLabel = new Label(excursion.getEvent() != null ? excursion.getEvent().getTitle() : "Non spécifié");
                Label eventTitle = new Label("Événement:");
                eventTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
                detailsGrid.add(eventTitle, 2, 2);
                detailsGrid.add(eventLabel, 3, 2);

                // Ajouter tous les éléments au conteneur d'informations
                infoContainer.getChildren().addAll(titleLabel, detailsGrid);

                // Conteneur pour les boutons d'action
                VBox actionContainer = new VBox(10);
                actionContainer.setAlignment(javafx.geometry.Pos.CENTER);
                actionContainer.setPrefWidth(120);

                // Boutons avec icônes
                Button editButton = new Button("Modifier");
                editButton.getStyleClass().add("edit-button");
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 100px;");
                editButton.setOnAction(e -> editExcursion(excursion));

                Button deleteButton = new Button("Supprimer");
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 100px;");
                deleteButton.setOnAction(e -> deleteExcursion(excursion));

                actionContainer.getChildren().addAll(editButton, deleteButton);

                // Assembler le conteneur principal
                mainContainer.getChildren().addAll(infoContainer, actionContainer);

                // Ajouter un effet de survol
                mainContainer.setOnMouseEntered(e -> {
                    mainContainer.setStyle("-fx-background-color: #f5f5f5;");
                });
                mainContainer.setOnMouseExited(e -> {
                    mainContainer.setStyle("-fx-background-color: white;");
                });

                setGraphic(mainContainer);
            } catch (Exception e) {
                setText("Erreur d'affichage: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // La méthode viewExcursion a été supprimée car le bouton "Voir" a été retiré de l'interface

    private void editExcursion(Excursion excursion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/ExcursionEdit.fxml"));
            Parent root = loader.load();

            ExcursionEditController controller = loader.getController();
            controller.setExcursion(excursion);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier l'excursion");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recharger les excursions après la modification et mettre à jour la pagination
            loadExcursions();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de modification", e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteExcursion(Excursion excursion) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer l'excursion");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer l'excursion \"" + excursion.getTitre() + "\" ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = excursionService.deleteExcursion(excursion.getId_excursion());
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", null, "L'excursion a été supprimée avec succès.");
                    // Recharger les excursions et mettre à jour la pagination
                    loadExcursions();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", null, "Impossible de supprimer l'excursion.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}