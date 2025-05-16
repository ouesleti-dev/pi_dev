package Controllers.Admin;

import Models.Excursion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class ExcursionViewController implements Initializable {

    @FXML
    private Label titreLabel;

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
    private Button closeButton;

    private Excursion excursion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Rien à initialiser pour le moment
    }

    public void setExcursion(Excursion excursion) {
        this.excursion = excursion;

        // Afficher les détails de l'excursion
        titreLabel.setText(excursion.getTitre());
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
            userLabel.setText(excursion.getUser().getPrenom() + " " + excursion.getUser().getNom());
        } else {
            userLabel.setText("Non spécifié");
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
