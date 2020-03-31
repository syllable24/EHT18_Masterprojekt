package com.example.eht18_masterprojekt.Feature_SMS_Import;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.eht18_masterprojekt.Feature_Med_List.MainActivityView;
import com.example.eht18_masterprojekt.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImportSmsView extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String INBOX = "content://sms/inbox";

    Map<String, String> smsMap;
    RecyclerView rvSmsList;
    FloatingActionButton fabRefresh;
    ProgressBar pbCheckedSms;
    Toolbar toolbar;

    private IntentFilter medListInitFilter = new IntentFilter("MedList_Init_Successful");
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ImportSmsView.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivity();
        boolean permission = initPermission();
        registerReceiver(br, medListInitFilter);

        if (permission){ // false: Permissions müssen vom User eingegeben werden -> wird in Callback onRequestPermissionResult behandelt.
            startSmsInit();
        }

        //sendSms("");

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(br, medListInitFilter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startSmsInit();
        }
        else{
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * Darstellen der importierten SMS in RecyclerView
     */
    private void initSmsListView(){
        if (smsMap.size() > 0) {
            SmsImportRecyclerViewAdapter srav = new SmsImportRecyclerViewAdapter(smsMap, this);
            LinearLayoutManager lm = new LinearLayoutManager(this);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                    this,
                    lm.getOrientation());
            rvSmsList.setLayoutManager(lm);
            rvSmsList.addItemDecoration(dividerItemDecoration);
            rvSmsList.setAdapter(srav);

        }
        else{
            Toast.makeText(this, "Keine  gültige SMS gefunden!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Prüfen der READ_SMS Permission. Falls eine SMS gesendet werden soll muss die SEND_SMS permission
     * eingeholt werden.
     */
    private boolean initPermission(){
        String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS};

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
             ||  ( ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(permissions, 1);
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * Ausführen des SMS-Initialisierungsprozesses
     */
    private void startSmsInit(){
        ScanInboxTask task = new ScanInboxTask(this);
        task.execute();
    }

    /**
     * Aktualisieren der Progress Bar des SMS Import.
     * @param addedSms
     * @param maxSms
     */
    private void updateProgressBar(Integer addedSms, int maxSms){
        Log.d("APP", "Number of SMS found: " + addedSms + " Total SMS found: " + maxSms);

        if (pbCheckedSms.getMax() != maxSms) {
            pbCheckedSms.setMax(maxSms);
        }
        pbCheckedSms.setProgress(addedSms);
    }

    /**
     * Setzen der Member-Vars.
     */
    private void initActivity(){
        setContentView(R.layout.activity_import_sms);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvSmsList = findViewById(R.id.rvSmsList);
        fabRefresh = findViewById(R.id.fab);
        pbCheckedSms = findViewById(R.id.pb_checkedSms);

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSmsInit();
            }
        });
    }

    /**
     * Um eingehende SMS zu simulieren. SEND_SMS Permission notwendig.
     */
    private void sendSms(String destinationAddress){
        try {
            Intent intent=new Intent(getApplicationContext(), MainActivityView.class);
            PendingIntent pi= PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
            SmsManager sms=SmsManager.getDefault();

            // HL7v3 CDA SMS
            //ArrayList msgParts = sms.divideMessage("<?xml version=\"1.0\"?> <ClinicalDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\" xmlns:cda=\"urn:hl7-org:v3\" xmlns:sdtc=\"urn:hl7-org:sdtc\"> <realmCode code=\"AT\"/> <typeId root=\"2.16.840.1.113883.1.3\" extension=\"POCD_HD000040\"/> <id root=\"2.16.840.1.113883.3.3208.101.1\" extension=\"20130607100315-CCDA-CCD\"/> <code code=\"57828-6\" displayName=\"Dauermedikationsliste\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/> <title>Dauermedikationsliste</title> <effectiveTime value=\"20200328172200-0600\"/> <confidentialityCode code=\"N\" codeSystem=\"2.16.840.1.113883.5.25\"/> <languageCode code=\"de-AT\"/> <setId root=\"2.16.840.1.113883.4.823.1.12345\" extension=\"1\"/> <versionNumber value=\"1\"/> <recordTarget> <patientRole> <id root=\"2.16.840.1.113883.4.823.1\" extension=\"20130607100800-FakeID\"/> <id root=\"2.16.840.1.113883.9.9.9\" extension=\"Test.Klient@fakemail.com\"/> <addr use=\"HP\"> <streetAddressLine>Radetzkystrasse 11</streetAddressLine> <city>Graz</city> <state>ST</state> <postalCode>8010</postalCode> <country>AT</country> </addr> <telecom value=\"tel:0664123456789\" use=\"HP\"/> <telecom value=\"mailto:Test.Klient@fakemail.com\"></telecom> <patient> <name use=\"L\"> <given>Test</given> <family>Klient</family> </name> <administrativeGenderCode code=\"M\" codeSystem=\"2.16.840.1.113883.5.1\" displayName=\"Male\" codeSystemName=\"AdministrativeGender\"/> <birthTime value=\"19010101\"/> <maritalStatusCode code=\"M\" displayName=\"Married\" codeSystem=\"2.16.840.1.113883.5.2\" codeSystemName=\"MaritalStatus\"/> <raceCode code=\"2106-3\" displayName=\"White\" codeSystem=\"2.16.840.1.113883.6.238\" codeSystemName=\"OMB Standards for Race and Ethnicity\"/> <ethnicGroupCode code=\"2186-5\" displayName=\"Not Hispanic or Latino\" codeSystem=\"2.16.840.1.113883.6.238\" codeSystemName=\"OMB Standards for Race and Ethnicity\"/> <languageCommunication> <languageCode code=\"de\"/> <modeCode code=\"ESP\" displayName=\"Expressed spoken\" codeSystem=\"2.16.840.1.113883.5.60\" codeSystemName=\"LanguageAbilityMode\"/> <proficiencyLevelCode code=\"E\" displayName=\"Excellent\" codeSystem=\"2.16.840.1.113883.5.61\" codeSystemName=\"LanguageAbilityProficiency\"/> </languageCommunication> </patient> </patientRole> </recordTarget> <author> <time value=\"202001011722-0500\"/> <assignedAuthor> <id extension=\"66666\" root=\"2.16.840.1.113883.4.6\"/> <id root=\"2.16.840.1.113883.4.823.1\" extension=\"20130607100800-FakeID\"/> <code code=\"ONESELF\" codeSystem=\"2.16.840.1.113883.5.111\" codeSystemName=\"RoleCode\" displayName=\"self\"/> <addr use=\"HP\"> <streetAddressLine>Radetzkystrasse 15</streetAddressLine> <city>Graz</city> <state>ST</state> <postalCode>8010</postalCode> <country>AT</country> </addr> <telecom value=\"tel:0676 123456789\" use=\"HP\"/> <assignedPerson> <name use=\"L\"> <given>Test</given> <family>Arzt</family> </name> </assignedPerson> </assignedAuthor> </author> <custodian> <assignedCustodian> <representedCustodianOrganization> <id extension=\"44444\" root=\"2.16.840.1.113883.4.6\"/> <name>Testdomain.com</name> <telecom value=\"tel:+1(202)776-7700\" use=\"WP\"/> <addr use=\"WP\"> <streetAddressLine>Radetzkystrasse 20</streetAddressLine> <city>Graz</city> <state>ST</state> <postalCode>8010</postalCode> <country>AT</country> </addr> </representedCustodianOrganization> </assignedCustodian> </custodian> <component> <StructuredBody> <confidentialityCode code=\"N\" codeSystem=\"2.16.840.1.113883.5.25\"/> <languageCode code=\"de-AT\"/> <section> <code code=\"57828-6\" displayName=\"Dauermedikationsliste\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/> <title>Dauermedikationsliste</title> <text> <table border=\"1\"> <thead> <tr> <th>Medikament</th> <th>Einnahmezeit</th> <th>Dosis</th> <th>Einheit</th> </tr> </thead> <tbody> <tr> <td>Aspirin</td> <td>08:00</td> <td>1</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>09:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>10:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>11:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>12:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>13:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>14:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>15:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>16:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>17:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>18:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>19:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Aspirin</td> <td>20:00</td> <td>2</td> <td>Tablette</td> </tr> <tr> <td>Paracetamol</td> <td>13:00</td> <td>15</td> <td>mg</td> </tr> <tr> <td>Salzlösung</td> <td>13:00</td> <td>2000</td> <td>ml</td> </tr> <tr> <td>Hustensaft</td> <td>14:00</td> <td>1,5</td> <td>Löffel</td> </tr> </tbody> </table> </text> </section> </StructuredBody> </component> </ClinicalDocument>");

            // Plain XML SMS
            ArrayList msgParts = sms.divideMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SMS xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"SmsTemplate.xsd\"> <Medikamente> <Medikament bezeichnung=\"Aspirin\" einheit=\"Tablette\"> <Einnahme zeit=\"08:00\" dosis=\"1\"/> <Einnahme zeit=\"09:00\" dosis=\"2\"/> <Einnahme zeit=\"10:00\" dosis=\"2\"/> <Einnahme zeit=\"11:00\" dosis=\"2\"/> <Einnahme zeit=\"12:00\" dosis=\"2\"/> <Einnahme zeit=\"13:00\" dosis=\"2\"/> <Einnahme zeit=\"14:00\" dosis=\"2\"/> <Einnahme zeit=\"15:00\" dosis=\"2\"/> <Einnahme zeit=\"16:00\" dosis=\"2\"/> <Einnahme zeit=\"17:00\" dosis=\"2\"/> <Einnahme zeit=\"18:00\" dosis=\"2\"/> <Einnahme zeit=\"19:00\" dosis=\"2\"/> <Einnahme zeit=\"20:00\" dosis=\"2\"/> </Medikament> <Medikament bezeichnung=\"Paracetamol\" einheit=\"mg\"> <Einnahme zeit=\"13:00\" dosis=\"15\"/> </Medikament> <Medikament bezeichnung=\"Salzlösung\" einheit=\"ml\"> <Einnahme zeit=\"13:00\" dosis=\"2000\"/> </Medikament> <Medikament bezeichnung=\"Hustensaft\" einheit=\"Löffel\"> <Einnahme zeit=\"13:00\" dosis=\"1,5\"/> </Medikament> </Medikamente> <Ordination arzt=\"Dr. Mustermann\"/> </SMS>");

            sms.sendMultipartTextMessage(destinationAddress, null, msgParts, null, null);

            Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("SMS-Send", e.getMessage());
        }
    }

    private class ScanInboxTask extends AsyncTask<Void, Integer, Map<String, String>> {
        Context ctx;
        List<String> rawSms = new ArrayList<>();

        Map<String, String> tempSmsMap = new LinkedHashMap<String, String>();
        Integer addedSmsCount = new Integer(0);

        public ScanInboxTask(Context ctx){
            super();
            this.ctx = ctx;
        }

        /**
         * Einlesen importierbarer roher SMS aus Inbox.
         *
         * SMS-Struktur:
         * 0 _id                 11 subject
         * 1 thread_id           12 body
         * 2 address             13 service_center
         * 3 person              14 locked
         * 4 date                15 sub_id
         * 5 date_sent           16 error_code
         * 6 protocol            17 creator
         * 7 read                18 seen
         * 8 status              19 priority
         * 9 type
         * 10 reply_path_present
         *
         * @return Liste von importierbaren SMS.
         */
        @Override
        protected Map<String, String> doInBackground(Void ... voids) {
            Cursor c = ctx.getContentResolver().query(Uri.parse(INBOX), null, null, null);
            Log.d("APP", c.getCount() + " SMS in Inbox");

            if (c.moveToFirst()) {
                do {
                    for (int i = 0; i < c.getColumnCount(); i++) {
                        rawSms.add(c.getString(i));
                    }

                    String receivedFrom = rawSms.get(SMS.ADDRESS);  //Für die Anzeige des Kontaktnamens: (rawSms.get(SMS.PERSON) == null) ? rawSms.get(SMS.ADDRESS) : rawSms.get(SMS.PERSON);
                    String receivedAt = rawSms.get(SMS.DATE);
                    String smsBody = rawSms.get(SMS.BODY);

                    String firstLine[] = smsBody.split("\\R", 1);
                    if(firstLine[0].contains("<?xml version=\"1.0\"")) {
                        tempSmsMap.put(receivedFrom + " " + receivedAt, smsBody);
                        addedSmsCount++;
                        Log.d("APP", "SmsCount : " + addedSmsCount);

                        if ((addedSmsCount % 10 == 0) || (addedSmsCount == c.getCount())) {
                            publishProgress(addedSmsCount, c.getCount());
                        }
                    }
                    rawSms.clear();
                } while (c.moveToNext());
            }
            c.close();
            return tempSmsMap;
        }

        /**
         * Aktualisieren der Progress Bar.
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("APP", "Number of valid SMS found: " + values[0] + " Total SMS found: " + values[1]);
            updateProgressBar(addedSmsCount, values[1]);
        }

        /**
         * Anzeigen der importierbaren SMS.
         * @param result
         */
        @Override
        protected void onPostExecute(Map<String, String> result) {
            super.onPostExecute(result);
            smsMap = result;
            initSmsListView();
        }
    }
}
