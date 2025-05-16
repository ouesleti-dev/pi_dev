package Services;

import Models.Event;
import Models.Excursion;
import Models.User;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les excursions
 */
public class ExcursionService {
    private static ExcursionService instance;
    private final Connection connection;
    private final EventService eventService;
    private final UserService userService;

    /**
     * Constructeur privé pour le pattern Singleton
     */
    private ExcursionService() {
        this.connection = MyDb.getInstance().getConn();
        this.eventService = EventService.getInstance();
        this.userService = new UserService();
    }

    /**
     * Obtenir l'instance unique du service
     * @return L'instance du service
     */
    public static ExcursionService getInstance() {
        if (instance == null) {
            instance = new ExcursionService();
        }
        return instance;
    }

    /**
     * Ajouter une nouvelle excursion
     * @param excursion L'excursion à ajouter
     * @return L'excursion ajoutée avec son ID généré
     * @throws SQLException En cas d'erreur SQL
     */
    public Excursion addExcursion(Excursion excursion) throws SQLException {
        String query = "INSERT INTO excursion (id_event, id_user, titre, destination, prix, date, duree, transport) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, excursion.getId_event());
            statement.setInt(2, excursion.getId_user());
            statement.setString(3, excursion.getTitre());
            statement.setString(4, excursion.getDestination());
            statement.setDouble(5, excursion.getPrix());
            statement.setDate(6, new java.sql.Date(excursion.getDate().getTime()));
            statement.setInt(7, excursion.getDuree());
            statement.setString(8, excursion.getTransport());

            statement.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    excursion.setId_excursion(generatedKeys.getInt(1));
                }
            }
        }
        return excursion;
    }

    /**
     * Mettre à jour une excursion existante
     * @param excursion L'excursion à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     * @throws SQLException En cas d'erreur SQL
     */
    public boolean updateExcursion(Excursion excursion) throws SQLException {
        String query = "UPDATE excursion SET id_event = ?, id_user = ?, titre = ?, destination = ?, prix = ?, date = ?, duree = ?, transport = ? WHERE id_excursion = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, excursion.getId_event());
            statement.setInt(2, excursion.getId_user());
            statement.setString(3, excursion.getTitre());
            statement.setString(4, excursion.getDestination());
            statement.setDouble(5, excursion.getPrix());
            statement.setDate(6, new java.sql.Date(excursion.getDate().getTime()));
            statement.setInt(7, excursion.getDuree());
            statement.setString(8, excursion.getTransport());
            statement.setInt(9, excursion.getId_excursion());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Supprimer une excursion
     * @param excursionId L'ID de l'excursion à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws SQLException En cas d'erreur SQL
     */
    public boolean deleteExcursion(int excursionId) throws SQLException {
        String query = "DELETE FROM excursion WHERE id_excursion = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, excursionId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Récupérer une excursion par son ID
     * @param excursionId L'ID de l'excursion
     * @return L'excursion ou null si elle n'existe pas
     * @throws SQLException En cas d'erreur SQL
     */
    public Excursion getExcursionById(int excursionId) throws SQLException {
        String query = "SELECT * FROM excursion WHERE id_excursion = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, excursionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createExcursionFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    /**
     * Récupérer toutes les excursions
     * @return La liste de toutes les excursions
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Excursion> getAllExcursions() throws SQLException {
        List<Excursion> excursions = new ArrayList<>();
        String query = "SELECT * FROM excursion";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                excursions.add(createExcursionFromResultSet(resultSet));
            }
        }
        return excursions;
    }

    /**
     * Récupérer toutes les excursions pour un événement spécifique
     * @param eventId L'ID de l'événement
     * @return La liste des excursions pour cet événement
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Excursion> getExcursionsByEventId(int eventId) throws SQLException {
        List<Excursion> excursions = new ArrayList<>();
        String query = "SELECT * FROM excursion WHERE id_event = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    excursions.add(createExcursionFromResultSet(resultSet));
                }
            }
        }
        return excursions;
    }

    /**
     * Récupérer toutes les excursions créées par un utilisateur spécifique
     * @param userId L'ID de l'utilisateur
     * @return La liste des excursions créées par cet utilisateur
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Excursion> getExcursionsByUserId(int userId) throws SQLException {
        List<Excursion> excursions = new ArrayList<>();
        String query = "SELECT * FROM excursion WHERE id_user = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    excursions.add(createExcursionFromResultSet(resultSet));
                }
            }
        }
        return excursions;
    }

    /**
     * Créer un objet Excursion à partir d'un ResultSet
     * @param resultSet Le ResultSet contenant les données de l'excursion
     * @return L'objet Excursion créé
     * @throws SQLException En cas d'erreur SQL
     */
    private Excursion createExcursionFromResultSet(ResultSet resultSet) throws SQLException {
        int id_excursion = resultSet.getInt("id_excursion");
        int id_event = resultSet.getInt("id_event");
        int id_user = resultSet.getInt("id_user");
        String titre = resultSet.getString("titre");
        String destination = resultSet.getString("destination");
        double prix = resultSet.getDouble("prix");
        Date date = resultSet.getDate("date");
        int duree = resultSet.getInt("duree");
        String transport = resultSet.getString("transport");

        Excursion excursion = new Excursion(id_excursion, id_event, id_user, titre, destination, prix, date, duree, transport);

        // Charger l'événement associé
        try {
            Event event = eventService.getEventById(id_event);
            excursion.setEvent(event);
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement de l'événement associé à l'excursion: " + e.getMessage());
        }

        // Charger l'utilisateur associé
        try {
            User user = userService.findUserById(id_user);
            excursion.setUser(user);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'utilisateur associé à l'excursion: " + e.getMessage());
        }

        return excursion;
    }

    /**
     * Vérifier si une excursion existe pour un événement donné
     * @param eventId L'ID de l'événement
     * @return true si au moins une excursion existe pour cet événement, false sinon
     * @throws SQLException En cas d'erreur SQL
     */
    public boolean hasExcursionsForEvent(int eventId) throws SQLException {
        String query = "SELECT COUNT(*) FROM excursion WHERE id_event = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}