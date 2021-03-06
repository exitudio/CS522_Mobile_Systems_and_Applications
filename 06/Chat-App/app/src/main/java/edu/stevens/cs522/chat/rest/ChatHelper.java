package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.UUID;

import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;


/**
 * Created by dduggan.
 */

public class ChatHelper {

    public static final String DEFAULT_CHAT_ROOM = "_default";

    private Context context;
    private ResultReceiverWrapper resultReceiverWrapper;

    private String chatName;

    private UUID clientID;

    public ChatHelper(Context context, ResultReceiverWrapper resultReceiverWrapper) {
        this.context = context;
        this.resultReceiverWrapper = resultReceiverWrapper;
        this.chatName = Settings.getChatName(context);
        this.clientID = Settings.getClientId(context);
    }

    // TODO provide a result receiver that will display a toast message upon completion
    public void register (String chatName, String serverUri) {
        if (chatName != null && !chatName.isEmpty()) {
            RegisterRequest request = new RegisterRequest(chatName, clientID);
            this.chatName = chatName;
            Settings.saveChatName(context, chatName);
            Settings.saveServerUri(context, serverUri);
            Settings.setRegistered(context, true);
            addRequest(request,resultReceiverWrapper);
        }
    }

    // TODO provide a result receiver that will display a toast message upon completion
    public void postMessage (String chatRoom, String message) {
        this.chatName = Settings.getChatName(context);
        this.clientID = Settings.getClientId(context);
        if (message != null && !message.isEmpty()) {
            if (chatRoom == null || chatRoom.isEmpty()) {
                chatRoom = DEFAULT_CHAT_ROOM;
            }
            Log.i(this.getClass().toString(), "postMessage() chatName="+chatName+" chatRoom="+chatRoom+", message="+message);
            PostMessageRequest request = new PostMessageRequest(chatName, clientID, chatRoom, message);
            addRequest(request,resultReceiverWrapper);
        }
    }

    private void addRequest(Request request, ResultReceiver receiver) {
        context.startService(createIntent(context, request, receiver));
    }

// I think it's redundant
//    private void addRequest(Request request) {
//        addRequest(request, null);
//    }

    /**
     * Use an intent to send the request to a background service. The request is included as a Parcelable extra in
     * the intent. The key for the intent extra is in the RequestService class.
     */
    public static Intent createIntent(Context context, Request request, ResultReceiver receiver) {
        Intent requestIntent = new Intent(context, RequestService.class);
        requestIntent.putExtra(RequestService.SERVICE_REQUEST_KEY, request);
        if (receiver != null) {
            requestIntent.putExtra(RequestService.RESULT_RECEIVER_KEY, receiver);
        }
        return requestIntent;
    }

    public static Intent createIntent(Context context, Request request) {
        return createIntent(context, request, null);
    }

}
