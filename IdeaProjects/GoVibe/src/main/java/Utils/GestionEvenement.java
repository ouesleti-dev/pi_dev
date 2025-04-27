package Utils;

import Models.Evenement;
import Models.Organisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestionEvenement {

    private Connection connection;

    public GestionEvenement() {
        this.connection = MyDb.getInstance().getConn();  // Récupère la connexion depuis MyDb
    }

    // Ajouter un organisateur
    public void ajouterOrganisateur(Organisateur organisateur) {
        String query = "INSERT INTO organisateur (nom, email, telephone) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, organisateur.getNom());
            statement.setString(2, organisateur.getEmail());
            statement.setString(3, organisateur.getTelephone());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ajouter un événement
    public void ajouterEvenement(Evenement evenement) {
        String query = "INSERT INTO evenement (titre, description, date, capacite, prix, statut, organisateur_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, evenement.getTitre());
            statement.setString(2, evenement.getDescription());
            statement.setDate(3, new java.sql.Date(evenement.getDate().getTime()));
            statement.setInt(4, evenement.getCapacite());
            statement.setDouble(5, evenement.getPrix());
            statement.setString(6, evenement.getStatut());
            statement.setInt(7, evenement.getOrganisateurId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lister tous les événements
    public List<Evenement> listerEvenements() {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Evenement evenement = new Evenement(
                        resultSet.getInt("id"),
                        resultSet.getString("titre"),
                        resultSet.getString("description"),
                        resultSet.getDate("date"),
                        resultSet.getInt("capacite"),
                        resultSet.getDouble("prix"),
                        resultSet.getString("statut"),
                        resultSet.getInt("organisateur_id")
                );
                evenements.add(evenement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    // Rechercher un événement par ID
    public Evenement chercherEvenementParId(int id) {
        Evenement evenement = null;
        String query = "SELECT * FROM evenement WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    evenement = new Evenement(
                            resultSet.getInt("id"),
                            resultSet.getString("titre"),
                            resultSet.getString("description"),
                            resultSet.getDate("date"),
                            resultSet.getInt("capacite"),
                            resultSet.getDouble("prix"),
                            resultSet.getString("statut"),
                            resultSet.getInt("organisateur_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenement;
    }

    // Supprimer un événement
    public void supprimerEvenement(int id) {
        String query = "DELETE FROM evenement WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lister les événements d'un organisateur
    public List<Evenement> listerEvenementsParOrganisateur(int organisateurId) {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement WHERE organisateur_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, organisateurId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Evenement evenement = new Evenement(
                            resultSet.getInt("id"),
                            resultSet.getString("titre"),
                            resultSet.getString("description"),
                            resultSet.getDate("date"),
                            resultSet.getInt("capacite"),
                            resultSet.getDouble("prix"),
                            resultSet.getString("statut"),
                            resultSet.getInt("organisateur_id")
                    );
                    evenements.add(evenement);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    // Mettre à jour un événement
    public void mettreAJourEvenement(Evenement evenement) {
        String query = "UPDATE evenement SET titre = ?, description = ?, date = ?, capacite = ?, prix = ?, statut = ?, organisateur_id = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, evenement.getTitre());
            statement.setString(2, evenement.getDescription());
            statement.setDate(3, new java.sql.Date(evenement.getDate().getTime()));
            statement.setInt(4, evenement.getCapacite());
            statement.setDouble(5, evenement.getPrix());
            statement.setString(6, evenement.getStatut());
            statement.setInt(7, evenement.getOrganisateurId());
            statement.setInt(8, evenement.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lister tous les organisateurs
    public List<Organisateur> listerOrganisateurs() {
        List<Organisateur> organisateurs = new ArrayList<>();
        String query = "SELECT * FROM organisateur";  // La requête SQL pour lister tous les organisateurs

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Organisateur organisateur = new Organisateur(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("email"),
                        resultSet.getString("telephone")
                );
                organisateurs.add(organisateur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return organisateurs;
    }

    public Organisateur chercherOrganisateurParId(int id) {
        Organisateur organisateur = null;
        String query = "SELECT * FROM organisateur WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    organisateur = new Organisateur(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("email"),
                            resultSet.getString("telephone")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return organisateur;
    }
}
