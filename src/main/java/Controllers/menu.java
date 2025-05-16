package Controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class menu {

    @FXML
    private void backToHome(ActionEvent event) {
        try {
            // Charger l'interface ClientDashboard (la page d'accueil principale)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDashboard.fxml"));
            Parent root = loader.load();
            
            // Obtenir la scène actuelle
            Scene currentScene = ((javafx.scene.Node) event.getSource()).getScene();
            Stage stage = (Stage) currentScene.getWindow();
            
            // Configurer la nouvelle scène
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord client");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du retour à l'accueil : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void openPostInterface(ActionEvent event) {
            try {
                // Charger l'interface Post (par exemple, sortPost.fxml ou updatePost.fxml)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostController.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Interface Posts");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Erreur lors de l'ouverture de l'interface Posts : " + e.getMessage());
            }
        }

        @FXML
        private void openCommentaireInterface(ActionEvent event) {
            try {
                // Charger l'interface Commentaire
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AjouterCommentaire.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Interface Commentaires");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Erreur lors de l'ouverture de l'interface Commentaires : " + e.getMessage());
            }
        }

        private void showAlert(String message) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

