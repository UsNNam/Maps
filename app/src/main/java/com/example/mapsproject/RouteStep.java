package com.example.mapsproject;

public class RouteStep {
    public String distance;
    public String instruction;
    public Integer thumbnail;

    public RouteStep(String distance, String instruction, Integer thumbnail) {
        this.distance = distance;
        this.instruction = instruction;
        this.thumbnail = thumbnail;
    }
}
