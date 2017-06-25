package edu.stevens.cs522.chat.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.stevens.cs522.chat.contracts.BaseContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String MESSAGE_CONTENT_PATH_SYNC = MessageContract.CONTENT_PATH_SYNC;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 15;

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int MESSAGES_SYNC = 3;
    private static final int PEERS_ALL_ROWS = 4;
    private static final int PEERS_SINGLE_ROW = 5;

    public static class DbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE = "CREATE TABLE ";
        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO initialize database tables
            db.execSQL(
                    DATABASE_CREATE +MESSAGES_TABLE+
                            " ("+ MessageContract._ID+" INTEGER PRIMARY KEY, "+
                            MessageContract.SEQUENCE_NUMBER+" INTEGER, "+
                            MessageContract.MESSAGE_TEXT+" TEXT, "+
                            MessageContract.CHAT_ROOM+" TEXT, "+
                            MessageContract.TIMESTAMP+" INTEGER, "+
                            MessageContract.LATITUDE+" NUMERIC, "+
                            MessageContract.LONGITUDE+" NUMERIC, "+
                            MessageContract.SENDER+" TEXT NOT NULL, "+
                            MessageContract.SENDER_ID+" INTEGER, " +
                            "FOREIGN KEY ("+ MessageContract.SENDER+") REFERENCES "+PEERS_TABLE+"("+ PeerContract.NAME+") ON DELETE CASCADE )"
            );
            db.execSQL(
                    DATABASE_CREATE +PEERS_TABLE+
                            " ("+ PeerContract._ID+", "+
                            PeerContract.NAME+" TEXT PRIMARY KEY, "+
                            PeerContract.TIMESTAMP+" INTEGER, "+
                            PeerContract.LATITUDE+" NUMERIC, "+
                            PeerContract.LONGITUDE+" NUMERIC)"
            );
            db.execSQL("CREATE INDEX AuthorsBookIndex ON "+MESSAGES_TABLE+"("+ MessageContract.SENDER_ID+")");
            Log.i("onCreate DB","created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO upgrade database if necessary
            Log.i("DB","onUpgrade");
            db.execSQL("DROP TABLE IF EXISTS "+MESSAGES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+PEERS_TABLE);
            onCreate(db);
        }
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL("PRAGMA	foreign_keys=ON;");
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_SYNC, MESSAGES_SYNC);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    public void logAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursorMessage = db.rawQuery("SELECT * FROM "+MESSAGES_TABLE,null);
        if(cursorMessage.moveToFirst()){
            do{
                ChatMessage message = new ChatMessage(cursorMessage);
                Log.i("***** MESSAGE ****","id:"+message.id+", seqNum:"+message.seqNum+", messageText:"+message.messageText+", timestamp:"+message.timestamp+", latitude:"+message.latitude+", longitude:"+message.longitude+", sender:"+message.sender+", senderId:"+message.senderId);
            }while(cursorMessage.moveToNext());
        }
        Cursor cursorPeer = db.rawQuery("SELECT * FROM "+PEERS_TABLE,null);
        if(cursorPeer.moveToFirst()){
            do{
                Peer peer = new Peer(cursorPeer);
                Log.i("***** PEER ****","id:"+peer.id+", name:"+peer.name+", latitude:"+peer.latitude+", longitude:"+peer.longitude+", timestamp:"+peer.timestamp);
            }while(cursorPeer.moveToNext());
        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(this.getClass().toString(),"insert uri"+uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new message.
                // Make sure to notify any observers
                long messageId = db.insert(MESSAGES_TABLE, null, values);
//                logAll();
                if(messageId>0){
                    Uri instanceUri = MessageContract.CONTENT_URI(messageId);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri,null);
                    Log.i(this.getClass().toString(),"notifyChange uri:"+instanceUri);
                    return instanceUri;
                }
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new peer.
                // Make sure to notify any observers
//                long peerId = db.insert(PEERS_TABLE, null, values);

                long peerId;
                String sql = "SELECT * FROM "+PEERS_TABLE+" WHERE "+ PeerContract.NAME+" = ?";
                String[] queryArgs = new String[]{PeerContract.getName(values)};
                Cursor cursor = db.rawQuery(sql, queryArgs);
                if (cursor == null || !cursor.moveToFirst()) {
                    //Insert new
                    peerId = db.insert(PEERS_TABLE, null, values);
                } else {
                    //Update
                    String clause = PeerContract.NAME+" = ? ";
                    peerId = db.update(PEERS_TABLE, values,clause,queryArgs);
                }

//                logAll();
                if(peerId>0){
                    Uri instanceUri = PeerContract.CONTENT_URI(peerId);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri,null);
                    return instanceUri;
                }
            case MESSAGES_SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i(this.getClass().toString(),"query() uri="+uri);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle query of all messages.
                //return db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                Log.i(this.getClass().toString(),"uri:"+MESSAGES_TABLE+" :: "+uri);
                logAll();
                //cursor.setNotificationUri(getContext().getContentResolver(),uri);
                return cursor;
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle query of all peers.
                cursor = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                Log.i(this.getClass().toString(),"uri:"+PEERS_ALL_ROWS+" :: "+uri);
                if(cursor.moveToFirst()) {
                    Peer peer2 = new Peer(cursor);
                    Log.i(this.getClass().toString(), "query() PEERS_ALL_ROWS, peer.timestamp=" + peer2.timestamp);
                }
                logAll();
                //cursor.setNotificationUri(getContext().getContentResolver(),uri);
                return cursor;
            case MESSAGES_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific message.
                throw new UnsupportedOperationException("Not yet implemented");
            case PEERS_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific peer.
                long peerId = PeerContract.getId(uri);
                cursor = db.rawQuery("SELECT * FROM "+PEERS_TABLE+" WHERE "+ PeerContract._ID+" = ?", new String[]{Long.toString(peerId)});
                if(cursor.moveToFirst()){
                    Peer peer = new Peer(cursor);
                    Log.i("ChatProvider.query()",peer.name+":"+peer.timestamp);
                }
                return cursor;
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] records) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.i("ChatProvider","uri="+uri);
        Log.i("ChatProvider","uriMatcher.match(uri)="+uriMatcher.match(uri));

        switch (uriMatcher.match(uri)) {
            case MESSAGES_SYNC:
                /*
                 * Do all of this in a single transaction.
                 */
                db.beginTransaction();
                try {

                    /*
                     * Delete the first N messages with sequence number = 0, where N = records.length.
                     */
                    int numReplacedMessages = Integer.parseInt(uri.getLastPathSegment());

                    String[] columns = {MessageContract.ID};
                    String selection = MessageContract.SEQUENCE_NUMBER + "=0";
                    Cursor cursor = db.query(MESSAGES_TABLE, columns, selection, null, null, null, MessageContract.TIMESTAMP+" DESC");
                    try {
                        if (numReplacedMessages > 0 && cursor.moveToFirst()) {
                            do {
                                String deleteSelection = MessageContract.ID + "=" + Long.toString(cursor.getLong(0));
                                db.delete(MESSAGES_TABLE, deleteSelection, null);
                                numReplacedMessages--;
                            } while (numReplacedMessages > 0 && cursor.moveToNext());
                        }
                    } finally {
                        cursor.close();
                    }

                    /*
                     * Insert the messages downloaded from server, which will include replacements for deleted records.
                     */
                    for (ContentValues record : records) {
                        printContentValues(record);
                        if (db.insert(MESSAGES_TABLE, null, record) == -1) {
                            throw new IllegalStateException("Failure to insert updated chat message record!");
                        }
                        Log.i(this.getClass().toString(),"++++++");
                    }
                    db.setTransactionSuccessful();

//                    logAll();
                    Uri instanceUri = MessageContract.CONTENT_URI_SYNC(numReplacedMessages);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri,null);
                    Log.i(this.getClass().toString(),"notifyChange uri:"+instanceUri);
                    return 1;
                } finally {
                    db.endTransaction();
                    logAll();
                }

                // TODO Make sure to notify any observers

            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    public void printContentValues(ContentValues vals) {
        Set<Map.Entry<String, Object>> s = vals.valueSet();
        Iterator itr = s.iterator();

        Log.d(this.getClass().toString(), "ContentValue Length :: " + vals.size());

        while (itr.hasNext()) {
            Map.Entry me = (Map.Entry) itr.next();
            String key = me.getKey().toString();
            Object value = me.getValue();

            Log.d(this.getClass().toString(), "Key:" + key + ", values:" + (String) (value == null ? null : value.toString()));
        }
        Log.d(this.getClass().toString(), "__________________");
    }
}
