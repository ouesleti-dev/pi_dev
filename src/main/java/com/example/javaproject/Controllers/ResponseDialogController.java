package com.example.javaproject.Controllers;

import com.example.javaproject.Entities.Reclamation;
import com.example.javaproject.Services.AIService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class ResponseDialogController {
    public ImageView userAvatar;
    @FXML private Label userNom;
    @FXML private Label userPrenom;
    @FXML private Label userEmail;
    @FXML private TextArea responseTextArea;
    @FXML private VBox reclamationDetailsContainer;
    @FXML
    private Button generateAIButton;

    private ListeReclamationsBackofficeController mainController;
    private Reclamation currentReclamation;

    public void setMainController(ListeReclamationsBackofficeController controller) {
        this.mainController = controller;
    }

    public void setCurrentReclamation(Reclamation reclamation) {
        this.currentReclamation = reclamation;
    }

    public void setUserDetails(String nom, String prenom, String email) {
        userNom.setText(nom);
        userPrenom.setText(prenom);
        userEmail.setText(email);
    }

    public void displayReclamationDetails(Reclamation reclamation) {
        reclamationDetailsContainer.getChildren().clear();

        addDetailLine("ID", String.valueOf(reclamation.getId()));
        addDetailLine("Titre", reclamation.getTitre());
        addDetailLine("Type", reclamation.getTypeReclamation());
        addDetailLine("Priorité", reclamation.getPriorite());
        addDetailLine("Catégorie", reclamation.getCategorie());
        addDetailLine("Statut", reclamation.getStatut());
        addDetailLine("Date", reclamation.getDateReclamation().toString());

        // Message avec style spécial
        Label messageTitle = new Label("Message:");
        messageTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #4c51bf; -fx-padding: 5px 0 0 0;");

        Label messageContent = new Label(reclamation.getMessage());
        messageContent.setWrapText(true);
        messageContent.setStyle("-fx-padding: 0 0 0 15px;");

        reclamationDetailsContainer.getChildren().addAll(
                messageTitle,
                messageContent
        );
    }

    private void addDetailLine(String label, String value) {
        HBox line = new HBox(10);
        Label titleLabel = new Label(label + ":");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 100px;");

        Label valueLabel = new Label(value);
        valueLabel.setWrapText(true);

        line.getChildren().addAll(titleLabel, valueLabel);
        reclamationDetailsContainer.getChildren().add(line);
    }

    @FXML
    private void handleGenerateAIResponse(ActionEvent event) {
        // Désactiver le bouton pendant le traitement
        generateAIButton.setDisable(true);
        generateAIButton.setText("Génération en cours...");

        // Afficher un indicateur de progression
        responseTextArea.setText("Génération de la réponse IA en cours...");
        responseTextArea.setStyle("-fx-text-fill: gray;");

        new Thread(() -> {
            try {
                // 1. Préparer le prompt
                String prompt = "En tant qu'assistant clientèle professionnel, "
                        + "réponds en français à cette réclamation en 2-3 phrases maximum :\n\n"
                        + currentReclamation.getMessage();

                // 2. Appeler le service IA
                String aiResponse = AIService.generateResponse(prompt);

                // 3. Mettre à jour l'UI
                Platform.runLater(() -> {
                    responseTextArea.setStyle("-fx-text-fill: black;");
                    responseTextArea.setText(aiResponse);
                    generateAIButton.setText("Générer avec IA");
                    generateAIButton.setDisable(false);

                    // Afficher une alerte de succès
                    showSuccessAlert("Réponse générée", "La réponse IA a été générée avec succès.");
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    // Réactiver le bouton en cas d'erreur
                    generateAIButton.setText("Réessayer");
                    generateAIButton.setDisable(false);

                    // Afficher l'erreur
                    showErrorAlert("Erreur de génération", e.getMessage());
                });
            }
        }).start();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        // Style personnalisé
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/com/example/styles/alerts.css").toExternalForm());
        dialogPane.getStyleClass().add("success-alert");

        Label content = new Label(message);
        content.setWrapText(true);
        content.setMaxWidth(400);
        alert.getDialogPane().setContent(content);

        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);

        // Style personnalisé
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/com/example/styles/alerts.css").toExternalForm());
        dialogPane.getStyleClass().add("error-alert");

        // Contenu avec défilement si nécessaire
        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(textArea, 0, 0);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    public String getResponse() {
        return responseTextArea.getText();
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String responseContent = getResponse();
        if (responseContent == null || responseContent.trim().isEmpty()) {
            mainController.showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez saisir une réponse valide");
            return;
        }
        mainController.handleSaveResponse(currentReclamation, responseContent);
        mainController.returnToListView();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        mainController.returnToListView();
    }
}