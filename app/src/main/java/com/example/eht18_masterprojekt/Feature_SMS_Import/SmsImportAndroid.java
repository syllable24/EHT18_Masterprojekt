package com.example.eht18_masterprojekt.Feature_SMS_Import;

import java.util.List;

class SmsImportAndroid implements SmsImporter {
    @Override
    public List<SmsDisplay> getSmsDisplays() {
        // TODO: Get all available SMS from Android Inbox
        throw new UnsupportedOperationException("FeatureNotImplementedYet");
    }

    @Override
    public String retrieveSmsText(String smsLocation) {
        throw new UnsupportedOperationException("FeatureNotImplementedYet");
    }
}