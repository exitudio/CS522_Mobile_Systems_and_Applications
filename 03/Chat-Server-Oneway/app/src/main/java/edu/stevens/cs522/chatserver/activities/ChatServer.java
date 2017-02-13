/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.util.DateUtils;

import static android.R.attr.port;

public class ChatServer extends Activity implements OnClickListener {

	final static public String TAG = ChatServer.class.getCanonicalName();
		
	/*
	 * Socket used both for sending and receiving
	 */
	private DatagramSocket serverSocket; 

	/*
	 * True as long as we don't get socket errors
	 */
	private boolean socketOK = true; 

    /*
     * UI for displayed received messages
     */
//	private SimpleCursorAdapter messages; //deprecated class
//  private SimpleCursorAdapter messagesAdapter; //deprecated class
	private ListView messageListView;
    private MessagesDbAdapter messagesDbAdapter;
    private Button next;

    /*
     * Use to configure the app (user name and port)
     */
    private SharedPreferences settings;


    ArrayAdapter arrayAdapter;
    ArrayList<String> messagesList;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the messages thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /**
         * Initialize settings to default values.
         */
		if (savedInstanceState == null) {
			PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		}

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        int port = Integer.valueOf(settings.getString(SettingsActivity.APP_PORT_KEY, getResources().getString(R.string.default_app_port)));

		try {
			serverSocket = new DatagramSocket(port);
		} catch (Exception e) {
			Log.e(TAG, "Cannot open socket", e);
			return;
		}

        setContentView(R.layout.messages);

        // TODO open the database using the database adapter
        messagesDbAdapter = new MessagesDbAdapter(this);
        messagesDbAdapter.open();
        messagesDbAdapter.logAll();
        messagesDbAdapter.fetchAllMessages();
        messagesDbAdapter.close();

        // TODO query the database using the database adapter, and manage the cursor on the messages thread

        //use SimpleCursorAdapter (deprecated) to display the messages received.
        messagesList = new ArrayList<String>();
        messagesList.add("yo");
        messageListView = (ListView) findViewById(R.id.message_list);
        arrayAdapter = new ArrayAdapter(this, R.layout.message, messagesList);
        messageListView.setAdapter(arrayAdapter);


        //bind the button for "next" to this activity as listener
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);

	}

    public void onDestroy() {
        super.onDestroy();
        messagesDbAdapter.close();
        closeSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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



    public void onClick(View v) {
        Log.i("in","yo");
        /*Message messageTemp = new Message();
        messageTemp.messageText = "text";
        messageTemp.timestamp = DateUtils.now();
        messageTemp.sender = "Sender1";

        Peer senderTemp = new Peer();
        senderTemp.name = messageTemp.sender;
        senderTemp.timestamp = messageTemp.timestamp;
        senderTemp.address = new InetAddress();
        senderTemp.port = 1234;

        messageTemp.senderId = messagesDbAdapter.persist(senderTemp);

        messagesDbAdapter.open();
        messagesDbAdapter.persist(messageTemp);
        messagesDbAdapter.logAll();
        messagesDbAdapter.close();

        if(true) {
            return;
        }*/
		byte[] receiveData = new byte[1024];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
            Log.i(TAG,serverSocket.getLocalAddress().getHostAddress());
			serverSocket.receive(receivePacket);
			Log.i(TAG, "Received a packet");
        } catch (Exception e) {

            Log.e(TAG, "Problems receiving packet: " + e.getMessage());
            socketOK = false;
        }
			InetAddress sourceIPAddress = receivePacket.getAddress();
			Log.i(TAG, "Source IP Address: " + sourceIPAddress);


            String receiveTextTemp = new String(receivePacket.getData(), 0, receivePacket.getLength());
            receiveTextTemp = "EXIT:"+DateUtils.now().getTime()+":"+receiveTextTemp;
			String msgContents[] = receiveTextTemp.split(":");
//			String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");

            Message message = new Message();
            message.sender = msgContents[0];
            message.timestamp = new Date(Long.parseLong(msgContents[1]));
            message.messageText = msgContents[2];

			Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);

            Peer sender = new Peer();
            sender.name = message.sender;
            sender.timestamp = message.timestamp;
            sender.address = receivePacket.getAddress();
            sender.port = receivePacket.getPort();

            messagesDbAdapter.open();
            message.senderId = messagesDbAdapter.persist(sender);
            messagesDbAdapter.persist(message);
            messagesDbAdapter.logAll();
            messagesDbAdapter.close();

//            messagesAdapter.notifyDataSetChanged();
            arrayAdapter.notifyDataSetChanged();


	}

    public void onClickOld(View v) {

        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {
            Log.i(TAG,serverSocket.getLocalAddress().getHostAddress());
            serverSocket.receive(receivePacket);
            Log.i(TAG, "Received a packet");

            InetAddress sourceIPAddress = receivePacket.getAddress();
            Log.i(TAG, "Source IP Address: " + sourceIPAddress);

			/*
			 * TODO: Extract sender and receiver from message and display.
			 */
            String receiveText = new String(receiveData, 0, receivePacket.getLength());
            Log.i(TAG,"receiveText:"+receiveText);
            arrayAdapter.add(receiveText);

        } catch (Exception e) {

            Log.e(TAG, "Problems receiving packet: " + e.getMessage());
            socketOK = false;
        }

    }

	/*
	 * Close the socket before exiting application
	 */
	public void closeSocket() {
		serverSocket.close();
	}

	/*
	 * If the socket is OK, then it's running
	 */
	boolean socketIsOK() {
		return socketOK;
	}
	
}