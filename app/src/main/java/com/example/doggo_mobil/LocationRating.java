package com.example.doggo_mobil;

public class LocationRating {
    private int id;
    private int stars;
    private String description;
    private int location_id;
    private int user_id;
    private String username;

    public LocationRating(int id, int stars, String description, int location_id, int user_id) {
        this.id = id;
        this.stars = stars;
        this.description = description;
        this.location_id = location_id;
        this.user_id = user_id;
    }

    public LocationRating(int id, int stars, String description, int location_id, int user_id, String username) {
        this.id = id;
        this.stars = stars;
        this.description = description;
        this.location_id = location_id;
        this.user_id = user_id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public int getStars() {
        return stars;
    }

    public String getDescription() {
        return description;
    }

    public int getLocation_id() {
        return location_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return String.format("%d\tStars: %d\n%s", user_id, stars, description);
    }
}
