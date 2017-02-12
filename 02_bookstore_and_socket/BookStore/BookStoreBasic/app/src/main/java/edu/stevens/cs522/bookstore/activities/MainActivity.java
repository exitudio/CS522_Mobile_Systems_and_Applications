package edu.stevens.cs522.bookstore.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.adapter.BooksAdapter;
import edu.stevens.cs522.bookstore.databases.CartDbAdapter;
import edu.stevens.cs522.bookstore.entities.Book;

public class MainActivity extends ListActivity {
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;
	static final private int DETAIL_REQUEST = ADD_REQUEST + 2;
	static final private int DELETE_REQUEST = ADD_REQUEST + 3;

	static final private String SHOPING_CART = "shopingcart";


	// There is a reason this must be an ArrayList instead of a List.
	@SuppressWarnings("unused")
	private ArrayList<Book> shoppingCart;
	private ArrayAdapter<Book> booksAdapter;

	//DB
	CartDbAdapter cartDbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cartDbAdapter = new CartDbAdapter(this);
		cartDbAdapter.open();
		shoppingCart = cartDbAdapter.fetchAllBooks();
		cartDbAdapter.logAllBooks();
		cartDbAdapter.close();

		// TODO Set the layout (use cart.xml layout)
		setContentView(R.layout.cart);

		// TODO use an array adapter to display the cart contents.
		booksAdapter = new BooksAdapter(this,shoppingCart);
		setListAdapter(booksAdapter);
//		getListView().setAdapter(booksAdapter);//do the samething

		//context menu
		final ListView listView = getListView();
		//final ListView listView = (ListView) findViewById(android.R.id.list); // the samething
		registerForContextMenu(listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				openContextMenu(view);
			}
		});

		CartDbAdapter cartDbAdapter = new CartDbAdapter(this);

		//mockup book
//		Author[] authors = {new Author("a","b","c")};
//		Book book = new Book(1,"Title 1",authors,"isbn...","Price...");
//		Book book2 = new Book(1,"Title 2",authors,"isbn...","Price...");
//		Book book3 = new Book(1,"Title 3",authors,"isbn...","Price...");
//		booksAdapter.add(book);
//		booksAdapter.add(book2);
//		booksAdapter.add(book3);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		Log.i("createContextMenu","..");
		menu.setHeaderTitle("Context Menu");
		menu.add(0, DETAIL_REQUEST, 0, "See Detail");
		menu.add(0, DELETE_REQUEST, 0, "Delete");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		try {
			menuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return false;
		}
		int id = (int) getListAdapter().getItemId(menuInfo.position);
        Book book = booksAdapter.getItem(id);
        Log.i("ClICK list id",""+id+","+book.id);
        Log.i("menu id:", Integer.toString(item.getItemId()) );
        switch(item.getItemId()){
            case DETAIL_REQUEST:
                Intent checkOutIntent = new Intent(this, ViewBookActivity.class);
                checkOutIntent.putExtra(ViewBookActivity.BOOK_KEY,book);
                startActivityForResult(checkOutIntent, CHECKOUT_REQUEST);
                break;
            case DELETE_REQUEST:
				cartDbAdapter.open();
				cartDbAdapter.delete(book);
				cartDbAdapter.close();
				updateView();
                break;
            default:
        }

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Log.i("onCreateOptionMenu","ok");
		// TODO provide ADD, DELETE and CHECKOUT options
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bookstore_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
		
		// ADD provide the UI for adding a book
		// Intent addIntent = new Intent(this, AddBookActivity.class);
		// startActivityForResult(addIntent, ADD_REQUEST);
		switch (item.getItemId()) {
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

		
		// DELETE delete the currently selected book
		
		// CHECKOUT provide the UI for checking out


		//return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// TODO Handle results from the Search and Checkout activities.
		
		// Use SEARCH_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
		
		// SEARCH: add the book that is returned to the shopping cart.
		
		// CHECKOUT: empty the shopping cart.

		switch (requestCode){
			case ADD_REQUEST:
				if(resultCode == Activity.RESULT_OK){
					Book book = (Book) intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
					cartDbAdapter.open();
					cartDbAdapter.persist(book);
					cartDbAdapter.logAllBooks();
					cartDbAdapter.close();
					updateView();
				}
				break;
			case CHECKOUT_REQUEST:
                if(resultCode == Activity.RESULT_OK){
					cartDbAdapter.open();
					cartDbAdapter.deleteAll();
					cartDbAdapter.close();
					updateView();
                }
				break;
			default:
				System.out.println("NO REQUEST CODE FOUND");
		}
	}

	private void updateView(){
		cartDbAdapter.open();
		shoppingCart = cartDbAdapter.fetchAllBooks();
		cartDbAdapter.logAllBooks();
		cartDbAdapter.close();

		booksAdapter = new BooksAdapter(this,shoppingCart);
		setListAdapter(booksAdapter);
	}

}