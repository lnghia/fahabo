package com.example.demo.DropBox;

import com.example.demo.Album.Entity.Image;

import java.util.ArrayList;

public class UploadResult {
    public ArrayList<Image> successUploads;
    public ArrayList<Image> failUploads;

    public UploadResult(){}

    public UploadResult(ArrayList<Image> successUploads, ArrayList<Image> failUploads) {
        this.successUploads = successUploads;
        this.failUploads = failUploads;
    }

    public ArrayList<Image> getSuccessUploads() {
        return successUploads;
    }

    public void setSuccessUploads(ArrayList<Image> successUploads) {
        this.successUploads = successUploads;
    }

    public ArrayList<Image> getFailUploads() {
        return failUploads;
    }

    public void setFailUploads(ArrayList<Image> failUploads) {
        this.failUploads = failUploads;
    }
}
