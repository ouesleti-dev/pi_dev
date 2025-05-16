
package Services;

import Models.Post;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostService implements IService<Post> {
    Connection conn;

    public PostService() {
        this.conn = MyDb.getInstance().getConn();
    }

    @Override
    public void Create(Post post) throws Exception {
        // Call the lowercase method for implementation
        create(post);
    }
    
    // Original method for backward compatibility with controllers
    public void create(Post post) {
        try {
            String req = "INSERT INTO Post (contenu, auteur, dateCreation) VALUES ('"
                    + post.getContenu() + "', '"
                    + post.getAuteur() + "', '"
                    + post.getDateCreation() + "')";

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(req);
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Post déjà existant : insertion ignorée.");
        } catch (SQLException e) {
            e.printStackTrace(); // pour les autres erreurs
        }
    }


    @Override
    public void Update(Post post) throws Exception {
        // Call the lowercase method for implementation
        update(post);
    }
    
    // Original method for backward compatibility with controllers
    public void update(Post post) throws Exception {
        String req = "UPDATE post SET contenu = ?, Auteur = ?, dateCreation = ? WHERE Idpost = ?";
        PreparedStatement stmt = conn.prepareStatement(req);
        stmt.setString(1, post.getContenu());
        stmt.setString(2, post.getAuteur());
        stmt.setDate(3, post.getDateCreation());
        stmt.setInt(4, post.getIdPost());
        stmt.executeUpdate();
    }

    @Override
    public List<Post> Display() throws Exception {
        // Call the lowercase method for implementation
        return display();
    }
    
    // Original method for backward compatibility with controllers
    public List<Post> display() throws Exception {
        String req = "SELECT * FROM Post";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(req);
        List<Post> listPost = new ArrayList<>();

        while (rs.next()) {
            Post p1 = new Post(); // Nécessite un constructeur par défaut

            p1.setIdPost(rs.getInt("IdPost"));
            p1.setContenu(rs.getString("contenu"));
            p1.setAuteur(rs.getString("auteur"));
            p1.setDateCreation(rs.getDate("dateCreation")); // Assure-toi que c'est java.sql.Date

            listPost.add(p1);
        }

        return listPost;
    }

    @Override
    public void Delete() throws Exception {
        // Since we can't take a Post parameter directly, we'll need to modify our approach
        // This method is for compliance with the IService interface
        System.out.println("Warning: Delete method called without Post parameter. Use delete(Post post) instead.");
    }
    
    // This is the original method that's used by your controllers
    public void delete(Post post) throws Exception {
        String req ="delete from post where Idpost = ?";
        PreparedStatement stmt = conn.prepareStatement(req);
        stmt.setInt(1, post.getIdPost());
        stmt.executeUpdate();
    }

    public String auteurLePlusActif() throws SQLException {
        String req = "SELECT auteur, COUNT(*) AS nb_posts FROM Post GROUP BY auteur ORDER BY nb_posts DESC LIMIT 1";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(req);
        if (rs.next()) {
            String auteur = rs.getString("auteur");
            int nbPosts = rs.getInt("nb_posts");
            return auteur + " est l'auteur le plus actif avec " + nbPosts + " posts.";
        }
        return "Aucun post trouvé.";
    }


    public List<Post> trierParDate(boolean croissant) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String order = croissant ? "ASC" : "DESC";
        String req = "SELECT * FROM post ORDER BY dateCreation " + order;
        PreparedStatement stmt = conn.prepareStatement(req);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Post post = new Post();
            post.setIdPost(rs.getInt("Idpost"));
            post.setAuteur(rs.getString("Auteur"));
            post.setContenu(rs.getString("contenu"));
            post.setDateCreation(rs.getDate("dateCreation"));
            posts.add(post);
        }
        return posts;
    }

    public List<Post> rechercher(String auteur) throws Exception {
        List<Post> resultats = new ArrayList<>();

        String req = "SELECT * FROM post WHERE auteur = ?";
        PreparedStatement stmt = conn.prepareStatement(req);
        stmt.setString(1, auteur);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Post post = new Post();
            post.setIdPost(rs.getInt("IdPost"));
            post.setContenu(rs.getString("contenu"));
            post.setAuteur(rs.getString("auteur"));
            post.setDateCreation(rs.getDate("dateCreation"));

            resultats.add(post);
        }

        return resultats;
    }
}




