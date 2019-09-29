package com.example.eht18_masterprojekt.Feature_SMS_Import;

import java.util.EventObject;

class SmsSelectedEvent extends EventObject {

    private SmsDisplay smsSource;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    SmsSelectedEvent(Object source) {
        super(source);
    }

    SmsDisplay getSmsSource() {
        return smsSource;
    }

    protected void setSmsSource(SmsDisplay smsSource) {
        this.smsSource = smsSource;
    }
}
