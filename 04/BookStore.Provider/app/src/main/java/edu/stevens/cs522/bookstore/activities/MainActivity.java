package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.providers.BookProvider;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements OnItemClickListener, AbsListView.MultiChoiceModeListener, LoaderManager.LoaderCallbacks<Cursor> {
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    static final private int LOADER_ID = 1;

    BookAdapter bookAdapter;
    private ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents)

		// TODO Set the layout (use cart.xml layout)
        setContentView(R.layout.cart);

        // Use a custom cursor adapter to display an empty (null) cursor.
        bookAdapter = new BookAdapter(this, null);
        lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(bookAdapter);

        // TODO set listeners for item selection and multi-choice CAB
        lv.setLongClickable(true);
        lv.setOnItemClickListener(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(this);


        // TODO use loader manager to initiate a query of the database
        LoaderManager lm = this.getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookstore_menu, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                return true;
            case R.id.checkout:
                Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
                startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // TODO Handle results from the Search and Checkout activities.

        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        switch(requestCode) {
            case ADD_REQUEST:
                if(resultCode == Activity.RESULT_OK) {
                    // ADD: add the book that is returned to the shopping cart.
                    // It is okay to do this on the main thread for BookStoreWithContentProvider
                    Book book = intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    ContentValues values = new ContentValues();
                    book.writeToProvider(values);
                    getContentResolver().insert(BookContract.CONTENT_URI, values);
                }
                break;
            case CHECKOUT_REQUEST:
                if(resultCode == Activity.RESULT_OK) {
                    // CHECKOUT: empty the shopping cart.
                    // It is okay to do this on the main thread for BookStoreWithContentProvider
                    final int bookNumber = bookAdapter.getCursor().getCount();
                    final String unitString = bookNumber>1?" books":" book";

                    getContentResolver().delete(BookContract.CONTENT_URI,null,null);
                    Toast.makeText(getApplicationContext(), "Checked Out "+bookNumber+unitString,
                                    Toast.LENGTH_LONG).show();
                }
                break;
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO save the shopping cart contents (which should be a list of parcelables).
		
	}

    /*
     * Loader callbacks
     */

	@Override
	public Loader onCreateLoader(int id, Bundle args) {
        Log.i(TAG,"onCreateLoader");
		// TODO use a CursorLoader to initiate a query on the database
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this,BookContract.CONTENT_URI, null, null, null, null);
            default:
                throw new IllegalStateException("bad case");
        }
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loaders, Cursor cursor) {
        Log.i(TAG,"onLoadFinished");
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
        bookAdapter.swapCursor(cursor);
//        cursor.setNotificationUri(this.getContentResolver(), BookContract.CONTENT_URI);
	}

	@Override
	public void onLoaderReset(Loader<Cursor>  loader) {
        Log.i(TAG,"onLoaderReset");
        // TODO reset the UI when the cursor is empty
	}


    /*
     * Selection of a book from the list view
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = bookAdapter.getCursor();
        cursor.moveToPosition(position);
        Book book = new Book(cursor);
        final Activity _this = this;
        Intent checkOutIntent = new Intent(_this, ViewBookActivity.class);
        checkOutIntent.putExtra(ViewBookActivity.BOOK_KEY,book);
        startActivityForResult(checkOutIntent, CHECKOUT_REQUEST);
    }


    /*
     * Handle multi-choice action mode for deletion of several books at once
     */

    Set<Long> selected;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.i(TAG,"onCreateActionMode");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.books_cab, menu);

        selected = new HashSet<Long>();
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        Log.i(TAG,"onItemCheckedStateChanged");
        View item = bookAdapter.getView(position,null,lv);
//        item.setBackgroundColor(Color.GREEN); is this correct view?
//        item.invalidate();
        item.setSelected(true);

        Cursor cursor = (Cursor) bookAdapter.getItem(position);
        Long bookId = BookContract.getId(cursor);
        Log.i(TAG,"getItem id="+bookId);
        if (checked) {
            selected.add( bookId );
        } else {
            selected.remove( bookId );
        }
        if(selected.size()>1) {
            mode.setTitle("Delete " + selected.size() + " Books");
        }else{
            mode.setTitle("Delete 1 Book");
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Log.i(TAG,"onActionItemClicked");
        switch(item.getItemId()) {
            case R.id.delete:
                //bookManager.deleteBooksAsync(selected);
                Long[] ids = new Long[selected.size()];
                selected.toArray(ids);
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
                getContentResolver().delete(BookContract.CONTENT_URI, select, args);
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

}