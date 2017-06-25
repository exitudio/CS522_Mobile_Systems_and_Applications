package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.rest.requests.PostMessageRequest;
import edu.stevens.cs522.chat.rest.requests.RegisterRequest;
import edu.stevens.cs522.chat.rest.requests.Request;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.DateUtils;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;


/**
 * Created by dduggan.
 */

public class ChatHelper {

    public static final String DEFAULT_CHAT_ROOM = "_default";

    private Context context;
    private ResultReceiverWrapper resultReceiverWrapper;

    public ChatHelper(Context context, ResultReceiverWrapper resultReceiverWrapper) {
        this.context = context;
        this.resultReceiverWrapper = resultReceiverWrapper;
    }

    // TODO provide a result receiver that will display a toast message upon completion
    public void register (String chatName, String serverUri) {
        if (chatName != null && !chatName.isEmpty()) {
            Settings.saveChatName(context, chatName);
            Settings.saveServerUri(context, serverUri);
            Settings.setRegistered(context, true);
            RegisterRequest request = new RegisterRequest(chatName);
            addRequest(request, resultReceiverWrapper);
        }
    }

    // TODO provide a result receiver that will display a toast message upon completion
    public void postMessage (String chatRoom, String text) {
        if (text != null && !text.isEmpty()) {
            if (chatRoom == null || chatRoom.isEmpty()) {
                chatRoom = DEFAULT_CHAT_ROOM;
            }
            ChatMessage message = new ChatMessage();
            message.seqNum = 0;
            message.messageText = text;
            message.chatRoom = chatRoom;
            message.timestamp = DateUtils.now();
            message.longitude = 123.0;
            message.latitude = 456.0;
            message.sender = Settings.getChatName(context);

            PostMessageRequest request = new PostMessageRequest(message);
            addRequest(request, resultReceiverWrapper);
        }
    }

    private void addRequest(Request request, ResultReceiver receiver) {
        context.startService(createIntent(context, request, receiver));
    }

    //redundant
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
