package edu.stevens.cs522.bookstore.entities;


import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

	// TODO Modify this to implement the Parcelable interface.

	// TODO redefine toString() to display book title and price (why?).

	public int id;

	public String title;

	public Author[] authors;

	public String isbn;

	public String price;

	public Book(int id, String title, Author[] author, String isbn, String price) {
		this.id = id;
		this.title = title;
		this.authors = author;
		this.isbn = isbn;
		this.price = price;
	}

	public String getAuthorsTostring(){
		String authorsString = "";
		for(int i=0; i<=this.authors.length-1; i++){
			authorsString += this.authors[i].firstName+" "+this.authors[i].middleInitial+" "+this.authors[i].lastName;
		}
		return authorsString;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.title);
		dest.writeTypedArray(this.authors, flags);
		dest.writeString(this.isbn);
		dest.writeString(this.price);
	}

	protected Book(Parcel in) {
		this.id = in.readInt();
		this.title = in.readString();
		this.authors = in.createTypedArray(Author.CREATOR);
		this.isbn = in.readString();
		this.price = in.readString();
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
}