package com.example.demo.ResponseFormat;

import java.util.ArrayList;

public class Response {
    private Object data;
    private ArrayList<String> errors;

    public Response(){
        data = null;
        errors = new ArrayList<>();
    }

    public Response(Object data, ArrayList<String> errors) {
        this.data = data;
        this.errors = errors;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<String> errors) {
        this.errors = errors;
    }
}
