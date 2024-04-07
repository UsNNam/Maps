package com.example.mapsproject.Entity;


import java.util.ArrayList;
import java.util.List;

public class OpenAIMessage {
    String role;
    ArrayList<OpenAIContent> content;

    public OpenAIMessage() {
        content = new ArrayList<>();
    }

    public void setRole(String role) {
        this.role = role;
    }
    public void addContent(OpenAIContent content) {
        this.content.add(content);
    }
    public List<OpenAIContent> getContent() {
        return content;
    }

}
