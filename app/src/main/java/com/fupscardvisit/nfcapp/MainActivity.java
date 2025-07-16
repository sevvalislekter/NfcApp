package com.fupscardvisit.nfcapp;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText urlEditText;
    private Button button, button2;
    public SharedPreferences sharedPreferences;
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

        if (nfcAdapter == null) {
            Toast.makeText(this, "Bu cihaz nfc desteklemiyor", Toast.LENGTH_LONG).show();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Lütfen nfc özelliğini ayarlardan açın", Toast.LENGTH_LONG).show();
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

            button.setEnabled(false);
            button2.setEnabled(false);
            Toast.makeText(this, "Etiketi yaklaştırın,URL yazılacak.", Toast.LENGTH_LONG).show();
        });

        button.setOnClickListener(v ->
                Toast.makeText(this, "Etiketi yaklaştırın,içerik okunacak.", Toast.LENGTH_LONG).show()
        );
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
                Intent writeIntent = new Intent(this, WriteActivity.class);
                writeIntent.putExtras(intent);
                startActivity(writeIntent);
            } else {
                Intent readIntent = new Intent(this, ReadActivity.class);
                readIntent.putExtras(intent);
                startActivity(readIntent);
            }
        }
    }
}