package com.example.doggo_mobil;

public class Location {
    private int id;
    private String name;
    private String description;
    private double lat;
    private double lng;
    private boolean allowed;
    private int user_id;

    public Location(int id, String name, String description, double lat, double lng, boolean allowed, int user_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.allowed = allowed;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getUser_id() {
        return user_id;
    }

    @Override
    public String toString() {
        return getName();
    }
}
