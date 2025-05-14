package com.example.javaproject.Controllers;

import com.example.javaproject.Entities.Reclamation;
import com.example.javaproject.Entities.Reponse;
import com.example.javaproject.Entities.User;
import com.example.javaproject.Services.ReclamationService;
import com.example.javaproject.Services.ReponseService;
import com.example.javaproject.Services.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class MesReponsesController {

    @FXML
    private ListView<Reponse> reponsesList;
    private ListeReclamationsBackofficeController mainController;
    private final ReponseService reponseService = new ReponseService();
    private final UserService utilisateurService = new UserService();

    @FXML
    public void initialize() {
        configureListCells();
        reponsesList.setPrefHeight(Region.USE_COMPUTED_SIZE);
        reponsesList.setPrefWidth(Region.USE_COMPUTED_SIZE);
    }

    public void setMainController(ListeReclamationsBackofficeController controller) {
        this.mainController = controller;
    }

    private void configureListCells() {
        reponsesList.setCellFactory(param -> new ListCell<Reponse>() {
            private final GridPane rowContainer = new GridPane();
            private final Label dateLabel = new Label();
            private final Label reclamationLabel = new Label();
            private final Label reponseLabel = new Label();
            private final Label utilisateurLabel = new Label();
            private final Button updateBtn = new Button("🔄");
            private final Button deleteBtn = new Button("❌");
            private final HBox actionBox = new HBox(5, updateBtn, deleteBtn);

            {
                // Configuration des contraintes de colonnes (identique au FXML)
                ColumnConstraints colDate = new ColumnConstraints();
                colDate.setPercentWidth(15);
                colDate.setHalignment(HPos.CENTER);

                ColumnConstraints colReclamation = new ColumnConstraints();
                colReclamation.setPercentWidth(25);
                colReclamation.setHalignment(HPos.LEFT);

                ColumnConstraints colReponse = new ColumnConstraints();
                colReponse.setPercentWidth(30);
                colReponse.setHalignment(HPos.LEFT);

                ColumnConstraints colUser = new ColumnConstraints();
                colUser.setPercentWidth(15);
                colUser.setHalignment(HPos.CENTER);

                ColumnConstraints colAction = new ColumnConstraints();
                colAction.setPercentWidth(15);
                colAction.setHalignment(HPos.CENTER);

                rowContainer.getColumnConstraints().addAll(colDate, colReclamation, colReponse, colUser, colAction);
                rowContainer.setHgap(5);
                rowContainer.setPadding(new Insets(5));
                rowContainer.setPrefWidth(800); // Ajustez selon votre besoin

                // Style commun avec alignement explicite
                String commonStyle = "-fx-font-size: 16px; -fx-text-fill: #2d3748; -fx-padding: 0 5px; -fx-font-weight: bold;";

                dateLabel.setStyle(commonStyle + "-fx-alignment: center;");
                reclamationLabel.setStyle(commonStyle + "-fx-alignment: center-left;");
                reponseLabel.setStyle(commonStyle + "-fx-alignment: center-left; -fx-wrap-text: true;");
                utilisateurLabel.setStyle(commonStyle + "-fx-alignment: center; -fx-text-fill: #4c51bf;");

                // Configuration des boutons
                updateBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-min-width: 10;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-min-width: 10;");
                actionBox.setAlignment(Pos.CENTER);

                // Ajout des composants
                rowContainer.add(dateLabel, 0, 0);
                rowContainer.add(reclamationLabel, 1, 0);
                rowContainer.add(reponseLabel, 2, 0);
                rowContainer.add(utilisateurLabel, 3, 0);
                rowContainer.add(actionBox, 4, 0);
            }

            @Override
            protected void updateItem(Reponse reponse, boolean empty) {
                super.updateItem(reponse, empty);

                if (empty || reponse == null) {
                    setGraphic(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    dateLabel.setText(reponse.getDateReponse().format(formatter));

                    reclamationLabel.setText(reponse.getReclamationTitre());
                    reponseLabel.setText(reponse.getContenu());

                    User utilisateur = utilisateurService.getById(reponse.getIdUtilisateur());
                    utilisateurLabel.setText(utilisateur != null ?
                            utilisateur.getNom() + " " + utilisateur.getPrenom() : "Inconnu");

                    updateBtn.setOnAction(e -> handleUpdateReponse(reponse));
                    deleteBtn.setOnAction(e -> handleDeleteReponse(reponse));

                    setGraphic(rowContainer);
                }
            }
        });
    }

    private void handleUpdateReponse(Reponse reponse) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javaproject/UpdateReponseDialog.fxml"));
            Parent root = loader.load();

            UpdateReponseDialogController controller = loader.getController();
            controller.setReponse(reponse);

            Stage dialog = new Stage();
            dialog.setScene(new Scene(root));
            dialog.setTitle("Modifier la réponse");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();

            // Rafraîchir après modification
            loadReponses();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'interface de modification", Alert.AlertType.ERROR);
        }
    }

    private void handleDeleteReponse(Reponse reponse) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer cette réponse ?");
        confirmation.setContentText("Cette action est irréversible.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // 1. Récupérer l'ID de la réclamation associée
                    int reclamationId = reponse.getIdReclamation(); // Adaptez selon votre structure

                    // 2. Supprimer la réponse
                    ReponseService reponseService = new ReponseService();
                    boolean deleteSuccess = reponseService.deleteEntity(reponse);

                    if (deleteSuccess) {
                        // 3. Mettre à jour le statut de la réclamation
                        ReclamationService reclamationService = new ReclamationService();
                        Reclamation reclamation = reclamationService.findById(reclamationId);

                        if (reclamation != null) {
                            reclamation.setStatut("En attente");
                            reclamationService.updateEntity(reclamation);

                            // Optionnel : vérifier si la mise à jour a réussi
                            Reclamation updatedReclamation = reclamationService.findById(reclamationId);
                            if ("En attente".equals(updatedReclamation.getStatut())) {
                                showAlert("Succès", "Réponse supprimée", Alert.AlertType.INFORMATION);
                            } else {
                                showAlert("Avertissement", "Réponse supprimée mais problème de mise à jour du statut", Alert.AlertType.WARNING);
                            }
                        }

                        loadReponses(); // Rafraîchir l'affichage
                    } else {
                        showAlert("Erreur", "Échec de la suppression de la réponse", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showAlert("Erreur critique", "Erreur: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadReponses() {
        reponsesList.setItems(FXCollections.observableArrayList(
                reponseService.getReponsesByUtilisateur(2) // Remplacez par l'ID de l'utilisateur connecté
        ));
    }

    @FXML
    private void handleRetourButton() {
        if (mainController != null) {
            mainController.returnToListView();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}