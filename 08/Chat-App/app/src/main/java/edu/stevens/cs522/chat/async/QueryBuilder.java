package edu.stevens.cs522.chat.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import edu.stevens.cs522.chat.contracts.ChatroomContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor> {

    public static interface IQueryListener<T> {

        public void handleResults(TypedCursor<T> results);

        public void closeResults();

    }

    //slide73
    public static <T> void executeQuery(String tag, Context context, Uri uri, int loaderID, IEntityCreator<T> creator, IQueryListener<T> listener) {
        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID, creator, listener);
        LoaderManager lm = ((Activity) context).getLoaderManager();
        lm.initLoader(loaderID, null, qb);
    }
    public static <T> LoaderManager executeQuery(String tag, Context context, Uri uri, int loaderID,IEntityCreator<T> creator, Bundle bundle, IQueryListener<T> listener) {
//        int loaderID = (int) (100+bundle.getLong(ChatroomContract.ID));

//        LoaderManager lm = ((Activity) context).getLoaderManager();
//        QueryBuilder<ChatMessage> qb = new QueryBuilder<ChatMessage>(tag, context, MessageContract.CONTENT_URI, loaderID, creator, listener);
//        lm.restartLoader(LOADER_ID, bundle, qb);


        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID, creator, listener);
        LoaderManager lm = ((Activity) context).getLoaderManager();
        lm.restartLoader(loaderID, bundle, qb);
        lm.initLoader(loaderID, bundle, qb);
        return lm;
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

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.i("QueryBuilder","onCreateLoader::"+loaderID+", args="+(args!=null));
        if(args!=null){
            long chatRoomID = args.getLong(ChatroomContract.ID);
            Log.i("QueryBuilder","onCreateLoader:: chatRoomID="+chatRoomID);
            return new CursorLoader(context, uri, null, MessageContract.CHAT_ROOM_ID+"=?", new String[]{""+chatRoomID}, null);
        }else {
            return new CursorLoader(context, uri, null, null, null, null);
        }
        //slide 34
        /*switch (loaderID) {
            case MessageManager.LOADER_ID:
                return new CursorLoader(context,uri, null, null, null, null);
            case PeerManager.LOADER_ID:
                return new CursorLoader(context,uri, null, null, null, null);
            default:
                return null; //Throw exception; An invalid id was passed in
        }*/
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(this.getClass().toString(),"onLoadFinished");

//        if (cursor.moveToFirst()) {
//            do{
//                ChatMessage message = new ChatMessage(cursor);
//                Log.i("***** MESSAGE ****","id:"+message.id+", messageText:"+message.messageText+", timestamp:"+message.timestamp+", sender:"+message.sender+", senderId:"+message.senderId);
//            }while (cursor.moveToNext());
//        }
        cursor.setNotificationUri(context.getContentResolver(), uri);

        listener.handleResults(new TypedCursor<T>(cursor, creator));
        //slide75
        /*if (loader.getId() == MessageManager.LOADER_ID) {
            listener.handleResults(new TypedCursor<T>(cursor, creator));
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }*/
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
