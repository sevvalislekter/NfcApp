package com.fupscardvisit.nfcapp.utils;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
public class ReadUtil {
    public static String readUrl(Intent intent) {
        Parcelable[] msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (msgs != null && msgs.length > 0) {
            NdefMessage msg = (NdefMessage) msgs[0];
            NdefRecord record = msg.getRecords()[0];
            Uri uri = record.toUri();
            if (uri != null) {
                return uri.toString();
            }
        }
        return null;
    }
}
