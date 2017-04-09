package edu.stevens.cs522.chat.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.UUID;

/**
 * Created by dduggan.
 */

public class Settings {

    public static String SETTINGS = "settings";

    private static String REGISTERED_KEY = "registered";

    private static String CLIENT_ID_KEY = "client-id";

    private static String CHAT_NAME_KEY = "user-name";

    private static final String CHAT_SERVER_KEY = "server-uri";

    public static UUID getClientId(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

        String clientID = prefs.getString(CLIENT_ID_KEY, null);
        if (clientID == null) {
            clientID = UUID.randomUUID().toString();
            Log.i("settings","clientID:"+clientID);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(CLIENT_ID_KEY, clientID);
            editor.commit();
        }
        return UUID.fromString(clientID);
    }

    public static String getChatName(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return prefs.getString(CHAT_NAME_KEY, null);
    }

    public static void saveChatName(Context context, String chatName) {
//        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit();
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString(CHAT_NAME_KEY, chatName);
        editor.commit();
    }

    public static String getServerUri(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(CHAT_SERVER_KEY, null);
    }

    public static void saveServerUri(Context context, String serverUri) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit();
        // SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(CHAT_SERVER_KEY, serverUri);
        editor.commit();
    }

    public static boolean isRegistered(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return prefs.getBoolean(REGISTERED_KEY, false);
    }

    public static void setRegistered(Context context, boolean init) {
//        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit();
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(REGISTERED_KEY, init);
        editor.commit();
    }

}
