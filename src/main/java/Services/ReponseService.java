package Services;

import Models.Reponse;
import Interfaces.IService;
import Utils.MyDb;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReponseService implements IService<Reponse> {
    private final Connection cnx;

    public ReponseService() {
        cnx = MyDb.getInstance().getConn();
    }

    @Override
    public void addEntity(Reponse reponse) {
        String query = "INSERT INTO reponse (idreclamation, idutilisateur, contenu, datereponse) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reponse.getIdReclamation());
            pst.setInt(2, reponse.getIdUtilisateur());
            pst.setString(3, reponse.getContenu());
            pst.setTimestamp(4, Timestamp.valueOf(reponse.getDateReponse() != null ? reponse.getDateReponse() : LocalDateTime.now()));

            pst.executeUpdate();

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reponse.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réponse : " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(Reponse reponse) {
        String query = "UPDATE reponse SET contenu = ?, datereponse = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, reponse.getContenu());
            pst.setTimestamp(2, Timestamp.valueOf(reponse.getDateReponse()));
            pst.setInt(3, reponse.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la réponse : " + e.getMessage());
        }
    }

    @Override
    public boolean deleteEntity(Reponse reponse) {
        String query = "DELETE FROM reponse WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reponse.getId());
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réponse : " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Reponse> getAllData() {
        List<Reponse> list = new ArrayList<>();
        String query = "SELECT * FROM reponse";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Reponse r = new Reponse();
                r.setId(rs.getInt("id"));
                r.setIdReclamation(rs.getInt("idreclamation"));
                r.setIdUtilisateur(rs.getInt("idutilisateur"));
                r.setContenu(rs.getString("contenu"));
                r.setDateReponse(rs.getTimestamp("datereponse").toLocalDateTime());

                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réponses : " + e.getMessage());
        }

        return list;
    }

    public List<Reponse> getReponsesByUtilisateur(int idUtilisateur) {
        List<Reponse> list = new ArrayList<>();
        String query = "SELECT r.*, rec.titre as reclamation_titre, rec.statut as reclamation_statut " +
                "FROM reponse r " +
                "JOIN reclamation rec ON r.idreclamation = rec.id " +
                "WHERE r.idutilisateur = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, idUtilisateur);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Reponse r = new Reponse();
                    r.setId(rs.getInt("id"));
                    r.setIdReclamation(rs.getInt("idreclamation"));
                    r.setIdUtilisateur(rs.getInt("idutilisateur"));
                    r.setContenu(rs.getString("contenu"));
                    r.setDateReponse(rs.getTimestamp("datereponse").toLocalDateTime());
                    r.setReclamationTitre(rs.getString("reclamationtitre"));

                    list.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réponses: " + e.getMessage());
        }

        return list;
    }

    public Reponse findByReclamationId(int reclamationId) {
        String query = "SELECT * FROM reponse WHERE idreclamation = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reclamationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Reponse r = new Reponse();
                    r.setId(rs.getInt("id"));
                    r.setIdReclamation(rs.getInt("idreclamation"));
                    r.setIdUtilisateur(rs.getInt("idutilisateur"));
                    r.setContenu(rs.getString("contenu"));
                    r.setDateReponse(rs.getTimestamp("datereponse").toLocalDateTime());
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par réclamation : " + e.getMessage());
        }
        return null;
    }
}