package com.example.mapsproject.Entity;
public enum TravelMode{
    DRIVE, MOTOR, TRANSIT;
    public String getString(){
        switch (this){
            case DRIVE:
                return "DRIVE";
            case MOTOR:
                return "TWO_WHEELER";
            case TRANSIT:
                return "TRANSIT";
            default:
                return "DRIVE";
        }
    }
}
