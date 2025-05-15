package Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Classe utilitaire pour créer la table reset_codes dans la base de données
 */
public class ResetCodeMigration {

    /**
     * Crée la table reset_codes si elle n'existe pas
     * @param connection La connexion à la base de données
     * @throws SQLException En cas d'erreur SQL
     */
    public static void createResetCodesTableIfNotExists(Connection connection) throws SQLException {
        // Vérifier si la connexion est valide
        if (connection == null || connection.isClosed()) {
            throw new SQLException("La connexion à la base de données n'est pas valide");
        }

        // Créer la table reset_codes
        String createTableSQL = ""
                + "CREATE TABLE IF NOT EXISTS reset_codes ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "user_id INT NOT NULL,"
                + "code VARCHAR(6) NOT NULL,"
                + "expiration_time TIMESTAMP NOT NULL,"
                + "is_used BOOLEAN DEFAULT FALSE,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE"
                + ");";

        try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
            statement.executeUpdate();
            System.out.println("Table reset_codes créée ou déjà existante");
        }
    }
}
