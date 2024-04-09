package com.example.mapsproject.Entity;

public class OpenAIContentV2 {
    String type;
    String image_url;

    public OpenAIContentV2() {
    }

    public OpenAIContentV2(String type, String image_url) {
        this.type = type;
        this.image_url = image_url;
    }

    public String getType() {
        return type;
    }

    public String getImage_url() {
        return image_url;
    }
}
