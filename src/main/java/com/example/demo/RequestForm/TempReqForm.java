package com.example.demo.RequestForm;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class TempReqForm {
    @Valid
    private List<Temp> temp;

    @Data
    public static class Temp{
        private String a;
        private int b;
    }
}
