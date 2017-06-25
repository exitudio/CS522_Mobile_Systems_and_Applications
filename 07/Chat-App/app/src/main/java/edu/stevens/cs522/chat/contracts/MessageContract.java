package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.Date;

import edu.stevens.cs522.chat.util.DateUtils;

import static android.R.attr.id;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));

    /*
     * A special URI for replacing messages after sequence numbers are assigned by server.
     * The number in the URI specifies how many messages to be replaced after server assigns seq numbers.
     */
    private static final Uri CONTENT_URI_SYNC = withExtendedPath(CONTENT_URI, "sync");

    public static final Uri CONTENT_URI_SYNC(int id) {
        return CONTENT_URI_SYNC(Integer.toString(id));
    }

    private static final Uri CONTENT_URI_SYNC(String id) {
        return withExtendedPath(CONTENT_URI_SYNC, id);
    }

    public static final String CONTENT_PATH_SYNC = CONTENT_PATH(CONTENT_URI_SYNC("#"));


    public static final String ID = _ID;

    public static final String SEQUENCE_NUMBER = "sequence_number";

    public static final String MESSAGE_TEXT = "message_text";

    public static final String CHAT_ROOM = "chat_room";

    public static final String TIMESTAMP = "timestamp";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String SENDER = "sender";
    public static final String SENDER_ID = "sender_id";

    public static final String[] COLUMNS = {ID, SEQUENCE_NUMBER, MESSAGE_TEXT, CHAT_ROOM, TIMESTAMP, LATITUDE, LONGITUDE, SENDER, SENDER_ID};


    /*
     * SEQUENCE_NUMBER
     */
    private static int sequenceNumberColumn = -1;

    public static long getSequenceNumber(Cursor cursor) {
        if (sequenceNumberColumn < 0) {
            sequenceNumberColumn = cursor.getColumnIndexOrThrow(SEQUENCE_NUMBER);
        }
        return cursor.getLong(sequenceNumberColumn);
    }

    public static void putSequenceNumberColumn(ContentValues out, long seqNum) {
        out.put(SEQUENCE_NUMBER, seqNum);
    }



    // TODO remaining getter and putter operations for other columns
    /*
     * MESSAGE_TEXT
     */

    private static int messageTextColumn = -1;

    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    /*
     * ID column
     */

    private static int idColumn = -1;
    public static Long getId(Cursor cursor) {
        if (idColumn < 0) {
            idColumn =  cursor.getColumnIndexOrThrow(_ID);
        }
        return cursor.getLong(idColumn);
    }
    public static void putId(ContentValues values, Long id) {
        values.put(_ID, id);
    }

    /*
    * Timestamp
     */
    private static int timeStampColumn = -1;
    public static Date getTimeStamp(Cursor cursor) {
        if (timeStampColumn < 0) {
            timeStampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
//        return cursor.getLong(timeStampColumn);
        return DateUtils.getDate(cursor,timeStampColumn);
    }
    public static void putTimeStamp(ContentValues out, Date timestamp) {
//        out.put(TIMESTAMP, timestamp);
        DateUtils.putDate(out,TIMESTAMP,timestamp);
    }


    /*
    * sender
     */
    private static int senderColumn = -1;
    public static String getSender(Cursor cursor) {
        if (senderColumn < 0) {
            senderColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(senderColumn);
    }
    public static void putSender(ContentValues out, String sender) {
        out.put(SENDER, sender);
    }

 /*
    * sender_id
     */
    private static int senderIdColumn = -1;
    public static long getSenderId(Cursor cursor) {
        if (senderIdColumn < 0) {
            senderIdColumn = cursor.getColumnIndexOrThrow(SENDER_ID);
        }
        Log.i("MessageContract","senderIdColumn="+senderIdColumn);
        return cursor.getLong(senderIdColumn);
    }
    public static void putSenderId(ContentValues out, long senderId) {
        out.put(SENDER_ID, senderId);
    }


    /*
    * CHAT_ROOM
     */
    private static int chatRoomColumn = -1;
    public static String getChatRoom(Cursor cursor) {
        if (chatRoomColumn < 0) {
            chatRoomColumn = cursor.getColumnIndexOrThrow(CHAT_ROOM);
        }
        return cursor.getString(chatRoomColumn);
    }
    public static void putChatRoom(ContentValues out, String chatRoom) {
        out.put(CHAT_ROOM, chatRoom);
    }

    /*
    * LATITUDE
     */
    private static int latitudeColumn = -1;
    public static Double getLatitude(Cursor cursor) {
        if (latitudeColumn < 0) {
            latitudeColumn = cursor.getColumnIndexOrThrow(LATITUDE);
        }
        return cursor.getDouble(latitudeColumn);
    }
    public static void putLatitude(ContentValues out, Double latitude) {
        out.put(LATITUDE, latitude);
    }
    /*
    * LONGITUDE
     */
    private static int longitudeColumn = -1;
    public static Double getLongitude(Cursor cursor) {
        if (longitudeColumn < 0) {
            longitudeColumn = cursor.getColumnIndexOrThrow(LONGITUDE);
        }
        return cursor.getDouble(longitudeColumn);
    }
    public static void putLongitude(ContentValues out, Double longitude) {
        out.put(LONGITUDE, longitude);
    }

}
