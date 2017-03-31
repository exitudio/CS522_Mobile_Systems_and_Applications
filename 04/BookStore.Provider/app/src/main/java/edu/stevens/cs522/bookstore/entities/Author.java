package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;

import static android.R.attr.author;

public class Author implements Parcelable {

	public long id;
	public String name;
	public int bookFk = -1;

	public Author(String authorText) {
		this.name = authorText;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeString(this.name);
	}

	protected Author(Parcel in) {
		this.id = in.readLong();
		this.name = in.readString();
	}
	public Author(Cursor cursor) {
		this.name = AuthorContract.getFirstName(cursor);
		this.bookFk = AuthorContract.getBookFk(cursor);
	}

	public static final Parcelable.Creator<Author> CREATOR = new Parcelable.Creator<Author>() {
		@Override
		public Author createFromParcel(Parcel source) {
			return new Author(source);
		}

		@Override
		public Author[] newArray(int size) {
			return new Author[size];
		}
	};

	public void writeToProvider(ContentValues out, int _bookFk) {
		//BookContract.putId(out, id); //no write id when persist
		AuthorContract.putFirstName(out, name);
		AuthorContract.putBookFk(out, _bookFk);
	}

	public String toString() {
		return name;
	}

}
