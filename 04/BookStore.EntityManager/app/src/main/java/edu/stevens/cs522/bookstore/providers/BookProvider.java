package edu.stevens.cs522.bookstore.providers;

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
import android.text.TextUtils;
import android.util.Log;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;

import static edu.stevens.cs522.bookstore.contracts.BookContract.CONTENT_PATH;
import static edu.stevens.cs522.bookstore.contracts.BookContract.CONTENT_PATH_ITEM;

public class BookProvider extends ContentProvider {
    public BookProvider() {
    }

    private static final String AUTHORITY = BookContract.AUTHORITY;
    private static final String CONTENT_PATH = BookContract.CONTENT_PATH;
    private static final String CONTENT_PATH_ITEM = BookContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 2;
    private static final String BOOKS_TABLE = "books";
    private static final String AUTHORS_TABLE = "authors";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int ALL_ROWS = 1;// I changed to public
    private static final int SINGLE_ROW = 2;// I changed to public

    public static class DbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS ";
        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(this.getClass().toString(),"onCreate");
            db.execSQL(
                    DATABASE_CREATE+BOOKS_TABLE+
                            " ("+ BookContract._ID+" INTEGER PRIMARY KEY, "+
                            BookContract.TITLE+" TEXT, "+
                            BookContract.AUTHORS+" TEXT, "+
                            BookContract.ISBN+" TEXT, "+
                            BookContract.PRICE+" REAL)"
            );
            db.execSQL(
                    DATABASE_CREATE+AUTHORS_TABLE+
                            "("+BookContract._ID+" INTEGER PRIMARY KEY, " +
                            AuthorContract.NAME+" TEXT, " +
                            AuthorContract.BOOK_FK+" INTEGER NOT NULL, " +
                            "FOREIGN KEY ("+AuthorContract.BOOK_FK+") REFERENCES "+BOOKS_TABLE+"("+BookContract._ID+") ON DELETE CASCADE " +
                            ")"
            );
            db.execSQL("CREATE INDEX AuthorsBookIndex ON "+AUTHORS_TABLE+"("+AuthorContract.BOOK_FK+")");

            Log.i("onCreate","init");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
            db.execSQL("DROP TABLE IF EXISTS "+BOOKS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+AUTHORS_TABLE);
            onCreate(db);
        }
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        Log.i(this.getClass().toString(),"onCreate");
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);

        createTestData();
        logAllBooks();
        return true;
    }

    private void createTestData(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Book book = new Book(1,"title1",null,"isbn",Float.valueOf(33));
        ContentValues bookCv=new ContentValues();
        book.writeToProvider(bookCv);
        long bookId = db.insert(BOOKS_TABLE, null, bookCv);
        db.close();
    }

    public void logAllBooks() {
        //ALL QUERY
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursorQueryAll = db.rawQuery("SELECT " + BOOKS_TABLE + "." + BookContract._ID + " ," + BookContract.TITLE + " ," + BookContract.PRICE + " ," + BookContract.ISBN + ", " +
                        "GROUP_CONCAT(" + AuthorContract.NAME + ",'|') as " + BookContract.AUTHORS + " " +
                        "FROM " + BOOKS_TABLE + " JOIN " + AUTHORS_TABLE + " " +
                        "ON " + BOOKS_TABLE + "." + BookContract._ID + " = " + AUTHORS_TABLE + "." + AuthorContract.BOOK_FK + " " +
                        "GROUP BY " + BOOKS_TABLE + "." + BookContract._ID + " ," + BookContract.TITLE + " ," + BookContract.PRICE + " ," + BookContract.ISBN
                , null);

        if (cursorQueryAll.moveToFirst()) {
            do {
                Log.i("db", TextUtils.join(",", cursorQueryAll.getColumnNames()));
                Book book = new Book(cursorQueryAll);
                Log.i("****** ALL query",
                        BookContract._ID + " : " + book.id + ", " +
                                BookContract.TITLE + " : " + book.title + ", " +
                                BookContract.PRICE + " : " + book.price + ", " +
                                BookContract.ISBN + " : " + book.isbn + ", " +
                                BookContract.AUTHORS + " : " + book.getFirstAuthor()
                );
            } while (cursorQueryAll.moveToNext());
        }
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH_ITEM, SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        /*switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                return contentType("book");
            case SINGLE_ROW:
                return contentItemType("book");
            default:
                throw new IllegalStateException("insert: bad case");*/
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new row.
                // Make sure to notify any observers

                /*long row = db.insert(BOOKS_TABLE,null,values);
                if(row>0){
                    Uri instanceUri = BookContract.CONTENT_URI(row);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri,null);
                    return instanceUri;
                }*/

                throw new UnsupportedOperationException("Not yet implemented");
            case SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODO: Implement this to handle query of all books.
                return db.query(BOOKS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            case SINGLE_ROW:
                // TODO: Implement this to handle query of a specific book.
                // String selection = BookContract._ID+"=?";
                // String[] selectionArgs = {getId(uri)};
                throw new UnsupportedOperationException("Not yet implemented");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        /*switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODO: Implement this to handle query of all books.
            case SINGLE_ROW:
                // TODO: Implement this to handle query of a specific book.
            default:
                throw new IllegalStateException("insert: bad case");
        }*/
        throw new IllegalStateException("Update of books not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        /*switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODO: Implement this to handle query of all books.
            case SINGLE_ROW:
                // TODO: Implement this to handle query of a specific book.
            default:
                throw new IllegalStateException("insert: bad case");
        }*/
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
