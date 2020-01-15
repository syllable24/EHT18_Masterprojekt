package com.example.eht18_masterprojekt.Feature_SMS_Import;

import android.content.Context;

import java.util.Date;

import static com.example.eht18_masterprojekt.Feature_SMS_Import.SmsType.HL7v3;
import static com.example.eht18_masterprojekt.Feature_SMS_Import.SmsType.XML;

class SmsDirector {

    SmsBuilder builder;
    Context context;

    public SmsDirector(String smsBody, String smsReceivedFrom, java.util.Date smsReceivedAt, Context ctx) {
        context = ctx;
        String cleanedRawSms = cleanSms(smsBody);
        SmsType s = SMS.querySmsType(context, cleanedRawSms);
        if (s == XML){
            Date receivedAt = smsReceivedAt;
            String address = smsReceivedFrom;
            this.builder = new SMS.XmlSmsBuilder(address,receivedAt, cleanedRawSms);
        }
        else if(s == HL7v3){
            //this.builder = new SMS.Hl7v3SmsBuilder(qSMS);
        }
        else if (s == null){
            throw new IllegalArgumentException("Unknown SMS Format");
        }
    }

    public SmsDirector buildMedikamente(){
        builder.buildMedikamente();
        return this;
    }

    public SmsDirector buildOrdinationsInformationen(){
        builder.buildOrdinationsInformationen();
        return this;
    }

    public SMS getSms() {
        return builder.getSMS();
    }

    /**
     * Entfernen von Steuerzeichen, Whitespace Nodes und Umlauten aus einer XML-SMS.
     * @param input
     * @return Bereinigten String
     */
    private String cleanSms(String input){
        String t = input.replaceAll("\\p{Cntrl}", "").replaceAll(">\\s*<", "><");;
        t = t.replace( "ä", "ae" );
        t = t.replace( "ö", "oe" );
        t = t.replace( "ü", "ue" );
        t = t.replace( "Ä", "Ae" );
        t = t.replace( "Ö", "Oe" );
        t = t.replace( "Ü", "Ue" );
        t = t.replace( "ß", "ss" );
        return t;
    }

}
