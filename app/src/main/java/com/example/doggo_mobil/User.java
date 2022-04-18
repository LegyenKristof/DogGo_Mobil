package com.example.doggo_mobil;

public class User {
    private int id;
    private String username;
    private String email;
    private int permission;
    private String remember_token;

    public User(int id, String username, String email, int permission, String remember_token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.permission = permission;
        this.remember_token = remember_token;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getPermission() {
        return permission;
    }

    public String getRemember_token() {
        return remember_token;
    }
}
