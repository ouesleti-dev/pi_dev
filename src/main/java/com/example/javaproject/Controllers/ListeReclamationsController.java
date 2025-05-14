package com.example.javaproject.Controllers;

import com.example.javaproject.Entities.Reclamation;
import com.example.javaproject.Entities.Reponse;
import com.example.javaproject.Services.ReclamationService;
import com.example.javaproject.Services.ReponseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ListeReclamationsController implements Initializable {

    @FXML private FlowPane cardsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> priorityFilter;
    @FXML private ComboBox<String> sortComboBox;

    private List<Reclamation> originalReclamations;
    private String currentSortField = "date";
    private boolean ascendingOrder = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeFilters();
        initializeSortOptions();
        loadReclamations();

        // Setup listeners
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterAndSortReclamations());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterAndSortReclamations());
        priorityFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterAndSortReclamations());
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) handleSortSelection(newVal);
        });
    }

    private void initializeFilters() {
        // Mise à jour avec les nouveaux statuts et priorités
        statusFilter.getItems().addAll("Tous", "En attente", "Approuvée");
        priorityFilter.getItems().addAll("Tous", "Urgent", "Important", "Normal", "Faible");

        statusFilter.setValue("Tous");
        priorityFilter.setValue("Tous");
    }

    private void initializeSortOptions() {
        sortComboBox.getItems().addAll(
                "Date (plus récent)",
                "Date (plus ancien)",
                "Titre (A-Z)",
                "Titre (Z-A)",
                "Priorité (Urgent à Faible)",
                "Priorité (Faible à Urgent)"
        );
    }

    private void loadReclamations() {
        ReclamationService service = new ReclamationService();
        originalReclamations = service.getAllData();
        filterAndSortReclamations();
    }

    private void filterAndSortReclamations() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue();
        String selectedPriority = priorityFilter.getValue();

        List<Reclamation> filtered = originalReclamations.stream()
                .filter(rec -> searchText.isEmpty() ||
                        rec.getTitre().toLowerCase().contains(searchText) ||
                        rec.getMessage().toLowerCase().contains(searchText))
                .filter(rec -> selectedStatus.equals("Tous") || rec.getStatut().equals(selectedStatus))
                .filter(rec -> selectedPriority.equals("Tous") || rec.getPriorite().equals(selectedPriority))
                .sorted(this::compareReclamations)
                .collect(Collectors.toList());

        displayReclamations(filtered);
    }

    private int compareReclamations(Reclamation r1, Reclamation r2) {
        switch (currentSortField) {
            case "date":
                return ascendingOrder ?
                        r1.getDateReclamation().compareTo(r2.getDateReclamation()) :
                        r2.getDateReclamation().compareTo(r1.getDateReclamation());
            case "titre":
                return ascendingOrder ?
                        r1.getTitre().compareToIgnoreCase(r2.getTitre()) :
                        r2.getTitre().compareToIgnoreCase(r1.getTitre());
            case "priorite":
                return comparePriorities(r1.getPriorite(), r2.getPriorite());
            default:
                return 0;
        }
    }

    private int comparePriorities(String p1, String p2) {
        // Nouvel ordre de priorité
        Map<String, Integer> priorityOrder = Map.of(
                "Urgent", 1,
                "Important", 2,
                "Normal", 3,
                "Faible", 4
        );

        int order1 = priorityOrder.getOrDefault(p1, 0);
        int order2 = priorityOrder.getOrDefault(p2, 0);

        return ascendingOrder ? Integer.compare(order1, order2) : Integer.compare(order2, order1);
    }

    private void handleSortSelection(String sortOption) {
        switch (sortOption) {
            case "Date (plus récent)":
                currentSortField = "date";
                ascendingOrder = false;
                break;
            case "Date (plus ancien)":
                currentSortField = "date";
                ascendingOrder = true;
                break;
            case "Titre (A-Z)":
                currentSortField = "titre";
                ascendingOrder = true;
                break;
            case "Titre (Z-A)":
                currentSortField = "titre";
                ascendingOrder = false;
                break;
            case "Priorité (Urgent à Faible)":
                currentSortField = "priorite";
                ascendingOrder = true;
                break;
            case "Priorité (Faible à Urgent)":
                currentSortField = "priorite";
                ascendingOrder = false;
                break;
            case "Statut (A-Z)":
                currentSortField = "statut";
                ascendingOrder = true;
                break;
            case "Statut (Z-A)":
                currentSortField = "statut";
                ascendingOrder = false;
                break;
        }
        filterAndSortReclamations();
    }

    private void displayReclamations(List<Reclamation> reclamations) {
        cardsContainer.getChildren().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        reclamations.forEach(rec -> {
            VBox card = createReclamationCard(rec, formatter);
            cardsContainer.getChildren().add(card);
        });
    }

    private VBox createReclamationCard(Reclamation rec, DateTimeFormatter formatter) {
        VBox card = new VBox();
        card.getStyleClass().add("reclamation-card");
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setMaxWidth(400);

        // Titre avec priorité (couleur différente selon la priorité)
        Label title = new Label(rec.getTitre());
        title.getStyleClass().add("card-header");

        // Appliquer une couleur en fonction de la priorité
        switch(rec.getPriorite()) {
            case "Urgent":
                title.setStyle("-fx-text-fill: #dc3545;"); // Rouge
                break;
            case "Important":
                title.setStyle("-fx-text-fill: #fd7e14;"); // Orange
                break;
            case "Normal":
                title.setStyle("-fx-text-fill: #28a745;"); // Vert
                break;
            case "Faible":
                title.setStyle("-fx-text-fill: #6c757d;"); // Gris
                break;
        }

        // Contenu
        Label message = createCardField("💬 " + rec.getMessage());
        Label date = createCardField("📅 " + rec.getDateReclamation().format(formatter));
        Label priority = createCardField("❗ Priorité : " + rec.getPriorite());
        Label status = createCardField("✅ Statut : " + rec.getStatut());

        // Boutons
        HBox buttonsContainer = new HBox(10);

        // Bouton Supprimer
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> handleDeleteReclamation(rec, card));

        // Bouton Voir Réponse (seulement si statut est "Approuvée")
        Button viewResponseButton = new Button("Voir réponse");
        viewResponseButton.getStyleClass().add("view-button");
        viewResponseButton.setVisible("Approuvée".equals(rec.getStatut()));
        viewResponseButton.setOnAction(e -> handleViewResponse(rec));

        buttonsContainer.getChildren().addAll(deleteButton, viewResponseButton);
        card.getChildren().addAll(title, message, date, priority, status, buttonsContainer);

        return card;
    }

    private Label createCardField(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("card-field");
        label.setWrapText(true);
        label.setMaxWidth(380);
        return label;
    }

    private void handleDeleteReclamation(Reclamation reclamation, VBox card) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la réclamation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ReclamationService service = new ReclamationService();
                service.delete(reclamation);
                cardsContainer.getChildren().remove(card);
                showAlert("Succès", "Réclamation supprimée avec succès", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void handleViewResponse(Reclamation reclamation) {
        try {
            ReponseService reponseService = new ReponseService();
            Reponse reponse = reponseService.findByReclamationId(reclamation.getId());

            if (reponse != null) {
                // Création de la fenêtre popup
                Stage popup = new Stage();
                popup.initModality(Modality.APPLICATION_MODAL);
                popup.setTitle("Réponse à votre réclamation");
                popup.setMinWidth(500);
                popup.setMinHeight(400);

                // Conteneur principal avec style
                VBox root = new VBox(20);
                root.setPadding(new Insets(25));
                root.setStyle("-fx-background-color: #f8f9fa;");
                root.setAlignment(Pos.TOP_CENTER);

                // Titre stylisé et centré
                Label titleLabel = new Label("RÉPONSE À VOTRE RÉCLAMATION");
                titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
                titleLabel.setAlignment(Pos.CENTER);
                titleLabel.setMaxWidth(Double.MAX_VALUE);
                titleLabel.setPadding(new Insets(0, 0, 15, 0));

                // Date de réponse avec icône
                HBox dateBox = new HBox(10);
                dateBox.setAlignment(Pos.CENTER);
                Label dateIcon = new Label("📅");
                dateIcon.setStyle("-fx-font-size: 16px;");
                Label dateLabel = new Label("Réponse du " +
                        reponse.getDateReponse().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
                dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                dateBox.getChildren().addAll(dateIcon, dateLabel);

                // Contenu de la réponse avec style amélioré
                TextArea contentArea = new TextArea(reponse.getContenu());
                contentArea.setEditable(false);
                contentArea.setWrapText(true);
                contentArea.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-background-color: white; -fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 8px; -fx-background-radius: 8px; " +
                        "-fx-padding: 15px;");
                contentArea.setPrefHeight(200);

                // Bouton Fermer stylisé en rouge et centré
                Button closeButton = new Button("FERMER");
                closeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-font-size: 14px; " +
                        "-fx-padding: 8px 25px; -fx-background-radius: 8px; " +
                        "-fx-border-radius: 8px; -fx-cursor: hand;");
                closeButton.setOnAction(e -> popup.close());

                // Effet hover pour le bouton
                closeButton.setOnMouseEntered(e ->
                        closeButton.setStyle("-fx-background-color: #c82333; -fx-text-fill: white; " +
                                "-fx-font-weight: bold; -fx-font-size: 14px; " +
                                "-fx-padding: 8px 25px; -fx-background-radius: 8px; " +
                                "-fx-border-radius: 8px; -fx-cursor: hand;"));
                closeButton.setOnMouseExited(e ->
                        closeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; " +
                                "-fx-font-weight: bold; -fx-font-size: 14px; " +
                                "-fx-padding: 8px 25px; -fx-background-radius: 8px; " +
                                "-fx-border-radius: 8px; -fx-cursor: hand;"));

                // Assemblage des composants
                root.getChildren().addAll(titleLabel, dateBox, contentArea, closeButton);

                // Configuration de la scène
                Scene scene = new Scene(root);
                popup.setScene(scene);
                popup.showAndWait();
            } else {
                showAlert(Alert.AlertType.WARNING, "Aucune réponse",
                        "Aucune réponse trouvée pour cette réclamation");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la réponse : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javaproject/AjouterReclamation.fxml"));
            Parent root = loader.load();
            Scene currentScene = ((Node)event.getSource()).getScene();
            Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(newScene);
            stage.setTitle("Ajouter une Réclamation");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}