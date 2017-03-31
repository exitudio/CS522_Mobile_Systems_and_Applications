package edu.stevens.cs522.chatserver.util;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.entities.Message;

/**
 * Created by dduggan.
 */

public class MessageAdapter extends ResourceCursorAdapter {

    protected final static int ROW_LAYOUT = android.R.layout.simple_list_item_1;

    public MessageAdapter(Context context, Cursor cursor) {
        super(context, ROW_LAYOUT, cursor, 0);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO
        TextView messageLine = (TextView) view.findViewById(android.R.id.text1);
        Message message = new Message(cursor);
        messageLine.setText(message.sender+":"+message.timestamp+":"+message.messageText);
    }

    @Override
    public View	newView(Context	context, Cursor cur, ViewGroup parent)	{
        LayoutInflater inflater	= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT,	parent,	false);
    }
}
