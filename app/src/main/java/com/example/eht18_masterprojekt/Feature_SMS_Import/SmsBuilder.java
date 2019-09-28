package com.example.eht18_masterprojekt.Feature_SMS_Import;

interface SmsBuilder {
    void validateSms();
    void buildMedikamente();
    void buildOrdinationsInformationen();
    SMS getSMS();
}
