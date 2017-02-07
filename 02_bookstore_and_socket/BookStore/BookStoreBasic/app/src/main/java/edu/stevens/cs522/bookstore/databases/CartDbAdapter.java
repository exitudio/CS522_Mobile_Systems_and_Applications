package edu.stevens.cs522.bookstore.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;


/**
 * Created by dduggan.
 */

public class CartDbAdapter {

    private static final String DATABASE_NAME = "books.db";
    private static final String BOOK_TABLE = "books";
    private static final String AUTHOR_TABLE = "authors";
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
            // TODO
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " +BOOK_TABLE+
                    " ("+ BookContract._ID+" INTEGER PRIMARY KEY, "+
                            BookContract.TITLE+" TEXT, "+
                            BookContract.AUTHORS+" TEXT, "+
                            BookContract.ISBN+" TEXT, "+
                            BookContract.PRICE+" TEXT)"
            );
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " +AUTHOR_TABLE+
                    "(_id INTEGER PRIMARY KEY, first_name text, middle_initial text, last_name text)"
            );
            Log.i("onCreate","init");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
            db.execSQL("DROP TABLE IF EXISTS "+BOOK_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+AUTHOR_TABLE);
            onCreate(db);
        }
    }


    public CartDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void test(){
        ContentValues cv=new ContentValues();

        cv.put("title", "t3");
        cv.put("price", "a3");
        db.insert(BOOK_TABLE, "title", cv);
        //db.close();

        //query
        Cursor cursor = db.query(BOOK_TABLE,
                new String[]{"_id","title","price"},
                null, null, null, null, null);

        if( cursor.moveToFirst()){
            do{
                String idStr = cursor.getString( cursor.getColumnIndexOrThrow("_id") );
                String titleStr = cursor.getString( cursor.getColumnIndexOrThrow("title") );
                String priceStr = cursor.getString( cursor.getColumnIndexOrThrow("price") );
                Log.i("CartDbAdapter query", "id:"+idStr+", title=t2 :"+titleStr+" , price:"+priceStr);
            }while (cursor.moveToNext());
        }

        //query one
        cursor = db.query(BOOK_TABLE,
                new String[]{"_id","title","price"},
                "title=?",
                new String[]{"t2"}, null, null, null);
        if( cursor.moveToFirst()){
            String titleStr = cursor.getString( cursor.getColumnIndexOrThrow("title") );
            String priceStr = cursor.getString( cursor.getColumnIndexOrThrow("price") );
            Log.i("CartDbAdapter", "title=t2 :"+titleStr+" , price:"+priceStr);
        }

        //delete
        db.delete(BOOK_TABLE,
                "_id=5",null);


        //query again
        cursor = db.query(BOOK_TABLE,
                new String[]{"_id","title","price"},
                null, null, null, null, null);

        if( cursor.moveToFirst()){
            do{
                String titleStr = cursor.getString( cursor.getColumnIndexOrThrow("title") );
                String priceStr = cursor.getString( cursor.getColumnIndexOrThrow("price") );
                Log.i("CartDbAdapter", " query2 title:"+titleStr+" , price:"+priceStr);
            }while (cursor.moveToNext());
        }
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void insert(Book book){
        ContentValues cv=new ContentValues();
        book.writeToProvider(cv);
        book.id = db.insert(BOOK_TABLE, "title", cv);
    }

    public void logAllBooks(){
        Cursor cursor = db.query(BOOK_TABLE,
                new String[]{BookContract._ID, BookContract.TITLE, BookContract.ISBN, BookContract.PRICE, BookContract.AUTHORS},
                null, null, null, null, null);

        if( cursor.moveToFirst()){
            do{
                int idStr = cursor.getInt( cursor.getColumnIndexOrThrow(BookContract._ID) );
                String titleStr = cursor.getString( cursor.getColumnIndexOrThrow(BookContract.TITLE) );
                String priceStr = cursor.getString( cursor.getColumnIndexOrThrow(BookContract.PRICE) );
                String isbnStr = cursor.getString( cursor.getColumnIndexOrThrow(BookContract.ISBN) );
                String autorIdStr = cursor.getString( cursor.getColumnIndexOrThrow(BookContract.AUTHORS) );
                Log.i("CartDbAdapter query",
                        BookContract._ID+" : "+idStr+", "+
                        BookContract.TITLE+" : "+titleStr+", "+
                        BookContract.PRICE+" : "+priceStr+", "+
                        BookContract.ISBN+" : "+isbnStr+", "+
                        BookContract.AUTHORS+" : "+autorIdStr
                );
            }while (cursor.moveToNext());
        }
    }

    public ArrayList<Book> fetchAllBooks(){
        ArrayList<Book> books = new ArrayList<Book>();
        Cursor cursor = db.query(BOOK_TABLE,
                new String[]{BookContract._ID, BookContract.TITLE, BookContract.ISBN, BookContract.PRICE, BookContract.AUTHORS},
                null, null, null, null, null);

        if( cursor.moveToFirst()){
            do{
                Book book = new Book(cursor);
                Log.i("title",book.title);
                Log.i("isbn",book.isbn);
                Log.i("price",book.price);
                books.add(book);
            }while (cursor.moveToNext());
        }
        return books;
    }

    /*public Cursor fetchAllBooks() {
        // TODO
        return null;
    }*/

    public Book fetchBook(long rowId) {
        // TODO
        return null;
    }

    public void persist(Book book) throws SQLException {
        // TODO
    }

    public boolean delete(Book book) {
        // TODO
        return db.delete(BOOK_TABLE,BookContract._ID+"="+book.id,null) == 1;
    }

    public boolean deleteAll() {
        // TODO
        db.execSQL("delete from "+ BOOK_TABLE);
        return true;
    }

    public void close() {
        // TODO
        db.close();
    }

}
