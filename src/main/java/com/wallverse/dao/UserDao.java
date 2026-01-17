package com.wallverse.dao;

import com.wallverse.model.User;
import com.wallverse.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao extends BaseDao {

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, salt) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getSalt());
            return ps.executeUpdate() == 1;

        } catch (SQLException ex) {
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT id, username, email, password_hash, salt FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String salt = rs.getString("salt");
                    String expectedHash = rs.getString("password_hash");
                    String actualHash = PasswordUtil.hashPassword(password, salt);
                    if (expectedHash.equals(actualHash)) {
                        return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            expectedHash,
                            salt
                        );
                    }
                }
            }
        } catch (SQLException ex) {
            return null;
        }
        return null;
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException ex) {
            return false;
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException ex) {
            return false;
        }
    }
}
