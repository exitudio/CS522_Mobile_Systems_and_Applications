package edu.stevens.cs522.chat.rest;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_SEND;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RequestService extends IntentService {

    public static final String SERVICE_REQUEST_KEY = "edu.stevens.cs522.chat.rest.extra.REQUEST";

    public static final String RESULT_RECEIVER_KEY = "edu.stevens.cs522.chat.rest.extra.RECEIVER";

    private RequestProcessor processor;

    public RequestService() {
        super("RequestService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        processor = new RequestProcessor(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Request request = intent.getParcelableExtra(SERVICE_REQUEST_KEY);
        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER_KEY);
        Log.i(this.getClass().toString(),"onHandleIntent: clientID"+request.clientID.toString());
        Response response = processor.process(request);
        Log.i(this.getClass().toString()," onHandleIntent() : httpResponseCode="+response.httpResponseCode);
        Log.i(this.getClass().toString()," onHandleIntent() : httpResponseMessage="+response.httpResponseMessage);

        if (receiver != null) {
            // TODO UI should display a toast message on completion of the operation
            Log.i(this.getClass().toString(), "receiver");
            receiver.send(RESULT_OK, null);
        }
    }



}
