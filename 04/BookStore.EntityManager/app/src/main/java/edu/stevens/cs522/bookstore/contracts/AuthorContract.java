package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by dduggan.
 */

public class AuthorContract implements BaseColumns {

    public static final String ID = _ID;
    public static final String NAME = "name";
    public static final String BOOK_FK = "book_fk";

    /*
     * NAME column
     */

    private static int nameColumn = -1;

    public static String getFirstName(Cursor cursor) {
        if (nameColumn < 0) {
            nameColumn =  cursor.getColumnIndexOrThrow(NAME);;
        }
        return cursor.getString(nameColumn);
    }

    public static void putFirstName(ContentValues values, String firstName) {
        values.put(NAME, firstName);
    }

    /*
     * bookid column
     */

    private static int lastBookFkColumn = -1;

    public static int getBookFk(Cursor cursor) {
        if (lastBookFkColumn < 0) {
            lastBookFkColumn =  cursor.getColumnIndexOrThrow(BOOK_FK);;
        }
        return cursor.getInt(lastBookFkColumn);
    }

    public static void putBookFk(ContentValues values, int bookFk) {
        Log.i("bookFk",""+bookFk);
        values.put(BOOK_FK, bookFk);
    }
    // TODO complete the definitions of the operations for Parcelable, cursors and contentvalues

}
