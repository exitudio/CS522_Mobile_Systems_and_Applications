package com.ekkasit.app.hello;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_text);
        String sendText = getIntent().getExtras().getString(Communication.SEND_TEXT.toString());
        ((TextView) findViewById(R.id.textView)).setText(sendText);
    }
}
