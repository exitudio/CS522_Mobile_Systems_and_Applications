package edu.stevens.cs522.bookstore.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Book;

/**
 * Created by exit on 1/28/17.
 */

public class BooksAdapter extends ArrayAdapter<Book> {
    public BooksAdapter(Context context, ArrayList<Book> books)	{
        super(context,	0,	books);
    }
    @Override
    public View	getView(int position, View convertView, ViewGroup parent)	{
        //	Get	the	data	item	for	this	position
        Book book = (Book) getItem(position);
        //	Check	if	an	existing	view	is	being	reused,	otherwise	inflate	the	view
        if (convertView	== null)	{
            convertView	= LayoutInflater.from(getContext()).inflate(R.layout.cart_row,	parent,	false);
        }
        //	Lookup	view	for	data	population
        TextView title	= (TextView) convertView.findViewById(R.id.cart_row_title);
        TextView author	= (TextView) convertView.findViewById(R.id.cart_row_author);
        //	Populate	the	data	into	the	template	view	using	the	data	object
        title.setText(book.title);
        String authorsName = "";
        for(int i=0; i<=book.authors.length-1; i++){
            authorsName += book.authors[i].firstName+" "+
                            book.authors[i].middleInitial+" "+
                            book.authors[i].lastName;
        }
        author.setText(authorsName);
        //	Return	the	completed	view	to	render	on	screen

//        convertView.setOnCreateContextMenuListener(this);
        return convertView;
    }

}
