/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.ChatRoom;
import edu.stevens.cs522.chat.managers.TypedCursor;

public class ChatFragment extends Fragment implements OnClickListener, QueryBuilder.IQueryListener<ChatMessage> {

    public final static String TAG = ChatFragment.class.getCanonicalName();

    public final static String CHATROOM_KEY = "chatroom";

    public interface IChatListener {

        public void getMessages(ChatRoom chatroom);

        public void addMessageDialog(ChatRoom chatroom);

    }

    private IChatListener listener;

    private Context context;

    private ChatRoom chatroom;
		
    /*
     * UI for displaying received messages
     */
	private ListView messageList;

    private View addButton;

    private SimpleCursorAdapter messagesAdapter;


    public ChatFragment() {
    }


    @Override
    public void onAttach(Activity context) {
        Log.i(TAG,"onAttach");
        super.onAttach(context);
        this.context = context;
        if (context instanceof IChatListener) {
            listener = (IChatListener) context;
        } else {
            throw new IllegalStateException("Activity must implement IChatListener!");
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
		super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle!=null) {
            chatroom = getArguments().getParcelable(CHATROOM_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messages, container, false);

        addButton = rootView.findViewById(R.id.add_button);
        addButton.setOnClickListener(this);

        // TODO use SimpleCursorAdapter to display the messages received.
        String[] from =	new	String[] { MessageContract.SENDER, MessageContract.MESSAGE_TEXT };
        int[] to = new	int[] { android.R.id.text1, android.R.id.text2 };
        messagesAdapter = new SimpleCursorAdapter(rootView.getContext(), android.R.layout.simple_list_item_2, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
        messageList = (ListView) rootView.findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

	public void onResume() {
        super.onResume();

        if (chatroom != null) {
            // TODO initiate a query for all messages in the activity
            listener.getMessages(chatroom);
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }


    public void setChatroom(ChatRoom chatroom) {
        this.chatroom = chatroom;
        Log.i(TAG, "setChatroom chatroom.name="+chatroom.name);

        // TODO initiate a query for all messages in the activity
        listener.getMessages(chatroom);
    }

    @Override
    public void handleResults(TypedCursor<ChatMessage> results) {
        // TODO
        messagesAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // TODO
    }

    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {

        if (chatroom != null) {
            listener.addMessageDialog(chatroom);
        }

    }

}