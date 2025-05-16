
    package Controllers;

    import Models.Post;
    import Services.PostService;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;

    public class updatePost {

        @FXML
        private Label Auteur;

        @FXML
        private Label Contenu;

        @FXML
        private Label Date;

        @FXML
        private Label IDpost;

        @FXML
        private TextField auteur;

        @FXML
        private TextField contenu;

        @FXML
        private DatePicker dateCreation; // Champ conservé pour FXML, mais non utilisé

        @FXML
        private TextField idPost;

        @FXML
        private Label up;

        @FXML
        private Button update;

        @FXML
        void update(ActionEvent event) throws Exception {
            // Valider les champs
            if (idPost.getText().isEmpty() || auteur.getText().isEmpty() || contenu.getText().isEmpty()) {
                showAlert("Veuillez remplir tous les champs obligatoires (ID, Auteur, Contenu).");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idPost.getText());
            } catch (NumberFormatException e) {
                showAlert("L'ID du post doit être un nombre valide.");
                return;
            }

            Post post = new Post();
            post.setIdPost(id);
            post.setAuteur(auteur.getText());
            post.setContenu(contenu.getText());
            // Supprimé : post.setDateCreation(Date.valueOf(localDate));

            PostService postService = new PostService();
            try {
                postService.update(post);
                showAlert("Article mis à jour avec succès !");
            } catch (Exception e) {
                showAlert("Erreur lors de la mise à jour : " + e.getMessage());
            }
        }

        // Méthode utilitaire pour afficher une alerte
        private void showAlert(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

