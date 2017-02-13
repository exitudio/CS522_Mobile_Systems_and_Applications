package edu.stevens.cs522.chatserver.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class MessagesDbAdapter {

    private static final String DATABASE_NAME = "messages.db";

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEER_TABLE = "view_peers";

    private static final int DATABASE_VERSION = 8;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE = null; // TODO

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " +MESSAGE_TABLE+
                            " ("+ MessageContract._ID+" INTEGER PRIMARY KEY, "+
                            MessageContract.MESSAGE_TEXT+" TEXT, "+
                            MessageContract.TIMESTAMP+" INTEGER, "+
                            MessageContract.SENDER+" TEXT, "+
                            MessageContract.SENDER_ID+" INTEGER NOT NULL, " +
                            "FOREIGN KEY ("+MessageContract.SENDER_ID+") REFERENCES "+PEER_TABLE+"("+PeerContract._ID+") ON DELETE CASCADE )"
            );
            db.execSQL(
                    "CREATE TABLE " +PEER_TABLE+
                            " ("+ PeerContract._ID+" INTEGER PRIMARY KEY, "+
                            PeerContract.NAME+" TEXT, "+
                            PeerContract.TIMESTAMP+" INTEGER, "+
                            PeerContract.ADDRESS+" TEXT, "+
                            PeerContract.PORT+" INTEGER)"
            );

            Log.i("onCreate DB","created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+MESSAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+PEER_TABLE);
        }
    }


    public MessagesDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        db.execSQL("PRAGMA	foreign_keys=ON;");
    }

    public void logAll(){
        Cursor cursorMessage = db.rawQuery("SELECT * FROM "+MESSAGE_TABLE,null);
        if(cursorMessage.moveToFirst()){
            do{
                Message message = new Message(cursorMessage);
                Log.i("***** MESSAGE ****","id:"+message.id+", messageText:"+message.messageText+", timestamp:"+message.timestamp+", sender:"+message.sender+", senderId:"+message.senderId);
            }while(cursorMessage.moveToNext());
        }
        Cursor cursorPeer = db.rawQuery("SELECT * FROM "+PEER_TABLE,null);
        if(cursorPeer.moveToFirst()){
            do{
                Peer peer = new Peer(cursorPeer);
                Log.i("***** PEER ****","id:"+peer.id+", name:"+peer.name+", timestamp:"+peer.timestamp+", address:"+peer.address+", port:"+peer.port);
            }while(cursorMessage.moveToNext());
        }
    }

    public Cursor fetchAllMessages() {
        // TODO
        return null;
    }

    public Cursor fetchAllPeers() {
        // TODO
        return null;
    }

    public Peer fetchPeer(long peerId) {
        // TODO
        return null;
    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        // TODO
        return null;
    }

    public void persist(Message message) throws SQLException {
        ContentValues messageCv=new ContentValues();
        message.writeToProvider(messageCv);
        long messageId = db.insert(MESSAGE_TABLE, null, messageCv);
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     * @param peer
     * @return The database key of the (inserted or updated) peer record
     * @throws SQLException
     */
    public long persist(Peer peer) throws SQLException {
        ContentValues peerCv=new ContentValues();
        peer.writeToProvider(peerCv);
        long peerId = db.insert(PEER_TABLE, null, peerCv);
        if(peerId<0) {
            throw new SQLException("Failed to add peer " + peer.name);
        }
        return peerId;
    }

    public void close() {
        // TODO
        db.close();
    }
}