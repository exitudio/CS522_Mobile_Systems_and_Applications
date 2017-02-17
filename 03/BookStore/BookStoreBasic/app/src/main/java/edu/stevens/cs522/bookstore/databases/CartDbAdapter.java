package edu.stevens.cs522.bookstore.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;


/**
 * Created by dduggan.
 */

public class CartDbAdapter {

    private static final String DATABASE_NAME = "books.db";
    private static final String BOOK_TABLE = "Books";
    private static final String AUTHOR_TABLE = "Authors";
    private static final int DATABASE_VERSION = 18;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS ";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO
            db.execSQL(
                    DATABASE_CREATE+BOOK_TABLE+
                    " ("+ BookContract._ID+" INTEGER PRIMARY KEY, "+
                            BookContract.TITLE+" TEXT, "+
                            BookContract.AUTHORS+" TEXT, "+
                            BookContract.ISBN+" TEXT, "+
                            BookContract.PRICE+" REAL)"
            );
            db.execSQL(
                    DATABASE_CREATE+AUTHOR_TABLE+
                    "("+BookContract._ID+" INTEGER PRIMARY KEY, " +
                            AuthorContract.FIRST_NAME+" TEXT, " +
                            AuthorContract.MIDDLE_INITIAL+" TEXT, " +
                            AuthorContract.LAST_NAME+" TEXT, " +
                            AuthorContract.BOOK_FK+" INTEGER NOT NULL, " +
                            "FOREIGN KEY ("+AuthorContract.BOOK_FK+") REFERENCES "+BOOK_TABLE+"("+BookContract._ID+") ON DELETE CASCADE " +
                    ")"
            );
            db.execSQL("CREATE INDEX AuthorsBookIndex ON "+AUTHOR_TABLE+"("+AuthorContract.BOOK_FK+")");

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
        if(db==null){
            throw new SQLException("Failed to open database ");
        }
        db.execSQL("PRAGMA	foreign_keys=ON;");
    }

    public void logAllBooks(){
        //ALL QUERY
        Cursor cursorQueryAll = db.rawQuery("SELECT "+BOOK_TABLE+"."+BookContract._ID+" ,"+BookContract.TITLE+" ,"+BookContract.PRICE+" ,"+BookContract.ISBN+", "+
                        "GROUP_CONCAT("+AuthorContract.LAST_NAME+",'|') as "+BookContract.AUTHORS+" "+
                        "FROM "+BOOK_TABLE+" JOIN "+AUTHOR_TABLE+" "+
                        "ON "+BOOK_TABLE+"."+BookContract._ID+" = "+AUTHOR_TABLE+"."+AuthorContract.BOOK_FK+" "+
                        "GROUP BY "+BOOK_TABLE+"."+BookContract._ID+" ,"+BookContract.TITLE+" ,"+BookContract.PRICE+" ,"+BookContract.ISBN
                ,null);

        if( cursorQueryAll.moveToFirst()){
            do{
                Log.i("db", TextUtils.join(",",cursorQueryAll.getColumnNames()));
                Book book = new Book(cursorQueryAll);
                Log.i("****** ALL query",
                        BookContract._ID+" : "+book.id+", "+
                                BookContract.TITLE+" : "+book.title+", "+
                                BookContract.PRICE+" : "+book.price+", "+
                                BookContract.ISBN+" : "+book.isbn+", "+
                                BookContract.AUTHORS+" : "+book.getFirstAuthor()
                );
            }while (cursorQueryAll.moveToNext());
        }
        if(true) {
            return;
        }
        // BOOK QUERY
        Cursor cursor = db.query(BOOK_TABLE,
                new String[]{BookContract._ID, BookContract.TITLE, BookContract.ISBN, BookContract.PRICE, BookContract.AUTHORS},
                null, null, null, null, null);

        if( cursor.moveToFirst()){
            do{
                Book book = new Book(cursor);
                Log.i("****** BOOK TABLE ****",
                        BookContract._ID+" : "+book.id+", "+
                                BookContract.TITLE+" : "+book.title+", "+
                                BookContract.PRICE+" : "+book.price+", "+
                                BookContract.ISBN+" : "+book.isbn+", "+
                                BookContract.AUTHORS+" : "+book.getFirstAuthor()
                );
            }while (cursor.moveToNext());
        }

        //AUTHOR QUERY
        Cursor cursorAuthor = db.query(AUTHOR_TABLE,
                new String[]{AuthorContract.FIRST_NAME, AuthorContract.MIDDLE_INITIAL, AuthorContract.LAST_NAME, AuthorContract.BOOK_FK},
                null, null, null, null, null);
        if( cursorAuthor.moveToFirst()){
            do{
                Author author = new Author(cursorAuthor);
                Log.i("****** AUTHOR TABLE **","first:"+author.firstName+", mid:"+author.middleInitial+", last:"+author.lastName+" bookFk:"+author.bookFk);
            }while (cursorAuthor.moveToNext());
        }


    }

    public Cursor fetchAllBooks(){
        Cursor cursor = db.rawQuery("SELECT "+BOOK_TABLE+"."+BookContract._ID+" ,"+BookContract.TITLE+" ,"+BookContract.PRICE+" ,"+BookContract.ISBN+", "+
                        "GROUP_CONCAT("+AuthorContract.LAST_NAME+",'|') as "+BookContract.AUTHORS+" "+
                        "FROM "+BOOK_TABLE+" JOIN "+AUTHOR_TABLE+" "+
                        "ON "+BOOK_TABLE+"."+BookContract._ID+" = "+AUTHOR_TABLE+"."+AuthorContract.BOOK_FK+" "+
                        "GROUP BY "+BOOK_TABLE+"."+BookContract._ID+" ,"+BookContract.TITLE+" ,"+BookContract.PRICE+" ,"+BookContract.ISBN
                ,null);
        return cursor;
    }

    public ArrayList<Book> fetchAllBooksArrayList(){
        ArrayList<Book> books = new ArrayList<Book>();

        //ALL QUERY
        Cursor cursorQueryAll = db.rawQuery("SELECT "+BOOK_TABLE+"."+BookContract._ID+" ,"+BookContract.TITLE+" ,"+BookContract.PRICE+" ,"+BookContract.ISBN+", "+
                        "GROUP_CONCAT("+AuthorContract.LAST_NAME+",'|') as "+BookContract.AUTHORS+" "+
                        "FROM "+BOOK_TABLE+" JOIN "+AUTHOR_TABLE+" "+
                        "ON "+BOOK_TABLE+"."+BookContract._ID+" = "+AUTHOR_TABLE+"."+AuthorContract.BOOK_FK+" "+
                        "GROUP BY "+BOOK_TABLE+"."+BookContract._ID+" ,"+BookContract.TITLE+" ,"+BookContract.PRICE+" ,"+BookContract.ISBN
                ,null);

        if( cursorQueryAll.moveToFirst()){
            do{
                Log.i("db", TextUtils.join(",",cursorQueryAll.getColumnNames()));
                Book book = new Book(cursorQueryAll);
                Log.i("****** ALL query",
                        BookContract._ID+" : "+book.id+", "+
                                BookContract.TITLE+" : "+book.title+", "+
                                BookContract.PRICE+" : "+book.price+", "+
                                BookContract.ISBN+" : "+book.isbn+", "+
                                BookContract.AUTHORS+" : "+book.getFirstAuthor()
                );
                books.add(book);
            }while (cursorQueryAll.moveToNext());
        }
        return books;
    }

    // since SimpleCursorAdapter is deprecated so I create my own adapter
    // and return ArrayList<Books> instead
    /*public Cursor fetchAllBooks() {
        return null;
    }*/

    public Book fetchBook(long rowId) {
        Cursor cursor = db.rawQuery("SELECT "+BOOK_TABLE+"."+BookContract._ID+" ,"+BookContract.TITLE+" ,"+BookContract.PRICE+" ,"+BookContract.ISBN+", "+
                        "GROUP_CONCAT("+AuthorContract.LAST_NAME+",'|') as "+BookContract.AUTHORS+" "+
                        "FROM "+BOOK_TABLE+" JOIN "+AUTHOR_TABLE+" "+
                        "ON "+BOOK_TABLE+"."+BookContract._ID+" = "+AUTHOR_TABLE+"."+AuthorContract.BOOK_FK+" "+
                        "WHERE "+BOOK_TABLE+"."+BookContract._ID+" = "+rowId+" "+
                        "GROUP BY "+BOOK_TABLE+"."+BookContract._ID+" ,"+BookContract.TITLE+" ,"+BookContract.PRICE+" ,"+BookContract.ISBN

                ,null);

        if( cursor.moveToFirst()){
            Book book = new Book(cursor);
            Log.i("****** ALL query",
                    BookContract._ID+" : "+book.id+", "+
                            BookContract.TITLE+" : "+book.title+", "+
                            BookContract.PRICE+" : "+book.price+", "+
                            BookContract.ISBN+" : "+book.isbn+", "+
                            BookContract.AUTHORS+" : "+book.getFirstAuthor()
            );
            return book;
        }
        return null;
    }

    public void persist(Book book) throws SQLException {
        ContentValues bookCv=new ContentValues();
        book.writeToProvider(bookCv);
        long bookId = db.insert(BOOK_TABLE, null, bookCv);
        Log.i("*** persist ***","BookId = "+bookId);
        for( int i=0; i<book.authors.length; i++) {
            ContentValues authorCv=new ContentValues();
            book.authors[i].writeToProvider(authorCv, (int) bookId);
            long authorId = db.insert(AUTHOR_TABLE, null, authorCv );
        }
        //cannot insert
        if(bookId>-1) {
            book.id = bookId;
        }else{
            throw new SQLException("Failed to open database ");
        }
    }

    public boolean delete(Book book) {
        return db.delete(BOOK_TABLE,BookContract._ID+"="+book.id,null) == 1;
    }

    public boolean deleteAll() {
        db.execSQL("delete from "+ BOOK_TABLE);
        return true;
    }

    public void close() {
        db.close();
    }

}
