package com.example.eht18_masterprojekt.Feature_SMS_Import;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.OrdinationsInformationen;
import com.example.eht18_masterprojekt.R;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.stream.StreamSource;
import mf.javax.xml.validation.Schema;
import mf.javax.xml.validation.SchemaFactory;
import mf.javax.xml.validation.Validator;
import mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory;


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

    public List<Medikament> getMedList() {
        return medList;
    }

    public OrdinationsInformationen getOrdiInfo() {
        return ordiInfo;
    }

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

    private Document getBody() {
        return body;
    }

    private void setBody(Document body) {
        this.body = body;
    }

    static SmsType querySmsType(Context context, String rawSms){
        Map<Integer, SmsType> supportedSmsTypes = new TreeMap<>();
        supportedSmsTypes.put(R.raw.xml_sms_schema, SmsType.XML);
        supportedSmsTypes.put(100, SmsType.HL7v3); //placeholder: supportedSmsTypes.add(R.raw.hl7v3_sms_schema);
        Source xmlSource = new StreamSource(new StringReader(rawSms));

        for (Integer smsType : supportedSmsTypes.keySet()){
            try {
                if (smsType == 100){ // placeholder: xsd schema for HL7v3 CDA
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document xmlDocument = dBuilder.parse(new InputSource(new StringReader(rawSms)));
                    XPathFactory xPathFactory = XPathFactory.newInstance();
                    XPath xpath = xPathFactory.newXPath();
                    XPathExpression selectMedTableBody = xpath.compile("//component/StructuredBody/section/text//tbody");
                    NodeList tbody = (NodeList) selectMedTableBody.evaluate(xmlDocument, XPathConstants.NODESET);
                    if (tbody.getLength() != 0)return SmsType.HL7v3;
                }
                else {
                    Source schemaSource = new StreamSource(context.getResources().openRawResource(smsType));
                    SchemaFactory sf = new XMLSchemaFactory();
                    Schema xmlSchema = sf.newSchema(schemaSource);

                    Validator v = xmlSchema.newValidator();
                    v.validate(xmlSource); // Wenn die Validierung nicht funktioniert wird eine SAXException geworfen.
                    return SmsType.XML;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                Log.d("SMS-Import","SMS nicht im plain XML Format!");
            } catch(XPathExpressionException e){
                Log.d("SMS-Import","SMS nicht im HL7v3 CDA Format!");
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static class Hl7v3SmsBuilder extends SmsBuilder {

        SMS hl7v3Sms;

        Hl7v3SmsBuilder(String smsSender, Date smsReceivedAt, String smsBody) {
            try {
                hl7v3Sms = new SMS();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document xmlDocument = dBuilder.parse(new InputSource(new StringReader(smsBody)));

                hl7v3Sms.setBody(xmlDocument);
                hl7v3Sms.setSender(smsSender);
                hl7v3Sms.setReceivedAt(smsReceivedAt);
            }

            catch (IOException e) {
                e.printStackTrace();
            }
            catch (SAXException e) {
                e.printStackTrace();
            }
            catch (javax.xml.parsers.ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void buildMedikamente() {
            try {
                hl7v3Sms.medList = parseMedTableBody();
            } catch (XPathExpressionException e) {
                Log.e("MED-INIT", "XPath Error");
                e.printStackTrace();
            }
        }

        private List<Medikament> parseMedTableBody() throws XPathExpressionException {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression selectMedTableBody = xpath.compile("//component/StructuredBody/section/text//tbody/tr");
            NodeList tbody = (NodeList) selectMedTableBody.evaluate(hl7v3Sms.getBody(), XPathConstants.NODESET);
            List<Medikament> importedList = new ArrayList<>();

            for (int i = 0; i < tbody.getLength(); i++){
                Node entry = tbody.item(i);
                NodeList content = entry.getChildNodes();

                String medName = content.item(0).getTextContent();
                String medEinnahmeZeit = content.item(1).getTextContent();
                String medDosis = content.item(2).getTextContent();
                String medEinheit = content.item(3).getTextContent();

                Medikament m = new Medikament(medName, medEinheit);
                if (importedList.contains(m)){
                    Medikament knownMed = importedList.get(importedList.indexOf(m));
                    knownMed.getEinnahmeProtokoll().addEinnahme(LocalTime.parse(medEinnahmeZeit), medDosis);
                }
                else{
                    m.getEinnahmeProtokoll().addEinnahme(LocalTime.parse(medEinnahmeZeit), medDosis);
                    importedList.add(m);
                }
            }
            return importedList;
        }

        private List<String> parseMedTableHeader() throws XPathExpressionException {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression selectMedTableHeader = xpath.compile("//component/StructuredBody/section/text//thead");
            NodeList thead = (NodeList) selectMedTableHeader.evaluate(hl7v3Sms.getBody(), XPathConstants.NODESET); // Darf nur 1 head-definition retournieren

            Node header = thead.item(0);
            NodeList headerContent = header.getChildNodes();
            List<String> headerValues = new ArrayList<>();

            for (int i = 0; i < headerContent.getLength(); i++){
                Node th = headerContent.item(i);
                headerValues.add(th.getNodeValue());
                Log.d("HL7v3_MED-INIT", "Med-Header: " + th.getNodeValue() + " eingelesen.");
            }
            return headerValues;
        }

        @Override
        public void buildOrdinationsInformationen() {
            // TODO: Build OrdiInfos from content of CDA Doc
        }

        @Override
        public SMS getSMS() {
            return hl7v3Sms;
        }
    }

    static class XmlSmsBuilder extends SmsBuilder {

        private final int BEZEICHNUNG = 0;
        private final int EINHEIT     = 1;
        private final int EINNAHME_ZEIT    = 0;
        private final int EINNAHME_DOSIS    = 1;

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
            catch (javax.xml.parsers.ParserConfigurationException e) {
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

        /**
         * Einlesen der Medikamente, Einnahmezeiten und deren Dosis aus übergebener XML-NodeList.
         *
         * @param medList
         * @return Medikationsliste
         */
        private List<Medikament> parseMedList(NodeList medList){
            List<Medikament> xmlMedList = new ArrayList<>();
            try {
                for (int i = 0; i < medList.getLength(); i++) {
                    Log.d("MED-INIT", "Starte einlesen von Med " + i + "...");
                    Node xmlMedikament = medList.item(i);
                    NamedNodeMap medAttributes = xmlMedikament.getAttributes();

                    Medikament m = new Medikament(
                             medAttributes.item(BEZEICHNUNG).getNodeValue()
                            ,medAttributes.item(EINHEIT).getNodeValue()
                    );
                    NodeList xmlMedEinnahmen = xmlMedikament.getChildNodes();

                    for(int j = 0; j < xmlMedEinnahmen.getLength(); j++){
                        Node xmlEinnahme = xmlMedEinnahmen.item(j);
                        LocalTime einnahmeZeit = LocalTime.parse(xmlEinnahme.getAttributes().item(EINNAHME_ZEIT).getNodeValue());
                        String einnahmeDosis =  xmlEinnahme.getAttributes().item(EINNAHME_DOSIS).getNodeValue();
                        m.getEinnahmeProtokoll().addEinnahme(einnahmeZeit, einnahmeDosis);
                    }
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
