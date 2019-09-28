package com.example.eht18_masterprojekt.Feature_SMS_Import;

import com.example.eht18_masterprojekt.MainActivityView;

import java.util.List;

public class SmsImportController {

    SmsImportModel model;
    ImportSmsView view;
    SmsDirector smsDirector;

    public SmsImportController(SmsImportModel m, ImportSmsView v){
        view = v;
        model = m;
        model.addObserver(view);

        checkInbox();
    }

    private void checkInbox(){
        // find importable SMS in Inbox
        ImportProxy i = new ImportProxy();
        List<SmsDisplay> lst = i.getSmsDisplay();
        model.setSmsList(lst);
    }

    private SMS importSMS(String smsLocation){
        // determine SMS type
        smsDirector = new SmsDirector(new SMS.PlainTextSmsBuilder(smsLocation));
        return smsDirector.getSms();
    }

    private boolean deleteSMS(){
        throw new UnsupportedOperationException("Feature not implemented yet");
    }
}
