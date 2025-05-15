package Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Classe représentant un code de réinitialisation de mot de passe
 */
public class ResetCode {
    private int id;
    private int userId;
    private String code;
    private Timestamp expirationTime;
    private boolean isUsed;
    private Timestamp createdAt;

    // Constructeur par défaut
    public ResetCode() {
        this.isUsed = false;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Constructeur pour la création d'un nouveau code
    public ResetCode(int userId, String code, Timestamp expirationTime) {
        this();
        this.userId = userId;
        this.code = code;
        this.expirationTime = expirationTime;
    }

    // Constructeur complet
    public ResetCode(int id, int userId, String code, Timestamp expirationTime, boolean isUsed, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.code = code;
        this.expirationTime = expirationTime;
        this.isUsed = isUsed;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Timestamp getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Timestamp expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Vérifie si le code est expiré
     * @return true si le code est expiré, false sinon
     */
    public boolean isExpired() {
        return expirationTime.before(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Vérifie si le code est valide (non expiré et non utilisé)
     * @return true si le code est valide, false sinon
     */
    public boolean isValid() {
        return !isExpired() && !isUsed;
    }

    @Override
    public String toString() {
        return "ResetCode{" +
                "id=" + id +
                ", userId=" + userId +
                ", code='" + code + '\'' +
                ", expirationTime=" + expirationTime +
                ", isUsed=" + isUsed +
                ", createdAt=" + createdAt +
                '}';
    }
}
