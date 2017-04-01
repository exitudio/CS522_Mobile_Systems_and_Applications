package edu.stevens.cs522.chat.async;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dduggan.
 */

public class SimpleQueryBuilder<T> implements IContinue<Cursor> {

    //slide65
    private IEntityCreator<T> helper;
    private ISimpleQueryListener<T> listener;

    private SimpleQueryBuilder(IEntityCreator<T> helper, ISimpleQueryListener<T> listener) {
        this.helper = helper;
        this.listener = listener;
    }
    public static <T> void executeQuery(Context context, Uri uri, IEntityCreator<T> helper, ISimpleQueryListener<T> listener) {
        SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(helper, listener);
        AsyncContentResolver resolver = new AsyncContentResolver(context.getContentResolver());
        resolver.queryAsync(uri, null, null, null, null, qb);
    }


    public interface ISimpleQueryListener<T> {

        public void handleResults(List<T> results);

    }


    @Override
    public void kontinue(Cursor cursor) {
        //slide 67
        List<T> instances = new ArrayList<T>();
        if(cursor.moveToFirst()){
            do {
                T instance = helper.create(cursor);
                instances.add(instance);
            } while(cursor.moveToNext());
        }
        cursor.close();
        listener.handleResults(instances);
    }

}
