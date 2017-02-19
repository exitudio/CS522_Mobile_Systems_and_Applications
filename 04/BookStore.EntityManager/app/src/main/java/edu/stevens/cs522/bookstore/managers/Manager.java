package edu.stevens.cs522.bookstore.managers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;

import edu.stevens.cs522.bookstore.async.AsyncContentResolver;
import edu.stevens.cs522.bookstore.async.IEntityCreator;

/**
 * Created by dduggan.
 */

public abstract class Manager<T> {

    protected final Activity context;//I change to protected and Activity

    private final IEntityCreator<T> creator;

    private final int loaderID;

    protected final String tag;//I change to protected

    protected Manager(Activity context,
                      IEntityCreator<T> creator,
                      int loaderID) {
        this.context = context;
        this.creator = creator;
        this.loaderID = loaderID;
        this.tag = this.getClass().getCanonicalName();
    }

    private ContentResolver syncResolver;

    private AsyncContentResolver asyncResolver;

    protected ContentResolver getSyncResolver() {
        if (syncResolver == null)
            syncResolver = context.getContentResolver();
        return syncResolver;
    }

    protected AsyncContentResolver getAsyncResolver() {
        if (asyncResolver == null)
            asyncResolver = new AsyncContentResolver(context.getContentResolver());
        return asyncResolver;
    }

    // TODO Provide operations for executing queries (see lectures)


}
