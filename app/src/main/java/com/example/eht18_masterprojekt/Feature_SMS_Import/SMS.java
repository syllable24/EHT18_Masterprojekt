package com.example.eht18_masterprojekt.Feature_SMS_Import;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.MedikamentEinnahme;
import com.example.eht18_masterprojekt.Core.OrdinationsInformationen;
import com.example.eht18_masterprojekt.R;


import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mf.javax.xml.parsers.DocumentBuilder;
import mf.javax.xml.parsers.DocumentBuilderFactory;
import mf.javax.xml.parsers.ParserConfigurationException;
import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.stream.StreamSource;
import mf.javax.xml.validation.Schema;
import mf.javax.xml.validation.SchemaFactory;
import mf.javax.xml.validation.Validator;
import mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory;
import mf.org.w3c.dom.Document;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.NodeList;

class SMS {
    // Relevante Inhalte einer SMS, die mittels Cursor aus dem SMS-INBOX Content provider ausgelesen wurden.
    public static final int ADDRESS = 2;
    public static final int PERSON = 3;
    public static final int DATE = 4;
    public static final int DATE_SENT = 5;
    public static final int SUBJECT = 11;
    public static final int BODY = 12;

    String sender;
    Date receivedAt;
    Document body;
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

    public Document getBody() {
        return body;
    }

    private void setBody(Document body) {
        this.body = body;
    }

    public static SmsType querySmsType(Context context, String rawSms){
        try {
            Source xmlSource = new StreamSource(new StringReader(rawSms));
            Source schemaSource = new StreamSource(context.getResources().openRawResource(R.raw.xml_sms_schema));
            SchemaFactory sf = new XMLSchemaFactory();
            Schema xmlSchema = sf.newSchema(schemaSource);

            Validator v = xmlSchema.newValidator();
            v.validate(xmlSource); // Wenn die Validierung nicht funktioniert wird eine SAXException geworfen.

            return SmsType.XML;

            // TODO: Add query for HL7v3

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            Log.d("SMS-Import","SMS nicht im XML Format!");
        }
        return null;
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

        XmlSmsBuilder(String smsSender, Date smsReceivedAt, String smsBody) {
            try {
                xmlSms = new SMS();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document xmlDocument = dBuilder.parse(new InputSource(new StringReader(smsBody)));

                xmlSms.setBody(xmlDocument);
                xmlSms.setSender(smsSender);
                xmlSms.setReceivedAt(smsReceivedAt);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (SAXException e) {
                e.printStackTrace();
            }
            catch (ParserConfigurationException e){
                e.printStackTrace();
            }
        }

        @Override
        public void buildMedikamente() {
            NodeList xmlAllMedikamente = xmlSms.getBody().getElementsByTagName("Medikamente"); // Darf nur einen Node retournieren
            NodeList adds = xmlAllMedikamente.item(0).getChildNodes();
            Log.d("MED-INIT", adds.getLength() + " Medikamente in SMS");
            xmlSms.medList = parseMedList(adds);
        }

        private List<Medikament> parseMedList(NodeList medList){
            List<Medikament> xmlMedList = new ArrayList<>();
            try {
                for (int i = 0; i < medList.getLength(); i++) {
                    Log.d("MED-INIT", "Starte einlesen von Med " + i + "...");
                    Node xmlMedikament = medList.item(i);
                    NodeList xmlMedDetails = xmlMedikament.getChildNodes();

                    Medikament m = new Medikament();
                    m.setPharmazentralnummer(Integer.parseInt(xmlMedDetails.item(PZN).getTextContent()));
                    m.setBezeichnung(xmlMedDetails.item(BEZEICHNUNG).getTextContent());
                    m.setEinheit(xmlMedDetails.item(EINHEIT).getTextContent());
                    m.setStueckzahl(Integer.parseInt(xmlMedDetails.item(ANZAHL).getTextContent()));

                    MedikamentEinnahme me = new MedikamentEinnahme();
                    NodeList xmlEinnahmeDetails = xmlMedDetails.item(EINNAHME).getChildNodes();

                    if (xmlEinnahmeDetails.item(FRUEH).getTextContent().equals("1")) {
                        me.add(ZEIT_FRUEH, m.getStueckzahl() + " " + m.getEinheit());
                    }
                    if (xmlEinnahmeDetails.item(MITTAG).getTextContent().equals("1")) {
                        me.add(ZEIT_MITTAG, m.getStueckzahl() + " " + m.getEinheit());
                    }
                    if (xmlEinnahmeDetails.item(ABEND).getTextContent().equals("1")) {
                        me.add(ZEIT_ABEND, m.getStueckzahl() + " " + m.getEinheit());
                    }
                    if (xmlEinnahmeDetails.item(NACHT).getTextContent().equals("1")) {
                        me.add(ZEIT_NACHT, m.getStueckzahl() + " " + m.getEinheit());
                    }
                    m.setEinnahmeZeiten(me);
                    xmlMedList.add(m);
                    Log.d("MED-INIT", "Med " + m.getBezeichnung() + " eingelesen!");
                }
            }
            catch(Exception e){
                Log.e("MED-INIT", e.getMessage());
            }
            return xmlMedList;
        }

        @Override
        public void buildOrdinationsInformationen() {
            Element xmlOrdinationsInformationen = xmlSms.getBody().getElementById("OrdinationsInformationen");
        }

        @Override
        public SMS getSMS() {
            return xmlSms;
        }
    }
}
