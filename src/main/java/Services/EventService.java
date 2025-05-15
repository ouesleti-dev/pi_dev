package Services;

import Models.Event;
import Models.User;
import Services.SMSService;
import Utils.EventValidator;
import Utils.MyDb;

import java.util.Map;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les événements
 */
public class EventService {
    private static EventService instance;
    private final Connection connection;

    // Constantes pour les statuts d'événement
    public static final String STATUS_PENDING = "en attente";
    public static final String STATUS_APPROVED = "accepté";
    public static final String STATUS_REJECTED = "rejeté";

    /**
     * Constructeur privé pour le pattern Singleton
     */
    private EventService() {

                this.connection = MyDb.getInstance().getConn();

    }

    /**
     * Obtenir l'instance unique du service
     * @return L'instance du service
     */
    public static EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }

    /**
     * Ajouter un nouvel événement
     * @param event L'événement à ajouter
     * @throws SQLException En cas d'erreur SQL
     * @throws IllegalArgumentException Si l'événement est invalide
     */
    public void addEvent(Event event) throws SQLException, IllegalArgumentException {
        // Valider l'événement
        Map<String, String> errors = EventValidator.validate(event);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(EventValidator.formatErrors(errors));
        }

        // Définir le statut par défaut à "en attente" si non spécifié
        if (event.getStatus() == null || event.getStatus().isEmpty()) {
            event.setStatus(STATUS_PENDING);
        }

        try {
            // Essayer d'abord avec la colonne prix
            String query = "INSERT INTO event (user_id, title, description, date_debut, date_fin, status, image, prix) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, event.getUser().getId());
                statement.setString(2, event.getTitle());
                statement.setString(3, event.getDescription());
                statement.setTimestamp(4, new Timestamp(event.getDate_debut().getTime()));
                statement.setTimestamp(5, new Timestamp(event.getDate_fin().getTime()));
                statement.setString(6, event.getStatus());
                statement.setString(7, event.getImage());
                statement.setDouble(8, event.getPrix());

                statement.executeUpdate();

                // Récupérer l'ID généré
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        event.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            // Si la colonne prix n'existe pas, essayer sans cette colonne
            if (e.getMessage().contains("Unknown column 'prix'")) {
                System.out.println("La colonne 'prix' n'existe pas, tentative d'insertion sans cette colonne");
                String query = "INSERT INTO event (user_id, title, description, date_debut, date_fin, status, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, event.getUser().getId());
                    statement.setString(2, event.getTitle());
                    statement.setString(3, event.getDescription());
                    statement.setTimestamp(4, new Timestamp(event.getDate_debut().getTime()));
                    statement.setTimestamp(5, new Timestamp(event.getDate_fin().getTime()));
                    statement.setString(6, event.getStatus());
                    statement.setString(7, event.getImage());

                    statement.executeUpdate();

                    // Récupérer l'ID généré
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            event.setId(generatedKeys.getInt(1));
                        }
                    }
                }
            } else {
                // Autre erreur SQL, la propager
                throw e;
            }
        }

        // Envoyer un SMS à l'administrateur pour l'informer du nouvel événement
        sendNewEventSMS(event);
    }

    /**
     * Mettre à jour un événement existant
     * @param event L'événement à mettre à jour
     * @throws SQLException En cas d'erreur SQL
     * @throws IllegalArgumentException Si l'événement est invalide
     */
    public void updateEvent(Event event) throws SQLException, IllegalArgumentException {
        // Valider l'événement
        Map<String, String> errors = EventValidator.validate(event);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(EventValidator.formatErrors(errors));
        }

        // Vérifier que l'événement existe
        if (event.getId() <= 0 || getEventById(event.getId()) == null) {
            throw new IllegalArgumentException("L'événement n'existe pas");
        }

        try {
            // Essayer d'abord avec la colonne prix
            String query = "UPDATE event SET title = ?, description = ?, date_debut = ?, date_fin = ?, status = ?, image = ?, prix = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, event.getTitle());
                statement.setString(2, event.getDescription());
                statement.setTimestamp(3, new Timestamp(event.getDate_debut().getTime()));
                statement.setTimestamp(4, new Timestamp(event.getDate_fin().getTime()));
                statement.setString(5, event.getStatus());
                statement.setString(6, event.getImage());
                statement.setDouble(7, event.getPrix());
                statement.setInt(8, event.getId());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            // Si la colonne prix n'existe pas, essayer sans cette colonne
            if (e.getMessage().contains("Unknown column 'prix'")) {
                System.out.println("La colonne 'prix' n'existe pas, tentative de mise à jour sans cette colonne");
                String query = "UPDATE event SET title = ?, description = ?, date_debut = ?, date_fin = ?, status = ?, image = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, event.getTitle());
                    statement.setString(2, event.getDescription());
                    statement.setTimestamp(3, new Timestamp(event.getDate_debut().getTime()));
                    statement.setTimestamp(4, new Timestamp(event.getDate_fin().getTime()));
                    statement.setString(5, event.getStatus());
                    statement.setString(6, event.getImage());
                    statement.setInt(7, event.getId());

                    statement.executeUpdate();
                }
            } else {
                // Autre erreur SQL, la propager
                throw e;
            }
        }
    }

    /**
     * Mettre à jour le statut d'un événement
     * @param eventId L'ID de l'événement
     * @param status Le nouveau statut
     * @throws SQLException En cas d'erreur SQL
     * @throws IllegalArgumentException Si l'événement n'existe pas
     */
    public void updateEventStatus(int eventId, String status) throws SQLException, IllegalArgumentException {
        // Vérifier que l'événement existe
        Event event = getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("L'événement n'existe pas");
        }

        // Vérifier que le statut est valide
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Le statut n'est pas valide");
        }

        String query = "UPDATE event SET status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, eventId);

            statement.executeUpdate();

            // Si le statut est accepté, envoyer un SMS à l'administrateur
            if (status.equals(STATUS_APPROVED)) {
                // Récupérer les informations de l'événement pour le SMS
                String eventTitle = event.getTitle();
                String userName = event.getUser().getPrenom() + " " + event.getUser().getNom();

                // Envoyer un SMS à l'administrateur
                sendEventApprovalSMS(eventTitle, userName);
            }
        }
    }

    /**
     * Vérifier si un statut est valide
     * @param status Le statut à vérifier
     * @return true si le statut est valide, false sinon
     */
    private boolean isValidStatus(String status) {
        return status != null && (
                status.equals(STATUS_PENDING) ||
                        status.equals(STATUS_APPROVED) ||
                        status.equals(STATUS_REJECTED)
        );
    }

    /**
     * Envoyer un SMS pour notifier de l'approbation d'un événement
     * @param eventTitle Le titre de l'événement
     * @param userName Le nom de l'utilisateur qui a créé l'événement
     */
    private void sendEventApprovalSMS(String eventTitle, String userName) {
        try {
            // Numéro de téléphone de l'administrateur
            String adminPhoneNumber = "+21620418907";

            // Message à envoyer
            String message = "Un nouvel événement a été approuvé: '" + eventTitle + "' créé par " + userName;

            // Envoyer le SMS
            SMSService.sendSMS(adminPhoneNumber, message);

            System.out.println("SMS envoyé à l'administrateur: " + message);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Envoyer un SMS pour notifier de la création d'un nouvel événement
     * @param event L'événement créé
     */
    private void sendNewEventSMS(Event event) {
        try {
            // Vérifier que l'événement et l'utilisateur ne sont pas null
            if (event == null || event.getUser() == null) {
                System.err.println("Impossible d'envoyer le SMS: événement ou utilisateur null");
                return;
            }

            // Numéro de téléphone de l'administrateur
            String adminPhoneNumber = "+21620418907";

            // Récupérer les informations de l'événement pour le SMS
            String eventTitle = event.getTitle();
            String userName = event.getUser().getPrenom() + " " + event.getUser().getNom();

            // Message à envoyer
            String message = "Nouvel événement en attente d'approbation: '" + eventTitle + "' créé par " + userName;

            // Envoyer le SMS
            SMSService.sendSMS(adminPhoneNumber, message);

            System.out.println("SMS envoyé à l'administrateur: " + message);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Supprimer un événement
     * @param eventId L'ID de l'événement à supprimer
     * @throws SQLException En cas d'erreur SQL
     */
    public void deleteEvent(int eventId) throws SQLException {
        String query = "DELETE FROM event WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, eventId);
            statement.executeUpdate();
        }
    }

    /**
     * Récupérer un événement par son ID
     * @param eventId L'ID de l'événement
     * @return L'événement ou null s'il n'existe pas
     * @throws SQLException En cas d'erreur SQL
     */
    public Event getEventById(int eventId) throws SQLException {
        String query = "SELECT e.*, u.id as user_id, u.nom, u.prenom, u.email FROM event e JOIN user u ON e.user_id = u.id WHERE e.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createEventFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    /**
     * Récupérer tous les événements
     * @return La liste des événements
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.*, u.id as user_id, u.nom, u.prenom, u.email FROM event e JOIN user u ON e.user_id = u.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                events.add(createEventFromResultSet(resultSet));
            }
        }

        return events;
    }

    /**
     * Récupérer les événements organisés par un utilisateur
     * @param userId L'ID de l'utilisateur
     * @return La liste des événements
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Event> getEventsByUser(int userId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.*, u.id as user_id, u.nom, u.prenom, u.email FROM event e JOIN user u ON e.user_id = u.id WHERE e.user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    events.add(createEventFromResultSet(resultSet));
                }
            }
        }

        return events;
    }

    /**
     * Créer un objet Event à partir d'un ResultSet
     * @param resultSet Le ResultSet contenant les données de l'événement
     * @return L'objet Event créé
     * @throws SQLException En cas d'erreur SQL
     */
    private Event createEventFromResultSet(ResultSet resultSet) throws SQLException {
        Event event = new Event();
        event.setId(resultSet.getInt("id"));
        event.setTitle(resultSet.getString("title"));
        event.setDescription(resultSet.getString("description"));
        event.setDate_debut(resultSet.getTimestamp("date_debut"));
        event.setDate_fin(resultSet.getTimestamp("date_fin"));
        // Le champ max_participants a été supprimé
        event.setStatus(resultSet.getString("status"));
        event.setImage(resultSet.getString("image"));

        // Récupérer le prix (avec gestion des valeurs nulles)
        try {
            event.setPrix(resultSet.getDouble("prix"));
        } catch (SQLException e) {
            // Si le champ prix n'existe pas ou est null, on met 0.0 par défaut
            event.setPrix(0.0);
        }

        // Créer l'organisateur
        User user = new User();
        user.setId(resultSet.getInt("user_id"));
        user.setNom(resultSet.getString("nom"));
        user.setPrenom(resultSet.getString("prenom"));
        user.setEmail(resultSet.getString("email"));

        event.setUser(user);

        return event;
    }
}