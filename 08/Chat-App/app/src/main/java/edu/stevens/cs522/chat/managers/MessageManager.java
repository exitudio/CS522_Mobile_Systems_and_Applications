package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import edu.stevens.cs522.chat.async.AsyncContentResolver;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chat.contracts.ChatroomContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.ChatRoom;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<ChatMessage> {

    public static final int LOADER_ID = 1;

    private static final IEntityCreator<ChatMessage> creator = new IEntityCreator<ChatMessage>() {
        @Override
        public ChatMessage create(Cursor cursor) {
            return new ChatMessage(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = getAsyncResolver();//new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllMessagesAsync(ChatRoom chatroom, IQueryListener<ChatMessage> listener) {
        Log.i(this.getClass().toString(),"getAllMessagesAsync()");

//        QueryBuilder.executeQuery(
//                tag,
//                (Activity) context,
//                MessageContract.CONTENT_URI(chatroom.id), LOADER_ID,
//                new IEntityCreator<ChatMessage>() {
//                    @Override
//                    public ChatMessage create(Cursor cursor) {
//                        Log.i(this.getClass().toString(), "IEntity");
//                        return new ChatMessage(cursor);
//                    }
//                },
//                listener
//        );

        Bundle bundle = new Bundle();
        bundle.putLong(ChatroomContract.ID,chatroom.id);



        QueryBuilder.executeQuery(
                tag,
                (Activity) context,
                MessageContract.CONTENT_URI,
                LOADER_ID,
                new IEntityCreator<ChatMessage>() {
                    @Override
                    public ChatMessage create(Cursor cursor) {
                        Log.i(this.getClass().toString(), "IEntity");
                        return null;
                    }
                },
                bundle,
                listener
        );
    }

    public void persistAsync(final ChatMessage message) {
        //slide 58-59
        ContentValues values = new ContentValues();
        message.writeToProvider(values);

        contentResolver.insertAsync(MessageContract.CONTENT_URI, values,
                new IContinue<Uri>() {
                    public void kontinue(Uri id) {
                        Log.i(this.getClass().toString(),"kontinue:"+id);
                        message.id = MessageContract.getId(id);
                    }
                });
    }

}
