package com.example.mapsproject.Entity;

public class TaxiDescription {
    private String name;
    private String description;
    private String price;
    private String phoneNumber;

    public TaxiDescription(String name, String description, String price, String phoneNumber) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
