package com.example.eht18_masterprojekt.Feature_SMS_Import;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.MedikamentEinnahme;
import com.example.eht18_masterprojekt.Core.OrdinationsInformationen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class SMS {
    // Relevante Inhalte einer SMS, die mittels Cursor aus dem SMS-INBOX Content provider ausgelesen wurden.
    public static final int ADDRESS = 2;
    public static final int DATE = 4;
    public static final int DATE_SENT = 5;
    public static final int SUBJECT = 11;
    public static final int BODY = 12;

    String sender;
    Date receivedAt;
    List<Medikament> medList;
    OrdinationsInformationen ordiInfo;

    private SMS() {
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public String getSender() {
        return sender;
    }

    private void setSender(String sender) {
        this.sender = sender;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    private void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    static class Hl7v3SmsBuilder extends SmsBuilder {

        SMS hl7v3Sms;

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

    static class XmlSmsBuilder extends SmsBuilder {

        private final int PZN         = 0;
        private final int BEZEICHNUNG = 1;
        private final int ANZAHL      = 2;
        private final int EINHEIT     = 3;
        private final int EINNAHME    = 4;

        private final int FRUEH  = 0;
        private final int MITTAG = 1;
        private final int ABEND  = 2;
        private final int NACHT  = 3;

        private final LocalTime ZEIT_FRUEH  = LocalTime.of(8,0);
        private final LocalTime ZEIT_MITTAG = LocalTime.of(12, 0);
        private final LocalTime ZEIT_ABEND  = LocalTime.of(16,0);
        private final LocalTime ZEIT_NACHT  = LocalTime.of(20,0);

        private SMS xmlSms;
        private Document smsBody;

        XmlSmsBuilder(String smsSender, Date smsReceivedAt, Document smsBody){
            this.smsBody = smsBody;
            xmlSms = new SMS();
            xmlSms.setSender(smsSender);
            xmlSms.setReceivedAt(smsReceivedAt);
        }

        @Override
        public void buildMedikamente() {
            Log.d("MED-INIT", smsBody.getDocumentElement().getNodeName());

            NodeList xmlAllMedikamente = smsBody.getElementsByTagName("Medikamente");

            for (int i = 0; i < xmlAllMedikamente.getLength(); i++){
                Log.d("MED-INIT", xmlAllMedikamente.item(i).getNodeName());
            }

            NodeList meds = xmlAllMedikamente.item(0).getChildNodes();
            xmlSms.medList = parseMedList(meds);
        }

        private List<Medikament> parseMedList(NodeList medList){
            List<Medikament> xmlMedList = new ArrayList<>();
            try {
                for (int i = 0; i < medList.getLength(); i++) {
                    Node xmlMedikament = medList.item(i);
                    Log.d("MED-INIT", xmlMedikament.getNodeName());

                    NodeList xmlMedDetails = xmlMedikament.getChildNodes();

                    Medikament m = new Medikament();
                    m.setPharmazentralnummer(Integer.parseInt(xmlMedDetails.item(PZN).getNodeValue()));
                    m.setBezeichnung(xmlMedDetails.item(BEZEICHNUNG).getNodeValue());
                    m.setEinheit(xmlMedDetails.item(EINHEIT).getNodeValue());
                    m.setStueckzahl(Integer.parseInt(xmlMedDetails.item(ANZAHL).getNodeValue()));

                    MedikamentEinnahme me = new MedikamentEinnahme();
                    NodeList xmlEinnahmeDetails = xmlMedDetails.item(EINNAHME).getChildNodes();

                    if (xmlEinnahmeDetails.item(FRUEH).getNodeValue().equals("1")) {
                        me.add(ZEIT_FRUEH, m.getStueckzahl() + " " + m.getEinheit());
                    }
                    if (xmlEinnahmeDetails.item(MITTAG).getNodeValue().equals("1")) {
                        me.add(ZEIT_MITTAG, m.getStueckzahl() + " " + m.getEinheit());
                    }
                    if (xmlEinnahmeDetails.item(ABEND).getNodeValue().equals("1")) {
                        me.add(ZEIT_ABEND, m.getStueckzahl() + " " + m.getEinheit());
                    }
                    if (xmlEinnahmeDetails.item(NACHT).getNodeValue().equals("1")) {
                        me.add(ZEIT_NACHT, m.getStueckzahl() + " " + m.getEinheit());
                    }
                    m.setEinnahmeZeiten(me);
                    xmlMedList.add(m);
                }
            }
            catch(Exception e){
                Log.e("MED-INIT", e.getMessage());
            }
            return xmlMedList;
        }

        @Override
        public void buildOrdinationsInformationen() {
            Element xmlOrdinationsInformationen = smsBody.getElementById("OrdinationsInformationen");
        }

        @Override
        public SMS getSMS() {
            return xmlSms;
        }
    }
}
