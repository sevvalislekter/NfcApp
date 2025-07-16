package com.fupscardvisit.nfcapp.utils;
import android.content.Context;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
public class WriteUtil {
    private static final String TAG = "NFC_WRITE";
    public static void writeToNfc(Context context, Tag tag, String url) {
        NdefRecord uriRecord = NdefRecord.createUri(Uri.parse(url));
        NdefMessage message = new NdefMessage(new NdefRecord[]{uriRecord});
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                ndef.writeNdefMessage(message);
                ndef.close();
                Toast.makeText(context, "URL başarıyla yazıldı.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Etiket NDEF desteklemiyor.", Toast.LENGTH_LONG).show();
            }
        } catch (IOException | FormatException e) {
            Log.e(TAG, "Yazma hatası: " + e.getMessage(), e);
            Toast.makeText(context, "Yazma hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}