package com.example.eht18_masterprojekt.Feature_SMS_Import;

import java.util.List;

public interface SmsImporter {
    List<SmsDisplay> getSmsDisplays();
    String retrieveSmsText(String smsLocation);
}
