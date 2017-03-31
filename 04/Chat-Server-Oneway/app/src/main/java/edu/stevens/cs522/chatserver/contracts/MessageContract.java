package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

import edu.stevens.cs522.chatserver.util.DateUtils;

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


    public static final String ID = _ID;
    public static final String MESSAGE_TEXT = "message_text";
    public static final String TIMESTAMP = "timestamp";
    public static final String SENDER = "sender";
    public static final String SENDER_ID = "sender_id";

    // TODO remaining columns in Messages table

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
     * ID Sender
     */
    private static int senderIdColumn = -1;
    public static Long getSenderId(Cursor cursor) {
        if (senderIdColumn < 0) {
            senderIdColumn =  cursor.getColumnIndexOrThrow(SENDER_ID);;
        }
        return cursor.getLong(senderIdColumn);
    }
    public static void putSenderId(ContentValues values, Long id) {
        values.put(SENDER_ID, id);
    }
}
