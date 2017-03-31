package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.PeerManager;

import static android.R.attr.id;

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
        }else{
            Log.i("viewPeerActivity",peer.port+"");
            ((TextView) findViewById(R.id.view_user_name)).setText(peer.name);
            ((TextView) findViewById(R.id.view_address)).setText(peer.address.toString());
            ((TextView) findViewById(R.id.view_port)).setText(Integer.toString(peer.port));
            ((TextView) findViewById(R.id.view_timestamp)).setText(peer.timestamp.toString());
        }

        // TODO init the UI

    }

}
