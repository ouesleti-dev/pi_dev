package Controllers;

import Models.Post;
import Services.PostService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class deletePost {

    @FXML
    private Label Delete;

    @FXML
    private Label IdPost;

    @FXML
    private Button delete;

    @FXML
    private TextField idPost;

    @FXML
    void delete(ActionEvent event) throws Exception {
        int id = Integer.parseInt(idPost.getText());

        PostService service = new PostService() ;
        Post post = new Post();

        post.setIdPost(id);

        service.delete(post);



    }



}

