package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;

public class AddBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_RESULT_KEY = "book_result";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//provide SEARCH and CANCEL options
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_book_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// SEARCH: return the book details to the BookStore activity
		// CANCEL: cancel the search request
		switch (item.getItemId()) {
			case R.id.search:
				searchBook();
			case R.id.cacel:
				Intent cancelIntent = new Intent();
				setResult(Activity.RESULT_CANCELED, cancelIntent);
				finish();
			default:
				return super.onOptionsItemSelected(item);

		}
	}
	
	public Book searchBook(){
		/*
		 * Search for the specified book.
		 */

		//get string
		String title = ((EditText) findViewById(R.id.search_title)).getText().toString();
		String authorName = ((EditText) findViewById(R.id.search_author)).getText().toString();
		String[] authorNames = authorName.split(" ");
		List<Author> authors = new ArrayList<Author>();
		if(authorNames!=null) {
			for( int i=0; i<authorNames.length; i++) {
				Author author;
				authors.add(new Author(authorNames[i]) );
			}
		}else{
			authors.add( new Author("-") );
		}
		String isbn = ((EditText) findViewById(R.id.search_isbn)).getText().toString();
		String priceStr = ((EditText) findViewById(R.id.search_price)).getText().toString();
		Float price;
		if(priceStr+""!="") {
			price = Float.valueOf(priceStr);
		}else{
			price = Float.valueOf(0);
		}

		//create book object
		Author[] authorArray = new Author[authors.size()];
		authorArray = (Author[]) authors.toArray(new Author[authors.size()]);
		Book book = new Book(-1,title, authorArray ,isbn,price);
		//intent
		Intent returnIntent = new Intent();
		returnIntent.putExtra(BOOK_RESULT_KEY, book);
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
		return null;
	}

	private String checkNull(String[] authorNames, int index){
		if(index>authorNames.length-1 || authorNames[index]==""){
			return "-";
		}
		return authorNames[index];
	}

}