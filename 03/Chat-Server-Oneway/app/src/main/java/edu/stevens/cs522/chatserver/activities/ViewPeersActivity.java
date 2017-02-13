package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    private ArrayList<Peer> peers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        MessagesDbAdapter messagesDbAdapter = new MessagesDbAdapter(this);
        messagesDbAdapter.open();
        peers = messagesDbAdapter.fetchAllPeers();
        messagesDbAdapter.close();

        ArrayList<String> peerStrings = new ArrayList<String>();
        for(int i=0; i<peers.size(); i++){
            peerStrings.add( peers.get(i).name);
        }

        ListView peerListView = (ListView) findViewById(R.id.peerList);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, peerStrings);
        peerListView.setAdapter(arrayAdapter);

        peerListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_ID_KEY, peers.get(position).id);
        startActivity(intent);
    }

}
