package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chat.contracts.PeerContract;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    public long id;
    // Use as PK
    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public Double longitude;

    public Double latitude;

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
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public void writeToProvider(ContentValues out) {
//        MessageContract.putId(out, id);
        PeerContract.putName(out,name);
        PeerContract.putTimeStamp(out,timestamp);
        PeerContract.putLatitude(out,latitude);
        PeerContract.putLongitude(out,longitude);
    }

    public Peer(Cursor cursor) {
        this.id = PeerContract.getId(cursor);
        this.name = PeerContract.getName(cursor);
        this.timestamp = PeerContract.getTimeStamp(cursor);
        this.latitude = PeerContract.getLatitude(cursor);
        this.longitude = PeerContract.getLongitude(cursor);
    }

    protected Peer(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {
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
