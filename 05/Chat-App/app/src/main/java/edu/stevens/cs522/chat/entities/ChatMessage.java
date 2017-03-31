package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable{

    public long id;
    public String messageText;
    public Date timestamp;
    public String sender;
    public long senderId;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.messageText);
        dest.writeLong(this.timestamp != null ? this.timestamp.getTime() : -1);
        dest.writeString(this.sender);
        dest.writeLong(this.senderId);
    }
    public ChatMessage() {}
    public ChatMessage(Cursor cursor) {
        this.id = MessageContract.getId(cursor);
        this.messageText = MessageContract.getMessageText(cursor);
        this.timestamp = MessageContract.getTimeStamp(cursor);
        this.sender = MessageContract.getSender(cursor);
        this.senderId = MessageContract.getSenderId(cursor);
    }

    protected ChatMessage(Parcel in) {
        this.id = in.readLong();
        this.messageText = in.readString();
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
        this.sender = in.readString();
        this.senderId = in.readLong();
    }

    public void writeToProvider(ContentValues out) {
//        MessageContract.putId(out, id);
        MessageContract.putMessageText(out,messageText);
        MessageContract.putTimeStamp(out,timestamp);
        MessageContract.putSender(out,sender);
        MessageContract.putSenderId(out,senderId);
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

}
