package edu.stevens.cs522.chat.rest.requests;

import android.os.Parcel;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;

import edu.stevens.cs522.chat.rest.responses.RegisterResponse;
import edu.stevens.cs522.chat.rest.RequestProcessor;
import edu.stevens.cs522.chat.rest.responses.Response;

/**
 * Created by dduggan.
 */

public class RegisterRequest extends Request {

    public String chatName;

    public RegisterRequest(String chatName) {
        super();
        this.chatName = chatName;
    }

    @Override
    public String getRequestEntity() throws IOException {
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        return new RegisterResponse(connection);
    }

    @Override
    public Response process(RequestProcessor processor) {
        return processor.perform(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        super.writeToParcel(dest,flags);
        dest.writeString(this.chatName);
    }

    public RegisterRequest(Parcel in) {
        super(in);
        this.chatName = in.readString();
        // TODO
    }

    public static Creator<RegisterRequest> CREATOR = new Creator<RegisterRequest>() {
        @Override
        public RegisterRequest createFromParcel(Parcel source) {
            return new RegisterRequest(source);
        }

        @Override
        public RegisterRequest[] newArray(int size) {
            return new RegisterRequest[size];
        }
    };

}
