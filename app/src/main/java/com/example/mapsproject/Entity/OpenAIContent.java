package com.example.mapsproject.Entity;



public class OpenAIContent {
    String type;
    String text;

    public OpenAIContent() {
    }
    public OpenAIContent(String type, String text) {
        this.type = type;
        this.text = text;
    }
    public String getType() {
        return type;
    }
    public String getText() {
        return text;
    }

}
