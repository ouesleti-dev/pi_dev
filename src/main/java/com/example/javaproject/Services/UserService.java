package com.example.javaproject.Services;

import com.example.javaproject.Entities.User;
import com.example.javaproject.Interfaces.IService;
import com.example.javaproject.Tools.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    private final Connection cnx;

    public UserService() {
        cnx = Myconnection.getInstance().getCnx();
    }

    @Override
    public void addEntity(User user) {
        String query = "INSERT INTO user(nom, prenom, email, telephone, password, is_verified, role, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getTelephone());
            pst.setString(5, user.getPassword());
            pst.setBoolean(6, user.isVerified());
            pst.setString(7, user.getRole());
            pst.setTimestamp(8, Timestamp.valueOf(user.getCreatedAt()));

            pst.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur: " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(User user) {
        String query = "UPDATE user SET nom=?, prenom=?, email=?, telephone=?, password=?, " +
                "is_verified=?, role=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getTelephone());
            pst.setString(5, user.getPassword());
            pst.setBoolean(6, user.isVerified());
            pst.setString(7, user.getRole());
            pst.setInt(8, user.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteEntity(User user) {
        String query = "DELETE FROM user WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, user.getId());
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> getAllData() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setTelephone(rs.getString("telephone"));
                u.setPassword(rs.getString("password"));
                u.setVerified(rs.getBoolean("is_verified"));
                u.setRole(rs.getString("role"));
                u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                users.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        }
        return users;
    }

    public User getById(int id) {
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setTelephone(rs.getString("telephone"));
                    u.setPassword(rs.getString("password"));
                    u.setVerified(rs.getBoolean("is_verified"));
                    u.setRole(rs.getString("role"));
                    u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par ID: " + e.getMessage());
        }
        return null;
    }

    public User findByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, email);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setTelephone(rs.getString("telephone"));
                    u.setPassword(rs.getString("password"));
                    u.setVerified(rs.getBoolean("is_verified"));
                    u.setRole(rs.getString("role"));
                    u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par email: " + e.getMessage());
        }
        return null;
    }
}