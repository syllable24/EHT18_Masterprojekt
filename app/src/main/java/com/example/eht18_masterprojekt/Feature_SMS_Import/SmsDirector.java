package com.example.eht18_masterprojekt.Feature_SMS_Import;

import android.content.Context;

import com.example.eht18_masterprojekt.R;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.stream.StreamSource;
import mf.javax.xml.validation.Schema;
import mf.javax.xml.validation.SchemaFactory;
import mf.javax.xml.validation.Validator;
import mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory;

import static com.example.eht18_masterprojekt.Feature_SMS_Import.SmsType.HL7v3;
import static com.example.eht18_masterprojekt.Feature_SMS_Import.SmsType.XML;

class SmsDirector {

    SmsBuilder builder;
    SmsType s;
    Context context;

    public SmsDirector(List<String> rawSMS, Context ctx) {
        context = ctx;
        String cleanedRawSms = removeControlSymbols(rawSMS.get(SMS.BODY));
        Object smsContent = querySmsTypeAndContent(cleanedRawSms);
        if (s == XML){
            Date receivedAt = new Date(Long.parseLong(rawSMS.get(SMS.DATE)));
            String address = rawSMS.get(SMS.ADDRESS);
            this.builder = new SMS.XmlSmsBuilder(address,receivedAt ,(Document) smsContent);
        }
        else if(s == HL7v3){
            //this.builder = new SMS.Hl7v3SmsBuilder(qSMS);
        }
        else if (s == null){
            throw new IllegalArgumentException("Unknown SMS Format");
        }
    }

    public SMS getSms() {
        builder.buildMedikamente();
        //builder.buildOrdinationsInformationen();
        return builder.getSMS();
    }

    public Object querySmsTypeAndContent(String rawSMS) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringElementContentWhitespace(true);

            Source xmlSource = new StreamSource(new StringReader(rawSMS));
            Source schemaSource = new StreamSource(context.getResources().openRawResource(R.raw.xml_sms_schema));
            SchemaFactory sf = new XMLSchemaFactory();
            Schema xmlSchema = sf.newSchema(schemaSource);

            Validator v = xmlSchema.newValidator();
            v.validate(xmlSource); // Wenn die Validierung nicht funktioniert wird eine SAXException geworfen.

            s = SmsType.XML;

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document xmlDocument = dBuilder.parse(new InputSource(new StringReader(rawSMS)));

            return xmlDocument;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        //TODO: Add Query for HL7v3

        return null;
    }

    /**
     * Entfernen von Steuerzeichen aus einem String.
     * @param input
     * @return Bereinigten String
     */
    private String removeControlSymbols(String input){
        return input.replaceAll("\\p{Cntrl}", "");
    }

}
