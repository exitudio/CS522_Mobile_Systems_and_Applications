package edu.stevens.cs522.chatserver.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chatserver.contracts.BaseContract;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;
    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;
    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;
    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;
    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;

    private static final String DATABASE_NAME = "chat.db";
    private static final int DATABASE_VERSION = 6;
    private static final String MESSAGES_TABLE = "messages";
    private static final String PEERS_TABLE = "peers";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int PEERS_ALL_ROWS = 3;
    private static final int PEERS_SINGLE_ROW = 4;

    public static class DbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE = "CREATE TABLE ";

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    DATABASE_CREATE +MESSAGES_TABLE+
                            " ("+ MessageContract._ID+" INTEGER PRIMARY KEY, "+
                            MessageContract.MESSAGE_TEXT+" TEXT, "+
                            MessageContract.TIMESTAMP+" INTEGER, "+
                            MessageContract.SENDER+" TEXT, "+
                            MessageContract.SENDER_ID+" INTEGER NOT NULL, " +
                            "FOREIGN KEY ("+MessageContract.SENDER_ID+") REFERENCES "+PEERS_TABLE+"("+PeerContract._ID+") ON DELETE CASCADE )"
            );
            db.execSQL(
                    DATABASE_CREATE +PEERS_TABLE+
                            " ("+ PeerContract._ID+" INTEGER PRIMARY KEY, "+
                            PeerContract.NAME+" TEXT, "+
                            PeerContract.TIMESTAMP+" INTEGER, "+
                            PeerContract.ADDRESS+" TEXT, "+
                            PeerContract.PORT+" INTEGER)"
            );
            db.execSQL("CREATE INDEX AuthorsBookIndex ON "+MESSAGES_TABLE+"("+MessageContract.SENDER_ID+")");
            Log.i("onCreate DB","created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    public void logAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursorMessage = db.rawQuery("SELECT * FROM "+MESSAGES_TABLE,null);
        if(cursorMessage.moveToFirst()){
            do{
                Message message = new Message(cursorMessage);
                Log.i("***** MESSAGE ****","id:"+message.id+", messageText:"+message.messageText+", timestamp:"+message.timestamp+", sender:"+message.sender+", senderId:"+message.senderId);
            }while(cursorMessage.moveToNext());
        }
        Cursor cursorPeer = db.rawQuery("SELECT * FROM "+PEERS_TABLE,null);
        if(cursorPeer.moveToFirst()){
            do{
                Peer peer = new Peer(cursorPeer);
                Log.i("***** PEER ****","id:"+peer.id+", name:"+peer.name+", timestamp:"+peer.timestamp+", address:"+peer.address+", port:"+peer.port);
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
                String sql = "SELECT * FROM "+PEERS_TABLE+" WHERE "+PeerContract.NAME+" = ?";
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
        Log.i(this.getClass().toString(),"query uri"+uri);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL("PRAGMA	foreign_keys=ON;");

        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                cursor = db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                Log.i(this.getClass().toString(),"uri:"+MESSAGES_TABLE+" :: "+uri);
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                return cursor;
            case PEERS_ALL_ROWS:
                cursor = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                Log.i(this.getClass().toString(),"uri:"+PEERS_ALL_ROWS+" :: "+uri);
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                return cursor;
            case MESSAGES_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific message.
                throw new UnsupportedOperationException("Not yet implemented");
            case PEERS_SINGLE_ROW:
                long peerId = PeerContract.getId(uri);
                Cursor cursorPeer = db.rawQuery("SELECT * FROM "+PEERS_TABLE+" WHERE "+PeerContract._ID+" = ?", new String[]{Long.toString(peerId)});
                if(cursorPeer.moveToFirst()){
                    Peer peer = new Peer(cursorPeer);
                    Log.i("fetchPeer",peer.name+":"+peer.port);
                }
                return cursorPeer;
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

}
