package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.regex.Pattern;

/**
 * Created by dduggan.
 */

public class BookContract implements BaseColumns {

    public static final String TITLE = "title";
    public static final String AUTHORS = "authors";
    public static final String ISBN = "isbn";
    public static final String PRICE = "price";

    /*
     * ID column
     */

    private static int idColumn = -1;

    public static Long getId(Cursor cursor) {
        if (idColumn < 0) {
            idColumn =  cursor.getColumnIndexOrThrow(_ID);;
        }
        return cursor.getLong(idColumn);
    }

    public static void putId(ContentValues values, Long id) {
        values.put(_ID, id);
    }
    /*
     * TITLE column
     */

    private static int titleColumn = -1;

    public static String getTitle(Cursor cursor) {
        if (titleColumn < 0) {
            titleColumn =  cursor.getColumnIndexOrThrow(TITLE);;
        }
        return cursor.getString(titleColumn);
    }

    public static void putTitle(ContentValues values, String title) {
        values.put(TITLE, title);
    }
    /*
     * Synthetic authors column
     */
    public static final char SEPARATOR_CHAR = '|';

    private static final Pattern SEPARATOR = Pattern.compile(Character.toString(SEPARATOR_CHAR), Pattern.LITERAL);

    public static String[] readStringArray(String in) {
        return SEPARATOR.split(in);
    }

    private static int authorColumn = -1;

    public static String[] getAuthors(Cursor cursor) {
        if (authorColumn < 0) {
            authorColumn =  cursor.getColumnIndexOrThrow(AUTHORS);
        }
        Log.i("Author",cursor.getString(authorColumn));
        return readStringArray(cursor.getString(authorColumn));
    }

    /*
     * ISBN column
     */

    private static int isbnColumn = -1;

    public static String getIsbn(Cursor cursor) {
        if (isbnColumn < 0) {
            isbnColumn =  cursor.getColumnIndexOrThrow(ISBN);;
        }
        return cursor.getString(isbnColumn);
    }

    public static void putIsbn(ContentValues values, String isbn) {
        values.put(ISBN, isbn);
    }

    /*
     * PRICE column
     */

    private static int priceColumn = -1;

    public static Float getPrice(Cursor cursor) {
        if (priceColumn < 0) {
            priceColumn =  cursor.getColumnIndexOrThrow(PRICE);;
        }
        return cursor.getFloat(priceColumn);
    }

    public static void putPrice(ContentValues values, Float price) {
        values.put(PRICE, price);
    }
}
