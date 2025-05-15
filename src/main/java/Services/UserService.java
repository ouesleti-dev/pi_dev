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

    /**
     * Trouve un utilisateur par son email
     * @param email L'email de l'utilisateur
     * @return L'utilisateur trouvé ou null s'il n'existe pas
     */
    public User findUserByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

                    // Récupérer le statut de bannissement
                    try {
                        boolean isBanned = rs.getBoolean("is_banned");
                        user.setBanned(isBanned);
                    } catch (SQLException e) {
                        // La colonne n'existe peut-être pas encore, ignorer l'erreur
                        user.setBanned(false); // Par défaut, l'utilisateur n'est pas banni
                    }

                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur par email: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Trouve un utilisateur par son ID
     * @param id L'ID de l'utilisateur
     * @return L'utilisateur trouvé ou null s'il n'existe pas
     */
    public User findUserById(int id) {
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

                    // Récupérer le statut de bannissement
                    try {
                        boolean isBanned = rs.getBoolean("is_banned");
                        user.setBanned(isBanned);
                    } catch (SQLException e) {
                        // La colonne n'existe peut-être pas encore, ignorer l'erreur
                        user.setBanned(false); // Par défaut, l'utilisateur n'est pas banni
                    }

                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur par ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Met à jour le mot de passe d'un utilisateur
     * @param user L'utilisateur avec le nouveau mot de passe
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateUserPassword(User user) {
        String query = "UPDATE user SET password = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, user.getPassword());
            ps.setInt(2, user.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        // Cette méthode est héritée de l'interface mais nous utilisons updateUser à la place
    }

    /**
     * Met à jour les informations d'un utilisateur dans la base de données
     * @param user L'utilisateur avec les informations mises à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateUser(User user) {
        try {
            // Afficher les informations de l'utilisateur dans la console pour déboguer
            System.out.println("Mise à jour de l'utilisateur dans la base de données:");
            System.out.println("ID: " + user.getId());
            System.out.println("Nom: " + user.getNom());
            System.out.println("Prénom: " + user.getPrenom());
            System.out.println("Téléphone: " + user.getTelephone());

            // Ne pas inclure la colonne adresse qui n'existe pas dans la base de données
            // Ne pas mettre à jour l'email car il est en lecture seule
            String req = "UPDATE user SET nom = ?, prenom = ?, telephone = ?";

            // Vérifier si le mot de passe doit être mis à jour
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                req += ", password = ?";
                System.out.println("Le mot de passe sera également mis à jour");
            }

            req += " WHERE id = ?";
            System.out.println("Requête SQL: " + req);

            PreparedStatement ps = conn.prepareStatement(req);
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getTelephone());
            System.out.println("Paramètres de la requête: nom=" + user.getNom() + ", prenom=" + user.getPrenom() + ", telephone=" + user.getTelephone());

            int paramIndex = 4;

            // Ajouter le mot de passe s'il doit être mis à jour
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                ps.setString(paramIndex++, user.getPassword());
            }

            ps.setInt(paramIndex, user.getId());
            System.out.println("ID de l'utilisateur à mettre à jour: " + user.getId());

            int rowsAffected = ps.executeUpdate();
            System.out.println("Nombre de lignes affectées par la mise à jour: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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

            // Récupérer le statut de bannissement
            try {
                boolean isBanned = rs.getBoolean("is_banned");
                user.setBanned(isBanned);
            } catch (SQLException e) {
                // La colonne n'existe peut-être pas encore, ignorer l'erreur
                user.setBanned(false); // Par défaut, l'utilisateur n'est pas banni
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

            // Récupérer le statut de bannissement
            try {
                boolean isBanned = rs.getBoolean("is_banned");
                user.setBanned(isBanned);
            } catch (SQLException e) {
                // La colonne n'existe peut-être pas encore, ignorer l'erreur
                user.setBanned(false); // Par défaut, l'utilisateur n'est pas banni
            }

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

    /**
     * Bannir un utilisateur par son ID
     * @param userId ID de l'utilisateur à bannir
     * @throws Exception si une erreur survient lors du bannissement
     */
    public void banUser(int userId) throws Exception {
        // Vérifier d'abord si la colonne is_banned existe
        try {
            String req = "UPDATE user SET is_banned = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(req)) {
                ps.setBoolean(1, true);
                ps.setInt(2, userId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    throw new Exception("Aucun utilisateur trouvé avec l'ID: " + userId);
                }

                System.out.println("Utilisateur avec ID " + userId + " a été banni avec succès");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Unknown column 'is_banned'")) {
                // La colonne n'existe pas encore, essayer de l'ajouter
                try {
                    Utils.DatabaseMigration.addIsBannedColumnToUserTable(conn);
                    // Réessayer le bannissement après avoir ajouté la colonne
                    banUser(userId);
                } catch (SQLException ex) {
                    throw new Exception("Impossible de bannir l'utilisateur: " + ex.getMessage());
                }
            } else {
                throw new Exception("Erreur lors du bannissement de l'utilisateur: " + e.getMessage());
            }
        }
    }

    /**
     * Débannir un utilisateur par son ID
     * @param userId ID de l'utilisateur à débannir
     * @throws Exception si une erreur survient lors du débannissement
     */
    public void unbanUser(int userId) throws Exception {
        try {
            String req = "UPDATE user SET is_banned = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(req)) {
                ps.setBoolean(1, false);
                ps.setInt(2, userId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    throw new Exception("Aucun utilisateur trouvé avec l'ID: " + userId);
                }

                System.out.println("Utilisateur avec ID " + userId + " a été débanni avec succès");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Unknown column 'is_banned'")) {
                // La colonne n'existe pas encore, essayer de l'ajouter
                try {
                    Utils.DatabaseMigration.addIsBannedColumnToUserTable(conn);
                    // Réessayer le débannissement après avoir ajouté la colonne
                    unbanUser(userId);
                } catch (SQLException ex) {
                    throw new Exception("Impossible de débannir l'utilisateur: " + ex.getMessage());
                }
            } else {
                throw new Exception("Erreur lors du débannissement de l'utilisateur: " + e.getMessage());
            }
        }
    }

    /**
     * Vérifie si un utilisateur est banni
     * @param userId ID de l'utilisateur à vérifier
     * @return true si l'utilisateur est banni, false sinon
     * @throws Exception si une erreur survient lors de la vérification
     */
    public boolean isUserBanned(int userId) throws Exception {
        try {
            String req = "SELECT is_banned FROM user WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(req)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getBoolean("is_banned");
                    } else {
                        throw new Exception("Aucun utilisateur trouvé avec l'ID: " + userId);
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Unknown column 'is_banned'")) {
                // La colonne n'existe pas encore, essayer de l'ajouter
                try {
                    Utils.DatabaseMigration.addIsBannedColumnToUserTable(conn);
                    // Réessayer la vérification après avoir ajouté la colonne
                    return isUserBanned(userId);
                } catch (SQLException ex) {
                    System.err.println("Impossible de vérifier si l'utilisateur est banni: " + ex.getMessage());
                    return false; // Par défaut, considérer que l'utilisateur n'est pas banni
                }
            } else {
                System.err.println("Erreur lors de la vérification du statut de bannissement: " + e.getMessage());
                return false; // Par défaut, considérer que l'utilisateur n'est pas banni
            }
        }
    }
}
