package com.example.demo.DropBox;

import com.example.demo.Helpers.Helper;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Data
public class ItemToUpload {
    private String name;

    private InputStream inputStream;

    public ItemToUpload(String name, String byteData){
        this.name = name;
//        this.inputStream = new ByteArrayInputStream(byteData.getBytes(StandardCharsets.UTF_8));
        this.inputStream = new ByteArrayInputStream(Helper.getInstance().base64ToBytes(byteData));
    }
}
