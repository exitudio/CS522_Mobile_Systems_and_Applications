package edu.stevens.cs522.chat.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.settings.Settings;

/**
 * Created by dduggan.
 *
 * The API used by the Web service for synchronizing messages with server.
 * It is assumed that all operations are invoked on the background thread for Web services.
 */

public class RequestManager extends Manager<ChatMessage> {

    private static final int LOADER_ID = 1;

    private static final String MAX_SEQNO_COLUMN = "max_seq_num";

    private static final IEntityCreator<ChatMessage> creator = new IEntityCreator<ChatMessage>() {
        @Override
        public ChatMessage create(Cursor cursor) {
            return new ChatMessage(cursor);
        }
    };

    public RequestManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void persist(ChatMessage message) {
        //insert peer
        Peer sender = new Peer();
        sender.name = Settings.getChatName(context);
        sender.latitude = message.latitude;
        sender.longitude = message.longitude;
        sender.timestamp = message.timestamp;
        ContentValues peerValues = new ContentValues();
        sender.writeToProvider(peerValues);
        Uri peerUri = getSyncResolver().insert(PeerContract.CONTENT_URI, peerValues);
        long senderId = PeerContract.getId(peerUri);

        //insert message
        ContentValues values = new ContentValues();
        message.senderId = senderId;
        message.writeToProvider(values);
        Uri uri = getSyncResolver().insert(MessageContract.CONTENT_URI, values);
        message.id = MessageContract.getId(uri);
    }

    /**
     * Get the last sequence number in the messages database.
     * @return
     */
    public long getLastSequenceNumber() {
        String selection = MessageContract.SEQUENCE_NUMBER + "<>0";
        String[] columns = { String.format("MAX(%s) as %s", MessageContract.SEQUENCE_NUMBER, MAX_SEQNO_COLUMN) };
        Cursor cursor = getSyncResolver().query(MessageContract.CONTENT_URI, columns, selection, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            } else {
                // Empty database
                return 0;
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Get all unsent messages, identified by sequence number = 0.
     * @return
     */
    public TypedCursor<ChatMessage> getUnsentMessages() {
        String selection = MessageContract.SEQUENCE_NUMBER + "=0";
        String[] columns = MessageContract.COLUMNS;
        Cursor cursor = getSyncResolver().query(MessageContract.CONTENT_URI, columns, selection, null, MessageContract.TIMESTAMP);
        return new TypedCursor(cursor, creator);
    }

    /**
     * Sync messages with server.  Replacement of messages should be done as part of a transaction
     * because the user may be adding chat messages as we are updating.
     * @param numMessagesReplaced
     * @param downloadedMessages
     */
    public void syncMessages(int numMessagesReplaced, List<ChatMessage> downloadedMessages) {
        ContentValues[] records = new ContentValues[downloadedMessages.size()];
        for (int ix=0; ix<downloadedMessages.size(); ix++) {
            records[ix] = new ContentValues();
            downloadedMessages.get(ix).writeToProvider(records[ix]);
        }
        Log.i("RequestManager",MessageContract.CONTENT_URI_SYNC(numMessagesReplaced)+"");
        getSyncResolver().bulkInsert(MessageContract.CONTENT_URI_SYNC(numMessagesReplaced), records);
    }

    public void deletePeers() {
        // TODO
    }

    public void persist(Peer peer) {
        // TODO
    }
}
