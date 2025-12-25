package com.jeffrey.models;

public class User {
    private String id;
    private String name;
    private String language;
    private String email;
    private String passwordHash;
    private String wishlist;
    private String createdAt;

    public User(String id, String name, String language, String email, String passwordHash, String wishlist, String createdAt) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.email = email;
        this.passwordHash = passwordHash;
        this.wishlist = wishlist;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLanguage() { return language; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getWishlist() { return wishlist; }
    public String getCreatedAt() { return createdAt; }

    // Setters if needed
    public void setName(String name) { this.name = name; }
    public void setLanguage(String language) { this.language = language; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setWishlist(String wishlist) { this.wishlist = wishlist; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
