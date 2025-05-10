package Controllers;

import Models.Panier;
import Services.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class PaiementEnLigneController implements Initializable {

    @FXML
    private TextField panierId;

    @FXML
    private TextField dateCreation;

    @FXML
    private TextField montantTotal;

    @FXML
    private TextField nomTitulaire;

    @FXML
    private TextField emailField;

    @FXML
    private TextField numeroCarteField;

    @FXML
    private ComboBox<String> moisExpiration;

    @FXML
    private ComboBox<String> anneeExpiration;

    @FXML
    private TextField cvcField;

    @FXML
    private TextField adresseField;

    @FXML
    private TextField complementAdresseField;

    @FXML
    private TextField villeField;

    @FXML
    private TextField codePostalField;

    @FXML
    private ComboBox<String> paysComboBox;

    @FXML
    private Label messageErreur;

    @FXML
    private Button annulerButton;

    @FXML
    private Button payerButton;

    private double montantTotalValue;
    private int panierIdValue;
    private Timestamp dateCreationValue;
    private boolean confirmed = false;
    private StripeService stripeService;
    private List<Panier> panierList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser le service Stripe
        stripeService = new StripeService();

        // Initialiser les ComboBox pour les mois
        List<String> mois = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            mois.add(String.format("%02d", i));
        }
        moisExpiration.setItems(FXCollections.observableArrayList(mois));

        // Initialiser les ComboBox pour les années
        List<String> annees = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int i = 0; i < 10; i++) {
            annees.add(String.valueOf(currentYear + i).substring(2));
        }
        anneeExpiration.setItems(FXCollections.observableArrayList(annees));

        // Initialiser les ComboBox pour les pays
        paysComboBox.setItems(FXCollections.observableArrayList(
                "Tunisie", "Algérie", "Maroc", "Libye", "France", "Italie", "Espagne", "Allemagne"
        ));

        // Masquer le message d'erreur par défaut
        messageErreur.setText("");

        // Ajouter des validateurs
        setupValidators();
    }

    private void setupValidators() {
        // Validation du numéro de carte (format simplifié pour les tests)
        numeroCarteField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                // Supprimer les espaces pour la validation
                String cardNumber = newValue.replaceAll("\\s", "");

                // Limiter à 16 chiffres
                if (cardNumber.length() > 16) {
                    numeroCarteField.setText(oldValue);
                    return;
                }

                // Formater avec des espaces tous les 4 chiffres
                if (newValue.length() > 0 && !newValue.equals(oldValue)) {
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < cardNumber.length(); i++) {
                        if (i > 0 && i % 4 == 0) {
                            formatted.append(" ");
                        }
                        formatted.append(cardNumber.charAt(i));
                    }
                    numeroCarteField.setText(formatted.toString());
                }
            }
        });

        // Validation du CVC (3 ou 4 chiffres)
        cvcField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d{0,4}")) {
                cvcField.setText(oldValue);
            }
        });
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotalValue = montantTotal;
        this.montantTotal.setText(String.format("%.2f €", montantTotal));
    }

    public void setPanierId(int panierId) {
        this.panierIdValue = panierId;
        this.panierId.setText(String.valueOf(panierId));
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreationValue = dateCreation;
        this.dateCreation.setText(dateCreation.toString());
    }

    public void setPanierList(List<Panier> panierList) {
        this.panierList = panierList;
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
    void handlePayer(ActionEvent event) {
        if (validateForm()) {
            try {
                // Afficher un indicateur de chargement ou désactiver le bouton
                payerButton.setDisable(true);
                payerButton.setText("Traitement en cours...");
                messageErreur.setText(""); // Effacer les messages d'erreur précédents

                // Convertir le montant en centimes pour Stripe (1€ = 100 centimes)
                long amountInCents = (long) (montantTotalValue * 100);

                // Créer une description pour le paiement
                String description = "Paiement GoVibe - Panier #" + panierIdValue;

                // Traiter le paiement avec Stripe (utilise une carte de test)
                boolean paymentSuccess = stripeService.processTestPayment(
                        amountInCents,
                        "eur",
                        description
                );

                if (paymentSuccess) {
                    confirmed = true;
                    showSuccessDialog();
                    closeStage();
                } else {
                    messageErreur.setText("Le paiement a été traité par Stripe mais n'a pas pu être complété. " +
                                         "Veuillez vérifier votre compte Stripe pour plus de détails.");
                    // Réactiver le bouton
                    payerButton.setDisable(false);
                    payerButton.setText("Réessayer");
                }
            } catch (com.stripe.exception.StripeException e) {
                // Utiliser la méthode de gestion des erreurs du service Stripe
                String errorMessage = stripeService.handleStripeError(e);
                messageErreur.setText(errorMessage);
                e.printStackTrace();

                // Réactiver le bouton
                payerButton.setDisable(false);
                payerButton.setText("Réessayer");
            } catch (Exception e) {
                // Afficher un message d'erreur plus convivial pour les autres types d'erreurs
                String errorMessage = "Erreur lors du traitement du paiement";

                // Ajouter des détails spécifiques selon le type d'erreur
                if (e.getMessage() != null) {
                    errorMessage += ": " + e.getMessage();
                }

                messageErreur.setText(errorMessage);
                System.err.println("Détail de l'erreur: " + e.getMessage());
                e.printStackTrace();

                // Réactiver le bouton
                payerButton.setDisable(false);
                payerButton.setText("Réessayer");
            }
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nomTitulaire.getText().trim().isEmpty()) {
            errors.append("- Le nom du titulaire est requis\n");
        }

        if (emailField.getText().trim().isEmpty() || !isValidEmail(emailField.getText())) {
            errors.append("- Une adresse email valide est requise\n");
        }

        if (numeroCarteField.getText().trim().isEmpty() ||
                numeroCarteField.getText().replaceAll("\\s", "").length() < 16) {
            errors.append("- Un numéro de carte valide est requis (16 chiffres)\n");
        }

        if (moisExpiration.getValue() == null) {
            errors.append("- Le mois d'expiration est requis\n");
        }

        if (anneeExpiration.getValue() == null) {
            errors.append("- L'année d'expiration est requise\n");
        }

        if (cvcField.getText().trim().isEmpty() || cvcField.getText().length() < 3) {
            errors.append("- Le code CVC est requis (3 ou 4 chiffres)\n");
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

        if (errors.length() > 0) {
            messageErreur.setText(errors.toString());
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void showSuccessDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paiement réussi");
        alert.setHeaderText("Votre paiement a été traité avec succès");
        alert.setContentText(
                "Récapitulatif de votre commande:\n\n" +
                "Numéro de panier: " + panierIdValue + "\n" +
                "Date de création: " + dateCreationValue + "\n" +
                "Montant total payé: " + String.format("%.2f €", montantTotalValue) + "\n\n" +
                "Un email de confirmation a été envoyé à " + emailField.getText() + "\n\n" +
                "Merci pour votre achat!"
        );
        alert.showAndWait();
    }

    private void closeStage() {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }
}
