package com.example.demo.ResponseFormat;

import java.util.ArrayList;

public class Response {
    private Object data;
    private ArrayList<String> message;

    public Response(){
        data = null;
        message = new ArrayList<>();
    }

    public Response(Object data, ArrayList<String> error) {
        this.data = data;
        this.message = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ArrayList<String> getError() {
        return message;
    }

    public void setError(ArrayList<String> error) {
        this.message = error;
    }
}
