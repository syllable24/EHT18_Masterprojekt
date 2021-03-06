package com.example.eht18_masterprojekt.Feature_SMS_Import;

import android.content.Context;

import java.util.Date;

import static com.example.eht18_masterprojekt.Feature_SMS_Import.SmsType.HL7v3;
import static com.example.eht18_masterprojekt.Feature_SMS_Import.SmsType.XML;

class SmsDirector {

    SmsBuilder builder;
    Context context;

    public SmsDirector(String smsBody, String smsReceivedFrom, java.util.Date smsReceivedAt, Context ctx) throws SmsFormatException{
        context = ctx;
        String cleanedRawSms = cleanSms(smsBody);
        SmsType s = SMS.querySmsType(context, cleanedRawSms);
        Date receivedAt = smsReceivedAt;
        String address = smsReceivedFrom;

        if (s == XML){
            this.builder = new SMS.XmlSmsBuilder(address, receivedAt, cleanedRawSms);
        }
        else if(s == HL7v3){
            this.builder = new SMS.Hl7v3SmsBuilder(address, receivedAt, cleanedRawSms);
        }
        else if (s == null){
            throw new SmsFormatException("Unknown SMS Format");
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
     * Entfernen von Steuerzeichen und Whitespace Nodes aus einer XML-SMS.
     * @param input
     * @return Bereinigten String
     */
    private String cleanSms(String input){
        return input.replaceAll("\\p{Cntrl}", "") // Steuerzeichen
                    .replaceAll(">\\s*<", "><"); // Whitespaces
    }
}
