package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stevens.cs522.bookstore.contracts.BookContract;

public class Book implements Parcelable {
	
	// TODO Modify this to implement the Parcelable interface.

	public long id;
	public String title;
	public Author[] authors;
	public String isbn;
	public Float price;

    public Book() {
    }

	public Book(Cursor cursor) {
		this.id = BookContract.getId(cursor);
		this.title = BookContract.getTitle(cursor);
		List<Author> authorList = new ArrayList<Author>();
		String[] authorStrings = BookContract.getAuthors(cursor);
		for( int i=0; i<authorStrings.length; i++){
			authorList.add( new Author(authorStrings[i]) );
//			Log.i(this.getClass().toString(), "authorList:"+authorStrings[i]);
		}
		this.authors = authorList.toArray(new Author[authorList.size()]);
//        this.authors = new Author[]{new Author("f","m","l")};// BookContract.getAuthors(cursor);
		this.isbn = BookContract.getIsbn(cursor);
		this.price = BookContract.getPrice(cursor);
	}

	protected Book(Parcel in) {
		this.id = in.readLong();
		this.title = in.readString();
		this.authors = in.createTypedArray(Author.CREATOR);
		this.isbn = in.readString();
		this.price = in.readFloat();
	}

	public Book(int id, String title, Author[] author, String isbn, Float price) {
		this.id = id;
		this.title = title;
		this.authors = author;
		this.isbn = isbn;
		this.price = price;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeString(this.title);
		dest.writeTypedArray(this.authors, flags);
		dest.writeString(this.isbn);
		dest.writeFloat(this.price);
	}

	public String getFirstAuthor() {
		if (authors != null && authors.length > 0) {
			return authors[0].toString();
		} else {
			return "";
		}
	}

	public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
		@Override
		public Book createFromParcel(Parcel source) {
			return new Book(source);
		}

		@Override
		public Book[] newArray(int size) {
			return new Book[size];
		}
	};

	public void writeToProvider(ContentValues out) {
		//BookContract.putId(out, id); //no write id when persist
		BookContract.putTitle(out, title);
		BookContract.putIsbn(out, isbn);
		BookContract.putPrice(out, price);

		BookContract.putAuthors(out, getAuthorNames());
//		Log.i(this.getClass().toString(),TextUtils.join("|",authorsString));
	}

	public String[] getAuthorNames(){
		String[] authorsString = new String[authors.length];
		for(int i=0; i<authors.length; i++) {
			authorsString[i]=authors[i].toString();
		}
		return authorsString;
	}

}