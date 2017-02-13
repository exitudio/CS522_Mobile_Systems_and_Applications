package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import edu.stevens.cs522.chatserver.R;

/**
 * Created by dduggan.
 */

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String USERNAME_KEY = "username";

    public static final String APP_PORT_KEY = "app_port";

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i("SettingsFragment","onCreate");
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the messages content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        setValue();
    }
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences userDetails = getApplicationContext().getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        setValue();
    }

    private void setValue(){
        /*SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String username = settings.getString(SettingsActivity.USERNAME_KEY, getResources().getString(R.string.default_user_name));
        ((EditText) findViewById(R.id.username_edit)).setText(username);

        int port = Integer.valueOf(settings.getString(SettingsActivity.APP_PORT_KEY, getResources().getString(R.string.default_app_port)));
        ((EditText) findViewById(R.id.port_edit)).setText(username);*/
    }
}