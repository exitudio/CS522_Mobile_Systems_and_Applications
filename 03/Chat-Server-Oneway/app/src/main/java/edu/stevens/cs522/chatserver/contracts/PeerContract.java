package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chatserver.util.DateUtils;
import edu.stevens.cs522.chatserver.util.InetAddressUtils;

/**
 * Created by dduggan.
 */

public class PeerContract implements BaseColumns {

    // TODO define column names, getters for cursors, setters for contentvalues
    public static final String NAME = "name";
    public static final String TIMESTAMP = "timestamp";
    public static final String ADDRESS = "address";
    public static final String PORT = "port";

    /*
     * ID column
     */
    private static int idColumn = -1;
    public static Long getId(Cursor cursor) {
        if (idColumn < 0) {
            idColumn =  cursor.getColumnIndexOrThrow(_ID);
        }
        return cursor.getLong(idColumn);
    }
    public static void putId(ContentValues values, Long id) {
        values.put(_ID, id);
    }

    /*
     * Name
     */
    private static int nameColumn = -1;
    public static String getName(Cursor cursor) {
        if (nameColumn < 0) {
            nameColumn = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(nameColumn);
    }
    public static void putName(ContentValues out, String name) {
        out.put(NAME, name);
    }

    /*
     * TIMESTAMP
     */
    private static int timeStampColumn = -1;
    public static Date getTimeStamp(Cursor cursor) {
        if (timeStampColumn < 0) {
            timeStampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
//        return cursor.getLong(timeStampColumn);
        return DateUtils.getDate(cursor,timeStampColumn);
    }
    public static void putTimeStamp(ContentValues out, Date timestamp) {
//        out.put(TIMESTAMP, timestamp);
        DateUtils.putDate(out,TIMESTAMP,timestamp);
    }

    /*
     * ADDRESS
     */
    private static int addressColumn = -1;
    public static InetAddress getAddress(Cursor cursor) {
        if (addressColumn < 0) {
            addressColumn = cursor.getColumnIndexOrThrow(ADDRESS);
        }
        return InetAddressUtils.getAddress(cursor,addressColumn);
//        return cursor.getString(addressColumn);
    }
    public static void putAddress(ContentValues out, InetAddress address) {
//        out.put(ADDRESS, address);
        InetAddressUtils.putAddress(out,ADDRESS,address);
    }


    /*
     * PORT
     */
    private static int portColumn = -1;
    public static int getPort(Cursor cursor) {
        if (portColumn < 0) {
            portColumn = cursor.getColumnIndexOrThrow(PORT);
        }
        return cursor.getInt(portColumn);
    }
    public static void putPort(ContentValues out, int port) {
        out.put(PORT, port);
    }

}
