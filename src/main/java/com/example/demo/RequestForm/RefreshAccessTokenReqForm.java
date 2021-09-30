package com.example.demo.RequestForm;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RefreshAccessTokenReqForm {

    private String refreshToken;
}
