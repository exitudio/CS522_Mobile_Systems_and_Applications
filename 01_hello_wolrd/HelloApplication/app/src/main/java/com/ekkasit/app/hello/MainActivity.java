package com.ekkasit.app.hello;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Context _this = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String sendText = ((EditText) findViewById(R.id.ipText)).getText().toString();

                Intent showTextActivity = new Intent(_this,ShowTextActivity.class);
                showTextActivity.putExtra(Communication.SEND_TEXT.toString(),sendText);
                startActivity(showTextActivity);
            }
        });
    }
}
