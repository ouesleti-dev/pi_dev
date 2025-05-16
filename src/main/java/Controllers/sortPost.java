package Controllers;

import Models.Post;
import Services.PostService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class sortPost {

    @FXML
    private Button TrieCroissant;

    @FXML
    private Button TrieDeCroissant;

    @FXML
    private TableView<Post> tablePosts;

    @FXML
    private TableColumn<Post, String> auteur;

    @FXML
    private TableColumn<Post, String> contenu;

    @FXML
    private TableColumn<Post, String> date;

    @FXML
    private Label sort;

    private PostService postService = new PostService();

    @FXML
    public void initialize() {
        // Vérifier si les composants FXML sont initialisés
        if (tablePosts == null || auteur == null || contenu == null || date == null || sort == null) {
            showAlert("Erreur : Un ou plusieurs composants FXML (tablePosts, auteur, contenu, date, sort) ne sont pas initialisés. Vérifiez le fichier FXML.");
            return;
        }

        // Configurer les colonnes
        auteur.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAuteur()));
        contenu.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getContenu()));
        date.setCellValueFactory(data -> {
            java.sql.Date dateCreation = data.getValue().getDateCreation();
            return new javafx.beans.property.SimpleStringProperty(dateCreation != null ? dateCreation.toString() : "");
        });
    }

    @FXML
    void TrieCroissant(ActionEvent event) {
        afficherPostsTries(true);
    }

    @FXML
    void TrieDeCroissant(ActionEvent event) {
        afficherPostsTries(false);
    }

    private void afficherPostsTries(boolean croissant) {
        if (tablePosts == null) {
            showAlert("Erreur : TableView non initialisé.");
            return;
        }
        try {
            List<Post> posts = postService.trierParDate(croissant);
            ObservableList<Post> observablePosts = FXCollections.observableArrayList(posts);
            tablePosts.setItems(observablePosts);
            sort.setText("Trié par date (" + (croissant ? "croissant" : "décroissant") + ")");
        } catch (SQLException e) {
            sort.setText("Erreur lors du tri : " + e.getMessage());
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