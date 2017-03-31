package edu.stevens.cs522.chatserver.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.CursorAdapter;

import java.util.List;

import edu.stevens.cs522.chatserver.async.AsyncContentResolver;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IEntityCreator;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chatserver.async.SimpleQueryBuilder;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;


/**
 * Created by dduggan.
 */

public class PeerManager extends Manager<Peer> {

    public static final int LOADER_ID = 2;

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public PeerManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllPeersAsync(IQueryListener<Peer> listener) {
        QueryBuilder.executeQuery(
                tag,
                context,
                PeerContract.CONTENT_URI, LOADER_ID,
                new IEntityCreator<Peer>() {
                    @Override
                    public Peer create(Cursor cursor) {
                        Log.i(this.getClass().toString(), "IEntity");
                        return null;
                    }
                },
                listener
        );
    }

    public void getPeerAsync(long id, final IContinue<Peer> callback) {
        // TODO need to check that peer is not null (not in database)
        //slide68-69
        SimpleQueryBuilder.executeQuery(context, PeerContract.CONTENT_URI(id),
                new IEntityCreator<Peer>() {
                    @Override
                    public Peer create(Cursor cursor) {
                        Log.i(tag,"getPeerAsync : create");
                        return new Peer(cursor);
                    }
                },  new SimpleQueryBuilder.ISimpleQueryListener<Peer>() {
                    public void handleResults(List<Peer> peers) {
                        Log.i(tag,"getPeerAsync : handleResults"+peers.size());
                        for(Peer peer :peers){
                            Log.i(tag,"getPeerAsync : peer name:"+peer.name);
                        }
                        if( peers.size()>0) {
                            callback.kontinue(peers.get(0));
                        }else{
                            callback.kontinue(null);
                        }
                    }
                });
    }

    public void persistAsync(final Peer peer, final IContinue<Long> callback) {
        //slide 58-59
        ContentValues values = new ContentValues();
        peer.writeToProvider(values);

        getAsyncResolver().insertAsync(PeerContract.CONTENT_URI, values,
            new IContinue<Uri>() {
                public void kontinue(Uri uri) {
                    Log.i(this.getClass().toString(),"kontinue:"+uri);
                    peer.id = PeerContract.getId(uri);
                    callback.kontinue(peer.id);
                }
            });
    }

}
