package com.example.mapsproject.Entity;


import java.util.ArrayList;
import java.util.List;

public class OpenAIResponse {
    String id ;
    String object;
    String created;
    String model;

    List<OpenAIChoice> choices;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<OpenAIChoice> getChoices() {
        return choices;
    }
    public OpenAIResponse() {
        choices = new ArrayList<>();
    }


}
