package com.fupscardvisit.nfcapp;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fupscardvisit.nfcapp.utils.ReadUtil;
public class ReadActivity extends AppCompatActivity {
    public EditText urlEditText;
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlEditText=findViewById(R.id.urlEditText);
        String url= ReadUtil.readUrl(getIntent());
        if(url!=null){
            urlEditText.setText(url);
            Toast.makeText(this,"Url okundu",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
        }else {
            Toast.makeText(this,"Url okunmadı",Toast.LENGTH_LONG).show();
        }
    }
}