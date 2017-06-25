package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import static edu.stevens.cs522.chat.contracts.MessageContract.SEQUENCE_NUMBER;

/**
 * Created by dduggan.
 */

public class ChatroomContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Chatroom");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = _ID;

    public static final String NAME = "name";

    public static final String[] COLUMNS = {ID, NAME};


    static private int idColumn = -1;

    static public Long getId(Cursor cursor) {
        if (idColumn < 0) {
            idColumn = cursor.getColumnIndexOrThrow(ID);
        }
        return cursor.getLong(idColumn);
    }
    static public void putId(ContentValues values, Long id) {
        values.put(_ID, id);
    }


    static private int nameColumn = -1;

    static public String getName(Cursor cursor) {
        if (nameColumn < 0) {
            nameColumn = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(nameColumn);
    }

    static public void putName(ContentValues out, String name) {
        out.put(NAME, name);
    }

}
