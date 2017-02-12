package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;

public class Author implements Parcelable {
	
	// TODO Modify this to implement the Parcelable interface.

	// NOTE: middleInitial may be NULL!
	
	public String firstName;
	
	public String middleInitial;
	
	public String lastName;
	public int bookFk = -1;

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
	public Author(Cursor cursor) {
		this.firstName = AuthorContract.getFirstName(cursor);
		this.middleInitial = AuthorContract.getMiddleInitial(cursor);
		this.lastName = AuthorContract.getLastName(cursor);
		this.bookFk = AuthorContract.getBookFk(cursor);
	}

	public Author(String authorText) {
		String[] name = authorText.split(" ");
		switch (name.length) {
			case 0:
				firstName = lastName = "";
				break;
			case 1:
				firstName = "";
				lastName = name[0];
				break;
			case 2:
				firstName = name[0];
				lastName = name[1];
				break;
			default:
				firstName = name[0];
				middleInitial = name[1];
				lastName = name[2];
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (firstName != null && !"".equals(firstName)) {
			sb.append(firstName);
			sb.append(' ');
		}
		if (middleInitial != null && !"".equals(middleInitial)) {
			sb.append(middleInitial);
			sb.append(' ');
		}
		if (lastName != null && !"".equals(lastName)) {
			sb.append(lastName);
		}
		return sb.toString();
	}

    public void writeToProvider(ContentValues out, int _bookFk) {
        //BookContract.putId(out, id); //no write id when persist
        AuthorContract.putFirstName(out, firstName);
        AuthorContract.putMiddleInitial(out, middleInitial);
        AuthorContract.putLastName(out, lastName);
        AuthorContract.putBookFk(out, _bookFk);
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
