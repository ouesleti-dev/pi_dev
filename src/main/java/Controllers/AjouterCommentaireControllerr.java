package Controllers;

import Models.Commentaire;
import Services.BadWordsService;
import Services.CommentaireService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Date;

public class AjouterCommentaireControllerr {

    @FXML
    private Button Create;

    @FXML
    private Button modifier;

    @FXML
    private Button supprimer;

    @FXML
    private Button Afficher;

    @FXML
    private TextField auteur;

    @FXML
    private TextField contenu;

    @FXML
    private DatePicker dateCommentaire;

    @FXML
    private TextField idPost;

    @FXML
    private Label resultLabel;

    private final CommentaireService commentaireService = new CommentaireService();
    private final BadWordsService badWordsService = new BadWordsService();

    @FXML
    void Create(ActionEvent event) {
        String contenuText = contenu.getText();
        String auteurText = auteur.getText();
        String idPostText = idPost.getText();
        java.time.LocalDate dateValue = dateCommentaire.getValue();

        if (contenuText == null || contenuText.trim().isEmpty() ||
                auteurText == null || auteurText.trim().isEmpty() ||
                idPostText == null || idPostText.trim().isEmpty() ||
                dateValue == null) {
            resultLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            int postId = Integer.parseInt(idPostText);
            Date sqlDate = Date.valueOf(dateValue);
            Commentaire commentaire = new Commentaire(contenuText, auteurText, sqlDate, postId);
            commentaireService.create(commentaire);
            resultLabel.setText("Commentaire ajouté avec succès !");
            contenu.clear();
            auteur.clear();
            idPost.clear();
            dateCommentaire.setValue(null);
        } catch (NumberFormatException e) {
            resultLabel.setText("L'ID du post doit être un nombre.");
        } catch (Exception e) {
            resultLabel.setText("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    void checkBadWords(ActionEvent event) {
        String contenuText = contenu.getText();
        if (contenuText == null || contenuText.trim().isEmpty()) {
            resultLabel.setText("Veuillez entrer un commentaire.");
            return;
        }

        if (badWordsService.containsBadWords(contenuText)) {
            String filteredContenu = badWordsService.filterBadWords(contenuText);
            contenu.setText(filteredContenu);
            resultLabel.setText("Mots inappropriés détectés et filtrés !");
        } else {
            resultLabel.setText("Aucun mot inapproprié détecté.");
        }
    }

    @FXML
    void Afficher(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AfficherCommentaire.fxml"));
        Parent root = loader.load();
        idPost.getScene().setRoot(root);
    }

    @FXML
    void modifier(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifierCommentaire.fxml"));
        Parent root = loader.load();
        modifier.getScene().setRoot(root);
    }

    @FXML
    void supprimer(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/supprimerCommentaire.fxml"));
        Parent root = loader.load();
        supprimer.getScene().setRoot(root);
    }
}