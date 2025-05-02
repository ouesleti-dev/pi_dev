package Models;

import java.sql.Timestamp;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String password;
    private boolean isVerified;
    private Role role;
    private Timestamp createdAt;



    public enum Role {
        ROLE_ADMIN,
        ROLE_SUPER_ADMIN,
        ROLE_CLIENT
    }

    // Constructeur par défaut
    public User() {
        this.role = Role.ROLE_CLIENT;
        this.isVerified = false;
    }

    // Constructeur sans id (pour création)
    public User(String nom, String prenom, String email, String telephone) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // Constructeur complet
    public User(int id, String nom, String prenom, String email, String telephone, String password,
                boolean isVerified, Role role, Timestamp createdAt) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.isVerified = isVerified;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", isVerified=" + isVerified +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}