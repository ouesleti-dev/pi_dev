package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDb {
    private String url = "jdbc:mysql://localhost:3306/govibe";
    private String user = "root";
    private String pwd = "";
    private Connection conn;
    private static MyDb instance;

    public static MyDb getInstance() {
        if (instance == null) {
            instance = new MyDb();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }

    private MyDb() {
        try {
            this.conn = DriverManager.getConnection(url, user, pwd);
            System.out.println("Connexion à la base de données établie !");

            // Vérifier et créer la table panier si elle n'existe pas
            createPanierTableIfNotExists();

            // Appliquer les migrations de base de données
            applyDatabaseMigrations();

        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données: " + e.getMessage());
        }
    }

    /**
     * Applique toutes les migrations de base de données nécessaires
     */
    private void applyDatabaseMigrations() {
        try {
            // Ajouter la colonne is_banned à la table user
            DatabaseMigration.addIsBannedColumnToUserTable(conn);

            // Ajouter la colonne prix à la table event
            DatabaseMigration.addPrixColumnToEventTable(conn);

            // Créer la table reset_codes pour la fonctionnalité de mot de passe oublié
            ResetCodeMigration.createResetCodesTableIfNotExists(conn);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'application des migrations: " + e.getMessage());
        }
    }

    private void createPanierTableIfNotExists() {
        String createTableSQL = ""
            + "CREATE TABLE IF NOT EXISTS panier ("
            + "id_panier INT AUTO_INCREMENT PRIMARY KEY,"
            + "id_events INT NOT NULL,"
            + "prix INT NOT NULL,"
            + "quantite INT NOT NULL,"
            + "prix_total INT NOT NULL,"
            + "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "statut VARCHAR(50) NOT NULL DEFAULT 'ABONDONNE'"
            + ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table panier vérifiée/créée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création de la table panier: " + e.getMessage());
        }
    }
}
