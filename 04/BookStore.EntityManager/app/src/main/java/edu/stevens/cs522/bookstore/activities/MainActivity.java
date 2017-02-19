package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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

import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.managers.BookManager;
import edu.stevens.cs522.bookstore.managers.TypedCursor;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements OnItemClickListener, AbsListView.MultiChoiceModeListener, IQueryListener {
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    private BookManager bookManager;
    private BookAdapter bookAdapter;

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

        // TODO Set listeners for item selection and multi-choice CAB
        lv.setLongClickable(true);
        lv.setOnItemClickListener(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(this);

        // Initialize the book manager and query for all books
        bookManager = new BookManager(this);
        bookManager.getAllBooksAsync(this);
        Log.i(TAG,"onCreate");

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        Log.i(TAG,"onCreateOptionMenu");
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
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// TODO Handle results from the Search and Checkout activities.

        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        switch(requestCode) {
            case ADD_REQUEST:
                // ADD: add the book that is returned to the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                Book book = (Book) intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                bookManager.persistAsync(book);
                bookManager.getAllBooksAsync(this);
                break;
            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                break;
        }

	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO save the shopping cart contents (which should be a list of parcelables).
		
	}

    /*
     * TODO Query listener callbacks
     */

    @Override
    public void handleResults(TypedCursor results) {
        // TODO update the adapter
        if (results.moveToFirst()) {
            Book book = new Book(results.getCursor());
            Log.i(this.getClass().toString(),"handleResults:::"+
                    BookContract._ID+" : "+book.id+", "+
                    BookContract.TITLE+" : "+book.title+", "+
                    BookContract.PRICE+" : "+book.price+", "+
                    BookContract.ISBN+" : "+book.isbn+", "+
                    BookContract.AUTHORS+" : "+book.getFirstAuthor()
            );
        }
        bookAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // TODO update the adapter
    }


    /*
     * Selection of a book from the list view
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO query for this book's details, and send to ViewBookActivity
        // ok to do on main thread for BookStoreWithContentProvider
        Log.i(TAG,"click:"+position);
        Cursor cursor = bookAdapter.getCursor();
        cursor.moveToPosition(position);
        Book book = new Book(cursor);
        Intent checkOutIntent = new Intent(this, ViewBookActivity.class);
        checkOutIntent.putExtra(ViewBookActivity.BOOK_KEY,book);
        startActivityForResult(checkOutIntent, CHECKOUT_REQUEST);
    }


    /*
     * Handle multi-choice action mode for deletion of several books at once
     */

    Set<Long> selected;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO inflate the menu for the CAB
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
                // TODO delete the selected books
                bookManager.deleteBooksAsync(selected);
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        Log.i(TAG,"onPrepareActionMode");
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        Log.i(TAG,"onDestroyActionMode");
    }

}