/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.Date;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.ChatroomContract;
import edu.stevens.cs522.chat.dialog.SendMessage;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.ChatRoom;
import edu.stevens.cs522.chat.managers.ChatroomManager;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.rest.ServiceManager;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class ChatroomActivity extends AppCompatActivity implements IIndexManager<ChatRoom>, ChatFragment.IChatListener, SendMessage.IMessageSender, ResultReceiverWrapper.IReceive {

    private final static String TAG = ChatroomActivity.class.getCanonicalName();

    private final static String SHOWING_CHATROOMS_TAG = "INDEX-FRAGMENT";

    private final static String SHOWING_MESSAGES_TAG = "CHAT-FRAGMENT";

    private final static String ADDING_MESSAGE_TAG = "ADD-MESSAGE-DIALOG";

    /*
     * Managers
     */
    private ChatroomManager chatroomManager;

    private MessageManager messageManager;

    private PeerManager peerManager;

    private ServiceManager serviceManager;

    /**
     * Fragments
     */
    private boolean isTwoPane;

    private IIndexManager.Callback<ChatRoom> indexFragment;

    private ChatFragment chatFragment;


    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;

    private static String CURRENT_ROOM_KEY= "currentroomkey";
    private static String CURRENT_ROOM_ID_KEY= "currentroomidkey";
    private String currentRoom = "";
    private long currentRoomID = 0;

    /*
	 * Called when the activity is first created. 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatrooms);
        if(savedInstanceState!=null){
            currentRoom = savedInstanceState.getString(CURRENT_ROOM_KEY);
            currentRoomID = savedInstanceState.getLong(CURRENT_ROOM_ID_KEY);
        }
        Log.i(TAG,"onCreate currentRoom="+currentRoom);

        // TODO initialize sendResultReceiver and serviceManager
        sendResultReceiver = new ResultReceiverWrapper(new Handler());
        serviceManager = new ServiceManager(this);

        isTwoPane = getResources().getBoolean(R.bool.is_two_pane);
        Log.i(TAG,"isTwoPane="+isTwoPane);
        if (!isTwoPane) {
            // TODO add an index fargment as the fragment in the frame layout

//            if(indexFragment==null) {
//                Log.i(TAG," onCreate ... no indexFragment");
//                indexFragment = new IndexFragment();
//            }
//            getFragmentManager().beginTransaction().add(R.id.fragment_container, (IndexFragment) indexFragment, IndexFragment.TAG).commit();
        }

        // TODO create the message and peer and chatroom managers
        peerManager = new PeerManager(this);
        messageManager = new MessageManager(this);
//        messageManager.getAllMessagesAsync(this);
        chatroomManager = new ChatroomManager(this);

        // TODO instantiate helper for service
        helper = new ChatHelper(this, sendResultReceiver);




        /**
         * Initialize settings to default values.
         */
        if (!Settings.isRegistered(this)) {
            Settings.getClientId(this);
            // TODO launch registration activity
            startActivity(new Intent(this, RegisterActivity.class));
        }

//        Button syncButton = (Button) findViewById(R.id.button_test_send);
//        syncButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                serviceManager.triggerSync();
//            }
//        });
    }
    @Override
    public void onBackPressed() {

        // your code.
        if(currentRoom!="") {
            if (!isTwoPane) {
                super.onBackPressed();
            } else {
                chatFragment.setChatroom(new ChatRoom());
            }
            indexFragment.clearHighlight();
        }
        currentRoom = "";
        Log.i(TAG,"onBackPressed() currentRoom="+currentRoom);
    }

    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResumen"+ (messageManager==null));
        sendResultReceiver.setReceiver(this);
        serviceManager.scheduleBackgroundOperations();
    }

    public void onPause() {
        Log.i(TAG,"onPause");
        super.onPause();
        sendResultReceiver.setReceiver(null);
        serviceManager.cancelBackgroundOperations();
    }

    public void onDestroy() {
        Log.i(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG,"onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_ROOM_KEY,currentRoom);
        outState.putLong(CURRENT_ROOM_ID_KEY,currentRoomID);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.i(TAG,"onConfigurationChanged() newConfig.orientation="+newConfig.orientation);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.i(TAG,"onConfigurationChanged() remove chatFragment");
                Fragment chatFragmentTag = getFragmentManager().findFragmentByTag(ChatFragment.TAG);
                if(chatFragmentTag!=null) {
                    getFragmentManager().beginTransaction().remove(chatFragmentTag).commit();
                }
//                chatFragment = null;
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            //remove fragment
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
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


    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
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

    /**
     * Callbacks for index fragment
     */

    @Override
    public SimpleCursorAdapter getIndexTitles(Callback<ChatRoom> callback) {
        Log.i(TAG,"SimpleCursorAdapter getIndexTitles");
        indexFragment = callback;
        indexFragment.setIndexTitle(getString(R.string.chat_rooms_title));
        chatroomManager.getAllChatroomsAsync(chatroomQueryListener);

        String[] from = {ChatroomContract.NAME};
        int[] to = { android.R.id.text1 };
        return new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1, null, from, to, 0);
    }

    @Override
    public void onItemSelected(ChatRoom chatroom) {
        currentRoom = chatroom.name;
        currentRoomID = chatroom.id;
        Log.i(TAG,"onItemSelected isTwoPane="+isTwoPane+" , currentRoom="+currentRoom);
        if (isTwoPane) {
            // For two pane, push selection of chatroom to chat fragment, which will then
            // ask the parent activity to query the database.
            chatFragment = (ChatFragment) getFragmentManager().findFragmentById(R.id.chat_fragment);
            chatFragment.setChatroom(chatroom);
        } else {
            // For single pane, replace index fragment with messages fragment
            chatFragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putParcelable(ChatFragment.CHATROOM_KEY, chatroom);
            chatFragment.setArguments(args);
            // TODO replace index fragment
            getFragmentManager().beginTransaction().add(R.id.index_fragment, chatFragment, ChatFragment.TAG).addToBackStack(null).commit();

        }
    }

    /**
     * Callbacks for querying database for chatrooms
     */

    private QueryBuilder.IQueryListener<ChatRoom> chatroomQueryListener = new QueryBuilder.IQueryListener<ChatRoom>() {

        @Override
        public void handleResults(TypedCursor<ChatRoom> results) {
            Log.i(TAG,"QueryBuilder.IQueryListener<ChatRoom> handleResults() results::"+(results==null));
            indexFragment.setTitles(results);
            //set chatroom if exist
            if(currentRoom!=""){
                //here is a bug from callback of loader
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        onItemSelected(new ChatRoom(currentRoomID,currentRoom));
                    }
                };
                handler.sendEmptyMessage(1);
            }
        }

        @Override
        public void closeResults() {
            indexFragment.clearTitles();
        }

    };

    /**
     * Callbacks for chat fragment
     */

    @Override
    public void getMessages(ChatRoom chatroom) {

        Bundle bundle = new Bundle();
        bundle.putLong(ChatroomContract.ID,chatroom.id);
        messageManager.getAllMessagesAsync(chatroom, messageQueryListener);
    }

    @Override
    public void addMessageDialog(ChatRoom chatroom) {
        SendMessage.launch(this, ADDING_MESSAGE_TAG, chatroom);
    }

    /**
     * Callbacks for querying for messages
     */

    private QueryBuilder.IQueryListener<ChatMessage> messageQueryListener = new QueryBuilder.IQueryListener<ChatMessage>() {

        @Override
        public void handleResults(TypedCursor<ChatMessage> results) {
            Log.i(TAG,"QueryBuilder.IQueryListener handleResults");
            if (chatFragment != null) {
                chatFragment.handleResults(results);
            }
        }

        @Override
        public void closeResults() {
            if (chatFragment != null) {
                chatFragment.closeResults();
            }
        }

    };

    /**
     * Callbacks for message posting dialog
     */

    @Override
    public void send(ChatRoom chatroom, String message, Double latitude, Double longitude, Date timestamp) {
        helper.postMessage(chatroom.name, message);
    }
}