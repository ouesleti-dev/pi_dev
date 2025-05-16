package Controllers;

import Models.Reclamation;
import Models.User;
import Services.AuthService;
import Services.BadWordsService;
import Services.ReclamationService;
import Services.SMSServices;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;

public class AjouterReclamation {

    @FXML private TextField titreField;
    @FXML private TextArea messageField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> prioriteComboBox;
    @FXML private ComboBox<String> categorieComboBox;

    @FXML private Label titreErrorLabel;
    @FXML private Label messageErrorLabel;
    @FXML private Label typeErrorLabel;
    @FXML private Label prioriteErrorLabel;
    @FXML private Label categorieErrorLabel;
    @FXML private Pane backgroundPane;

    private final BadWordsService badWordsService = new BadWordsService();
    private final SMSServices smsService = new SMSServices();
    private final ReclamationService reclamationService = new ReclamationService();
    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll(
                "Problème de réservation",
                "Modification d'événement",
                "Problème de paiement",
                "Problème de compte",
                "Incident technique",
                "autre"
        );
        prioriteComboBox.getItems().addAll(
                "Urgent",
                "Important",
                "Normal",
                "Faible"
        );
        categorieComboBox.getItems().addAll(
                "Réservation en ligne",
                "Gestion d'événements",
                "Paiements",
                "Compte utilisateur",
                "Support technique",
                "Autre"
        );
        clearErrorLabels();
        initAnimatedBackground();
    }

    private void initAnimatedBackground() {
        if (backgroundPane == null) return;

        final int circleCount = 10;  // Augmenté légèrement pour un meilleur effet
        final double minRadius = 40; // Taille minimale augmentée
        final double maxRadius = 100; // Taille maximale augmentée

        double paneWidth = backgroundPane.getWidth() > 0 ? backgroundPane.getWidth() : 1200;
        double paneHeight = backgroundPane.getHeight() > 0 ? backgroundPane.getHeight() : 700;

        for (int i = 0; i < circleCount; i++) {
            Circle circle = new Circle();
            circle.getStyleClass().add("animated-circle");

            double radius = minRadius + Math.random() * (maxRadius - minRadius);
            double centerX = Math.random() * paneWidth;
            double centerY = Math.random() * paneHeight;

            circle.setRadius(radius);
            circle.setCenterX(centerX);
            circle.setCenterY(centerY);

            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2 + Math.random() * 2), circle);
            scaleTransition.setFromX(1);
            scaleTransition.setFromY(1);
            scaleTransition.setToX(1.5);
            scaleTransition.setToY(1.5);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(Animation.INDEFINITE);

            TranslateTransition translateTransition = new TranslateTransition(
                    Duration.seconds(12 + Math.random() * 10), circle);


            double moveX = (Math.random() - 0.5) * paneWidth * 0.4;
            double moveY = (Math.random() - 0.5) * paneHeight * 0.4;

            translateTransition.setByX(moveX);
            translateTransition.setByY(moveY);
            translateTransition.setAutoReverse(true);
            translateTransition.setCycleCount(Animation.INDEFINITE);
            translateTransition.setInterpolator(Interpolator.EASE_BOTH);

            new ParallelTransition(circle, scaleTransition, translateTransition).play();
            backgroundPane.getChildren().add(circle);
        }
    }

    private void clearErrorLabels() {
        titreErrorLabel.setText("");
        messageErrorLabel.setText("");
        typeErrorLabel.setText("");
        prioriteErrorLabel.setText("");
        categorieErrorLabel.setText("");
    }
    @FXML
    private void handleAjouterButton(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        // Récupérer l'utilisateur actuellement connecté
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous devez être connecté pour soumettre une réclamation.");
            return;
        }
        
        Reclamation reclamation = new Reclamation();
        reclamation.setIdUtilisateur(currentUser.getId());
        reclamation.setTitre(titreField.getText());
        reclamation.setMessage(messageField.getText());
        reclamation.setTypeReclamation(typeComboBox.getValue());
        reclamation.setPriorite(prioriteComboBox.getValue());
        reclamation.setCategorie(categorieComboBox.getValue());
        reclamation.setStatut("En attente");
        reclamation.setDateReclamation(LocalDateTime.now());

        // 3. Enregistrement via le service
        try {
            reclamationService.Create(reclamation);
            
            // 4. Envoi du SMS à l'admin (en arrière-plan)
            sendAdminNotification(reclamation);

            // 5. Affichage de l'alerte de succès
            Alert successAlert = createSuccessAlert("Succès", "Réclamation ajoutée avec succès !");
            successAlert.showAndWait();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'enregistrement: " + e.getMessage());
        }
    }

    private void sendAdminNotification(Reclamation reclamation) {
        new Thread(() -> {
            try {
                smsService.sendAdminNotification("21656521654"); // Numéro admin
            } catch (Exception e) {
                System.err.println("Échec envoi SMS: " + e.getMessage());
            }
        }).start();
    }

    private void clearForm() {
        titreField.clear();
        messageField.clear();
        typeComboBox.setValue(null);
        prioriteComboBox.setValue(null);
        categorieComboBox.setValue(null);
        clearErrorLabels();
    }

    // Votre méthode existante inchangée
    private Alert createSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Text checkIcon = new Text("✓");
        checkIcon.setFont(Font.font("System", FontWeight.BOLD, 24));
        checkIcon.setFill(Color.GREEN);

        alert.setGraphic(checkIcon);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f8fff8;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #006400;");

        return alert;
    }

    @FXML
    private void handleListeButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javaproject/liste_reclamations.fxml"));
            Parent root = loader.load();

            Scene currentScene = ((Node)event.getSource()).getScene();
            Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(newScene);
            stage.setTitle("Liste des Réclamations");
            stage.sizeToScene();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page des réclamations: " + e.getMessage());
        }
    }

    @FXML
    private void validateTitre() {
        String titre = titreField.getText();
        if (titre.isEmpty()) {
            setErrorStyle(titreField, titreErrorLabel, "Le titre est obligatoire");
        } else if (titre.length() < 5) {
            setWarningStyle(titreField, titreErrorLabel, "Minimum 5 caractères");
        } else {
            setValidStyle(titreField, titreErrorLabel);
        }
    }

    @FXML
    private void validateMessage() {
        String message = messageField.getText();
        if (message.isEmpty()) {
            setErrorStyle(messageField, messageErrorLabel, "Le message est obligatoire");
        } else if (message.length() < 10) {
            setWarningStyle(messageField, messageErrorLabel, "Minimum 10 caractères");
        } else {
            setValidStyle(messageField, messageErrorLabel);
        }
    }

    private void setErrorStyle(Control control, Label errorLabel, String message) {
        control.setStyle("-fx-border-color: #f44336; -fx-border-width: 2px;");
        errorLabel.setText(message);
    }

    private void setWarningStyle(Control control, Label errorLabel, String message) {
        control.setStyle("-fx-border-color: #FF9800; -fx-border-width: 2px;");
        errorLabel.setText(message);
    }

    private void setValidStyle(Control control, Label errorLabel) {
        control.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px;");
        errorLabel.setText("");
    }

    private boolean validateForm() {
        clearErrorLabels();
        boolean isValid = true;

        // Validation du titre
        isValid &= validateTextField(titreField, titreErrorLabel,
                "Le titre est obligatoire",
                "Minimum 5 caractères",
                5,
                "titre");

        // Validation du message
        isValid &= validateTextField(messageField, messageErrorLabel,
                "Le message est obligatoire",
                "Minimum 10 caractères",
                10,
                "message");

        // Validation des ComboBox
        isValid &= validateComboBox(typeComboBox, typeErrorLabel, "un type");
        isValid &= validateComboBox(prioriteComboBox, prioriteErrorLabel, "une priorité");
        isValid &= validateComboBox(categorieComboBox, categorieErrorLabel, "une catégorie");

        return isValid;
    }

    private boolean validateTextField(TextInputControl field, Label errorLabel,
                                      String emptyError, String lengthError,
                                      int minLength, String fieldName) {
        String text = field.getText();
        if (text.isEmpty()) {
            setErrorStyle(field, errorLabel, emptyError);
            return false;
        }
        if (text.length() < minLength) {
            setErrorStyle(field, errorLabel, lengthError);
            return false;
        }

        try {
            if (badWordsService.containsBadWords(text)) {
                setErrorStyle(field, errorLabel, "Langage inapproprié détecté");
                return false;
            }
        } catch (Exception e) {
            setWarningStyle(field, errorLabel, "Service de vérification temporairement indisponible");
            System.err.println("Erreur API - " + fieldName + ": " + e.getMessage());
        }

        return true;
    }

    private boolean validateComboBox(ComboBox<String> comboBox, Label errorLabel, String errorMessage) {
        if (comboBox.getValue() == null) {
            setErrorStyle(comboBox, errorLabel, "Veuillez sélectionner " + errorMessage);
            return false;
        }
        setValidStyle(comboBox, errorLabel);
        return true;
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleAccueilButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDashboard.fxml"));
            Parent root = loader.load();

            Scene currentScene = ((Node)event.getSource()).getScene();
            Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(newScene);
            stage.setTitle("Tableau de bord");
            stage.sizeToScene();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page d'accueil: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBackofficeButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javaproject/ListeReclamationsBackoffice.fxml"));
            Parent root = loader.load();

            Scene currentScene = ((Node)event.getSource()).getScene();
            Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(newScene);
            stage.setTitle("Gestion des réclamations - Backoffice");
            stage.sizeToScene();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le backoffice: " + e.getMessage());
        }
    }
}