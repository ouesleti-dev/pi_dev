package Controllers;

import Models.Commentaire;
import Services.CommentaireService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SupprimerCommentaireController {

    @FXML
    private Button delete;

    @FXML
    private Label espace;

    @FXML
    private TextField idCommentaire;

    @FXML
    private Label slegon;

    @FXML
    void delete(ActionEvent event) throws Exception {

            int id = Integer.parseInt(idCommentaire.getText());

            CommentaireService service = new CommentaireService();
            Commentaire commentaire = new Commentaire();
            commentaire.setIdCommentaire(id);

            service.delete(commentaire);


    }

}
