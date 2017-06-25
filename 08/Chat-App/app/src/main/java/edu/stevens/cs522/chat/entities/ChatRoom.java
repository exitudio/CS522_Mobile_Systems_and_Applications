package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.ChatroomContract;

/**
 * Created by dduggan.
 */

public class ChatRoom implements Parcelable {

    // Primary key in the database
    public long id;

    // Name of the chat room
    public String name = "";

    public ChatRoom() {
    }
    public ChatRoom(long currentRoomID, String chatRoomName) {
        id = currentRoomID;
        name = chatRoomName;
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public ChatRoom(Cursor cursor) {
        // TODO
        this.id = ChatroomContract.getId(cursor);
        this.name = ChatroomContract.getName(cursor);
    }

    public void writeToProvider(ContentValues values) {
        Log.i(this.getClass().toString(), "writeToProvider(ContentValues)");
        // TODO
        ChatroomContract.putId(values, id);
        ChatroomContract.putName(values, name);
    }

    protected ChatRoom(Parcel in) {
        Log.i(this.getClass().toString(), "ChatRoom(Parcel)");
        id = in.readLong();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.i(this.getClass().toString(), "writeToParcel(Parcel)");
        dest.writeLong(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel in) {
            return new ChatRoom(in);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };

}
