package edu.stevens.cs522.chat.services;

import android.os.ResultReceiver;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by dduggan.
 */

public interface IChatService {

    public void send(String destAddress, int destPort, String sender, String message, ResultReceiver receiver);
}
