package edu.stevens.cs522.bookstore.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.managers.BookManager;
import edu.stevens.cs522.bookstore.managers.TypedCursor;
import edu.stevens.cs522.bookstore.providers.BookProvider;

/**
 * Created by dduggan.
 */

public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor> {

    public static interface IQueryListener<T> {
        public void handleResults(TypedCursor<T> results);
        public void closeResults();
    }


    //slide73
    public static <T> void executeQuery(String tag, Activity context, Uri uri, int loaderID, IEntityCreator<T> creator, IQueryListener<T> listener) {
        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID, creator, listener);
        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID, null, qb);
    }
    //slide72
    private String tag;
    private Context context;
    private Uri uri;
    private int loaderID;
    private IEntityCreator<T> creator;
    private IQueryListener<T> listener;
    private QueryBuilder(String tag, Context context, Uri uri, int loaderID, IEntityCreator<T> creator, IQueryListener<T> listener) {
        this.tag = tag;
        this.context = context;
        this.uri = uri;
        this.loaderID = loaderID;
        this.creator = creator;
        this.listener = listener;
    }

    // I create [ not use]
    private Activity activity;
    public QueryBuilder(Activity activity, int loaderId){
        this.activity = activity;
        LoaderManager loaderManager = activity.getLoaderManager();

        if (loaderManager.getLoader(loaderId) == null) {
            loaderManager.initLoader(loaderId, null, this);
        } else {
            loaderManager.restartLoader(loaderId, null, this);
        }
    }



    // TODO complete the implementation of this

    /*
     * onCreateLoader is a factory method that simply returns a new Loader.
     * The LoaderManager will call this method when it first creates the Loader.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        Log.i("QueryBuilder","onCreateLoader::"+loaderID);
        //slide 34
        switch (loaderID) {
            case BookManager.TEMP_LOADER_ID:
                return new CursorLoader(context,uri, null, null, null, null);
            default:
                return null; //Throw exception; An invalid id was passed in
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loaders, Cursor cursor) {
        Log.i(this.getClass().toString(),"onLoadFinished");

        if (cursor.moveToFirst()) {
            do{
                Book book = new Book(cursor);
                Log.i(this.getClass().toString(), "onLoadFinished:" +
                        BookContract._ID + " : " + book.id + ", " +
                        BookContract.TITLE + " : " + book.title + ", " +
                        BookContract.PRICE + " : " + book.price + ", " +
                        BookContract.ISBN + " : " + book.isbn + ", " +
                        BookContract.AUTHORS + " : " + book.getFirstAuthor()
                );
            }while (cursor.moveToNext());
        }


        //slide75
        if (loaders.getId() == BookManager.TEMP_LOADER_ID) {
            listener.handleResults(new TypedCursor<T>(cursor, creator));
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == loaderID) {
            listener.closeResults();
        }else{
            throw new IllegalStateException("Unexpected loader callback");
        }
    }
}
