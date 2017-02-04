package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

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
		// TODO provide SEARCH and CANCEL options
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_book_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO
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
		// TODO Just build a Book object with the search criteria and return that.

		//get string
		String title = ((EditText) findViewById(R.id.search_title)).getText().toString();
		String authorName = ((EditText) findViewById(R.id.search_author)).getText().toString();
		String[] authorNames = authorName.split(" ");
		Author author;
		if(authorNames!=null) {
			author = new Author(checkNull(authorNames,0), checkNull(authorNames,1), checkNull(authorNames,2) );
		}else{
			author = new Author("-","-","-");
		}
		String isbn = ((EditText) findViewById(R.id.search_isbn)).getText().toString();

		//create book object
		Author[] authors = {author};
		Book book = new Book(-1,title,authors,isbn,"");
		//intent
		Intent returnIntent = new Intent();
		returnIntent.putExtra("book", book);
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