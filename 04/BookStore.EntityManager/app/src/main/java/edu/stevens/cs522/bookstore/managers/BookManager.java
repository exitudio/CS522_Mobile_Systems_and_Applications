package edu.stevens.cs522.bookstore.managers;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.Set;

import edu.stevens.cs522.bookstore.async.AsyncContentResolver;
import edu.stevens.cs522.bookstore.async.IContinue;
import edu.stevens.cs522.bookstore.async.IEntityCreator;
import edu.stevens.cs522.bookstore.async.QueryBuilder;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.providers.BookProvider;

/**
 * Created by dduggan.
 */

public class BookManager extends Manager<Book> {

    public static final int TEMP_LOADER_ID = 10;

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<Book> creator = new IEntityCreator<Book>() {
        @Override
        public Book create(Cursor cursor) {
            return new Book(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public BookManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());

        Log.i("BookManager","init");
//        QueryBuilder queryBuilder = new QueryBuilder((Activity) context, TEMP_LOADER_ID);
        QueryBuilder.executeQuery(
                "test",
                (Activity)context,
                BookContract.CONTENT_URI, TEMP_LOADER_ID,
                new IEntityCreator<Book>() {
                    @Override
                    public Book create(Cursor cursor) {
                        return null;
                    }
                },
                new IQueryListener<Book>() {
                    @Override
                    public void closeResults() {

                    }

                    public void handleResults(TypedCursor<Book> books) {
                        if (books.moveToFirst()) {

                        }
                    }
                });
    }

    public void getAllBooksAsync(IQueryListener<Book> listener) {
        // TODO use QueryBuilder to complete this
    }

    public void getBookAsync(long id, IContinue<Book> callback) {
        // TODO
    }

    public void persistAsync(final Book book) {
        //slide 58-59
        ContentValues values = new ContentValues();
        book.writeToProvider(values);
        getAsyncResolver().insertAsync(BookContract.CONTENT_URI, values,
                new IContinue<Uri>() {
                    public void kontinue(Uri uri) {
                        book.id = BookContract.getId(uri);
                    }
                });
    }

    public void deleteBooksAsync(Set<Long> toBeDeleted) {
        Long[] ids = new Long[toBeDeleted.size()];
        toBeDeleted.toArray(ids);
        String[] args = new String[ids.length];

        StringBuilder sb = new StringBuilder();
        if (ids.length > 0) {
            sb.append(AuthorContract.ID);
            sb.append("=?");
            args[0] = ids[0].toString();
            for (int ix=1; ix<ids.length; ix++) {
                sb.append(" or ");
                sb.append(AuthorContract.ID);
                sb.append("=?");
                args[ix] = ids[ix].toString();
            }
        }
        String select = sb.toString();

        contentResolver.deleteAsync(BookContract.CONTENT_URI, select, args);
    }

}
