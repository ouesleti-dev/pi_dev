package Services;

import Models.User;
import Utils.MyDb;
// import org.mindrot.jbcrypt.BCrypt; // Temporairement désactivé

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    private Connection conn;

    public AuthService() {
        this.conn = MyDb.getInstance().getConn();
    }

    public User login(String email, String password) throws Exception {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");

                    // Vérification temporaire du mot de passe (sans BCrypt)
                    if (password.equals(hashedPassword)) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setEmail(rs.getString("email"));
                        user.setNom(rs.getString("nom"));
                        user.setPrenom(rs.getString("prenom"));
                        user.setRole(User.Role.valueOf(rs.getString("role")));
                        user.setVerified(rs.getBoolean("is_verified"));
                        return user;
                    }
                }
            }
        }
        throw new Exception("Email ou mot de passe incorrect");
    }

    public void register(User user) throws Exception {
        // Vérifier si l'email existe déjà
        if (emailExists(user.getEmail())) {
            throw new Exception("Cet email est déjà utilisé");
        }

        // Stockage temporaire du mot de passe en clair (sans BCrypt)
        String hashedPassword = user.getPassword();

        String query = "INSERT INTO user (nom, prenom, email, password, telephone, role, is_verified) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashedPassword);
            ps.setString(5, user.getTelephone());
            ps.setString(6, user.getRole().toString());
            ps.setBoolean(7, user.isVerified());

            ps.executeUpdate();
        }
    }

    private boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}