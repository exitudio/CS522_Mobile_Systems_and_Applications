package edu.stevens.cs522.bookstore.entities;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.BookContract;

public class Book implements Parcelable {


    public long id;
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

    public String getAuthorsToString() {
        String authorsString = "";
        for (int i = 0; i <= this.authors.length - 1; i++) {
            authorsString += this.authors[i].firstName + " " + this.authors[i].middleInitial + " " + this.authors[i].lastName;
        }
        return authorsString;
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
        dest.writeString(this.price);
    }

    protected Book(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.authors = in.createTypedArray(Author.CREATOR);
        this.isbn = in.readString();
        this.price = in.readString();
    }

    public Book(Cursor cursor) {
        this.id = BookContract.getId(cursor);
        this.title = BookContract.getTitle(cursor);
        this.authors = new Author[]{new Author("f","m","l")};// BookContract.getAuthors(cursor);
        this.isbn = BookContract.getIsbn(cursor);
        this.price = BookContract.getPrice(cursor);
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
        //BookContract.putId(out, id); //no write id when insert
        BookContract.putTitle(out, title);
        BookContract.putIsbn(out, isbn);
        BookContract.putPrice(out, price);
    }

}