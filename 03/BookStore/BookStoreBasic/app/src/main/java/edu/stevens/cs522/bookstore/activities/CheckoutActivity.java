package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import edu.stevens.cs522.bookstore.R;

public class CheckoutActivity extends Activity {

	private final int ORDER_REQUEST = 0;
	private final int CANCEL_REQUEST = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO display ORDER and CANCEL options.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.checkout_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
		
		// ORDER: display a toast message of how many books have been ordered and return
		
		// CANCEL: just return with REQUEST_CANCELED as the result code
		switch (item.getItemId()) {
			case R.id.checkout_menu:
				Toast.makeText(getApplicationContext(), "Checked Out",
						Toast.LENGTH_LONG).show();

				Intent returnIntent = new Intent();
				setResult(Activity.RESULT_OK, returnIntent);
				finish();
			case R.id.cancel_menu:
				Intent cancelIntent = new Intent();
				setResult(Activity.RESULT_CANCELED, cancelIntent);
				finish();
			default:
				return super.onOptionsItemSelected(item);

		}
	}
}