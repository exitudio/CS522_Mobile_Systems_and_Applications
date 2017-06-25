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

import edu.stevens.cs522.chat.contracts.BaseContract;
import edu.stevens.cs522.chat.contracts.ChatroomContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.ChatRoom;
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

    private static final String CHATROOM_CONTENT_PATH = ChatroomContract.CONTENT_PATH;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 10;

    private static final String CHATROOMS_TABLE = "chatrooms";

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";

    private static final String CHATROOM_NAME_INDEX = "chatroom_name_index";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int MESSAGES_SYNC = 3;
    private static final int PEERS_ALL_ROWS = 4;
    private static final int PEERS_SINGLE_ROW = 5;
    private static final int CHATROOMS_ALL_ROWS = 6;

    public static class DbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE = "CREATE TABLE ";
        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + CHATROOMS_TABLE + " ("
                    + ChatroomContract.ID + " INTEGER PRIMARY KEY,"
                    + ChatroomContract.NAME + " TEXT NOT NULL"
                    + ");");
            ContentValues values = new ContentValues();
            values.put(ChatroomContract.NAME, "_default");
            db.insert(CHATROOMS_TABLE, null, values);
            values.put(ChatroomContract.NAME, "room1");
            db.insert(CHATROOMS_TABLE, null, values);
            values.put(ChatroomContract.NAME, "room2");
            db.insert(CHATROOMS_TABLE, null, values);
            values.put(ChatroomContract.NAME, "room3");
            db.insert(CHATROOMS_TABLE, null, values);
            values.put(ChatroomContract.NAME, "room4");
            db.insert(CHATROOMS_TABLE, null, values);
            // TODO other chatroom names

            // TODO initialize other database tables
            db.execSQL(
                    DATABASE_CREATE +MESSAGES_TABLE+
                            " ("+ MessageContract._ID+" INTEGER PRIMARY KEY, "+
                            MessageContract.SEQUENCE_NUMBER+" INTEGER, "+
                            MessageContract.MESSAGE_TEXT+" TEXT, "+
                            MessageContract.CHAT_ROOM+" TEXT, "+
                            MessageContract.CHAT_ROOM_ID+" INTEGER, "+
                            MessageContract.TIMESTAMP+" INTEGER, "+
                            MessageContract.LATITUDE+" NUMERIC, "+
                            MessageContract.LONGITUDE+" NUMERIC, "+
                            MessageContract.SENDER+" TEXT NOT NULL, "+
                            MessageContract.SENDER_ID+" INTEGER, " +
                            "FOREIGN KEY ("+ MessageContract.SENDER+") REFERENCES "+PEERS_TABLE+"("+ PeerContract.NAME+") ON DELETE CASCADE, "+
                            "FOREIGN KEY ("+ MessageContract.CHAT_ROOM_ID+") REFERENCES "+CHATROOMS_TABLE+"("+ ChatroomContract.ID+"))"
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
            db.execSQL("DROP TABLE IF EXISTS "+CHATROOMS_TABLE);
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
        uriMatcher.addURI(AUTHORITY, CHATROOM_CONTENT_PATH, CHATROOMS_ALL_ROWS);
    }
    public void logAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursorChatRoom = db.rawQuery("SELECT * FROM "+CHATROOMS_TABLE,null);
        if(cursorChatRoom.moveToFirst()){
            do{
                ChatRoom chatRoom = new ChatRoom(cursorChatRoom);
                Log.i("***** CHAT_ROOM ****","id:"+chatRoom.id+", name:"+chatRoom.name);
            }while(cursorChatRoom.moveToNext());
        }
        Cursor cursorMessage = db.rawQuery("SELECT * FROM "+MESSAGES_TABLE,null);
        if(cursorMessage.moveToFirst()){
            do{
                ChatMessage message = new ChatMessage(cursorMessage);
                Log.i("***** MESSAGE ****","id:"+message.id+", seqNum:"+message.seqNum+", messageText:"+message.messageText+", timestamp:"+message.timestamp+", latitude:"+message.latitude+", longitude:"+message.longitude+", sender:"+message.sender+", senderId:"+message.senderId+", chatroom="+message.chatRoom+", chatroomId="+message.chatRoomId);
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
//                MessageContract.putChatRoomID();

                //add chatroomid
                String chatRoomQuerySQL = "SELECT * FROM "+CHATROOMS_TABLE+" WHERE "+ ChatroomContract.NAME+" = ?";
                String[] chatRoomQueryArgs = new String[]{MessageContract.getChatRoom(values)};
                Cursor chatRoomQueryCursor = db.rawQuery(chatRoomQuerySQL, chatRoomQueryArgs);
                chatRoomQueryCursor.moveToFirst();
                ChatRoom chatRoom = new ChatRoom(chatRoomQueryCursor);
                MessageContract.putChatRoomID(values,chatRoom.id);

                //insert message
                long messageId = db.insert(MESSAGES_TABLE, null, values);
                logAll();
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
                if(peerId>0){
                    Uri instanceUri = PeerContract.CONTENT_URI(peerId);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri,null);
                    return instanceUri;
                }
//            case CHATROOMS_ALL_ROWS:
//                // TODO: Implement this to handle requests to insert a new chatroom.
//                // Make sure to notify any observers
//                throw new UnsupportedOperationException("Not yet implemented");
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
        if(selectionArgs!=null) {
            Log.i(this.getClass().toString(), "query() selection=" + selection + ", selectionArgs=" + selectionArgs[0]+", uriMatcher.match(uri)="+uriMatcher.match(uri));
        }
        logAll();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle query of all messages.
                return db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle query of all peers.
                return db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            case CHATROOMS_ALL_ROWS:
                // TODO: Implement this to handle query of all chatrooms.
                return db.query(CHATROOMS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            case MESSAGES_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific message.
                long chatRoomID = MessageContract.getId(uri);
                Log.i(this.getClass().toString()," quer() chatRoomID="+chatRoomID);
                return db.query(MESSAGES_TABLE, projection, MessageContract.CHAT_ROOM_ID+"=?", new String[]{""+chatRoomID}, null, null, sortOrder);
            case PEERS_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific peer.
                long peerId = PeerContract.getId(uri);
                cursor = db.rawQuery("SELECT * FROM "+PEERS_TABLE+" WHERE "+ PeerContract._ID+" = ?", new String[]{Long.toString(peerId)});
                if(cursor.moveToFirst()){
                    Peer peer = new Peer(cursor);
                    Log.i("ChatProvider.query()",peer.name+":"+peer.timestamp);
                }
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
                    Cursor cursor = db.query(MESSAGES_TABLE, columns, selection, null, null, null, MessageContract.TIMESTAMP);
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
                        Log.i(this.getClass().toString(), "Chatroom="+MessageContract.getChatRoom(record)+", message="+MessageContract.getMessageText(record));

                        //add room id
                        String chatRoomQuerySQL = "SELECT * FROM "+CHATROOMS_TABLE+" WHERE "+ ChatroomContract.NAME+" = ?";
                        String[] chatRoomQueryArgs = new String[]{MessageContract.getChatRoom(record)};
                        Cursor chatRoomQueryCursor = db.rawQuery(chatRoomQuerySQL, chatRoomQueryArgs);
                        chatRoomQueryCursor.moveToFirst();
                        ChatRoom chatRoom = new ChatRoom(chatRoomQueryCursor);
                        MessageContract.putChatRoomID(record,chatRoom.id);
                        Log.i(this.getClass().toString()," insertBulk() chatRoom.id="+chatRoom.id);
                        //-------------

                        if (db.insert(MESSAGES_TABLE, null, record) == -1) {
//                            Log.i(this.getClass().toString(), "---- fail ---");
                            throw new IllegalStateException("Failure to insert updated chat message record!");
                        }
                    }

                    db.setTransactionSuccessful();

                    Uri instanceUri = MessageContract.CONTENT_URI_SYNC(numReplacedMessages);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri,null);
                    Log.i(this.getClass().toString(),"notifyChange uri:"+instanceUri);
                    return 1;
                } finally {
                    db.endTransaction();
                }
                // TODO Make sure to notify any observers

            default:
                throw new IllegalStateException("insert: bad case");
        }
    }


}
