package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.managers.PeerManager;
import edu.stevens.cs522.chatserver.managers.TypedCursor;
import edu.stevens.cs522.chatserver.util.MessageAdapter;
import edu.stevens.cs522.chatserver.util.PeerAdapter;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, QueryBuilder.IQueryListener<Peer> {

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */

    private PeerManager peerManager;

    private PeerAdapter peerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODO initialize peerAdapter with empty cursor (null)
        peerAdapter = new PeerAdapter(this,null);
        ListView listView = (ListView) findViewById(R.id.peerList);
        listView.setAdapter(peerAdapter);
        listView.setOnItemClickListener(this);

        peerManager = new PeerManager(this);
        peerManager.getAllPeersAsync(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            final Peer peer = new Peer(cursor);
            final Activity _this = this;
            peerManager.getPeerAsync(peer.id, new IContinue<Peer>() {
                @Override
                public void kontinue(Peer value) {
                    if( value!=null) {
                        Intent intent = new Intent(_this, ViewPeerActivity.class);
                        intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
                        startActivity(intent);
                    }else{
                        Toast.makeText(_this,"No Peer found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }

    @Override
    public void handleResults(TypedCursor<Peer> results) {
        peerAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // TODO
    }
}
