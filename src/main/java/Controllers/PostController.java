package Controllers;

import Models.Post;
import Services.PostService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class PostController {

    @FXML
    private Label Auteur;

    @FXML
    private Label Contenu;

    @FXML
    private Label DateCreation;

    @FXML
    private Label ajout;

    @FXML
    private TextField auteur;

    @FXML
    private TextField contenu;

    @FXML
    private Button create;

    @FXML
    private DatePicker dateCreation;

    @FXML
    private Button delete;

    @FXML
    private Button display;

    @FXML
    private Label gestion;

    @FXML
    private Button reserch;

    @FXML
    private Button sort;

    @FXML
    private Button statistic;

    @FXML
    private Button update;

    @FXML
    void create(ActionEvent event) throws IOException {
        PostService postService = new PostService();
        Post post = new Post();
        post.setAuteur(auteur.getText());
        post.setContenu(contenu.getText());
        post.setDateCreation(java.sql.Date.valueOf(dateCreation.getValue()));
        postService.create(post);

    }

    @FXML
    void delete(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/deletePost.fxml"));
        Parent root = loader.load();
        delete.getScene().setRoot(root);



    }



    @FXML
    void reserch(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reserchPost.fxml"));
        Parent root = loader.load();
        reserch.getScene().setRoot(root);

    }

    @FXML
    void sort(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sortPost.fxml"));
        Parent root = loader.load();
       sort.getScene().setRoot(root);

    }


    @FXML
    void statistic(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/statisticPost.fxml"));
        Parent root = loader.load();
        statistic.getScene().setRoot(root);

    }

    @FXML
    void update(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/updatePost.fxml"));
        Parent root = loader.load();
        update.getScene().setRoot(root);

    }

}
