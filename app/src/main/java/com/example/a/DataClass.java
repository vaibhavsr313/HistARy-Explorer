package com.example.a;

public class DataClass {
    private String name;
    private String email;
    private String userId;

    public DataClass() {
        // Required for Firebase
    }

    public DataClass(String name, String email, String userId) {
        this.name = name;
        this.email = email;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }
}

