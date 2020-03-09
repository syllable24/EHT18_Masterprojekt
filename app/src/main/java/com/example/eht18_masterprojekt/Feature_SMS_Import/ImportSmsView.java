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
        //sendSms();
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
            ArrayList msgParts = sms.divideMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<SMS>\n" +
                        "\t<Medikamente>\n" +
                        "\t\t<Medikament bezeichnung=\"Aspirin\" einheit=\"Tablette\">    \t\t    \t\t\n" +
                        "\t\t\t<Einnahme zeit=\"08:00\" dosis=\"1\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"10:00\" dosis=\"2\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"13:00\" dosis=\"3\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"17:30\" dosis=\"4\"/>\t\t\t    \t\t\n" +
                        "\t\t</Medikament>\n" +
                        "\t\t\n" +
                        "\t\t<Medikament bezeichnung=\"Paracetamol\" einheit=\"mg\">    \t\t    \t\t\n" +
                        "\t\t\t<Einnahme zeit=\"08:00\" dosis=\"15\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"09:00\" dosis=\"50\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"11:00\" dosis=\"100\"/>\t\t\t\n" +
                        "\t\t</Medikament>\t\n" +
                        "\t\t\n" +
                        "\t\t<Medikament bezeichnung=\"Salzlösung\" einheit=\"ml\">\n" +
                        "\t\t\t<Einnahme zeit=\"22:00\" dosis=\"1000\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"13:00\" dosis=\"2000\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"09:00\" dosis=\"1500\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"10:30\" dosis=\"250\"/>\t\t\t    \t\t\n" +
                        "\t\t</Medikament>\n" +
                        "\t\t\n" +
                        "\t\t<Medikament bezeichnung=\"Hustensaft\" einheit=\"Löffel\">    \t\t    \t\t\t\t\t\n" +
                        "\t\t\t<Einnahme zeit=\"17:32\" dosis=\"1,5\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"13:33\" dosis=\"2,3\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"11:40\" dosis=\"1,7\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"10:40\" dosis=\"2,1\"/>\n" +
                        "\t\t\t<Einnahme zeit=\"09:40\" dosis=\"4\"/>\n" +
                        "\t\t</Medikament>\t\n" +
                        "\t</Medikamente>\n" +
                        "\n" +
                        "\t<Ordination arzt=\"Dr. Mustermann\"/>\t\t\n" +
                        "</SMS>"
            );

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
