package com.example.demo.domain;

import lombok.Data;

import java.util.HashMap;

@Data
public class Image {
    private String name;
    private String base64Data;
    private String uri;

    public Image(){}

    public Image(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HashMap<String, Object> toJson(){
        return new HashMap<>(){{
            put("name", name);
            put("uri", uri);
        }};
    }
}
