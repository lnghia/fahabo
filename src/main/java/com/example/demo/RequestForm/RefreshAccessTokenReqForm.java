package com.example.demo.RequestForm;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RefreshAccessTokenReqForm {
    @NotNull
    private String refreshToken;
}
