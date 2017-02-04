package edu.stevens.cs522.bookstore.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Author implements Parcelable {
	
	// TODO Modify this to implement the Parcelable interface.

	// NOTE: middleInitial may be NULL!
	
	public String firstName;
	
	public String middleInitial;
	
	public String lastName;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.firstName);
		dest.writeString(this.middleInitial);
		dest.writeString(this.lastName);
	}

	public Author(String firstName, String middleInitial, String lastName) {
		this.firstName = firstName;
		this.middleInitial = middleInitial;
		this.lastName = lastName;
	}

	protected Author(Parcel in) {
		this.firstName = in.readString();
		this.middleInitial = in.readString();
		this.lastName = in.readString();
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
}
