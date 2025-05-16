package Controllers.Event;

import Models.Event;
import Models.Excursion;
import Models.User;
import Utils.PDFGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class ExcursionViewController implements Initializable {

    @FXML
    private Text excursionTitleText;

    @FXML
    private Label destinationLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label dureeLabel;

    @FXML
    private Label transportLabel;

    @FXML
    private Label eventLabel;

    @FXML
    private Label userLabel;

    @FXML
    private Button backToEventButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button downloadPdfButton;

    @FXML
    private Label downloadMessageLabel;

    private Excursion excursion;
    private Event event;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Rien à initialiser pour le moment
    }

    /**
     * Définir l'excursion à afficher
     * @param excursion L'excursion à afficher
     */
    public void setExcursion(Excursion excursion) {
        this.excursion = excursion;
        this.event = excursion.getEvent();

        // Afficher les détails de l'excursion
        excursionTitleText.setText(excursion.getTitre());
        destinationLabel.setText(excursion.getDestination());

        // Formater la date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateLabel.setText(dateFormat.format(excursion.getDate()));

        dureeLabel.setText(excursion.getDuree() + " jour(s)");
        transportLabel.setText(excursion.getTransport());

        // Afficher l'événement associé
        if (excursion.getEvent() != null) {
            eventLabel.setText(excursion.getEvent().getTitle());
        } else {
            eventLabel.setText("Non spécifié");
        }

        // Afficher l'utilisateur associé
        if (excursion.getUser() != null) {
            User user = excursion.getUser();
            userLabel.setText(user.getPrenom() + " " + user.getNom());
        } else {
            userLabel.setText("Non spécifié");
        }
    }

    /**
     * Gérer le clic sur le bouton "Retour à l'événement"
     */
    @FXML
    private void handleBackToEvent(ActionEvent event) {
        try {
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) backToEventButton.getScene().getWindow();
            currentStage.close();

            // Ouvrir la fenêtre de détails de l'événement
            if (this.event != null) {
                File file = new File("src/main/resources/fxml/event/EventView.fxml");
                if (file.exists()) {
                    URL url = file.toURI().toURL();
                    FXMLLoader loader = new FXMLLoader(url);
                    Parent root = loader.load();

                    EventViewController controller = loader.getController();
                    controller.setEvent(this.event);

                    Stage stage = new Stage();
                    stage.setTitle("Détails de l'événement");
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    stage.show();
                } else {
                    showAlert("Erreur", "Fichier FXML non trouvé: " + file.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des détails de l'événement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gérer le clic sur le bouton "Fermer"
     */
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Gérer le clic sur le bouton "Télécharger PDF"
     */
    @FXML
    private void handleDownloadPdf(ActionEvent event) {
        try {
            // Générer le PDF
            String pdfPath = PDFGenerator.generateExcursionPDF(this.excursion);

            // Afficher un message de succès
            downloadMessageLabel.setText("PDF généré avec succès: " + pdfPath);
            downloadMessageLabel.setVisible(true);
            downloadMessageLabel.setManaged(true);

            // Ouvrir le PDF avec l'application par défaut
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la génération du PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Afficher une alerte
     * @param title Titre de l'alerte
     * @param content Contenu de l'alerte
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
