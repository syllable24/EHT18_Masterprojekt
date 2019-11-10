package com.example.eht18_masterprojekt.Feature_SMS_Import;

import androidx.annotation.NonNull;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.MedikamentEinnahme;
import com.example.eht18_masterprojekt.Core.OrdinationsInformationen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

class SMS {

    List<Medikament> medList;
    OrdinationsInformationen ordiInfo;

    private SMS() {
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    static class Hl7v3SmsBuilder extends SmsBuilder {

        SMS hl7v3Sms;

        @Override
        public void validateSms() {

        }

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
        private String smsLocation;
        private Document rawSms;

        XmlSmsBuilder(String smsLocation){
            this.smsLocation = smsLocation;
        }

        @Override
        public void validateSms() {
            try {
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setValidating(true);
                DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
                rawSms = domBuilder.parse(smsLocation);
                xmlSms = new SMS();

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void buildMedikamente() {
            Element xmlAllMedikamente = rawSms.getElementById("Medikamente");
            NodeList medList = xmlAllMedikamente.getChildNodes();
            xmlSms.medList = parseMedList(medList);
        }

        private List<Medikament> parseMedList(NodeList medList){
            List<Medikament> xmlMedList = new ArrayList<>();
            for(int i = 0; i < medList.getLength(); i++){
                Node xmlMedikament = medList.item(i);
                NodeList xmlMedDetails =  xmlMedikament.getChildNodes();

                Medikament m = new Medikament();
                m.setPharmazentralnummer(Integer.parseInt(xmlMedDetails.item(PZN).getNodeValue()));
                m.setBezeichnung(xmlMedDetails.item(BEZEICHNUNG).getNodeValue());
                m.setEinheit(xmlMedDetails.item(EINHEIT).getNodeValue());
                m.setStueckzahl(Integer.parseInt(xmlMedDetails.item(ANZAHL).getNodeValue()));

                MedikamentEinnahme me = new MedikamentEinnahme();
                NodeList xmlEinnahmeDetails = xmlMedDetails.item(EINNAHME).getChildNodes();

                if (xmlEinnahmeDetails.item(FRUEH).getNodeValue().equals("1")){
                    me.add(ZEIT_FRUEH, m.getStueckzahl() + " " + m.getEinheit());
                }
                if (xmlEinnahmeDetails.item(MITTAG).getNodeValue().equals("1")){
                    me.add(ZEIT_MITTAG, m.getStueckzahl() + " " + m.getEinheit());
                }
                if (xmlEinnahmeDetails.item(ABEND).getNodeValue().equals("1")){
                    me.add(ZEIT_ABEND, m.getStueckzahl() + " " + m.getEinheit());
                }
                if (xmlEinnahmeDetails.item(NACHT).getNodeValue().equals("1")){
                    me.add(ZEIT_NACHT, m.getStueckzahl() + " " + m.getEinheit());
                }
                m.setEinnahmeZeiten(me);
                xmlMedList.add(m);
            }
            return xmlMedList;
        }

        @Override
        public void buildOrdinationsInformationen() {
            Element xmlOrdinationsInformationen = rawSms.getElementById("OrdinationsInformationen");
        }

        @Override
        public SMS getSMS() {
            return xmlSms;
        }
    }
}
