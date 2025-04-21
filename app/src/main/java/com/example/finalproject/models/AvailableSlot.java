package com.example.finalproject.models;

import com.google.gson.annotations.SerializedName;

public class AvailableSlot {

    @SerializedName("start")
    private String start;

    @SerializedName("end")
    private String end;

    public AvailableSlot(String start, String end) {
        this.start = start;
        this.end = end;
    }

    // Getters y Setters
    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}

