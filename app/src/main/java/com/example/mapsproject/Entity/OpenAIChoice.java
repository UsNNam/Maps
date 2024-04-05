package com.example.mapsproject.Entity;

public class OpenAIChoice {
    int index;
    OpenAIMessageResponse message;
    public OpenAIChoice() {

    }
    public OpenAIChoice(int index, OpenAIMessageResponse message) {
        this.index = index;
        this.message = message;
    }
    public OpenAIMessageResponse getMessage() {
        return message;
    }
    public class OpenAIMessageResponse{
        String role;
        String content;
        public OpenAIMessageResponse() {
        }
        public OpenAIMessageResponse(String role, String content) {
            this.role = role;
            this.content = content;
        }
        public String getRole() {
            return role;
        }
        public String getContent() {
            return content;
        }
    }
}