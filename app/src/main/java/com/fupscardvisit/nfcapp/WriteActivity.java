package com.fupscardvisit.nfcapp;
import android.content.Intent;
import android.nfc.Tag;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.fupscardvisit.nfcapp.utils.WriteUtil;
public class WriteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String url = intent.getStringExtra("url");
        if (tag != null && url != null) {
            WriteUtil.writeToNfc(this, tag, url);
        }
        finish();
    }
}
