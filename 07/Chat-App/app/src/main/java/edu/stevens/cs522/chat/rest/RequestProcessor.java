package edu.stevens.cs522.chat.rest;

import android.content.ContentValues;
import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.RequestManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.requests.PostMessageRequest;
import edu.stevens.cs522.chat.rest.requests.RegisterRequest;
import edu.stevens.cs522.chat.rest.requests.Request;
import edu.stevens.cs522.chat.rest.requests.SynchronizeRequest;
import edu.stevens.cs522.chat.rest.responses.ErrorResponse;
import edu.stevens.cs522.chat.rest.responses.Response;
import edu.stevens.cs522.chat.util.StringUtils;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    private RequestManager requestManager;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod =  new RestMethod(context);
        this.requestManager = new RequestManager(context);
    }

    public Response process(Request request) {
        return request.process(this);
    }

    public Response perform(RegisterRequest request) {
        return restMethod.perform(request);
    }

    public Response perform(PostMessageRequest request) {
        // We will just insert the message into the database, and rely on background sync to upload
        // return restMethod.perform(request)
        requestManager.persist(request.message);
        return request.getDummyResponse();
    }

    public Response perform(SynchronizeRequest request) {
        RestMethod.StreamingResponse response = null;
        final TypedCursor<ChatMessage> messages = requestManager.getUnsentMessages();
        int numMessagesReplaced = messages.getCount();
        Log.i("RequestProcessor","numMessagesReplaced="+numMessagesReplaced);
        try {
            RestMethod.StreamingOutput out = new RestMethod.StreamingOutput() {
                @Override
                public void write(final OutputStream os) throws IOException {
                    try {
                        JsonWriter wr = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
                        wr.beginArray();
                        /*
                         * TODO stream unread messages to the server:
                         * {
                         *   chatroom : ...,
                         *   timestamp : ...,
                         *   latitude : ...,
                         *   longitude : ....,
                         *   text : ...
                         * }
                         */
                        if(messages.moveToFirst()){
                            do{
                                ChatMessage message = new ChatMessage(messages.getCursor());
                                wr.beginObject();
                                wr.name("chatroom").value(message.chatRoom);
                                wr.name("timestamp").value(message.timestamp.getTime());
                                wr.name("latitude").value(message.latitude);
                                wr.name("longitude").value(message.longitude);
                                wr.name("text").value(message.messageText);
                                wr.endObject();
                            }while(messages.moveToNext());
                        }


                        wr.endArray();
                        wr.flush();
                    } finally {
                        messages.close();
                    }
                }
            };
            request.lastSequenceNumber = requestManager.getLastSequenceNumber();
            response = restMethod.perform(request, out);

            //json decode
            JsonReader rd = new JsonReader(new InputStreamReader(new BufferedInputStream(response.getInputStream()), StringUtils.CHARSET));
            String sectionName;
            String valueName;

            List<Peer> receivePeers = new ArrayList<Peer>();
            List<ChatMessage> receiveMessages = new ArrayList<ChatMessage>();

            rd.beginObject();
            while (rd.hasNext()) {
                sectionName = rd.nextName();
                Log.i("RequestProcessor", "sectionName="+sectionName);
                if(sectionName.equals("clients")) {
                    rd.beginArray();


                    while(rd.hasNext()){
                        rd.beginObject();
                        Peer receivePeer = new Peer();
                        while (rd.hasNext()) {
                            valueName = rd.nextName();
                            Log.i("RequestProcessor", "valueName="+valueName);

                            if (valueName.equals("username")) {
                                receivePeer.name = rd.nextString();
                            } else if (valueName.equals("timestamp")) {
                                long t = rd.nextLong();
                                Log.i("RequestProcessor", "t="+t);

                                receivePeer.timestamp = new Date(t);
                            } else if (valueName.equals("latitude")) {
                                receivePeer.latitude = rd.nextDouble();
                            } else if (valueName.equals("longitude")) {
                                receivePeer.latitude = rd.nextDouble();
                            }
                            Log.i("RequestProcessor", "JsonReader name=" + valueName);
                        }
                        receivePeers.add(receivePeer);
                        rd.endObject();

                        ContentValues values = new ContentValues();
                        receivePeer.writeToProvider(values);
                        context.getContentResolver().insert(PeerContract.CONTENT_URI, values);
                    }

                    rd.endArray();
                }else if(sectionName.equals("messages")){
                    rd.beginArray();

                    while (rd.hasNext()) {
                        rd.beginObject();

                        ChatMessage receiveMessage = new ChatMessage();
                        while (rd.hasNext()) {
                            valueName = rd.nextName();
                            if (valueName.equals("chatroom")) {
                                receiveMessage.chatRoom = rd.nextString();
                            } else if (valueName.equals("timestamp")) {
                                receiveMessage.timestamp = new Date(rd.nextLong());
                            } else if (valueName.equals("latitude")) {
                                receiveMessage.latitude = rd.nextDouble();
                            } else if (valueName.equals("longitude")) {
                                receiveMessage.latitude = rd.nextDouble();
                            } else if (valueName.equals("seqnum")) {
                                receiveMessage.seqNum = rd.nextInt();
                            } else if (valueName.equals("sender")) {
                                receiveMessage.sender = rd.nextString();
                            } else if (valueName.equals("text")) {
                                receiveMessage.messageText = rd.nextString();
                            }
                            Log.i("RequestProcessor", "JsonReader name=" + valueName);
                        }
                        receiveMessages.add(receiveMessage);
                        rd.endObject();


                    }
                    rd.endArray();
                }
            }
            rd.endObject();
            Log.i("RequestProcessor",rd.toString());
            // TODO parse data from server (messages and peers) and update database
            // See RequestManager for operations to help with this.
            requestManager.syncMessages(numMessagesReplaced,receiveMessages);

            return response.getResponse();

        } catch (IOException e) {
            return new ErrorResponse(request.id, e);

        } finally {
            if (response != null) {
                response.disconnect();
            }
        }
    }

}
