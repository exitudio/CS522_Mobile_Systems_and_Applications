package edu.stevens.cs522.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.ChatRoom;
import edu.stevens.cs522.chat.util.StringUtils;

/**
 * Created by dduggan.
 */

public class SendMessage extends DialogFragment {

    public interface IMessageSender {
        public void send(ChatRoom chatroom, String text, Double latitude, Double longitude, Date timestamp);
    }

    public static final String CHATROOM_KEY = "chatroom";

    public static void launch(Activity context, String tag, ChatRoom chatroom) {
        SendMessage dialog = new SendMessage();
        Bundle args = new Bundle();
        args.putParcelable(CHATROOM_KEY, chatroom);
        dialog.setArguments(args);
        dialog.show(context.getFragmentManager(), tag);
    }

    private IMessageSender listener;

    private ChatRoom chatroom;

    private Double latitude;

    private Double longitude;

    private Date timestamp;

    private EditText messageText;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (!(activity instanceof IMessageSender)) {
            throw new IllegalStateException("Activity must implement IAddChatroom.");
        }

        listener = (IMessageSender) activity;
    }

    private OnClickListener confirmListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(this.getClass().toString(),"confirmListener onClick()");
            if (!StringUtils.isEmptyInput(messageText.getText())) {
                SendMessage.this.dismiss();
                listener.send(chatroom, messageText.getText().toString(), latitude, longitude, timestamp);
            }
        }
    };

    private OnClickListener cancelListener = new OnClickListener() {
        public void onClick(View view) {
            SendMessage.this.getDialog().cancel();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatroom = getArguments().getParcelable(CHATROOM_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // If not using AlertDialog
        View rootView = inflater.inflate(R.layout.send_message, container, false);

        // TODO initialize the UI
        messageText = (EditText) rootView.findViewById(R.id.message_text);
        Button sendButton = (Button) rootView.findViewById(R.id.send);
        sendButton.setOnClickListener(confirmListener);
        Button cancelButton = (Button) rootView.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(cancelListener);


        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Not much to do unless using AlertDialog
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
