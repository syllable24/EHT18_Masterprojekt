package com.example.eht18_masterprojekt.Feature_SMS_Import;

import com.example.eht18_masterprojekt.MainActivityView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

class SmsImportModel extends Observable {

    private List<SmsDisplay> smsList;

    public void setSmsList(List<SmsDisplay> lst) {
        this.smsList = lst;
        this.setChanged();
        this.notifyObservers(smsList);
    }
}
