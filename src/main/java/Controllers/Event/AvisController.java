package Controllers.Event;

import Models.Avis;
import Models.Event;
import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Services.AuthService;
import Services.AvisService;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class AvisController implements Initializable {

    @FXML
    private Slider noteSlider;

    @FXML
    private Label noteLabel;

    @FXML
    private TextArea commentaireArea;

    @FXML
    private Button submitButton;

    @FXML
    private VBox avisContainer;

    @FXML
    private Label moyenneLabel;

    private Event event;
    private AvisService avisService;
    private AuthService authService;

    public AvisController() {
        avisService = AvisService.getInstance();
        authService = AuthService.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser le slider
        noteSlider.setMin(1);
        noteSlider.setMax(5);
        noteSlider.setValue(5);
        noteSlider.setMajorTickUnit(1);
        noteSlider.setMinorTickCount(0);
        noteSlider.setSnapToTicks(true);
        noteSlider.setShowTickMarks(true);
        noteSlider.setShowTickLabels(true);

        // Mettre à jour le label de note quand le slider change
        noteSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int note = newValue.intValue();
            noteLabel.setText(note + " étoile" + (note > 1 ? "s" : ""));
        });

        // Initialiser le label de note
        noteLabel.setText("5 étoiles");
    }

    public void setEvent(Event event) {
        this.event = event;

        // Charger les avis existants
        loadAvis();

        // Vérifier si l'utilisateur a déjà donné un avis
        checkUserRating();
    }

    private void loadAvis() {
        try {
            // Vider le conteneur
            avisContainer.getChildren().clear();

            // Récupérer les avis pour cet événement
            List<Avis> avisList = avisService.getAvisByEvent(event.getId());

            // Calculer la note moyenne
            double moyenne = avisService.getAverageRating(event.getId());
            moyenneLabel.setText(String.format("Note moyenne : %.1f/5", moyenne));

            // Afficher les avis
            for (Avis avis : avisList) {
                HBox avisBox = createAvisBox(avis);
                avisContainer.getChildren().add(avisBox);
            }

            // Afficher un message si aucun avis
            if (avisList.isEmpty()) {
                Label noAvisLabel = new Label("Aucun avis pour cet événement");
                noAvisLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #777777;");
                avisContainer.getChildren().add(noAvisLabel);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des avis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HBox createAvisBox(Avis avis) {
        HBox avisBox = new HBox(15); // Augmenter l'espacement entre les éléments
        avisBox.setStyle("-fx-padding: 10; -fx-border-color: #dddddd; -fx-border-radius: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 5;");
        avisBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT); // Aligner les éléments à gauche et au centre verticalement

        // Créer une boîte pour les informations qui prendra la majorité de l'espace
        VBox infoBox = new VBox(5);
        infoBox.setPrefWidth(400); // Définir une largeur préférée pour la boîte d'info
        infoBox.setMaxWidth(Double.MAX_VALUE); // Permettre à la boîte d'info de s'étendre
        javafx.scene.layout.HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS); // Permettre à la boîte d'info de s'étendre

        // Nom de l'utilisateur et date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Label userLabel = new Label(avis.getUser().getPrenom() + " " + avis.getUser().getNom() + " - " + dateFormat.format(avis.getDateAvis()));
        userLabel.setStyle("-fx-font-weight: bold;");

        // Note
        Label noteLabel = new Label(avis.getNote() + " étoile" + (avis.getNote() > 1 ? "s" : ""));
        noteLabel.setStyle("-fx-text-fill: #f39c12;");

        // Commentaire
        Label commentaireLabel = new Label(avis.getCommentaire());
        commentaireLabel.setWrapText(true);

        infoBox.getChildren().addAll(userLabel, noteLabel, commentaireLabel);
        avisBox.getChildren().add(infoBox);

        // Bouton de suppression (visible uniquement pour l'auteur ou l'admin)
        User currentUser = authService.getCurrentUser();
        if (currentUser != null && (currentUser.getId() == avis.getUser().getId() ||
                currentUser.getRole() == User.Role.ROLE_ADMIN ||
                currentUser.getRole() == User.Role.ROLE_SUPER_ADMIN)) {
            // Créer un bouton avec une largeur fixe suffisante pour afficher le texte
            Button deleteButton = new Button("Supprimer");
            deleteButton.setMinWidth(120);
            deleteButton.setPrefWidth(120);
            deleteButton.setMaxHeight(30);

            // Style du bouton avec padding suffisant
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
            deleteButton.setOnAction(e -> deleteAvis(avis.getId()));

            // Ajouter le bouton directement à la HBox principale
            avisBox.getChildren().add(deleteButton);
        }

        return avisBox;
    }

    private void checkUserRating() {
        try {
            User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                // Désactiver le formulaire si l'utilisateur n'est pas connecté
                noteSlider.setDisable(true);
                commentaireArea.setDisable(true);
                submitButton.setDisable(true);
                submitButton.setText("Connectez-vous pour donner un avis");
                return;
            }

            // Vérifier si l'utilisateur est l'organisateur de l'événement
            if (event.getUser() != null && event.getUser().getId() == currentUser.getId()) {
                noteSlider.setDisable(true);
                commentaireArea.setDisable(true);
                submitButton.setDisable(true);
                submitButton.setText("Vous ne pouvez pas évaluer votre propre événement");
                return;
            }

            // Vérifier si l'utilisateur a déjà donné un avis
            boolean hasRated = avisService.hasUserRatedEvent(currentUser.getId(), event.getId());
            if (hasRated) {
                noteSlider.setDisable(true);
                commentaireArea.setDisable(true);
                submitButton.setDisable(true);
                submitButton.setText("Vous avez déjà donné un avis");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        try {
            User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vous devez être connecté pour donner un avis");
                return;
            }

            // Vérifier si l'utilisateur est l'organisateur de l'événement
            if (this.event.getUser() != null && this.event.getUser().getId() == currentUser.getId()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vous ne pouvez pas évaluer votre propre événement");
                return;
            }

            // Vérifier si l'utilisateur a déjà donné un avis
            if (avisService.hasUserRatedEvent(currentUser.getId(), this.event.getId())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vous avez déjà donné un avis pour cet événement");
                return;
            }

            // Récupérer les valeurs
            int note = (int) noteSlider.getValue();
            String commentaire = commentaireArea.getText().trim();

            // Valider le commentaire
            if (commentaire.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir un commentaire");
                return;
            }

            // Créer l'avis
            Avis avis = new Avis();
            avis.setNote(note);
            avis.setCommentaire(commentaire);
            avis.setUser(currentUser);
            avis.setEvent(this.event);

            // Enregistrer l'avis
            boolean success = avisService.addAvis(avis);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre avis a été enregistré avec succès");

                // Réinitialiser le formulaire
                noteSlider.setValue(5);
                commentaireArea.clear();

                // Recharger les avis
                loadAvis();

                // Désactiver le formulaire
                noteSlider.setDisable(true);
                commentaireArea.setDisable(true);
                submitButton.setDisable(true);
                submitButton.setText("Vous avez déjà donné un avis");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'enregistrement de votre avis");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement de l'avis: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void deleteAvis(int avisId) {
        try {
            boolean success = avisService.deleteAvis(avisId);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'avis a été supprimé avec succès");

                // Recharger les avis
                loadAvis();

                // Réactiver le formulaire
                noteSlider.setDisable(false);
                commentaireArea.setDisable(false);
                submitButton.setDisable(false);
                submitButton.setText("Soumettre");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la suppression de l'avis");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de l'avis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Ferme la fenêtre lorsque le bouton "Fermer" est cliqué
     */
    @FXML
    public void handleClose(ActionEvent event) {
        Stage stage = (Stage) moyenneLabel.getScene().getWindow();
        stage.close();
    }
}