/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.services.ChatService;
import edu.stevens.cs522.chat.services.IChatService;
import edu.stevens.cs522.chat.util.MessageAdapter;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class ChatActivity extends Activity implements OnClickListener, QueryBuilder.IQueryListener<ChatMessage>, ServiceConnection, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;
	private ListView messageList;
    private MessageAdapter messagesAdapter;
    private MessageManager messageManager;
    private PeerManager peerManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText destinationHost;
    private EditText destinationPort;
    private EditText messageText;
    private Button sendButton;


    /*
     * Use to configure the app (user name and port)
     */
    private SharedPreferences settings;

    /*
     * Reference to the service, for sending a message
     */
    private IChatService chatService;

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
        setContentView(R.layout.messages);

        //initialize the UI.
        destinationHost = (EditText) findViewById(R.id.destination_host);
        destinationPort = (EditText) findViewById(R.id.destination_port);
        messageText = (EditText) findViewById(R.id.message_text);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        /**
         * Initialize settings to default values.
         */
		if (savedInstanceState == null) {
			PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		}

        settings = PreferenceManager.getDefaultSharedPreferences(this);


        // TODO use SimpleCursorAdapter to display the messages received.
        messagesAdapter = new MessageAdapter(this,null);
        messageList = (ListView) findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);
        // TODO create the message and peer managers, and initiate a query for all messages
        peerManager = new PeerManager(this);
        messageManager = new MessageManager(this);
        messageManager.getAllMessagesAsync(this);

        // TODO initiate binding to the service
        Intent intent = new Intent(this, ChatService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE );
        // TODO initialize sendResultReceiver
        sendResultReceiver = new ResultReceiverWrapper(new Handler());
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
    @Override
    public void onStop(){
        Log.i(TAG,"onStop");
        super.onStop();
    }

    public void onDestroy() {
        Log.i(TAG,"onDestroy");
        unbindService(this);
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.peers:
                Intent intentPeers = new Intent(this, ViewPeersActivity.class);
                startActivity(intentPeers);
                break;
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
    @Override
    public void onClick(View v) {
        Log.i(TAG,"onCLick");
        if (chatService != null) {
            /*
			 * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */

//          InetAddress destAddr = InetAddress.getByName( destinationHost.getText().toString() );
            String destAddr = destinationHost.getText().toString();
            int destPort = Integer.parseInt( destinationPort.getText().toString() );
            String username = settings.getString(SettingsActivity.USERNAME_KEY, getResources().getString(R.string.default_user_name));
            String message = messageText.getText().toString();

            // TODO get destination and message from UI, and username from preferences.
            chatService.send(destAddr,destPort,username,message,sendResultReceiver);
            // End todo


            messageText.setText("");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        Log.i(TAG,"onReceiveResult:"+resultCode);
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
        Log.i(TAG,"handleResults:");
        messagesAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // TODO
        Log.i(TAG,"closeResults:");
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        // TODO initialize chatService
        chatService = ((ChatService.ChatBinder) binder).getService();
        Log.i(TAG,"onServiceConnected bined, chatService="+chatService.toString());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        chatService = null;
    }
}