package com.example.eht18_masterprojekt.Core;

class SmsDirector {

    SmsBuilder builder;

    public SmsDirector(SmsBuilder b){
        this.builder = b;
    }

    public SMS getSms(){
        return builder.getSMS();
    }
}
