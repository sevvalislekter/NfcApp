package com.fupscardvisit.nfcapp;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import java.io.IOException;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "NFC_APP";
    private String lastWrittenUID = null;

   private  EditText urlEditText;
   private Button button, button2;
   private SharedPreferences sharedPreferences;
   private NfcAdapter nfcAdapter;
   private PendingIntent pendingIntent;
   private IntentFilter[] intentFiltersArray;
   private boolean isWriteMode = false;
   private String urlToWrite = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("NFC_UID_PREFS", MODE_PRIVATE);

        urlEditText = findViewById(R.id.urlEditText);
        button2 = findViewById(R.id.button2);
        button = findViewById(R.id.button);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

       if(nfcAdapter==null){
           Toast.makeText(this,"Bu cihaz nfc desteklemiyor",Toast.LENGTH_LONG).show();
           return;
       }
       if(!nfcAdapter.isEnabled()){
           Toast.makeText(this,"Lütfen nfc özellğini ayarlardan açın",Toast.LENGTH_LONG).show();
           startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
       }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFiltersArray = new IntentFilter[]{tagDetected};


        button2.setOnClickListener(v -> {
            urlToWrite = urlEditText.getText().toString().trim();
            if (urlToWrite.isEmpty()) {
                Toast.makeText(this, "Lütfen yazılacak URL girin.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!urlToWrite.startsWith("http://") && !urlToWrite.startsWith("https://")) {
                urlToWrite = "https://" + urlToWrite;
            }

            isWriteMode = true;
            urlEditText.setEnabled(false);
            // Yazma modunda butonları da devre dışı bırakabilirsiniz
            button.setEnabled(false);
            button2.setEnabled(false);
            Toast.makeText(this, "Etiketi yaklaştırın,URL yazılacak.", Toast.LENGTH_LONG).show();
        });

        button.setOnClickListener(v ->
                Toast.makeText(this, "Etiketi yaklaştırın,içerik okunacak.", Toast.LENGTH_LONG).show()
        );
    }
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (isWriteMode) {
                isWriteMode = false;
                writeToNfc(tag, urlToWrite);
            } else {
                readFromNfc(intent);
            }
        }
    }

    private void writeToNfc(Tag tag, String url) {
            String currentUID = bytesToHex(tag.getId());
            String previousUID = sharedPreferences.getString("lastUID", null);

            if (currentUID.equals(previousUID)) {
                Toast.makeText(this, "Bu etikete zaten yazıldı (önceden kaydedilmiş).", Toast.LENGTH_LONG).show();
                return;
            }

            NdefRecord uriRecord = NdefRecord.createUri(Uri.parse(url));
            NdefMessage message = new NdefMessage(new NdefRecord[]{uriRecord});

            try {
                Ndef ndef = Ndef.get(tag);
                if (ndef != null) {
                    ndef.connect();
                    ndef.writeNdefMessage(message);
                    ndef.close();


                    sharedPreferences.edit().putString("lastUID", currentUID).apply();

                    Toast.makeText(this, "URL başarıyla yazıldı.", Toast.LENGTH_LONG).show();
                    urlEditText.setText("");
                } else {
                    Toast.makeText(this, "Bu etiket yazılamıyor (NDEF desteklemiyor).", Toast.LENGTH_LONG).show();
                }
            } catch (IOException | FormatException e) {
                Log.e(TAG, "Yazma hatası: " + e.getMessage(), e);
                Toast.makeText(this, "Yazma hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {

                 urlEditText.setEnabled(true);
                 button.setEnabled(true);
                 button2.setEnabled(true);
    }
        }

        private void readFromNfc(Intent intent) {
        Parcelable[] Msgs=intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(Msgs !=null && Msgs.length>0){
            NdefMessage msg=(NdefMessage) Msgs[0];
            NdefRecord record=msg.getRecords()[0];
            Uri uri=record.toUri();
            if(uri!=null){
                String url=uri.toString();
                urlEditText.setText(url);
                Toast.makeText(this,"Etiketten url okundu",Toast.LENGTH_LONG).show();
                Intent browserIntent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(browserIntent);
            }
            else{
                Toast.makeText(this,"Url okunamadı",Toast.LENGTH_LONG).show();

            }
        }
        else {
            Toast.makeText(this,"Etiket boş veya okunamadı",Toast.LENGTH_LONG).show();
        }

    }
}
