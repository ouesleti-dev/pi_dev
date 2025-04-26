package Controllers;

import Models.User;
import Services.AuthService;
import Utils.UserValidation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private AuthService authService;

    public LoginController() {
        authService = new AuthService();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            // Validation de l'email avec UserValidation
            if (!validateInputs(email, password)) {
                return;
            }

            User user = authService.login(email, password);

            showError("");


            switch (user.getRole()) {
                case ROLE_ADMIN:
                    // Ouvrir dashboard admin
                    System.out.println("Redirection vers dashboard admin");
                    break;
                case ROLE_CLIENT:
                    // Ouvrir interface client
                    System.out.println("Redirection vers interface client");
                    break;
                case ROLE_SUPER_ADMIN:
                    // Ouvrir interface super admin
                    System.out.println("Redirection vers interface super admin");
                    break;
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private boolean validateInputs(String email, String password) {
        try {
            // Validation de l'email
            UserValidation.isValidEmail(email);

            UserValidation.isValidPassword(password);

            return true;

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return false;
        }
    }



    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(!message.isEmpty());
    }

    @FXML
    private void initialize() {
        // Ajouter des listeners pour effacer les messages d'erreur lors de la saisie
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setVisible(false);
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setVisible(false);
        });
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Authentification/register.fxml"));
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).setScene(new Scene(root));
        } catch (Exception e) {
            showError("Erreur de redirection");
        }
    }
}
