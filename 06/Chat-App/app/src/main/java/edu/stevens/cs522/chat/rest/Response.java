package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Config;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import edu.stevens.cs522.chat.util.EnumUtils;

/**
 * Created by dduggan.
 */

public abstract class Response implements Parcelable {

    private final static String TAG = Response.class.getCanonicalName();

    public static enum ResponseType {
        ERROR,
        REGISTER,
        POSTMESSAGE,
        UNREGISTER
    }

    //redundant
    //public final static String REQUEST_ID_HEADER = Request.REQUEST_ID_HEADER;

    public final static String RESPONSE_MESSAGE_HEADER = "X-Response-Message";

	/*
	 * These fields are obtained from the responsen metadata (response headers and status line).
	 * The fields in the subclass responses are obtained from the JSON body of the response entity.
	 */

    // Correlation identifier for the original request.
    public long id = 0;

    // Human-readable response message (optional)
    public String responseMessage = "";

    // HTTP status code.
    public int httpResponseCode = 0;

    // HTTP status line message.
    public String httpResponseMessage = "";

    public abstract boolean isValid();

    public Response(HttpURLConnection connection, JsonReader jsonReader) throws IOException {

        //-------- Log all header ------------------------------------------
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            String key = entry.getKey();
            if (key != null) {
                for (String value : entry.getValue()) {
                    if (value != null) {
                        Log.i(TAG, key+":"+value);
                    }
                }
            }
        }
        //----------- ADD id by json instead of header -----------------

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals("id")) {
                id = jsonReader.nextInt();
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        Log.i(this.getClass().toString(), "PostMessageResponse() jsonreader.id="+id);
        //-------------------------------------------------------------------

        String reqIdHeader = connection.getHeaderField(Request.REQUEST_ID_HEADER);
        if (reqIdHeader == null) {
            Log.w(TAG, "Missing HTTP response header: X-REQUEST-ID"); // this bug come from server no header for X-REQUEST-ID see above code.
        } else {
            try {
                id = Long.parseLong(reqIdHeader);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Number format error for HTTP response header: X-REQUEST-ID");
            }
        }

        String message = connection.getHeaderField(RESPONSE_MESSAGE_HEADER);
        if (message != null) {
            responseMessage = message;
        }

        httpResponseCode = connection.getResponseCode();

        httpResponseMessage = connection.getResponseMessage();
        Log.i(TAG,"Response() httpResponseMessage="+httpResponseMessage);
    }

    public Response(long id, String responseMessage, int httpResponseCode, String httpResponseMessage) {
        this.id = id;
        this.responseMessage = responseMessage;
        this.httpResponseCode = httpResponseCode;
        this.httpResponseMessage = httpResponseMessage;
    }

    public Response(Parcel in) {
        id = in.readLong();
        responseMessage = in.readString();
        httpResponseCode = in.readInt();
        httpResponseMessage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(responseMessage);
        out.writeInt(httpResponseCode);
        out.writeString(httpResponseMessage);
    }

    public int describeContents() {
        return 0;
    }

    public static Response createResponse(Parcel in) {
        ResponseType requestType = EnumUtils.readEnum(ResponseType.class, in);
        switch (requestType) {
            case ERROR:
                return new ErrorResponse(in);
            case REGISTER:
                return new RegisterResponse(in);
            case POSTMESSAGE:
                return new PostMessageResponse(in);
            default:
                break;
        }
        throw new IllegalArgumentException("Unknown request type: "+requestType.name());
    }

    public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
        public Response createFromParcel(Parcel in) {
            return createResponse(in);
        }

        public Response[] newArray(int size) {
            return new Response[size];
        }
    };



}
