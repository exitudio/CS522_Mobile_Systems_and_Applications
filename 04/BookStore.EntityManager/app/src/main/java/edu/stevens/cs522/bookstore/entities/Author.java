package edu.stevens.cs522.bookstore.entities;

import android.os.Parcel;
import android.os.Parcelable;

import static android.R.attr.author;

public class Author implements Parcelable {
	
	// TODO Modify this to implement the Parcelable interface.

	public long id;
	public String name;

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

	public static final Creator<Author> CREATOR = new Creator<Author>() {
		@Override
		public Author createFromParcel(Parcel source) {
			return new Author(source);
		}

		@Override
		public Author[] newArray(int size) {
			return new Author[size];
		}
	};
}
