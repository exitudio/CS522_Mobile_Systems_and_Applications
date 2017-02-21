package edu.stevens.cs522.bookstore.util;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Book;

/**
 * Created by dduggan.
 */

public class BookAdapter extends ResourceCursorAdapter {

    protected final static int ROW_LAYOUT = R.layout.cart_row;

    public BookAdapter(Context context, Cursor cursor) {
        super(context, ROW_LAYOUT, cursor, 0);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO
        TextView titleLine = (TextView) view.findViewById(R.id.cart_row_title);
        TextView authorLine	= (TextView) view.findViewById(R.id.cart_row_author);
        Book book = new Book(cursor);
        titleLine.setText(book.title);
        authorLine.setText(book.getFirstAuthor());
    }

    @Override
    public View	newView(Context	context, Cursor cur, ViewGroup parent)	{
        LayoutInflater inflater	= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT,	parent,	false);
    }
}
