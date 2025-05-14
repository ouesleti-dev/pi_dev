package Services;

import Models.Personne;
import Models.User;
import Models.UserSession;
import Utils.MyDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserService implements  IService<User>{

    Connection conn;

    public UserService(){
        this.conn = MyDb.getInstance().getConn();

    }

    @Override
    public void Create(User user) throws Exception {
        String req = "INSERT INTO user (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(req);
        stmt.setString(1, user.getNom());
        stmt.setString(2, user.getPrenom());
        stmt.setString(3, user.getEmail());
        stmt.setString(4, user.getTelephone());  // Assurez-vous que le téléphone est bien inclus
        stmt.executeUpdate();
    }

    @Override
    public void Update(User user) throws Exception {

    }

    @Override
    public List<User> Display() throws Exception {
        String req = "SELECT * FROM user";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(req);
        List<User> userList = new ArrayList<>();

        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNom(rs.getString("nom"));
            user.setPrenom(rs.getString("prenom"));
            user.setEmail(rs.getString("email"));
            user.setTelephone(rs.getString("telephone"));
            user.setVerified(rs.getBoolean("is_verified"));

            // Récupérer le rôle
            String roleStr = rs.getString("role");
            if (roleStr != null && !roleStr.isEmpty()) {
                try {
                    user.setRole(User.Role.valueOf(roleStr));
                } catch (IllegalArgumentException e) {
                    System.err.println("Rôle invalide dans la base de données: " + roleStr);
                    user.setRole(User.Role.ROLE_CLIENT); // Valeur par défaut
                }
            } else {
                user.setRole(User.Role.ROLE_CLIENT); // Valeur par défaut
            }

            // Récupérer la date de création
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                user.setCreatedAt(createdAt);
            }

            userList.add(user);
        }

        return userList;
    }

    @Override
    public void Delete() throws Exception {
        // Méthode générique non utilisée
    }

    public List<User> getClients() throws Exception {
        String req = "SELECT * FROM user WHERE role = 'ROLE_CLIENT'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(req);
        List<User> clientList = new ArrayList<>();

        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNom(rs.getString("nom"));
            user.setPrenom(rs.getString("prenom"));
            user.setEmail(rs.getString("email"));
            user.setTelephone(rs.getString("telephone"));
            user.setVerified(rs.getBoolean("is_verified"));
            user.setRole(User.Role.ROLE_CLIENT);

            // Récupérer la date de création
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                user.setCreatedAt(createdAt);
            }

            clientList.add(user);
        }

        return clientList;
    }

    /**
     * Supprime un utilisateur par son ID
     * @param userId ID de l'utilisateur à supprimer
     * @throws Exception si une erreur survient lors de la suppression
     */
    public void deleteById(int userId) throws Exception {
        String req = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new Exception("Aucun utilisateur trouvé avec l'ID: " + userId);
            }
        }
    }


}
