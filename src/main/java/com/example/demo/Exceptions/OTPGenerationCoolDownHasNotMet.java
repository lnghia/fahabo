package com.example.demo.Exceptions;

public class OTPGenerationCoolDownHasNotMet extends Exception{
    public OTPGenerationCoolDownHasNotMet(String errorMessage){
        super(errorMessage);
    }
}
