package com.example.demo.domain;

import com.dropbox.core.v2.sharing.PathLinkMetadata;
import lombok.Data;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

@Data
public class Image {
    private String name;
    private String base64Data;
    private String uri;
    private PathLinkMetadata metadata;

    public Image(){}

    public Image(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public Image(String name, Optional<PathLinkMetadata> metadata, String uri){
        this.name = name;
        this.metadata = metadata.get();
        this.uri = uri;
    }

    public Image(String name, Optional<PathLinkMetadata> metadata){
        this.name = name;
        this.metadata = metadata.get();
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
