package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Book;

/**
 * Created by exit on 1/31/17.
 */

public class DetailBookActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_book);

        Book book = getIntent().getExtras().getParcelable("book");
        Log.i("book",book.title);
        ((TextView) findViewById(R.id.detailTitle)).setText(book.title);
        ((TextView) findViewById(R.id.detailAuthor)).setText(book.getAuthorsTostring());
        ((TextView) findViewById(R.id.detailIsbn)).setText(book.isbn);
        ((TextView) findViewById(R.id.detailPrice)).setText(book.price);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        return false;
    }
}
