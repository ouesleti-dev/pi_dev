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
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListeReclamationsBackofficeController {

    @FXML
    private ListView<Reclamation> reclamationsList;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Button mesReponsesButton;

    private Parent reponsesView;
    private MesReponsesController reponsesController;

    private final ReclamationService service = new ReclamationService();

    @FXML
    public void initialize() {
        try {
            FXMLLoader reponsesLoader = new FXMLLoader(getClass().getResource("/com/example/javaproject/MesReponsesView.fxml"));
            reponsesView = reponsesLoader.load();
            reponsesController = reponsesLoader.getController();
            reponsesController.setMainController(this);

            configureListCells();
            loadReclamations();

            reclamationsList.setPrefHeight(Region.USE_COMPUTED_SIZE);
            reclamationsList.setPrefWidth(Region.USE_COMPUTED_SIZE);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void configureListCells() {
        reclamationsList.setCellFactory(param -> new ListCell<Reclamation>() {
            private final GridPane rowContainer = new GridPane();
            private final Label dateLabel = new Label();
            private final Label titleLabel = new Label();
            private final Label typeLabel = new Label();
            private final Label priorityLabel = new Label();
            private final Label statusLabel = new Label();
            private final Button actionBtn = new Button("Répondre");

            {
                ColumnConstraints colDate = new ColumnConstraints(120, 120, 120);
                ColumnConstraints colTitle = new ColumnConstraints(200, 200, 200);
                ColumnConstraints colType = new ColumnConstraints(400, 400, 400);
                ColumnConstraints colPriority = new ColumnConstraints(100, 100, 100);
                ColumnConstraints colStatus = new ColumnConstraints(100, 100, 100);
                ColumnConstraints colAction = new ColumnConstraints(120, 120, 120);

                colDate.setHalignment(HPos.CENTER);
                colTitle.setHalignment(HPos.LEFT);
                colType.setHalignment(HPos.LEFT);
                colPriority.setHalignment(HPos.CENTER);
                colStatus.setHalignment(HPos.CENTER);
                colAction.setHalignment(HPos.CENTER);

                rowContainer.getColumnConstraints().addAll(colDate, colTitle, colType, colPriority, colStatus, colAction);
                rowContainer.setHgap(0);
                rowContainer.setPadding(new Insets(8, 5, 8, 5));
                rowContainer.setPrefWidth(820); // Largeur totale

                String commonStyle = "-fx-font-size: 14px; -fx-text-fill: #2d3748; -fx-padding: 0 10px;"; // Padding horizontal

                dateLabel.setStyle(commonStyle + "-fx-alignment: center;");
                titleLabel.setStyle(commonStyle + "-fx-font-weight: bold; -fx-alignment: center-left;");
                typeLabel.setStyle(commonStyle + "-fx-alignment: center-left;");
                priorityLabel.setStyle(commonStyle + "-fx-alignment: center;");
                statusLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 3px 10px; "
                        + "-fx-background-radius: 10px; -fx-alignment: center;");

                actionBtn.setStyle("-fx-font-size: 13px; -fx-background-color: #4c51bf; "
                        + "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; "
                        + "-fx-background-radius: 5px; -fx-alignment: center;");

                rowContainer.add(dateLabel, 0, 0);
                rowContainer.add(titleLabel, 1, 0);
                rowContainer.add(typeLabel, 2, 0);
                rowContainer.add(priorityLabel, 3, 0);
                rowContainer.add(statusLabel, 4, 0);
                rowContainer.add(actionBtn, 5, 0);
            }

            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);

                if (empty || reclamation == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    dateLabel.setText(reclamation.getDateReclamation().format(formatter));

                    String fullTitle = reclamation.getTitre();
                    titleLabel.setText(fullTitle.length() > 45 ? fullTitle.substring(0, 42) + "..." : fullTitle);
                    titleLabel.setTooltip(new Tooltip(fullTitle));

                    typeLabel.setText(reclamation.getTypeReclamation());
                    priorityLabel.setText(reclamation.getPriorite());
                    statusLabel.setText(reclamation.getStatut());

                    // Style dynamique du statut
                    String statusStyle = "";
                    switch (reclamation.getStatut().toLowerCase()) {
                        case "en attente":
                            statusStyle = "-fx-background-color: #fffaf0; -fx-text-fill: #dd6b20;";
                            break;
                        case "traité":
                        case "traite":
                            statusStyle = "-fx-background-color: #f0fff4; -fx-text-fill: #38a169;";
                            break;
                        case "approuvé":
                        case "approuvee":
                        case "approuve":
                            statusStyle = "-fx-background-color: #ebf8ff; -fx-text-fill: #3182ce;";
                            break;
                        case "rejeté":
                        case "rejete":
                            statusStyle = "-fx-background-color: #fff5f5; -fx-text-fill: #e53e3e;";
                            break;
                        default:
                            statusStyle = "-fx-background-color: #edf2f7; -fx-text-fill: #2d3748;";
                    }
                    statusLabel.setStyle(statusLabel.getStyle() + statusStyle);

                    // Gestion du bouton Répondre
                    boolean canRespond = reclamation.getReponse() == null; // Vérifie directement si la réclamation n'a pas de réponse
                    actionBtn.setDisable(!canRespond);
                    actionBtn.setVisible(true);

                    if (canRespond) {
                        actionBtn.setStyle("-fx-font-size: 13px; -fx-background-color: #4c51bf; "
                                + "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5px 10px; "
                                + "-fx-background-radius: 5px; -fx-alignment: center;");
                    } else {
                        actionBtn.setStyle("-fx-opacity: 0.5; -fx-background-color: #a0aec0;");
                    }

                    actionBtn.setOnAction(event -> {
                        if (canRespond) {
                            handleResponse(reclamation);
                        }
                    });

                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: white;");
                    } else {
                        setStyle("-fx-background-color: #f8fafc;");
                    }

                    setGraphic(rowContainer);
                }
            }
        });
    }

    private boolean canRespondToReclamation(Reclamation reclamation) {
        if (reclamation == null) return false;

        String status = reclamation.getStatut().toLowerCase().replace("é", "e");
        boolean isFinalStatus = status.equals("traite")
                || status.equals("approuvee")
                || status.equals("approuve")
                || status.equals("rejete");

        return !isFinalStatus && reclamation.getReponse() == null;
    }

    private void loadReclamations() {
        List<Reclamation> reclamations = service.getAllData();
        reclamationsList.setItems(FXCollections.observableArrayList(reclamations));
    }

    private void handleResponse(Reclamation reclamation) {
        if (!canRespondToReclamation(reclamation)) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Réclamation traitée",
                    "Cette réclamation ne peut pas recevoir de nouvelle réponse.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javaproject/ResponseDialog.fxml"));
            Parent responseForm = loader.load();
            ResponseDialogController controller = loader.getController();

            controller.setMainController(this);
            controller.setCurrentReclamation(reclamation);

            UserService utilisateurService = new UserService();
            User utilisateur = utilisateurService.getById(reclamation.getIdUtilisateur());

            if (utilisateur != null) {
                controller.setUserDetails(
                        utilisateur.getNom(),
                        utilisateur.getPrenom(),
                        utilisateur.getEmail()
                );
            }

            controller.displayReclamationDetails(reclamation);
            rootPane.setCenter(responseForm);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors du chargement du formulaire de réponse: " + e.getMessage());
        }
    }

    public void returnToListView() {
        rootPane.setCenter(reclamationsList.getParent());
        loadReclamations();
        mesReponsesButton.setVisible(true);
    }

    public void handleSaveResponse(Reclamation reclamation, String responseContent) {
        if (responseContent == null || responseContent.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez saisir une réponse valide");
            return;
        }

        try {
            Reponse response = new Reponse();
            response.setIdReclamation(reclamation.getId());
            response.setContenu(responseContent);
            response.setDateReponse(LocalDateTime.now());
            response.setIdUtilisateur(2); // ID admin

            new ReponseService().addEntity(response);

            reclamation.setStatut("Traité");
            reclamation.setReponse(response);
            new ReclamationService().updateEntity(reclamation);

            loadReclamations();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponse enregistrée avec succès");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
    }

    @FXML
    private void handleMesReponsesButton() {
        rootPane.setCenter(reponsesView);
        reponsesController.loadReponses();
        mesReponsesButton.setVisible(false);
    }

    public void showAlert(Alert.AlertType type, String title, String message) {
         Alert alert = new Alert(type);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(message);
         alert.showAndWait();
    }
}