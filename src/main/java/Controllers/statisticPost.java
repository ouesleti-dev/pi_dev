package Controllers;

import Services.PostService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class statisticPost {

    @FXML
    private Button AuteurActif;

    @FXML
    private Label auteur;

    private PostService postService = new PostService();

    @FXML
    public void initialize() {
        // Initialiser le label par défaut
        auteur.setText("Cliquez pour voir l'auteur le plus actif.");
        // Vérifier si les composants FXML sont bien initialisés
        if (AuteurActif == null || auteur == null) {
            showAlert("Erreur : Composants FXML (bouton ou label) non initialisés. Vérifiez statisticPost.fxml.");
        }
    }

    @FXML
    void AuteurActif(ActionEvent event) {
        try {
            String result = postService.auteurLePlusActif();
            auteur.setText(result);
        } catch (SQLException e) {
            auteur.setText("Erreur lors du calcul.");
            showAlert("Erreur lors du calcul de l'auteur le plus actif : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}