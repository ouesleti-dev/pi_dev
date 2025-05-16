package Controllers.Admin;

import Models.Event;
import Models.Excursion;
import Models.User;
import Services.AuthService;
import Services.EventService;
import Services.ExcursionService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ExcursionAddController implements Initializable {

    @FXML
    private TextField titreField;

    @FXML
    private TextField destinationField;



    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField dureeField;

    @FXML
    private ComboBox<String> transportComboBox;

    @FXML
    private ComboBox<Event> eventComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private ExcursionService excursionService;
    private EventService eventService;
    private AuthService authService;

    public ExcursionAddController() {
        excursionService = ExcursionService.getInstance();
        eventService = EventService.getInstance();
        authService = AuthService.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les options de transport
        transportComboBox.setItems(FXCollections.observableArrayList(
                "Bus", "Train", "Avion", "Bateau", "Voiture", "Autre"
        ));

        // Charger les événements disponibles
        loadEvents();

        // Définir la date minimale (aujourd'hui)
        datePicker.setValue(LocalDate.now());
    }

    private void loadEvents() {
        try {
            List<Event> allEvents = eventService.getAllEvents();
            List<Event> availableEvents = new ArrayList<>();

            // Filtrer les événements qui ont déjà une excursion
            for (Event event : allEvents) {
                if (!excursionService.hasExcursionsForEvent(event.getId())) {
                    availableEvents.add(event);
                }
            }

            eventComboBox.setItems(FXCollections.observableArrayList(availableEvents));

            // Afficher un message si aucun événement n'est disponible
            if (availableEvents.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Information", "Tous les événements ont déjà une excursion associée.");
                saveButton.setDisable(true);
            } else {
                saveButton.setDisable(false);
            }

            // Définir comment afficher les événements dans le ComboBox
            eventComboBox.setCellFactory(param -> new ListCell<Event>() {
                @Override
                protected void updateItem(Event event, boolean empty) {
                    super.updateItem(event, empty);
                    if (empty || event == null) {
                        setText(null);
                    } else {
                        setText(event.getTitle());
                    }
                }
            });

            // Définir comment afficher l'événement sélectionné
            eventComboBox.setButtonCell(new ListCell<Event>() {
                @Override
                protected void updateItem(Event event, boolean empty) {
                    super.updateItem(event, empty);
                    if (empty || event == null) {
                        setText(null);
                    } else {
                        setText(event.getTitle());
                    }
                }
            });
        } catch (SQLException e) {
            showError("Erreur lors du chargement des événements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            // Valider les champs
            if (!validateFields()) {
                return;
            }

            // Récupérer les valeurs des champs
            String titre = titreField.getText().trim();
            String destination = destinationField.getText().trim();
            LocalDate localDate = datePicker.getValue();
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            int duree = Integer.parseInt(dureeField.getText().trim());
            String transport = transportComboBox.getValue();
            Event selectedEvent = eventComboBox.getValue();

            // Vérifier si une excursion existe déjà pour cet événement
            try {
                boolean hasExcursion = excursionService.hasExcursionsForEvent(selectedEvent.getId());
                if (hasExcursion) {
                    showError("Une excursion existe déjà pour cet événement. Vous ne pouvez pas ajouter plus d'une excursion par événement.");
                    return;
                }
            } catch (SQLException e) {
                showError("Erreur lors de la vérification des excursions existantes: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // Récupérer l'utilisateur courant
            User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                showError("Vous devez être connecté pour ajouter une excursion.");
                return;
            }

            // Créer l'objet Excursion
            Excursion excursion = new Excursion();
            excursion.setTitre(titre);
            excursion.setDestination(destination);
            excursion.setPrix(selectedEvent.getPrix()); // Utiliser le prix de l'événement
            excursion.setDate(date);
            excursion.setDuree(duree);
            excursion.setTransport(transport);
            excursion.setId_event(selectedEvent.getId());
            excursion.setId_user(currentUser.getId());

            // Enregistrer l'excursion
            Excursion savedExcursion = excursionService.addExcursion(excursion);
            if (savedExcursion != null) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Excursion ajoutée avec succès.");
                closeWindow();
            } else {
                showError("Erreur lors de l'ajout de l'excursion.");
            }
        } catch (NumberFormatException e) {
            showError("Format de nombre invalide. Veuillez vérifier les champs numériques.");
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        // Valider le titre
        if (titreField.getText().trim().isEmpty()) {
            errors.append("- Le titre est obligatoire\n");
        } else if (titreField.getText().trim().length() < 3) {
            errors.append("- Le titre doit contenir au moins 3 caractères\n");
        }

        // Valider la destination
        if (destinationField.getText().trim().isEmpty()) {
            errors.append("- La destination est obligatoire\n");
        }



        // Valider la date
        if (datePicker.getValue() == null) {
            errors.append("- La date est obligatoire\n");
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            errors.append("- La date ne peut pas être dans le passé\n");
        }

        // Valider la durée
        try {
            int duree = Integer.parseInt(dureeField.getText().trim());
            if (duree <= 0) {
                errors.append("- La durée doit être supérieure à 0\n");
            }
        } catch (NumberFormatException e) {
            errors.append("- La durée doit être un nombre entier valide\n");
        }

        // Valider le transport
        if (transportComboBox.getValue() == null || transportComboBox.getValue().trim().isEmpty()) {
            errors.append("- Le moyen de transport est obligatoire\n");
        }

        // Valider l'événement associé
        if (eventComboBox.getValue() == null) {
            errors.append("- L'événement associé est obligatoire\n");
        }

        // Afficher les erreurs s'il y en a
        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}