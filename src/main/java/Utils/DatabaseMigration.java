package Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Classe utilitaire pour effectuer des migrations de base de données
 */
public class DatabaseMigration {

    /**
     * Ajoute la colonne prix à la table event
     * @param connection La connexion à la base de données
     * @throws SQLException En cas d'erreur SQL
     */
    public static void addPrixColumnToEventTable(Connection connection) throws SQLException {
        // Vérifier si la connexion est valide
        if (connection == null || connection.isClosed()) {
            throw new SQLException("La connexion à la base de données n'est pas valide");
        }

        // Ajouter la colonne prix à la table event
        String addColumnSQL = "ALTER TABLE event ADD COLUMN prix DOUBLE DEFAULT 0.0";
        try (PreparedStatement statement = connection.prepareStatement(addColumnSQL)) {
            statement.executeUpdate();
            System.out.println("Colonne 'prix' ajoutée à la table 'event'");
        } catch (SQLException e) {
            // Si la colonne existe déjà, ignorer l'erreur
            if (e.getMessage().contains("Duplicate column name") || e.getMessage().contains("column already exists")) {
                System.out.println("La colonne 'prix' existe déjà dans la table 'event'");
            } else {
                throw e;
            }
        }

        // Mettre à jour les événements existants avec un prix par défaut
        String updateSQL = "UPDATE event SET prix = 0.0 WHERE prix IS NULL";
        try (PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated + " événements mis à jour avec un prix par défaut");
        }
    }
}
