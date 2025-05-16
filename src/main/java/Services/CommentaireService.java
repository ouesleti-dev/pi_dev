package Services;

import Models.Commentaire;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService implements IService<Commentaire> {

    private final Connection conn;
    private final BadWordsService badWordsService;

    public CommentaireService() {
        this.conn = MyDb.getInstance().getConn();
        this.badWordsService = new BadWordsService();
    }

    @Override
    public void Create(Commentaire commentaire) throws Exception {
        // Call the lowercase method for implementation
        create(commentaire);
    }
    
    // Original method for backward compatibility with controllers
    public void create(Commentaire commentaire) {
        try {
            // Filtrer les mots inappropriés dans le contenu
            String filteredContenu = badWordsService.filterBadWords(commentaire.getContenu());
            commentaire.setContenu(filteredContenu);

            String req = "INSERT INTO Commentaire (contenu, auteur, dateCommentaire, idPost) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(req);
            stmt.setString(1, commentaire.getContenu());
            stmt.setString(2, commentaire.getAuteur());
            stmt.setDate(3, commentaire.getDateCommentaire());
            stmt.setInt(4, commentaire.getIdPost());
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Commentaire déjà existant : insertion ignorée.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Update(Commentaire commentaire) throws Exception {
        // Call the lowercase method for implementation
        update(commentaire);
    }
    
    // Original method for backward compatibility with controllers
    public void update(Commentaire commentaire) throws Exception {
        // Filtrer les mots inappropriés dans le contenu
        String filteredContenu = badWordsService.filterBadWords(commentaire.getContenu());
        commentaire.setContenu(filteredContenu);

        String req = "UPDATE Commentaire SET contenu = ?, auteur = ?, dateCommentaire = ?, idPost = ? WHERE idCommentaire = ?";
        PreparedStatement stmt = conn.prepareStatement(req);
        stmt.setString(1, commentaire.getContenu());
        stmt.setString(2, commentaire.getAuteur());
        stmt.setDate(3, commentaire.getDateCommentaire());
        stmt.setInt(4, commentaire.getIdPost());
        stmt.setInt(5, commentaire.getIdCommentaire());
        stmt.executeUpdate();
    }

    @Override
    public List<Commentaire> Display() throws Exception {
        // Call the lowercase method for implementation
        return display();
    }
    
    // Original method for backward compatibility with controllers
    public List<Commentaire> display() throws Exception {
        String req = "SELECT * FROM Commentaire";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(req);
        List<Commentaire> listCommentaires = new ArrayList<>();

        while (rs.next()) {
            Commentaire c = new Commentaire();
            c.setIdCommentaire(rs.getInt("idCommentaire"));
            c.setContenu(rs.getString("contenu"));
            c.setAuteur(rs.getString("auteur"));
            c.setDateCommentaire(rs.getDate("dateCommentaire"));
            c.setIdPost(rs.getInt("idPost"));
            listCommentaires.add(c);
        }

        return listCommentaires;
    }

    @Override
    public void Delete() throws Exception {
        // This method is for compliance with the IService interface
        System.out.println("Warning: Delete method called without Commentaire parameter. Use delete(Commentaire commentaire) instead.");
    }

    // This is the original method that should be used by controllers
    public void delete(Commentaire commentaire) throws Exception {
        String req = "DELETE FROM Commentaire WHERE idCommentaire = ?";
        PreparedStatement stmt = conn.prepareStatement(req);
        stmt.setInt(1, commentaire.getIdCommentaire());
        stmt.executeUpdate();
    }
}