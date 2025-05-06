package com.example.finalproject.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Reservation implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;

    @SerializedName("classroom_id")
    private String classroomId;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    public Reservation(int id, int userId, String classroomId, String startTime, String endTime) {
        this.id = id;
        this.userId = userId;
        this.classroomId = classroomId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
