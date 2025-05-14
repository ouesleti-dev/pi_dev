package com.example.javaproject.Controllers;

import com.example.javaproject.Entities.Reponse;
import com.example.javaproject.Services.ReponseService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class UpdateReponseDialogController {

    @FXML private TextArea responseTextArea;
    @FXML private Button updateButton;

    private Reponse currentReponse;
    private ReponseService reponseService = new ReponseService();
    private Runnable refreshCallback;

    public void initialize() {
        // Configuration initiale si nécessaire
        updateButton.setDisable(true);
        responseTextArea.textProperty().addListener((obs, oldText, newText) -> {
            updateButton.setDisable(newText.trim().isEmpty());
        });
    }

    public void setReponse(Reponse reponse) {
        this.currentReponse = reponse;
        responseTextArea.setText(reponse.getContenu());
    }

    @FXML
    private void handleUpdate() {
        String newContent = responseTextArea.getText().trim();

        if (newContent.isEmpty()) {
            showAlert("Erreur", "Le contenu de la réponse ne peut pas être vide", Alert.AlertType.ERROR);
            return;
        }

        try {
            currentReponse.setContenu(newContent);
            reponseService.updateEntity(currentReponse);

            showAlert("Succès", "Réponse mise à jour avec succès", Alert.AlertType.INFORMATION);

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            closeWindow();

        } catch (Exception e) {
            showAlert("Erreur", "Échec de la mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) responseTextArea.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}