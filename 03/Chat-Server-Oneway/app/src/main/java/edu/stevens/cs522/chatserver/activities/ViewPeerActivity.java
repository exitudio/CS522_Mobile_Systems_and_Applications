package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_ID_KEY = "peer_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        long peerId = getIntent().getExtras().getLong(PEER_ID_KEY);

        MessagesDbAdapter messagesDbAdapter = new MessagesDbAdapter(this);
        messagesDbAdapter.open();
        Peer peer = messagesDbAdapter.fetchPeer(peerId);
        if(peer!=null){
            Log.i("viewPeerActivity",peer.port+"");
            ((TextView) findViewById(R.id.view_user_name)).setText(peer.name);
            ((TextView) findViewById(R.id.view_address)).setText(peer.address.toString());
            ((TextView) findViewById(R.id.view_port)).setText(Integer.toString(peer.port));
            ((TextView) findViewById(R.id.view_timestamp)).setText(peer.timestamp.toString());
        }
        messagesDbAdapter.close();
    }

}
