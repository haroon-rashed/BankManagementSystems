package bank.models;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User model - represents a bank system user with login credentials.
 * Demonstrates ENCAPSULATION with private fields.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private String role; // ADMIN, CUSTOMER
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String linkedAccountNumber;

    public User(String username, String passwordHash, String fullName, String email, String phone, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters - ENCAPSULATION
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String hash) { this.passwordHash = hash; }
    public String getFullName() { return fullName; }
    public void setFullName(String name) { this.fullName = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime t) { this.lastLogin = t; }
    public String getLinkedAccountNumber() { return linkedAccountNumber; }
    public void setLinkedAccountNumber(String acc) { this.linkedAccountNumber = acc; }
    public boolean isAdmin() { return "ADMIN".equals(role); }
}
