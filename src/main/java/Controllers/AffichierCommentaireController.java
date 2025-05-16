package Controllers;

import Models.Commentaire;
import Services.CommentaireService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Date;

public class AffichierCommentaireController {

    @FXML
    private TableColumn<Commentaire, String> auteur;

    @FXML
    private TableColumn<Commentaire, String> contenu;

    @FXML
    private TableColumn<Commentaire, Date> dateCommentaire;

    @FXML
    private TableColumn<Commentaire, Integer> idCommentaire;

    @FXML
    private TableColumn<Commentaire, Integer> idPost;

    @FXML
    private TableView<Commentaire> table;

    @FXML
    public void initialize() throws Exception {
        CommentaireService sc = new CommentaireService();
        ObservableList<Commentaire> obs = FXCollections.observableArrayList(sc.display());
        table.setItems(obs);

        auteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        contenu.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        dateCommentaire.setCellValueFactory(new PropertyValueFactory<>("dateCommentaire"));
        idCommentaire.setCellValueFactory(new PropertyValueFactory<>("idCommentaire"));
        idPost.setCellValueFactory(new PropertyValueFactory<>("idPost"));
    }


}
