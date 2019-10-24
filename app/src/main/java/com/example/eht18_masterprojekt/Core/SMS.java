package com.example.eht18_masterprojekt.Core;

import androidx.annotation.NonNull;

import java.util.List;

class SMS {

    List<Medikament> medList;
    OrdinationsInformationen ordiInfo;

    private SMS() {
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    static class Hl7v3SmsBuilder extends SmsBuilder {

        SMS hl7v3Sms;

        @Override
        public void validateSms() {

        }

        @Override
        public void buildMedikamente() {

        }

        @Override
        public void buildOrdinationsInformationen() {

        }

        @Override
        public SMS getSMS() {
            return null;
        }
    }

    static class PlainTextSmsBuilder extends SmsBuilder {

        private SMS plainTextSms;
        private String smsLocation;

        PlainTextSmsBuilder(String smsLocation){
            this.smsLocation = smsLocation;
        }

        @Override
        public void validateSms() {

        }

        @Override
        public void buildMedikamente() {

        }

        @Override
        public void buildOrdinationsInformationen() {

        }

        @Override
        public SMS getSMS() {
            return null;
        }
    }
}
