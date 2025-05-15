package Controllers;

import Models.User;
import Services.AuthService;
import Services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileUserController implements Initializable {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Text avatarText;

    private AuthService authService;
    private UserService userService;
    private User currentUser;

    public ProfileUserController() {
        authService = AuthService.getInstance();
        userService = new UserService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Récupérer l'utilisateur connecté
        currentUser = authService.getCurrentUser();

        if (currentUser != null) {
            // Afficher les informations de l'utilisateur dans la console pour déboguer
            System.out.println("Informations de l'utilisateur connecté:");
            System.out.println("ID: " + currentUser.getId());
            System.out.println("Nom: " + currentUser.getNom());
            System.out.println("Prénom: " + currentUser.getPrenom());
            System.out.println("Email: " + currentUser.getEmail());
            System.out.println("Téléphone: " + currentUser.getTelephone());

            // Remplir les champs avec les informations actuelles de l'utilisateur
            nomField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            emailField.setText(currentUser.getEmail());

            // Vérifier si le téléphone est null ou vide et l'afficher dans la console
            String telephone = currentUser.getTelephone();
            System.out.println("Téléphone avant affichage: " + telephone);
            telephoneField.setText(telephone != null ? telephone : "");

            // Définir la première lettre du prénom dans l'avatar
            if (avatarText != null && currentUser.getPrenom() != null && !currentUser.getPrenom().isEmpty()) {
                avatarText.setText(currentUser.getPrenom().substring(0, 1).toUpperCase());
            }

            // Configurer le style du message d'erreur/succès
            messageLabel.setVisible(false);
        }
    }

    @FXML
    public void handleSave(ActionEvent event) {
        // Vérifier que les champs obligatoires sont remplis
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty()) {
            showMessage("Veuillez remplir tous les champs obligatoires (nom, prénom)", true);
            return;
        }

        // L'email n'est pas modifiable, donc pas besoin de le vérifier

        // Vérifier que les mots de passe correspondent si un nouveau mot de passe est fourni
        if (!passwordField.getText().isEmpty() && !passwordField.getText().equals(confirmPasswordField.getText())) {
            showMessage("Les mots de passe ne correspondent pas", true);
            return;
        }

        try {
            // Afficher les valeurs des champs dans la console pour déboguer
            System.out.println("Valeurs des champs avant mise à jour:");
            System.out.println("Nom: " + nomField.getText());
            System.out.println("Prénom: " + prenomField.getText());
            System.out.println("Email: " + emailField.getText());
            System.out.println("Téléphone: " + telephoneField.getText());

            // Mettre à jour les informations de l'utilisateur
            currentUser.setNom(nomField.getText());
            currentUser.setPrenom(prenomField.getText());
            // Ne pas mettre à jour l'email car il est en lecture seule
            // currentUser.setEmail(emailField.getText());
            currentUser.setTelephone(telephoneField.getText());
            System.out.println("Téléphone après mise à jour: " + currentUser.getTelephone());
            // Ne pas mettre à jour l'adresse car la colonne n'existe pas dans la base de données

            // Mettre à jour le mot de passe si un nouveau est fourni
            if (!passwordField.getText().isEmpty()) {
                // Hacher le mot de passe avant de le stocker
                String hashedPassword = authService.hashPassword(passwordField.getText());
                currentUser.setPassword(hashedPassword);
            }

            // Enregistrer les modifications dans la base de données
            boolean success = userService.updateUser(currentUser);

            if (success) {
                // Afficher un message de succès
                showMessage("Profil mis à jour avec succès", false);

                // Attendre 2 secondes avant de retourner au tableau de bord
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        // Retourner au tableau de bord sur le thread JavaFX
                        javafx.application.Platform.runLater(() -> returnToDashboard(event));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                showMessage("Erreur lors de la mise à jour du profil", true);
            }
        } catch (Exception e) {
            showMessage("Erreur: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        // Retourner au tableau de bord sans enregistrer les modifications
        returnToDashboard(event);
    }

    private void returnToDashboard(ActionEvent event) {
        try {
            // Charger le tableau de bord client
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDashboard.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changer la scène
            stage.setScene(scene);
            stage.setTitle("Tableau de bord client");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du retour au tableau de bord: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        // Vérification simple de l'email
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    /**
     * Affiche un message d'erreur ou de succès
     * @param message Le message à afficher
     * @param isError true si c'est une erreur, false si c'est un succès
     */
    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);

        if (isError) {
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
