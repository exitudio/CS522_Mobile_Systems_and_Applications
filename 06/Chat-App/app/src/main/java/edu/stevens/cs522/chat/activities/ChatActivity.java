/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.rest.RegisterRequest;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.InetAddressUtils;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class ChatActivity extends Activity implements OnClickListener, QueryBuilder.IQueryListener<ChatMessage>, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;
	
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText chatRoomName;

    private EditText messageText;

    private Button sendButton;


    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
		super.onCreate(savedInstanceState);

        // TODO initialize sendResultReceiver
        sendResultReceiver = new ResultReceiverWrapper(new Handler());



        setContentView(R.layout.messages);

        // TODO use SimpleCursorAdapter to display the messages received.
        String[] from =	new	String[] { MessageContract.SENDER, MessageContract.MESSAGE_TEXT };
        int[] to = new	int[] { android.R.id.text1, android.R.id.text2 };
        messagesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
        messageList = (ListView) findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);
        //android.R.layout.simple_list_item_2

        // TODO create the message and peer managers, and initiate a query for all messages
        peerManager = new PeerManager(this);
        messageManager = new MessageManager(this);
        messageManager.getAllMessagesAsync(this);

        // TODO instantiate helper for service
        helper = new ChatHelper(this, sendResultReceiver);


        //--- EKKASIT EDIT --
        messageText = (EditText) findViewById(R.id.message_text);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        /**
         * Initialize settings to default values.
         */
//        Settings.setRegistered(this,false);
        if (!Settings.isRegistered(this)) {
            // TODO launch registration activity
            Intent intentRegister = new Intent(this, RegisterActivity.class);
            startActivity(intentRegister);
            return;
        }
    }

	public void onResume() {
        Log.i(TAG,"onResume");
        super.onResume();
        sendResultReceiver.setReceiver(this);
    }

    public void onPause() {
        Log.i(TAG,"onPause");
        super.onPause();
        sendResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG,"onCreateOptionMenu");
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG,"onOptionsItemSelected");
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // TODO PEERS provide the UI for viewing list of peers
            case R.id.peers:
                Intent intentPeers = new Intent(this, ViewPeersActivity.class);
                startActivity(intentPeers);
                break;

            // TODO SETTINGS provide the UI for settings
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }



    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {
        if (helper != null) {

            String chatRoom;

            String message = messageText.getText().toString();

            // TODO get chatRoom and message from UI, and use helper to post a message
            helper.postMessage(null,message);
            // TODO add the message to the database
            final ChatMessage chatMessage = new ChatMessage();
            chatMessage.sender = Settings.getChatName(this);
            chatMessage.timestamp = new Date(System.currentTimeMillis());
            chatMessage.messageText = message;

            Peer sender = new Peer();
            sender.name = chatMessage.sender;
            sender.timestamp = chatMessage.timestamp;
//            sender.address = receivePacket.getAddress();
//            sender.port = receivePacket.getPort();

            peerManager.persistAsync(sender, new IContinue<Long>() {
                @Override
                public void kontinue(Long id) {
                    Log.i("peerManager.","persistAsync");
                    chatMessage.senderId = id;
                    messageManager.persistAsync(chatMessage);
                }
            });

            // End todo

            Log.i(TAG, "Sent message: " + message);

            messageText.setText("");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        Log.i(TAG,"onReceiveResult");
        switch (resultCode) {
            case RESULT_OK:
                // TODO show a success toast message
                Toast.makeText(this, "Message sent successfully.", Toast.LENGTH_SHORT).show();
                break;
            default:
                // TODO show a failure toast message
                Toast.makeText(this, "Message sent fail.", Toast.LENGTH_SHORT).show();
                break;
        }
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

}