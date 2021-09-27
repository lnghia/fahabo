package com.example.demo.Helpers;

import java.util.HashMap;
import java.util.List;

public class ResponseHelper {
    private static ResponseHelper instance;

//    public HashMap<String, String> writeResponseData(List<String> keys, List<String> values){
//
//    }

    public static ResponseHelper getInstance(){
        if(instance == null) instance = new ResponseHelper();
        return instance;
    }
}
