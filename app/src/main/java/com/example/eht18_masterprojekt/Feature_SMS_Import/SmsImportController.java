package com.example.eht18_masterprojekt.Feature_SMS_Import;

import java.util.List;

public class SmsImportController implements SmsSelectedListener {

    private SmsImportModel model;
    private ImportSmsView view;
    private SmsDirector smsDirector;

    SmsImportController(SmsImportModel m, ImportSmsView v, SmsImporter i){
        view = v;
        model = m;
        view.addSmsSelectedListener(this);

        checkInbox(i);
    }

    private void checkInbox(SmsImporter i){
        // find importable SMS in Inbox
        List<SmsDisplay> lst = i.getSmsDisplays();
        model.setSmsList(lst);
    }

    private SMS importSMS(SmsDisplay source){
        // determine SMS type
        smsDirector = new SmsDirector(new SMS.PlainTextSmsBuilder(source.location));
        return smsDirector.getSms();
    }

    private boolean deleteSMS(){
        throw new UnsupportedOperationException("Feature not implemented yet");
    }

    @Override
    public void smsSelectedEventReceived(SmsSelectedEvent event) {
        importSMS(event.getSmsSource());
    }
}
