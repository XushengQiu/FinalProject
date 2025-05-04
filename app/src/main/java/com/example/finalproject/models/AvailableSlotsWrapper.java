package com.example.finalproject.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AvailableSlotsWrapper {

    @SerializedName("available_slots")
    private List<AvailableSlot> availableSlots;

    public List<AvailableSlot> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<AvailableSlot> availableSlots) {
        this.availableSlots = availableSlots;
    }
}