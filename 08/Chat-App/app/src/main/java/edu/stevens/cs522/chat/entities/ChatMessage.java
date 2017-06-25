package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable{

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum = 0;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public Double longitude = 123.0;

    public Double latitude = 456.0;

    // Sender username and FK (in local database)
    public String sender;
    public long senderId = -1;
    public long chatRoomId = -1;

    public ChatMessage() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public ChatMessage(Cursor cursor) {
        // TODO
        this.id = MessageContract.getId(cursor);
        this.seqNum = MessageContract.getSequenceNumber(cursor);
        this.messageText = MessageContract.getMessageText(cursor);
        this.chatRoom = MessageContract.getChatRoom(cursor);
        this.chatRoomId = MessageContract.getChatRoomID(cursor);
        this.timestamp = MessageContract.getTimeStamp(cursor);
        this.latitude = MessageContract.getLatitude(cursor);
        this.longitude = MessageContract.getLongitude(cursor);
        this.sender = MessageContract.getSender(cursor);
        this.senderId = MessageContract.getSenderId(cursor);
    }

    protected ChatMessage(Parcel in) {
        this.id = in.readLong();
        this.seqNum = in.readLong();
        this.messageText = in.readString();
        this.chatRoom = in.readString();
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.sender = in.readString();
        this.senderId = in.readLong();
        Log.i(this.getClass().toString(),"ChatMessage(Parcel in), timestamp="+this.timestamp+": tmpTimestamp="+tmpTimestamp);

    }

    public void writeToProvider(ContentValues out) {
        // TODO
        //        MessageContract.putId(out, id);
        MessageContract.putSequenceNumberColumn(out,seqNum);
        MessageContract.putMessageText(out,messageText);
        MessageContract.putChatRoom(out,chatRoom);
        MessageContract.putTimeStamp(out,timestamp);
        MessageContract.putLatitude(out,latitude);
        MessageContract.putLongitude(out,longitude);
        MessageContract.putSender(out,sender);
        MessageContract.putSenderId(out,senderId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.seqNum);
        dest.writeString(this.messageText);
        dest.writeString(this.chatRoom);
        dest.writeLong(this.timestamp != null ? this.timestamp.getTime() : -1);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.sender);
        dest.writeLong(this.senderId);
        Log.i(this.getClass().toString(),"writeToParcel(...)"+this.timestamp);
    }

    public static final Parcelable.Creator<ChatMessage> CREATOR = new Parcelable.Creator<ChatMessage>() {
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
