package com.example.eht18_masterprojekt.Core;

abstract class SmsBuilder {
    abstract void validateSms();
    abstract void buildMedikamente();
    abstract void buildOrdinationsInformationen();
    abstract SMS getSMS();
}
