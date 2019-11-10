package com.example.eht18_masterprojekt.Feature_SMS_Import;

abstract class SmsBuilder {
    abstract void validateSms();
    abstract void buildMedikamente();
    abstract void buildOrdinationsInformationen();
    abstract SMS getSMS();
}
