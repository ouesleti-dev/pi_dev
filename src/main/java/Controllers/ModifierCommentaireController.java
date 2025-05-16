package Controllers;

import Models.Commentaire;
import Services.CommentaireService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ModifierCommentaireController {

    @FXML
    private Label Auteur;

    @FXML
    private Label Contenu;

    @FXML
    private Label DateCommentaire;

    @FXML
    private Button Update;

    @FXML
    private TextField auteur;

    @FXML
    private TextField contenu;

    @FXML
    private DatePicker dateCommentaire;

    @FXML
    private TextField idCommentaire;

    @FXML
    void Update(ActionEvent event) throws Exception {
        int id = Integer.parseInt(idCommentaire.getText());

        Commentaire  commentaire= new Commentaire();
        commentaire.setIdCommentaire(id);
        commentaire.setAuteur(auteur.getText());
        commentaire.setContenu(contenu.getText());
        commentaire.setDateCommentaire(java.sql.Date.valueOf(dateCommentaire.getValue()));
        CommentaireService C = new CommentaireService();
       C.update(commentaire);




        }


    }


