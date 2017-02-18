package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

public class Book {
	
	// TODO Modify this to implement the Parcelable interface.

	public long id;
	public String title;
	public Author[] authors;
	public String isbn;
	public Float price;

    public Book() {
    }

	public Book(Cursor cursor) {
		// TODO init from cursor
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

	public void writeToParcel(Parcel out) {
		// TODO save state to parcel
	}

	public String getFirstAuthor() {
		if (authors != null && authors.length > 0) {
			return authors[0].toString();
		} else {
			return "";
		}
	}

	public void writeToProvider(ContentValues out) {
		// TODO write to ContentValues
	}


}