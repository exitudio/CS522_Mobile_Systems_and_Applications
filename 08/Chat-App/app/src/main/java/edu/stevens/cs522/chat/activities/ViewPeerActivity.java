package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_KEY = "peer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO init the UI
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }else{
            ((TextView) findViewById(R.id.view_user_name)).setText(peer.name);
            ((TextView) findViewById(R.id.view_timestamp)).setText(peer.timestamp.toString());
        }
    }

}
