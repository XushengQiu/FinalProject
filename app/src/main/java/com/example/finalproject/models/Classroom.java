package com.example.finalproject.models;

public class Classroom {
    private String id;
    private String name;
    private String block;
    private int floor;
    private int number;

    public Classroom(int capacity, int number, int floor, String block, String name, String id) {
        this.capacity = capacity;
        this.number = number;
        this.floor = floor;
        this.block = block;
        this.name = name;
        this.id = id;
    }

    private int capacity;

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
