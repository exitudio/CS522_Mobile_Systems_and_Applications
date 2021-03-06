package edu.stevens.cs522.bookstore.managers;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.List;
import java.util.Set;

import edu.stevens.cs522.bookstore.async.AsyncContentResolver;
import edu.stevens.cs522.bookstore.async.IContinue;
import edu.stevens.cs522.bookstore.async.IEntityCreator;
import edu.stevens.cs522.bookstore.async.QueryBuilder;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.async.SimpleQueryBuilder;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.providers.BookProvider;

/**
 * Created by dduggan.
 */

public class BookManager extends Manager<Book> {

    public static final int LOADER_ID = 1;

    private static final IEntityCreator<Book> creator = new IEntityCreator<Book>() {
        @Override
        public Book create(Cursor cursor) {
            return new Book(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public BookManager(Activity context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());

        Log.i("BookManager","init");
//        QueryBuilder queryBuilder = new QueryBuilder((Activity) context, TEMP_LOADER_ID);

        //AsyncQueryHandler
//        Author[] authors = new Author[]{new Author("AuthorName")};
//        Book book = new Book(1,"title2",authors,"isbn2",Float.valueOf(53));
//        persistAsync(book);


    }

    public void getAllBooksAsync(IQueryListener<Book> listener) {
        // TODO use QueryBuilder to complete this
        // LoaderManager
        QueryBuilder.executeQuery(
                tag,
                context,
                BookContract.CONTENT_URI, LOADER_ID,
                new IEntityCreator<Book>() {
                    @Override
                    public Book create(Cursor cursor) {
                        Log.i(this.getClass().toString(), "IEntity");
                        return null;
                    }
                },
                listener
                );
    }

    public void getBookAsync(long id, final IContinue<Book> callback) {
        // TODO
        //slide68-69
        SimpleQueryBuilder.executeQuery(context, BookContract.CONTENT_URI(id),
                new IEntityCreator<Book>() {
                    @Override
                    public Book create(Cursor cursor) {
                        Log.i(tag,"getBookAsync : create");
                        return new Book(cursor);
                    }
                },  new SimpleQueryBuilder.ISimpleQueryListener<Book>() {
                    public void handleResults(List<Book> books) {
                        Log.i(tag,"getBookAsync : handleResults"+books.size());
                        for(Book book :books){
                            Log.i(tag,"getBookAsync : book.title:"+book);
                        }
                        callback.kontinue(books.get(0));
                    }
                });
    }

    public void persistAsync(final Book book) {
        //slide 58-59
        ContentValues values = new ContentValues();
        book.writeToProvider(values);

//        context.getContentResolver().insert(BookContract.CONTENT_URI,values);
        getAsyncResolver().insertAsync(BookContract.CONTENT_URI, values,
                new IContinue<Uri>() {
                    public void kontinue(Uri uri) {
                        Log.i(this.getClass().toString(),"kontinue:"+uri);
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
        Log.i(tag,"deleteBooksAsync: select="+select);
        contentResolver.deleteAsync(BookContract.CONTENT_URI, select, args, null);
    }

    public void deleteBooksAsync(IContinue<Integer> callback) {
        contentResolver.deleteAsync(BookContract.CONTENT_URI, null, null, callback);
    }

}
