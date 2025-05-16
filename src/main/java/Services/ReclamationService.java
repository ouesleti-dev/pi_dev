package Services;

import Models.Reclamation;
import Models.Reponse;
import Utils.MyDb;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReclamationService implements Services.IService<Reclamation> {
    private final Connection cnx = MyDb.getInstance().getConn();

    @Override
    public void Create(Reclamation reclamation) throws Exception {
        addEntity(reclamation);
    }
    
    // Original method for backward compatibility with controllers
    public void addEntity(Reclamation reclamation) {
        String query = "INSERT INTO reclamation(idutilisateur, message, statut, typereclamation, datereclamation, titre, priorite, categorie) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reclamation.getIdUtilisateur());
            pst.setString(2, reclamation.getMessage());
            pst.setString(3, reclamation.getStatut());
            pst.setString(4, reclamation.getTypeReclamation());
            pst.setTimestamp(5, Timestamp.valueOf(reclamation.getDateReclamation() != null ?
                    reclamation.getDateReclamation() : LocalDateTime.now()));
            pst.setString(6, reclamation.getTitre());
            pst.setString(7, reclamation.getPriorite());
            pst.setString(8, reclamation.getCategorie());

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    reclamation.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @Override
    public void Update(Reclamation reclamation) throws Exception {
        updateEntity(reclamation);
    }

    // Original method for backward compatibility with controllers
    public void updateEntity(Reclamation reclamation) {
        String query = "UPDATE reclamation SET idutilisateur=?, message=?, statut=?, typereclamation=?, datereclamation=?, titre=?, priorite=?, categorie=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reclamation.getIdUtilisateur());
            pst.setString(2, reclamation.getMessage());
            pst.setString(3, reclamation.getStatut());
            pst.setString(4, reclamation.getTypeReclamation());
            pst.setTimestamp(5, Timestamp.valueOf(reclamation.getDateReclamation()));
            pst.setString(6, reclamation.getTitre());
            pst.setString(7, reclamation.getPriorite());
            pst.setString(8, reclamation.getCategorie());
            pst.setInt(9, reclamation.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    @Override
    public void Delete() throws Exception {
        System.out.println("Warning: Delete method called without Reclamation parameter. Use deleteEntity(Reclamation) instead.");
    }

    @Override
    public List<Reclamation> Display() throws Exception {
        return getAllData();
    }
    
    // Original method for backward compatibility
    public boolean deleteEntity(Reclamation reclamation) {
        String query = "DELETE FROM reclamation WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reclamation.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
        }
        return false;
    }

    // This method is used by the Display method to implement the IService interface
    public List<Reclamation> getAllData() {
        List<Reclamation> list = new ArrayList<>();
        String query = "SELECT * FROM reclamation";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Reclamation r = new Reclamation();
                r.setId(rs.getInt("id"));
                r.setIdUtilisateur(rs.getInt("idutilisateur"));
                r.setMessage(rs.getString("message"));
                r.setStatut(rs.getString("statut"));
                r.setTypeReclamation(rs.getString("typereclamation"));
                r.setDateReclamation(rs.getTimestamp("datereclamation").toLocalDateTime());
                r.setTitre(rs.getString("titre"));
                r.setPriorite(rs.getString("priorite"));
                r.setCategorie(rs.getString("categorie"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
        }
        return list;
    }

    public Reclamation findById(int id) {
        String query = "SELECT * FROM reclamation WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Reclamation r = new Reclamation();
                    r.setId(rs.getInt("id"));
                    r.setIdUtilisateur(rs.getInt("idutilisateur"));
                    r.setMessage(rs.getString("message"));
                    r.setStatut(rs.getString("statut"));
                    r.setTypeReclamation(rs.getString("typereclamation"));
                    r.setDateReclamation(rs.getTimestamp("datereclamation").toLocalDateTime());
                    r.setTitre(rs.getString("titre"));
                    r.setPriorite(rs.getString("priorite"));
                    r.setCategorie(rs.getString("categorie"));
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return null;
    }

    public void delete(Reclamation reclamation) throws SQLException {
        String query = "DELETE FROM reclamation WHERE id = ?";
        try (Connection conn = MyDb.getInstance().getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, reclamation.getId());
            pstmt.executeUpdate();
            System.out.println("Reclamation with ID " + reclamation.getId() + " deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error while deleting reclamation: " + e.getMessage());
            throw e;
        }
    }
    public List<Reclamation> getAllWithResponses() {
        List<Reclamation> reclamations = getAllData();
        ReponseService reponseService = new ReponseService();

        // Pour chaque réclamation, chercher la réponse associée
        return reclamations.stream().map(reclamation -> {
            Reponse reponse = reponseService.findByReclamationId(reclamation.getId());
            reclamation.setReponse(reponse);
            return reclamation;
        }).collect(Collectors.toList());
    }

    // Méthode pour vérifier si une réclamation a une réponse
    public boolean hasReponse(int reclamationId) {
        String query = "SELECT COUNT(*) FROM reponse WHERE idreclamation = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, reclamationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de réponse : " + e.getMessage());
        }
        return false;
    }
}
