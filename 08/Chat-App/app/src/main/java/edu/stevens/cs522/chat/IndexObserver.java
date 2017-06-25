package edu.stevens.cs522.chat;

import android.database.ContentObserver;
import android.os.Handler;
import android.widget.CursorAdapter;
import android.widget.ListView;

/**
 * Created by dduggan.
 */

public class IndexObserver extends ContentObserver {

    private CursorAdapter adapter;
    private ListView lv;

    public IndexObserver(CursorAdapter adapter, ListView lv) {
        super(new Handler());
        this.adapter = adapter;
        this.lv = lv;
    }

    @Override
    public void onChange(boolean self) {
        lv.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lv.setSelection(adapter.getCount() - 1);
            }
        });
    }

}