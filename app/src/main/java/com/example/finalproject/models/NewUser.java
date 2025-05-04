package com.example.finalproject.models;

public class NewUser {
    private String uid;
    private String name;
    private String degree;
    private String schoolNumber;
    private String role;


    public NewUser(String uid, String name, String degree, String schoolNumber) {
        this.uid = uid;
        this.name = name;
        this.degree = degree;
        this.schoolNumber = schoolNumber;
        this.role = "USER";
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSchoolNumber() {
        return schoolNumber;
    }

    public void setSchoolNumber(String schoolNumber) {
        this.schoolNumber = schoolNumber;
    }

    public String getRole() {
        return role;
    }
}
