package Services;

import Models.ResetCode;
import Models.User;
import Utils.MyDb;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Service pour gérer les codes de réinitialisation de mot de passe
 */
public class ResetCodeService {
    private Connection conn;
    private static final int CODE_EXPIRATION_MINUTES = 10;

    public ResetCodeService() {
        this.conn = MyDb.getInstance().getConn();
    }

    /**
     * Génère un code de réinitialisation à 6 chiffres pour un utilisateur
     * @param user L'utilisateur pour lequel générer un code
     * @return Le code généré
     * @throws SQLException En cas d'erreur SQL
     */
    public ResetCode generateResetCode(User user) throws SQLException {
        // Générer un code aléatoire à 6 chiffres
        String code = generateRandomCode();
        
        // Calculer la date d'expiration (10 minutes à partir de maintenant)
        Timestamp expirationTime = Timestamp.valueOf(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES));
        
        // Désactiver les anciens codes pour cet utilisateur
        disableExistingCodes(user.getId());
        
        // Créer un nouvel objet ResetCode
        ResetCode resetCode = new ResetCode(user.getId(), code, expirationTime);
        
        // Enregistrer le code dans la base de données
        saveResetCode(resetCode);
        
        return resetCode;
    }
    
    /**
     * Génère un code aléatoire à 6 chiffres
     * @return Le code généré
     */
    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Génère un nombre entre 100000 et 999999
        return String.valueOf(code);
    }
    
    /**
     * Désactive tous les codes existants pour un utilisateur
     * @param userId L'ID de l'utilisateur
     * @throws SQLException En cas d'erreur SQL
     */
    private void disableExistingCodes(int userId) throws SQLException {
        String query = "UPDATE reset_codes SET is_used = TRUE WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Enregistre un code de réinitialisation dans la base de données
     * @param resetCode Le code à enregistrer
     * @throws SQLException En cas d'erreur SQL
     */
    private void saveResetCode(ResetCode resetCode) throws SQLException {
        String query = "INSERT INTO reset_codes (user_id, code, expiration_time, is_used) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, resetCode.getUserId());
            ps.setString(2, resetCode.getCode());
            ps.setTimestamp(3, resetCode.getExpirationTime());
            ps.setBoolean(4, resetCode.isUsed());
            
            ps.executeUpdate();
            
            // Récupérer l'ID généré
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    resetCode.setId(rs.getInt(1));
                }
            }
        }
    }
    
    /**
     * Vérifie si un code est valide pour un utilisateur
     * @param userId L'ID de l'utilisateur
     * @param code Le code à vérifier
     * @return Le code de réinitialisation s'il est valide, null sinon
     * @throws SQLException En cas d'erreur SQL
     */
    public ResetCode verifyCode(int userId, String code) throws SQLException {
        String query = "SELECT * FROM reset_codes WHERE user_id = ? AND code = ? AND is_used = FALSE AND expiration_time > ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, code);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResetCode resetCode = new ResetCode();
                    resetCode.setId(rs.getInt("id"));
                    resetCode.setUserId(rs.getInt("user_id"));
                    resetCode.setCode(rs.getString("code"));
                    resetCode.setExpirationTime(rs.getTimestamp("expiration_time"));
                    resetCode.setUsed(rs.getBoolean("is_used"));
                    resetCode.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    return resetCode;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Marque un code comme utilisé
     * @param resetCode Le code à marquer comme utilisé
     * @throws SQLException En cas d'erreur SQL
     */
    public void markCodeAsUsed(ResetCode resetCode) throws SQLException {
        String query = "UPDATE reset_codes SET is_used = TRUE WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, resetCode.getId());
            ps.executeUpdate();
            
            // Mettre à jour l'objet local
            resetCode.setUsed(true);
        }
    }
    
    /**
     * Récupère le dernier code valide pour un utilisateur
     * @param userId L'ID de l'utilisateur
     * @return Le dernier code valide, null s'il n'y en a pas
     * @throws SQLException En cas d'erreur SQL
     */
    public ResetCode getLastValidCodeForUser(int userId) throws SQLException {
        String query = "SELECT * FROM reset_codes WHERE user_id = ? AND is_used = FALSE AND expiration_time > ? ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResetCode resetCode = new ResetCode();
                    resetCode.setId(rs.getInt("id"));
                    resetCode.setUserId(rs.getInt("user_id"));
                    resetCode.setCode(rs.getString("code"));
                    resetCode.setExpirationTime(rs.getTimestamp("expiration_time"));
                    resetCode.setUsed(rs.getBoolean("is_used"));
                    resetCode.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    return resetCode;
                }
            }
        }
        
        return null;
    }
}
