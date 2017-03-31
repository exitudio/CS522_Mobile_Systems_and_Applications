package edu.stevens.cs522.chat.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ResourceCursorAdapter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import edu.stevens.cs522.chat.activities.SettingsActivity;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

import static android.app.Activity.RESULT_OK;


public class ChatService extends Service implements IChatService, SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String TAG = ChatService.class.getCanonicalName();
    protected static final String SEND_TAG = "ChatSendThread";
    protected static final String RECEIVE_TAG = "ChatReceiveThread";
    protected IBinder binder = new ChatBinder();
    protected SendHandler sendHandler;
    protected Thread receiveThread;
    protected DatagramSocket chatSocket;
//    protected boolean socketOK = true;
    protected boolean finished = false;
    PeerManager peerManager;
    MessageManager messageManager;
    protected int chatPort;

    @Override
    public void onCreate() {
        Log.i(TAG,"onCreate");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        chatPort = Integer.valueOf( prefs.getString(SettingsActivity.APP_PORT_KEY, SettingsActivity.DEFAULT_APP_PORT) );


        prefs.registerOnSharedPreferenceChangeListener(this);

        peerManager = new PeerManager(this);
        messageManager = new MessageManager(this);

        try {
            chatSocket = new DatagramSocket(chatPort);
        } catch (Exception e) {
            IllegalStateException ex = new IllegalStateException("Unable to init client socket.");
            ex.initCause(e);
            throw ex;
        }

        // TODO initialize the thread that sends messages
        //slide 57
        //Create a background handler thread
        HandlerThread sendThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        sendThread.start();
        sendHandler = new SendHandler(sendThread.getLooper());
//        Messenger messenger = new Messenger(sendHandler); ///from slide 49
        // end TODO

        receiveThread = new Thread(new ReceiverThread());
        receiveThread.start();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy");
        finished = true;
        sendHandler.getLooper().getThread().interrupt();  // No-op?
        sendHandler.getLooper().quit();
        receiveThread.interrupt();
        chatSocket.close();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public final class ChatBinder extends Binder {

        public IChatService getService() {
            return ChatService.this;
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(SettingsActivity.APP_PORT_KEY)) {
            try {
                chatSocket.close();
                chatPort = Integer.valueOf( prefs.getString(SettingsActivity.APP_PORT_KEY, SettingsActivity.DEFAULT_APP_PORT) );
                chatSocket = new DatagramSocket(chatPort);

                //new receiveThread because chatSocket has to be renew.
                receiveThread = new Thread(new ReceiverThread());
                receiveThread.start();
            } catch (IOException e) {
                IllegalStateException ex = new IllegalStateException("Unable to change client socket.");
                ex.initCause(e);
                throw ex;
            }
        }
    }

    @Override
    public void send(String destAddress, int destPort, String sender, String messageText, ResultReceiver receiver) {
        Message message = sendHandler.obtainMessage();
        Bundle args = new Bundle();
        args.putString(SendHandler.DEST_ADDRESS, destAddress);
        args.putInt(SendHandler.DEST_PORT, destPort);
        args.putString(SendHandler.CHAT_NAME, sender);
        args.putString(SendHandler.CHAT_MESSAGE, messageText);
        args.putSerializable(SendHandler.RECEIVER, (ResultReceiverWrapper) receiver);
        message.setData(args);
        // TODO send the message to the sending thread
        //slide 68
        sendHandler.sendMessage(message);
        Log.i(TAG,"send");
    }


    private final class SendHandler extends Handler {

        public static final String CHAT_NAME = "edu.stevens.cs522.chat.services.extra.CHAT_NAME";
        public static final String CHAT_MESSAGE = "edu.stevens.cs522.chat.services.extra.CHAT_MESSAGE";
        public static final String DEST_ADDRESS = "edu.stevens.cs522.chat.services.extra.DEST_ADDRESS";
        public static final String DEST_PORT = "edu.stevens.cs522.chat.services.extra.DEST_PORT";
        public static final String RECEIVER = "edu.stevens.cs522.chat.services.extra.RECEIVER";

        public SendHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {
            //InetAddress.getByName(
            Bundle args = message.getData();
            InetAddress destAddress;
            int destPort = args.getInt(DEST_PORT);
            String sender = args.getString(CHAT_NAME);
            String messageText = args.getString(CHAT_MESSAGE);
            ResultReceiverWrapper receiverWrapper = (ResultReceiverWrapper) args.getSerializable(RECEIVER);
            try {
                destAddress = InetAddress.getByName(args.getString(DEST_ADDRESS));


                byte[] sendData;  // Combine sender and message text; default encoding is UTF-8
                ResultReceiver receiver;

                // TODO get data from message (including result receiver)

                //modify sedning string in form "sendr:timestamp:messageText"
                messageText = sender+":"+ System.currentTimeMillis() +":"+messageText;

                //String messageText = "change me";
                receiver = new ResultReceiver(this);
                //String stringData = message.toString();
                sendData = messageText.getBytes("UTF8");//stringData.getBytes("UTF8");
                //destAddr = InetAddress.getByName("192.168.1.12");//destinationHost.getText().toString() );
                //destPort = Integer.parseInt("6666");//destinationPort.getText().toString() );



                // End todo

                DatagramPacket sendPacket = new DatagramPacket(sendData,
                        sendData.length, destAddress, destPort);
                chatSocket.send(sendPacket);
                Log.i(TAG, "Sent packet: " + messageText);
                receiver.send(RESULT_OK, null);
                receiverWrapper.onReceiveResult(RESULT_OK,null);
            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown host exception: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IO exception: " + e.getMessage());
            }

        }
    }

    private final class ReceiverThread implements Runnable {
        private boolean socketOK = true;
        public void run() {
            Looper.prepare();
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            Log.i(TAG,"finished:"+finished +", socketOK: "+ socketOK);
            while (!finished && socketOK) {
                Log.i(TAG,"ReceiverThread run");
                try {

                    chatSocket.receive(receivePacket);
                    Log.i(TAG, "Received a packet");

                    InetAddress sourceIPAddress = receivePacket.getAddress();
                    Log.i(TAG, "Source IP Address: " + sourceIPAddress);

                    String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");

                    final ChatMessage message = new ChatMessage();
                    message.sender = msgContents[0];
                    message.timestamp = new Date(Long.parseLong(msgContents[1]));
                    message.messageText = msgContents[2];

                    Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);

                    Peer sender = new Peer();
                    sender.name = message.sender;
                    sender.timestamp = message.timestamp;
                    sender.address = receivePacket.getAddress();
                    sender.port = receivePacket.getPort();

                    peerManager.persistAsync(sender, new IContinue<Long>() {
                        @Override
                        public void kontinue(Long id) {
                            Log.i("peerManager.","persistAsync");
                            message.senderId = id;
                            messageManager.persistAsync(message);
                        }
                    });

                } catch (Exception e) {

                    Log.e(TAG, "Problems receiving packet.", e);
                    socketOK = false;
                }
                Log.i(TAG,"ReceiverThread run stop()");
            }
            Log.i(TAG,"ReceiverThread end while");
        }

    }

}
