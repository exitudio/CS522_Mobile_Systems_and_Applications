package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.PeerContract;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public InetAddress address;

    public int port;

    public Peer() {
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.timestamp != null ? this.timestamp.getTime() : -1);
        dest.writeSerializable(this.address);
        dest.writeInt(this.port);
    }

    public void writeToProvider(ContentValues out) {
//        MessageContract.putId(out, id);
        PeerContract.putName(out,name);
        PeerContract.putTimeStamp(out,timestamp);
        PeerContract.putAddress(out,address);
        PeerContract.putPort(out,port);
    }

    public Peer(Cursor cursor) {
        this.id = PeerContract.getId(cursor);
        this.name = PeerContract.getName(cursor);
        this.timestamp = PeerContract.getTimeStamp(cursor);
        this.address = PeerContract.getAddress(cursor);
        this.port = PeerContract.getPort(cursor);
    }

    protected Peer(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
        this.address = (InetAddress) in.readSerializable();
        this.port = in.readInt();
    }

    public static final Parcelable.Creator<Peer> CREATOR = new Parcelable.Creator<Peer>() {
        @Override
        public Peer createFromParcel(Parcel source) {
            return new Peer(source);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };
}
