package Services;

import Models.User;
import Models.UserSession;
import Services.RoleService;
import Utils.MyDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private Connection conn;

    public AuthService() {
        conn = MyDb.getInstance().getConn();
    }


    /**
     * Obtenir l'instance unique du service
     * @return L'instance du service
     */

    private static AuthService instance;

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }


    public User login(String email, String password) throws Exception {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Vérifier si l'utilisateur est banni
                    boolean isBanned = false;
                    try {
                        isBanned = rs.getBoolean("is_banned");
                    } catch (SQLException e) {
                        // La colonne n'existe peut-être pas encore, ignorer l'erreur
                        System.out.println("La colonne is_banned n'existe pas encore: " + e.getMessage());
                    }

                    if (isBanned) {
                        throw new Exception("Votre compte a été bloqué suite à une réclamation. Veuillez contacter l'administrateur pour plus d'informations");
                    }

                    String hashedPassword = rs.getString("password");

                    // Vérification du mot de passe avec BCrypt
                    // Si le mot de passe n'est pas au format BCrypt, on fait une vérification directe temporairement
                    boolean passwordMatches = false;
                    if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$")) {
                        // C'est un hash BCrypt, utiliser checkPassword
                        passwordMatches = checkPassword(password, hashedPassword);
                    } else {
                        // Temporairement, pour la compatibilité avec les anciens mots de passe non hachés
                        passwordMatches = password.equals(hashedPassword);
                    }

                    if (passwordMatches) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setEmail(rs.getString("email"));
                        user.setNom(rs.getString("nom"));
                        user.setPrenom(rs.getString("prenom"));
                        user.setTelephone(rs.getString("telephone"));
                        user.setRole(User.Role.valueOf(rs.getString("role")));
                        user.setVerified(rs.getBoolean("is_verified"));

                        // Récupérer le statut de bannissement
                        try {
                            user.setBanned(isBanned);
                        } catch (Exception e) {
                            // Ignorer si la méthode n'existe pas encore
                            System.out.println("Impossible de définir le statut de bannissement: " + e.getMessage());
                        }

                        // Stocker l'utilisateur dans la session
                        UserSession.getInstance().setCurrentUser(user);
                        System.out.println("Utilisateur stocké dans la session: " + user.getEmail());

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

        // Hacher le mot de passe avec BCrypt
        String hashedPassword = hashPassword(user.getPassword());

        // Essayer d'abord avec la colonne is_banned
        String query = "INSERT INTO user (nom, prenom, email, password, telephone, role, is_verified, is_banned) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashedPassword);
            ps.setString(5, user.getTelephone());
            ps.setString(6, user.getRole().toString());
            ps.setBoolean(7, user.isVerified());
            ps.setBoolean(8, false); // Par défaut, l'utilisateur n'est pas banni

            try {
                ps.executeUpdate();
            } catch (SQLException e) {
                // Si la colonne is_banned n'existe pas encore, utiliser l'ancienne requête
                if (e.getMessage().contains("Unknown column 'is_banned'")) {
                    query = "INSERT INTO user (nom, prenom, email, password, telephone, role, is_verified) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps2 = conn.prepareStatement(query)) {
                        ps2.setString(1, user.getNom());
                        ps2.setString(2, user.getPrenom());
                        ps2.setString(3, user.getEmail());
                        ps2.setString(4, hashedPassword);
                        ps2.setString(5, user.getTelephone());
                        ps2.setString(6, user.getRole().toString());
                        ps2.setBoolean(7, user.isVerified());
                        ps2.executeUpdate();
                    }
                } else {
                    throw e; // Relancer l'exception si c'est une autre erreur
                }
            }
        }

        // Envoyer l'email de bienvenue
        try {
            EmailService emailService = new EmailService();
            emailService.sendWelcomeEmail(user.getEmail(), user.getPrenom() + " " + user.getNom());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email de bienvenue: " + e.getMessage());
            // Ne pas bloquer l'inscription si l'envoi d'email échoue
        }
    }

    /**
     * Vérifie si un email existe déjà dans la base de données
     * @param email L'email à vérifier
     * @return true si l'email existe, false sinon
     * @throws SQLException En cas d'erreur SQL
     */
    public boolean emailExists(String email) throws SQLException {
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

    public void logout() {
        UserSession.getInstance().logout();
    }

    /**
     * Obtenir l'utilisateur actuellement connecté
     * @return L'utilisateur connecté ou null si aucun utilisateur n'est connecté
     */
    public User getCurrentUser() {
        return UserSession.getInstance().getCurrentUser();
    }

    /**
     * Hache un mot de passe en utilisant BCrypt
     * @param password Le mot de passe en clair
     * @return Le mot de passe haché
     */
    public String hashPassword(String password) {
        // Générer un sel aléatoire et hacher le mot de passe avec BCrypt
        // Le facteur de coût 12 est un bon compromis entre sécurité et performance
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Vérifie si un mot de passe en clair correspond à un hash BCrypt
     * @param plainPassword Le mot de passe en clair à vérifier
     * @param hashedPassword Le hash BCrypt stocké
     * @return true si le mot de passe correspond, false sinon
     */
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}