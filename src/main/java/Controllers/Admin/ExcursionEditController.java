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
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ExcursionEditController implements Initializable {

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
    private Excursion excursion;

    public ExcursionEditController() {
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
    }

    public void setExcursion(Excursion excursion) {
        this.excursion = excursion;

        // Remplir les champs avec les données de l'excursion
        titreField.setText(excursion.getTitre());
        destinationField.setText(excursion.getDestination());

        // Convertir la date Java en LocalDate
        if (excursion.getDate() != null) {
            // Conversion de java.sql.Date en LocalDate en utilisant java.sql.Date.toLocalDate()
            try {
                // Méthode 1: Utiliser directement la méthode toLocalDate() de java.sql.Date
                if (excursion.getDate() instanceof java.sql.Date) {
                    java.sql.Date sqlDate = (java.sql.Date) excursion.getDate();
                    datePicker.setValue(sqlDate.toLocalDate());
                }
                // Méthode 2: Utiliser Calendar pour extraire année, mois, jour
                else {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(excursion.getDate());
                    int year = cal.get(java.util.Calendar.YEAR);
                    // Mois commence à 0 dans Calendar, mais à 1 dans LocalDate
                    int month = cal.get(java.util.Calendar.MONTH) + 1;
                    int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
                    datePicker.setValue(LocalDate.of(year, month, day));
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la conversion de la date: " + e.getMessage());
                // En cas d'erreur, utiliser la date actuelle
                datePicker.setValue(LocalDate.now());
            }
        }

        dureeField.setText(String.valueOf(excursion.getDuree()));
        transportComboBox.setValue(excursion.getTransport());

        // Sélectionner l'événement associé
        if (excursion.getEvent() != null) {
            for (Event event : eventComboBox.getItems()) {
                if (event.getId() == excursion.getId_event()) {
                    eventComboBox.setValue(event);
                    break;
                }
            }
        }
    }

    private void loadEvents() {
        try {
            List<Event> events = eventService.getAllEvents();
            eventComboBox.setItems(FXCollections.observableArrayList(events));

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

            // Mettre à jour l'objet Excursion
            excursion.setTitre(titre);
            excursion.setDestination(destination);
            excursion.setPrix(selectedEvent.getPrix()); // Utiliser le prix de l'événement
            excursion.setDate(date);
            excursion.setDuree(duree);
            excursion.setTransport(transport);
            excursion.setId_event(selectedEvent.getId());

            // Enregistrer les modifications
            boolean updated = excursionService.updateExcursion(excursion);
            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Excursion modifiée avec succès.");
                closeWindow();
            } else {
                showError("Erreur lors de la modification de l'excursion.");
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
