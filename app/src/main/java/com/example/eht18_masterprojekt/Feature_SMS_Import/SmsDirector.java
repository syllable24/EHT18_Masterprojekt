package com.example.eht18_masterprojekt.Feature_SMS_Import;

class SmsDirector {

    SmsBuilder builder;

    public SmsDirector(SmsBuilder b){
        this.builder = b;
    }

    public SMS getSms(){
        return builder.getSMS();
    }
}
