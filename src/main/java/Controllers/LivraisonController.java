package Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class LivraisonController implements Initializable {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField adresseField;

    @FXML
    private TextField villeField;

    @FXML
    private TextField codePostalField;

    @FXML
    private ComboBox<String> paysComboBox;

    @FXML
    private ComboBox<String> paiementComboBox;

    @FXML
    private DatePicker dateLivraisonPicker;

    @FXML
    private TextField instructionsField;

    @FXML
    private Button annulerButton;

    @FXML
    private Button confirmerButton;

    private double montantTotal;
    private boolean confirmed = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser les ComboBox
        paysComboBox.setItems(FXCollections.observableArrayList(
                "Tunisie", "Algérie", "Maroc", "Libye", "France", "Italie", "Espagne", "Allemagne"
        ));

        paiementComboBox.setItems(FXCollections.observableArrayList(
                "Espèces", "Carte bancaire à la livraison", "Chèque"
        ));

        // Définir la date minimale pour la livraison (demain)
        dateLivraisonPicker.setValue(LocalDate.now().plusDays(1));

        // Ajouter des validateurs
        setupValidators();
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    void handleAnnuler(ActionEvent event) {
        confirmed = false;
        closeStage();
    }

    @FXML
    void handleConfirmer(ActionEvent event) {
        if (validateForm()) {
            confirmed = true;
            showConfirmationDialog();
            closeStage();
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().trim().isEmpty()) {
            errors.append("- Le nom est requis\n");
        }

        if (prenomField.getText().trim().isEmpty()) {
            errors.append("- Le prénom est requis\n");
        }

        String telephone = telephoneField.getText().trim();
        if (telephone.isEmpty()) {
            errors.append("- Le numéro de téléphone est requis\n");
        } else {
            try {
                // Vérifier que le numéro contient exactement 8 chiffres
                String digitsOnly = telephone.replaceAll("\\D", "");
                if (digitsOnly.length() != 8) {
                    errors.append("- Le numéro de téléphone doit contenir exactement 8 chiffres\n");
                }

                // Vérifier que tous les caractères sont des chiffres (redondant avec replaceAll mais plus sûr)
                Long.parseLong(digitsOnly);
            } catch (NumberFormatException e) {
                errors.append("- Le numéro de téléphone doit contenir uniquement des chiffres\n");
            }
        }

        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            errors.append("- Une adresse email valide est requise\n");
        }

        if (adresseField.getText().trim().isEmpty()) {
            errors.append("- L'adresse est requise\n");
        }

        if (villeField.getText().trim().isEmpty()) {
            errors.append("- La ville est requise\n");
        }

        if (codePostalField.getText().trim().isEmpty()) {
            errors.append("- Le code postal est requis\n");
        }

        if (paysComboBox.getValue() == null) {
            errors.append("- Le pays est requis\n");
        }

        if (paiementComboBox.getValue() == null) {
            errors.append("- Le mode de paiement est requis\n");
        }

        if (dateLivraisonPicker.getValue() == null) {
            errors.append("- La date de livraison est requise\n");
        } else if (dateLivraisonPicker.getValue().isBefore(LocalDate.now())) {
            errors.append("- La date de livraison doit être dans le futur\n");
        }

        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText("Veuillez corriger les erreurs suivantes:");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private void setupValidators() {
        // Validation en temps réel pour le téléphone (format tunisien - 8 chiffres)
        telephoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Accepter uniquement les chiffres et le signe +
            if (!newValue.matches("\\+?[0-9]*")) {
                telephoneField.setText(oldValue);
                return;
            }

            // Limiter à 8 chiffres (sans compter le +)
            String digitsOnly = newValue.replaceAll("\\D", "");
            if (digitsOnly.length() > 8) {
                // Si on dépasse 8 chiffres, revenir à l'ancienne valeur
                telephoneField.setText(oldValue);
            }
        });

        // Ajouter un indicateur visuel pour montrer le format attendu
        telephoneField.setPromptText("8 chiffres (ex: 12345678)");

        // Validation du code postal (numérique)
        codePostalField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) {
                codePostalField.setText(oldValue);
            }
        });
    }

    private void showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Commande confirmée");
        alert.setHeaderText("Votre commande a été confirmée");
        alert.setContentText(
                "Récapitulatif de votre commande:\n\n" +
                "Nom: " + prenomField.getText() + " " + nomField.getText() + "\n" +
                "Adresse de livraison: " + adresseField.getText() + "\n" +
                "Ville: " + villeField.getText() + ", " + codePostalField.getText() + "\n" +
                "Pays: " + paysComboBox.getValue() + "\n" +
                "Mode de paiement: " + paiementComboBox.getValue() + "\n" +
                "Date de livraison prévue: " + dateLivraisonPicker.getValue() + "\n\n" +
                "Montant total à payer: " + String.format("%.2f", montantTotal) + "\n\n" +
                "Merci pour votre commande!"
        );
        alert.showAndWait();
    }

    private void closeStage() {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }
}
