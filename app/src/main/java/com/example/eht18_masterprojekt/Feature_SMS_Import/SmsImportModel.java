package com.example.eht18_masterprojekt.Feature_SMS_Import;

import java.util.List;

class SmsImportModel{

    private List<SmsDisplay> smsList;

    void setSmsList(List<SmsDisplay> lst) {
        this.smsList = lst;
    }

    public List<SmsDisplay> getSmsList(){
        return this.smsList;
    }
}
