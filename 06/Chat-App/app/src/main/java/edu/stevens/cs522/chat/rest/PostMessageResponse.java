package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.UUID;

/**
 * Created by dduggan.
 */

public class PostMessageResponse extends Response {

    public PostMessageResponse(HttpURLConnection connection, JsonReader jsonReader) throws IOException {
        super(connection, jsonReader);
    }

    @Override
    public boolean isValid() { return true; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        super.writeToParcel(dest,flags);
    }

    public PostMessageResponse(Parcel in) {
        super(in);
        // TODO
    }

    public static Creator<PostMessageResponse> CREATOR = new Creator<PostMessageResponse>() {
        @Override
        public PostMessageResponse createFromParcel(Parcel source) {
            return new PostMessageResponse(source);
        }

        @Override
        public PostMessageResponse[] newArray(int size) {
            return new PostMessageResponse[size];
        }
    };
}
