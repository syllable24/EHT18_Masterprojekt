package com.example.eht18_masterprojekt.Feature_SMS_Import;

import android.content.Context;
import android.provider.Telephony;
import android.util.Log;

import com.example.eht18_masterprojekt.R;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.stream.StreamSource;
import mf.javax.xml.validation.Schema;
import mf.javax.xml.validation.SchemaFactory;
import mf.javax.xml.validation.Validator;
import mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory;

abstract class SmsBuilder {

    abstract void buildMedikamente();
    abstract void buildOrdinationsInformationen();
    abstract SMS getSMS();
}
