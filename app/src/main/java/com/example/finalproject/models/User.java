package com.example.finalproject.models;

public class User {
    private int id;
    private String uid;
    private String name;
    private String degree;
    private String schoolNumber;
    private String role;


    public User(int id, String uid, String name, String degree, String schoolNumber, String role) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.degree = degree;
        this.schoolNumber = schoolNumber;
        this.role = role;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getSchoolNumber() { return schoolNumber; }
    public void setSchoolNumber(String schoolNumber) { this.schoolNumber = schoolNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
